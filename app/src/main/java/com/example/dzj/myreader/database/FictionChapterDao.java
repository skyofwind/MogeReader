package com.example.dzj.myreader.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.dzj.myreader.broadcastreceiver.FictionUpdateReceiver;
import com.example.dzj.myreader.modle.LineData;
import com.example.dzj.myreader.modle.TxtFile;

import java.util.ArrayList;
import java.util.List;

public class FictionChapterDao {
    private static final String TAG = "FictionChapterDao";
    private FictionChapterDBHelper fictionChapterDBHelper;
    private Context context;
    private final String[] FICTION_CHAPTER_COLUMNS = new String[] {"Id", "fid","lineNum","chapterNum", "size", "length"};
    private static FictionChapterDao fictionChapterDao;

    public static FictionChapterDao getInstance(Context context){
        if(fictionChapterDao == null){
            fictionChapterDao = new FictionChapterDao(context);
        }
        return fictionChapterDao;
    }

    private FictionChapterDao(Context context){
        this.context = context;
        fictionChapterDBHelper = new FictionChapterDBHelper(context);
    }

    public List<LineData> getLinedatas(){
        log("getLinedatas");
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = fictionChapterDBHelper.getReadableDatabase();
            // select * from Orders
            cursor = db.query(FictionChapterDBHelper.TABLE_NAME, FICTION_CHAPTER_COLUMNS, null, null, null, null, null);

            if (cursor.getCount() > 0) {
                List<LineData> lineDatas = new ArrayList<LineData>(cursor.getCount());
                while (cursor.moveToNext()) {
                    lineDatas.add(parseLineData(cursor));
                }
                return lineDatas;
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return null;
    }

    public int insertChapters(int fid, List<LineData> lineDatas){
        log("insertChapters");
        int i = 0;
        if (lineDatas != null){
            for(LineData lineData : lineDatas){
                if(insertChapter(fid, lineData)){
                    i++;
                }
            }
        }
        return i;
    }

    public boolean insertChapter(int fid, LineData lineData){
        SQLiteDatabase db = null;
        try {
            db = fictionChapterDBHelper.getWritableDatabase();
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put(FICTION_CHAPTER_COLUMNS[1], fid);
            contentValues.put(FICTION_CHAPTER_COLUMNS[2], lineData.getLiteNum());
            contentValues.put(FICTION_CHAPTER_COLUMNS[3], lineData.getChapterNum());
            contentValues.put(FICTION_CHAPTER_COLUMNS[4], lineData.getSize());
            contentValues.put(FICTION_CHAPTER_COLUMNS[5], lineData.getLength());

            db.insertOrThrow(FictionChapterDBHelper.TABLE_NAME, null, contentValues);

            db.setTransactionSuccessful();
            return true;
        }catch (SQLiteConstraintException e){
            tip("主键重复");
        }catch (Exception e){
            Log.e(TAG, "", e);
        }finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
        return false;
    }

    public boolean deleteChpaters(int fid){
        log("deleteChpaters");
        SQLiteDatabase db = null;
        try {
            db = fictionChapterDBHelper.getWritableDatabase();
            db.beginTransaction();
            db.delete(FictionChapterDBHelper.TABLE_NAME, "fid = ?", new String[]{String.valueOf(fid)});
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
        return false;
    }

    private void tip(String s){
        Intent intent = new Intent();
        intent.setAction(FictionUpdateReceiver.TOAST_RECEIVER);
        intent.putExtra("toast", s);
        context.sendBroadcast(intent);
    }

    private LineData parseLineData(Cursor cursor){
        LineData lineData = new LineData();
        lineData.setChapterNum(cursor.getInt(cursor.getColumnIndex(FICTION_CHAPTER_COLUMNS[3])));
        lineData.setLength(cursor.getInt(cursor.getColumnIndex(FICTION_CHAPTER_COLUMNS[5])));
        lineData.setLiteNum(cursor.getInt(cursor.getColumnIndex(FICTION_CHAPTER_COLUMNS[2])));
        lineData.setSize(cursor.getInt(cursor.getColumnIndex(FICTION_CHAPTER_COLUMNS[4])));
        return lineData;
    }

    private void log(String s){
        Log.d(TAG, s);
    }
}
