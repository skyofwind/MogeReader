package com.example.dzj.myreader.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FictionChapterDBHelper extends SQLiteOpenHelper {

    private final static String TAG = "FictionChapterDBHelper";
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "fiction_chapter.db";
    public static final String TABLE_NAME = "FictionChapter";

    public FictionChapterDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        log("FictionDBHelper(Context context)");
    }

    /**
     *@author
     *@time
     * fid 父id
     * lineMum 在txt中的行数
     * chpaterNum, 第几章节
     * size 章节字符数
     * length 章节字符长度
     * title 标题
     * isRead 是否阅读过此章节 0否 1是
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        log("onCreate(SQLiteDatabase db)");
        String sql = "create table if not exists " + TABLE_NAME + " (Id integer primary key AUTOINCREMENT, fid integer, lineNum integer, chapterNum integer, size integer, length integer, title txet, isRead integer)";
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
