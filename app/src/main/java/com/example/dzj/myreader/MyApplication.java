package com.example.dzj.myreader;

import android.app.Application;

import com.example.dzj.myreader.utils.BaiduTTSUtil;
import com.example.dzj.myreader.utils.BuglyUtil;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        BuglyUtil.INSTANCE.initBugly(getApplicationContext(), true);
        initBaiduTTS();
    }

    private void initBaiduTTS(){
        BaiduTTSUtil.Companion.getInstance(this).initFile();
        BaiduTTSUtil.Companion.getInstance(this).initTTs();
    }
}
