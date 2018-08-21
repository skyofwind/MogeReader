package com.example.dzj.myreader.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FictionDBHelper extends SQLiteOpenHelper {
    private final static String TAG = "FictionDBHelper";
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "fiction.db";
    public static final String TABLE_NAME = "Fiction";

    public FictionDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        log("FictionDBHelper(Context context)");
    }

    /**
     *@author
     *@time
     * FictionName 小说名
     * FictionPath 小说文件路径
     * Charset 小说字符集
     * Chapter 上次退出时阅读的是第几章节
     * Page 上次退出阅读时在第几章节的第几页
     * chapterNum
     * sequence 0正序 1倒序
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        log("onCreate(SQLiteDatabase db)");
        String sql = "create table if not exists " + TABLE_NAME + " (Id integer primary key AUTOINCREMENT, FictionName text, FictionPath text, Charset text, Chapter integer, Page integer, chapterNum integer, sequence integer)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        log("onUpgrade");
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);
    }
    private void log(String s){
        Log.d(TAG, s);
    }
}
