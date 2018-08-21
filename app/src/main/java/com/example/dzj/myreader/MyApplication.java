package com.example.dzj.myreader;

import android.app.Application;

import com.example.dzj.myreader.utils.BuglyUtil;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        BuglyUtil.initBugly(getApplicationContext(), true);
    }
}
