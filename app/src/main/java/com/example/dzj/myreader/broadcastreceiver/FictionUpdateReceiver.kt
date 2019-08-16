package com.example.dzj.myreader.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

/**
 * Created by dzj on 2017/8/28.
 */

class FictionUpdateReceiver : BroadcastReceiver() {
    private var fictionUpdateListener: FictionUpdateListener? = null
    private var ficitionBarChangeListenner: FictionUpdateListener? = null
    private var gridLongPressListener: FictionUpdateListener? = null
    private var toastListener: ToastListener? = null

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == FICTION_UPDATE) {
            fictionUpdateListener!!.setFictionUpdateDisable()
            Log.i("mythistest", "接收到了广播")
        }
        if (intent.action == FICYION_BAR_CHANGE) {
            ficitionBarChangeListenner!!.setFictionUpdateDisable()
        }
        if (intent.action == GRID_LONG_PRESS) {
            gridLongPressListener!!.setFictionUpdateDisable()
        }
        if (intent.action == TOAST_RECEIVER) {
            val s = intent.getStringExtra("toast")
            if (s != null) {
                toastListener!!.setToast(s)
            }

        }
    }

    fun setFictionUpdateListener(fictionUpdateListener: FictionUpdateListener) {
        this.fictionUpdateListener = fictionUpdateListener
    }

    fun setFicitionBarChangeListenner(ficitionBarChangeListenner: FictionUpdateListener) {
        this.ficitionBarChangeListenner = ficitionBarChangeListenner
    }

    interface FictionUpdateListener {
        fun setFictionUpdateDisable()
    }

    interface ToastListener {
        fun setToast(s: String)
    }

    fun setGridLongPressListener(longPressListener: FictionUpdateListener) {
        this.gridLongPressListener = longPressListener
    }

    fun setToastListener(toastListener: ToastListener) {
        this.toastListener = toastListener
    }

    companion object {
        val FICTION_UPDATE = "fiction_update"
        val FICYION_BAR_CHANGE = "fiction_bar_change"
        val GRID_LONG_PRESS = "grid_long_press"
        val TOAST_RECEIVER = "toast_receiver"
    }
}
