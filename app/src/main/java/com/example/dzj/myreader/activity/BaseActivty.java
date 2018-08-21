package com.example.dzj.myreader.activity;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.example.dzj.myreader.R;
/**
 * Created by dzj on 2018/2/28.
 */

public class BaseActivty extends AppCompatActivity{
    //定时器相关
    private Dialog progressDialog;
    private boolean  progress=false;

    public void statrProgressDialog(String s){
        if(progressDialog == null){
            progressDialog = new Dialog(this, R.style.progress_dialog);
            progressDialog.setContentView(R.layout.dialog);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        TextView msg = (TextView) progressDialog.findViewById(R.id.id_tv_loadingmsg);
        if(s.equals("")){
            msg.setText("正在加载中");
        }else {
            msg.setText(s);
        }

        progress=true;
        progressDialog.show();
    }
    public void cancel(){
        if(progress){
            progress=false;
            progressDialog.dismiss();
        }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        cancel();
    }

}
