package com.example.dzj.myreader.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.dzj.myreader.broadcastreceiver.FictionUpdateReceiver;
import com.example.dzj.myreader.modle.TxtFile;

import java.util.ArrayList;
import java.util.List;

public class FictionDao {
    private static final String TAG = "FictionDao";
    private FictionDBHelper fictionDBHelper;
    private Context context;
    private final String[] FICTION_COLUMNS = new String[] {"Id", "FictionName","FictionPath","Charset", "Chapter", "Page", "chapterNum", "sequence", "hasForeword"};
    private static FictionDao fictionDao;

    public static FictionDao getInstance(Context context){
        if(fictionDao == null){
            fictionDao = new FictionDao(context);
        }
        return fictionDao;
    }

    private FictionDao(Context context){
        this.context = context;
        fictionDBHelper = new FictionDBHelper(context);
        log("FictionDao");
    }

    public List<TxtFile> getAllData(){
        log("getAllData()");
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = fictionDBHelper.getReadableDatabase();
            // select * from Orders
            cursor = db.query(FictionDBHelper.TABLE_NAME, FICTION_COLUMNS, null, null, null, null, null);

            if (cursor.getCount() > 0) {
                List<TxtFile> fictionList = new ArrayList<TxtFile>(cursor.getCount());
                while (cursor.moveToNext()) {
                    fictionList.add(parseFiction(cursor));
                }
                return fictionList;
            }
        }
        catch (Exception e) {
            Log.e(TAG, "", e);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return null;
    }

    public int insertFictions(List<TxtFile> files){
        int i = 0;
        if (files != null){
            for(TxtFile file : files){
                if(!isExist(file)){
                    if(insertFiction(file)){
                        i++;
                    }
                }
            }
        }
        return i;
    }

    public boolean insertFiction(TxtFile file){
        if(isExist(file)){
            return false;
        }
        log("insertFiction");
        SQLiteDatabase db = null;
        try {
            db = fictionDBHelper.getWritableDatabase();
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put(FICTION_COLUMNS[1], file.getName());
            contentValues.put(FICTION_COLUMNS[2], file.getPath());
            if(file.getCharset() == null){
                contentValues.put(FICTION_COLUMNS[3], "");
            }else {
                contentValues.put(FICTION_COLUMNS[3], file.getCharset());
            }

            contentValues.put(FICTION_COLUMNS[4], 0);
            contentValues.put(FICTION_COLUMNS[5], 0);
            contentValues.put(FICTION_COLUMNS[6], 0);
            contentValues.put(FICTION_COLUMNS[7], 0);
            contentValues.put(FICTION_COLUMNS[8], 0);
            db.insertOrThrow(FictionDBHelper.TABLE_NAME, null, contentValues);

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

    public synchronized boolean deleteFiction(int id){
        log("deleteFiction");
        SQLiteDatabase db = null;
        try {
            db = fictionDBHelper.getWritableDatabase();
            db.beginTransaction();
            db.delete(FictionDBHelper.TABLE_NAME, "Id = ?", new String[]{String.valueOf(id)});
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

    public boolean updateFiction(TxtFile file){
        log("updateFiction");
        SQLiteDatabase db = null;
        try {
            db = fictionDBHelper.getWritableDatabase();
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put(FICTION_COLUMNS[1], file.getName());
            contentValues.put(FICTION_COLUMNS[2], file.getPath());
            contentValues.put(FICTION_COLUMNS[3], file.getCharset());
            contentValues.put(FICTION_COLUMNS[4], file.getChapter());
            contentValues.put(FICTION_COLUMNS[5], file.getPage());
            contentValues.put(FICTION_COLUMNS[6], file.getChapterNum());
            contentValues.put(FICTION_COLUMNS[7], file.getSequence());
            contentValues.put(FICTION_COLUMNS[8], file.getHasForeword());

            db.update(FictionDBHelper.TABLE_NAME, contentValues,"Id = ?", new String[]{String.valueOf(file.getId())});
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

    public TxtFile getTxtFileByID(int id){
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = fictionDBHelper.getReadableDatabase();
            // select * from Orders
            cursor = db.query(FictionDBHelper.TABLE_NAME, FICTION_COLUMNS, "Id = ? ", new String[]{id+""}, null, null, null);
            cursor.moveToFirst();
            return parseFiction(cursor);
        }
        catch (Exception e) {
            Log.e(TAG, "", e);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return null;
    }

    public Boolean isExist(TxtFile file){
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = fictionDBHelper.getReadableDatabase();
            // select * from Orders
            cursor = db.query(FictionDBHelper.TABLE_NAME, FICTION_COLUMNS, "FictionName = ? and FictionPath = ?", new String[]{file.getName(), file.getPath()}, null, null, null);
            if(cursor.getCount() > 0){
                tip(file.getName()+"已经添加过了");
                return true;
            }
        }
        catch (Exception e) {
            Log.e(TAG, "", e);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return false;
    }

    public TxtFile parseFiction(Cursor cursor){
        TxtFile file = new TxtFile();
        file.setId(cursor.getInt(cursor.getColumnIndex(FICTION_COLUMNS[0])));
        file.setName(cursor.getString(cursor.getColumnIndex(FICTION_COLUMNS[1])));
        file.setPath(cursor.getString(cursor.getColumnIndex(FICTION_COLUMNS[2])));
        file.setCharset(cursor.getString(cursor.getColumnIndex(FICTION_COLUMNS[3])));
        file.setChapter(cursor.getInt(cursor.getColumnIndex(FICTION_COLUMNS[4])));
        file.setPage(cursor.getInt(cursor.getColumnIndex(FICTION_COLUMNS[5])));
        file.setChapterNum(cursor.getInt(cursor.getColumnIndex(FICTION_COLUMNS[6])));
        file.setSequence(cursor.getInt(cursor.getColumnIndex(FICTION_COLUMNS[7])));
        file.setHasForeword(cursor.getInt(cursor.getColumnIndex(FICTION_COLUMNS[8])));
        return file;
    }
    private void tip(String s){
        Intent intent = new Intent();
        intent.setAction(FictionUpdateReceiver.TOAST_RECEIVER);
        intent.putExtra("toast", s);
        context.sendBroadcast(intent);
    }
    private void log(String s){
        Log.d(TAG, s);
    }
}
