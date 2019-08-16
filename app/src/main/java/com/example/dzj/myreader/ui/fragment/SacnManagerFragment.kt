package com.example.dzj.myreader.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.fragment.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.LinearLayout

import com.example.dzj.myreader.R
import com.example.dzj.myreader.adpter.FictionListAdapter
import com.example.dzj.myreader.modle.TxtFile
import com.example.dzj.myreader.ui.fragment.SacnManagerFragment.Companion.mCompare
import com.example.dzj.myreader.utils.FileListUtil
import com.example.dzj.myreader.utils.ThreadUtil
import com.example.dzj.myreader.view.FunnyView

import java.io.File
import java.util.ArrayList
import java.util.Collections
import java.util.Comparator

/**
 * Created by dzj on 2017/8/21.
 */

class SacnManagerFragment : Fragment() {
    private var rootView: View? = null
    private var funnyView: FunnyView? = null
    private var listView: ExpandableListView? = null
    private var fictionListAdapter: FictionListAdapter? = null
    private var linearLayout: LinearLayout? = null
    private var txtFiles: MutableList<TxtFile>? = null
    private var day: MutableList<TxtFile>? = null
    private var week: MutableList<TxtFile>? = null
    private var month: MutableList<TxtFile>? = null
    private var other: MutableList<TxtFile>? = null
    private var group: MutableList<String>? = null
    private var files: MutableList<List<TxtFile>>? = null

    @SuppressLint("HandlerLeak")
    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                0x10 -> initView()
                0x11 -> removeFunnyView()
            }//((AddXiaoshuoActivity)getContext()).setIsfunnyCompleted(true);
        }
    }
    val add: List<TxtFile>
        get() = fictionListAdapter!!.add

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.scan_manager, null)

        linearLayout = rootView!!.findViewById<View>(R.id.scan_root) as LinearLayout
        funnyView = rootView!!.findViewById<View>(R.id.funny) as FunnyView
        funnyView!!.start()
        ThreadUtil.getInstance()!!.execute(Runnable {
            seachTxt()
            sort()
            initDatas()
        })
        return rootView
    }

    private fun removeFunnyView() {
        funnyView!!.stop()
        linearLayout!!.removeView(funnyView)
    }

    private fun initView() {
        listView = rootView!!.findViewById<View>(R.id.expandingListView) as ExpandableListView
        group = ArrayList()
        files = ArrayList()
        prepareData()
        fictionListAdapter = FictionListAdapter(context, group, files!!)
        listView!!.setOnChildClickListener { parent, v, groupPosition, childPosition, id -> false }
        listView!!.setOnGroupExpandListener { }
        listView!!.setOnGroupCollapseListener { }
        listView!!.setOnChildClickListener { parent, v, groupPosition, childPosition, id -> true }
        listView!!.setAdapter(fictionListAdapter)
        for (i in group!!.indices) {
            listView!!.expandGroup(i)
        }

    }

    private fun prepareData() {
        if (day!!.size > 0) {
            group!!.add("一天之内")
            files!!.add(day!!)
        }
        if (week!!.size > 0) {
            group!!.add("一周之内")
            files!!.add(week!!)
        }
        if (month!!.size > 0) {
            group!!.add("一月之内")
            files!!.add(month!!)
        }
        if (other!!.size > 0) {
            group!!.add("一月之前")
            files!!.add(other!!)
        }
    }

    private fun seachTxt() {
        txtFiles = ArrayList()
        val internal = FileListUtil.getStoragePath(context!!, true)
        val sdcard = FileListUtil.getStoragePath(context!!, false)
        getAllTxt(internal)
        getAllTxt(sdcard)
        //Log.i("seach_length",internal+" "+sdcard);

    }

    private fun sort() {
        txtFiles!!.sortWith(Comparator { o1, o2 -> mCompare(o1.lastModified, o2.lastModified) })
        txtFiles!!.reverse()
    }

    private fun initDatas() {
        day = ArrayList()
        week = ArrayList()
        month = ArrayList()
        other = ArrayList()
        val s = System.currentTimeMillis()
        for (t in txtFiles!!) {
            val time = s - t.lastModified
            if (time <= DAY) {
                day!!.add(t)
            } else if (time in (DAY + 1)..WEEK) {
                week!!.add(t)
            } else if (time in (WEEK + 1)..MONTH) {
                month!!.add(t)
            } else {
                other!!.add(t)
            }
        }
        mHandler.sendEmptyMessage(0x11)
        txtFiles!!.clear()
        mHandler.sendEmptyMessage(0x10)
    }

    private fun getAllTxt(path: String?) {
        if (null != path) {
            val file = File(path)
            if (!file.exists()) {
                return
            }
            val files = file.listFiles() ?: return
            for (f in files) {
                if (f.isDirectory) {
                    getAllTxt(f.path)
                } else {
                    if (f.name.length >= 5) {
                        val suffix = f.name.substring(f.name.length - 4, f.name.length)
                        if (suffix == ".txt") {
                            val txtFile = TxtFile(f.name, f.path, f.lastModified(), getFileSize(f.length()), true)
                            txtFiles!!.add(txtFile)
                        }
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
        fictionListAdapter!!.chooseAll()
    }

    companion object {
        private val TAG = "SacnManagerFragment"
        private val DAY: Long = 86400000
        private val WEEK = DAY * 7
        private val MONTH = DAY * 30
        fun mCompare(x: Long, y: Long): Int {
            return if (x < y) -1 else if (x == y) 0 else 1
        }
    }
}
