package com.example.dzj.myreader.ui.fragment

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.example.dzj.myreader.R
import com.example.dzj.myreader.ui.activity.AddXiaoshuoActivity
import com.example.dzj.myreader.ui.activity.MainActivity
import com.example.dzj.myreader.adpter.XiaoshuoManagerAdapter
import com.example.dzj.myreader.broadcastreceiver.FictionUpdateReceiver
import com.example.dzj.myreader.database.FictionDao
import com.example.dzj.myreader.modle.TxtFile

import java.util.ArrayList

/**
 * Created by dzj on 2017/8/19.
 */

class XiaoshuoManagerFragment : Fragment() {
    private var gridView: GridView? = null
    private var files: List<TxtFile>? = null
    private var adapter: XiaoshuoManagerAdapter? = null
    private var width: Int = 0
    private var fictionUpdateReceiver: FictionUpdateReceiver? = null
    private var isUpdate = false
    private var mBottomBar: LinearLayout? = null
    private var delete: TextView? = null

    internal var addBookClickListener: XiaoshuoManagerAdapter.AddBookClickListener = XiaoshuoManagerAdapter.AddBookClickListener {
        val intent = Intent(activity, AddXiaoshuoActivity::class.java)
        startActivity(intent)
    }
    internal var deleteListener: View.OnClickListener = View.OnClickListener {
        if (adapter != null) {
            adapter!!.deleteFiction()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.xiaoshuo_manager, container, false)
        initView(rootView)
        initData()
        setAdapter()
        return rootView
    }

    override fun onResume() {
        super.onResume()
        register()
        if (isUpdate) {
            update()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unRegister()
    }

    private fun initView(rootView: View) {
        gridView = rootView.findViewById<View>(R.id.grid_xiaoshuo) as GridView
        mBottomBar = rootView.findViewById<View>(R.id.mBottomBar) as LinearLayout
        delete = rootView.findViewById<View>(R.id.delete) as TextView

        delete!!.setOnClickListener(deleteListener)
    }

    private fun update() {
        Log.d("update", "我更新GridView了")
        initData()
        setAdapter()
        isUpdate = false
    }

    private fun register() {
        if (fictionUpdateReceiver == null) {
            fictionUpdateReceiver = FictionUpdateReceiver()
            fictionUpdateReceiver!!.setFictionUpdateListener(object : FictionUpdateReceiver.FictionUpdateListener {
                override fun setFictionUpdateDisable() {
                    isUpdate = true
                }
            })
            fictionUpdateReceiver!!.setFicitionBarChangeListenner(object : FictionUpdateReceiver.FictionUpdateListener {
                override fun setFictionUpdateDisable() {

                }
            })
            fictionUpdateReceiver!!.setGridLongPressListener(object : FictionUpdateReceiver.FictionUpdateListener {
                override fun setFictionUpdateDisable() {
                    val activity = activity as MainActivity?
                    if (activity!!.isBarGone) {
                        changeBottomBar()
                        activity.topBarChange()
                    }
                }
            })
            fictionUpdateReceiver!!.setToastListener(object : FictionUpdateReceiver.ToastListener {
                override fun setToast(s: String) {
                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
                }
            })
            val filter = IntentFilter()
            filter.addAction(FictionUpdateReceiver.FICTION_UPDATE)
            filter.addAction(FictionUpdateReceiver.FICYION_BAR_CHANGE)
            filter.addAction(FictionUpdateReceiver.GRID_LONG_PRESS)
            filter.addAction(FictionUpdateReceiver.TOAST_RECEIVER)
            activity!!.registerReceiver(fictionUpdateReceiver, filter)
        }
    }

    private fun unRegister() {
        if (fictionUpdateReceiver != null) {
            activity!!.unregisterReceiver(fictionUpdateReceiver)
            fictionUpdateReceiver = null
        }
    }

    private fun initData() {
        width = activity!!.windowManager.defaultDisplay.width
        files = FictionDao.getInstance(context).allData
        if (files == null) {
            files = ArrayList()
        }

    }

    private fun setAdapter() {
        adapter = XiaoshuoManagerAdapter(context, files!!, width)
        adapter!!.setAddBookClickListener(addBookClickListener)
        gridView!!.adapter = adapter
    }

    fun changeBottomBar() {
        if (mBottomBar != null) {
            if (mBottomBar!!.visibility == View.GONE) {
                mBottomBar!!.visibility = View.VISIBLE
            } else {
                mBottomBar!!.visibility = View.GONE
            }
        }
    }

    fun updateAdapterView() {
        if (adapter != null) {
            adapter!!.updateView()
        }
    }

    fun resetIsChoose(a: Boolean) {
        if (adapter != null) {
            adapter!!.resetIsChoose(a)
        }
    }

}
