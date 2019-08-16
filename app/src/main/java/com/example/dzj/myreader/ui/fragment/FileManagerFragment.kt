package com.example.dzj.myreader.ui.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView

import com.example.dzj.myreader.R
import com.example.dzj.myreader.ui.activity.AddXiaoshuoActivity
import com.example.dzj.myreader.adpter.FileListviewAdapter
import com.example.dzj.myreader.adpter.HRecyclerviewAdapter
import com.example.dzj.myreader.modle.TxtFile
import com.example.dzj.myreader.utils.FileListUtil

import java.io.File
import java.util.ArrayList

/**
 * Created by dzj on 2017/8/21.
 */

class FileManagerFragment : Fragment(), AddXiaoshuoActivity.FragmentBackListener {
    private var listView: ListView? = null
    private var recyclerView: RecyclerView? = null
    private var fileDatases: MutableList<TxtFile>? = null
    private var datas: MutableList<String>? = null
    private var skipPaths: MutableList<String>? = null

    private var fileListviewAdapter: FileListviewAdapter? = null
    private var recyclerviewAdapter: HRecyclerviewAdapter? = null

    internal var fileItemClickListener: FileListviewAdapter.FileItemClickListener = FileListviewAdapter.FileItemClickListener { position ->
        val file = File(fileDatases!![position].path)
        datas!!.add(file.name)
        skipPaths!!.add(file.path)
        fileDatases!!.clear()
        getFileTxt(file)
        getFileDiretory(file)
        viewReflash()
        //fileListviewAdapter.update();
    }
    internal var onItemClickListener: HRecyclerviewAdapter.OnItemClickListener = HRecyclerviewAdapter.OnItemClickListener { view, position ->
        var length = datas!!.size
        while (position + 1 < length) {
            datas!!.removeAt(position + 1)
            skipPaths!!.removeAt(position + 1)
            length--
        }
        fileDatases!!.clear()
        if (position == 0) {
            firstInit()
        } else {
            val file = File(skipPaths!![position])
            getFileTxt(file)
            getFileDiretory(file)
        }
        viewReflash()
        //fileListviewAdapter.update();
    }

    val add: List<TxtFile>
        get() = fileListviewAdapter!!.add

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.file_manager, null)
        listView = rootView.findViewById<View>(R.id.file_list_view) as ListView
        recyclerView = rootView.findViewById<View>(R.id.recycle) as RecyclerView

        initData()
        initView()

        return rootView
    }

    private fun initData() {
        fileDatases = ArrayList()
        datas = ArrayList()
        skipPaths = ArrayList()
        firstInit()
        datas!!.add(ROOT_PATH)
        skipPaths!!.add(ROOT_PATH)

    }

    private fun firstInit() {
        val internal = FileListUtil.getStoragePath(context!!, true)
        val sdcard = FileListUtil.getStoragePath(context!!, false)
        if (sdcard != null) {
            val file = File(sdcard)
            val mlen = "共" + file.listFiles().size + "项"
            val fileDatas = TxtFile(file.name, sdcard, 0, mlen, true)
            fileDatases!!.add(fileDatas)
        }
        if (internal != null) {
            val file = File(internal)
            val mlen = "共" + file.listFiles().size + "项"
            val fileDatas = TxtFile(file.name, internal, 0, mlen, true)
            fileDatases!!.add(fileDatas)
        }
    }

    private fun initView() {
        recyclerView!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerviewAdapter = HRecyclerviewAdapter(context, datas)
        recyclerviewAdapter!!.setOnItemClickListener(onItemClickListener)
        fileListviewAdapter = FileListviewAdapter(context, fileDatases!!)
        fileListviewAdapter!!.setFileItemClickListener(fileItemClickListener)
        listView!!.adapter = fileListviewAdapter
        recyclerView!!.adapter = recyclerviewAdapter
    }

    private fun viewReflash() {
        recyclerviewAdapter!!.refresh()
        recyclerView!!.smoothScrollToPosition(recyclerviewAdapter!!.itemCount - 1)
        fileListviewAdapter = FileListviewAdapter(context, fileDatases!!)
        fileListviewAdapter!!.setFileItemClickListener(fileItemClickListener)
        //setMyChooseListener();
        listView!!.adapter = fileListviewAdapter
    }

    fun setMyChooseListener() {
        if (fileListviewAdapter != null) {
            Log.i("mythistest", "我是" + TAG + "的")
        }
    }

    override fun onbackForword() {
        if (datas!!.size == 1) {
            activity!!.finish()
        } else if (datas!!.size == 2) {
            datas!!.removeAt(datas!!.size - 1)
            skipPaths!!.removeAt(skipPaths!!.size - 1)
            fileDatases!!.clear()
            firstInit()
            viewReflash()
        } else if (datas!!.size > 2) {
            val file = File(skipPaths!![skipPaths!!.size - 2])
            datas!!.removeAt(datas!!.size - 1)
            skipPaths!!.removeAt(skipPaths!!.size - 1)
            fileDatases!!.clear()
            getFileTxt(file)
            getFileDiretory(file)
            viewReflash()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (activity is AddXiaoshuoActivity) {
            (activity as AddXiaoshuoActivity).backListener = this
            (activity as AddXiaoshuoActivity).isInterception = true
        }
    }

    override fun onDetach() {
        super.onDetach()
        if (activity is AddXiaoshuoActivity) {
            (activity as AddXiaoshuoActivity).backListener = null
            (activity as AddXiaoshuoActivity).isInterception = false
        }
    }

    private fun getFileDiretory(file: File) {
        val files = file.listFiles()
        for (f in files) {
            if (f.isDirectory) {
                val mlen = "共" + f.listFiles().size + "项"
                val fileDatas = TxtFile(f.name, f.path, 0, mlen, true)
                fileDatases!!.add(fileDatas)
            }
        }
    }

    private fun getFileTxt(file: File) {
        val files = file.listFiles()
        for (f in files) {
            if (!f.isDirectory) {
                if (f.name.length >= 5) {
                    val suffix = f.name.substring(f.name.length - 4, f.name.length)
                    if (suffix == ".txt") {
                        val fileDatas = TxtFile(f.name, f.path, 0, getFileSize(f.length()), false)
                        fileDatases!!.add(fileDatas)
                    }
                }
            }
        }
    }

    private fun getFileSize(length: Long): String {
        var result: String? = null
        var size: Long = 0
        val kb: Long = 1024
        val mb = kb * 1024
        if (length >= 1024) {
            if (length >= mb) {
                size = length / mb
                result = size.toString() + "mb"
            } else {
                size = length / kb
                result = size.toString() + "kb"
            }

        } else {
            size = length
            result = size.toString() + "b"
        }
        return result
    }

    fun chooseAll() {
        fileListviewAdapter!!.chooseAll()
    }

    companion object {

        private val TAG = "FileManagerFragment"
        private val ROOT_PATH = "根目录"
    }
}
