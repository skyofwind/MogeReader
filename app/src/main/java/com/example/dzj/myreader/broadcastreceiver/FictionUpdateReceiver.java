package com.example.dzj.myreader.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by dzj on 2017/8/28.
 */

public class FictionUpdateReceiver extends BroadcastReceiver {
    public static final String FICTION_UPDATE = "fiction_update";
    public static final String FICYION_BAR_CHANGE = "fiction_bar_change";
    public static final String GRID_LONG_PRESS = "grid_long_press";
    public static final String TOAST_RECEIVER = "toast_receiver";
    private FictionUpdateListener fictionUpdateListener, ficitionBarChangeListenner, gridLongPressListener;
    private ToastListener toastListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(FICTION_UPDATE)){
            fictionUpdateListener.setFictionUpdateDisable();
            Log.i("mythistest","接收到了广播");
        }
        if(intent.getAction().equals(FICYION_BAR_CHANGE)){
            ficitionBarChangeListenner.setFictionUpdateDisable();
        }
        if(intent.getAction().equals(GRID_LONG_PRESS)){
            gridLongPressListener.setFictionUpdateDisable();
        }
        if(intent.getAction().equals(TOAST_RECEIVER)){
            String s = intent.getStringExtra("toast");
            if(s != null){
                toastListener.setToast(s);
            }

        }
    }
    public void setFictionUpdateListener(FictionUpdateListener fictionUpdateListener){
        this.fictionUpdateListener = fictionUpdateListener;
    }

    public void setFicitionBarChangeListenner(FictionUpdateListener ficitionBarChangeListenner){
        this.ficitionBarChangeListenner = ficitionBarChangeListenner;
    }
    public interface FictionUpdateListener {
        void setFictionUpdateDisable();
    }
    public interface ToastListener{
        void setToast(String s);
    }

    public void setGridLongPressListener(FictionUpdateListener longPressListener){
        this.gridLongPressListener = longPressListener;
    }

    public void setToastListener(ToastListener toastListener){
        this.toastListener = toastListener;
    }
}
