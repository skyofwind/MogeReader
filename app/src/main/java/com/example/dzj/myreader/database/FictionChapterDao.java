package com.example.dzj.myreader.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.dzj.myreader.broadcastreceiver.FictionUpdateReceiver;
import com.example.dzj.myreader.modle.Chapter;
import com.example.dzj.myreader.modle.LineData;
import com.example.dzj.myreader.modle.TxtFile;

import java.util.ArrayList;
import java.util.List;

public class FictionChapterDao {
    private static final String TAG = "FictionChapterDao";
    private FictionChapterDBHelper fictionChapterDBHelper;
    private Context context;
    private final String[] FICTION_CHAPTER_COLUMNS = new String[] {"Id", "fid","lineNum","chapterNum", "size", "length", "title", "isRead"};
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

    public synchronized List<LineData> getLinedatas(int fid){
        log("getLinedatas");
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = fictionChapterDBHelper.getReadableDatabase();
            // select * from Orders
            cursor = db.query(FictionChapterDBHelper.TABLE_NAME, FICTION_CHAPTER_COLUMNS, "fid = ?", new String[]{String.valueOf(fid)}, null, null, null);

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

    public synchronized int insertChapters(int fid, List<LineData> lineDatas){
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

    public synchronized boolean insertChapter(int fid, LineData lineData){
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
            contentValues.put(FICTION_CHAPTER_COLUMNS[6], lineData.getChapterTitle());
            contentValues.put(FICTION_CHAPTER_COLUMNS[7], 0);

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

    public synchronized LineData getLineDataById(int id){
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = fictionChapterDBHelper.getReadableDatabase();
            // select * from Orders
            cursor = db.query(FictionChapterDBHelper.TABLE_NAME, FICTION_CHAPTER_COLUMNS, "Id = ?", new String[]{String.valueOf(id)}, null, null, null);
            cursor.moveToFirst();
            return parseLineData(cursor);
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

    public synchronized boolean updateChapter(LineData lineData){
        SQLiteDatabase db = null;
        try {
            db = fictionChapterDBHelper.getWritableDatabase();
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();

            contentValues.put(FICTION_CHAPTER_COLUMNS[2], lineData.getLiteNum());
            contentValues.put(FICTION_CHAPTER_COLUMNS[3], lineData.getChapterNum());
            contentValues.put(FICTION_CHAPTER_COLUMNS[4], lineData.getSize());
            contentValues.put(FICTION_CHAPTER_COLUMNS[5], lineData.getLength());
            contentValues.put(FICTION_CHAPTER_COLUMNS[6], lineData.getChapterTitle());
            contentValues.put(FICTION_CHAPTER_COLUMNS[7], lineData.getIsRead());

            db.update(FictionChapterDBHelper.TABLE_NAME, contentValues,"Id = ?", new String[]{String.valueOf(lineData.getId())});
            db.setTransactionSuccessful();
            log("updateChapter "+lineData.getChapterNum()+" "+lineData.getId()+" "+lineData.getIsRead());
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

    public synchronized boolean updateChapter(Chapter chapter){
        SQLiteDatabase db = null;
        try {
            db = fictionChapterDBHelper.getWritableDatabase();
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put(FICTION_CHAPTER_COLUMNS[7], chapter.getIsRead());

            db.update(FictionChapterDBHelper.TABLE_NAME, contentValues,"Id = ?", new String[]{String.valueOf(chapter.getId())});
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

    public synchronized boolean deleteChpaters(int fid){
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

    private synchronized void tip(String s){
        Intent intent = new Intent();
        intent.setAction(FictionUpdateReceiver.TOAST_RECEIVER);
        intent.putExtra("toast", s);
        context.sendBroadcast(intent);
    }

    private synchronized LineData parseLineData(Cursor cursor){
        LineData lineData = new LineData();
        lineData.setId(cursor.getInt(cursor.getColumnIndex(FICTION_CHAPTER_COLUMNS[0])));
        lineData.setLiteNum(cursor.getInt(cursor.getColumnIndex(FICTION_CHAPTER_COLUMNS[2])));
        lineData.setChapterNum(cursor.getInt(cursor.getColumnIndex(FICTION_CHAPTER_COLUMNS[3])));
        lineData.setSize(cursor.getInt(cursor.getColumnIndex(FICTION_CHAPTER_COLUMNS[4])));
        lineData.setLength(cursor.getInt(cursor.getColumnIndex(FICTION_CHAPTER_COLUMNS[5])));
        lineData.setChapterTitle(cursor.getString(cursor.getColumnIndex(FICTION_CHAPTER_COLUMNS[6])));
        lineData.setIsRead(cursor.getInt(cursor.getColumnIndex(FICTION_CHAPTER_COLUMNS[7])));

        return lineData;
    }

    private void log(String s){
        Log.d(TAG, s);
    }
}
