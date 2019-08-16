package com.example.dzj.myreader.ui.activity

import android.app.Dialog
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.example.dzj.myreader.R

/**
 * Created by dzj on 2018/2/28.
 */

open class BaseActivty : AppCompatActivity() {
    //定时器相关
    private var progressDialog: Dialog? = null
    private var progress = false

    fun statrProgressDialog(s: String) {
        if (progressDialog == null) {
            progressDialog = Dialog(this, R.style.progress_dialog)
            progressDialog!!.setContentView(R.layout.dialog)
            progressDialog!!.setCancelable(true)
            progressDialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        }
        val msg = progressDialog!!.findViewById<View>(R.id.id_tv_loadingmsg) as TextView
        if (s == "") {
            msg.text = "正在加载中"
        } else {
            msg.text = s
        }

        progress = true
        progressDialog!!.show()
    }

    fun cancel() {
        if (progress) {
            progress = false
            progressDialog!!.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

}
