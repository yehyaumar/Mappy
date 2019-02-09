package com.yehyaumar.mappy.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import static com.yehyaumar.mappy.data.DbContract.*;

/**
 * Created by YehyaUmar on 8/6/2017.
 */
public class DbAdapter {

    private static SQLiteDatabase db;

    private DbAdapter (){

    }
    public static DbAdapter getDBInstance(Context context){
        DbHelper dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();
        return new DbAdapter();
    }
//
//    public DbAdapter open() throws SQLException{
//
//    }

//    public void close(){
//        dbHelper.close();
//    }
    public void beginTransaction(){
        db.beginTransaction();
    }
    public void setTransactionSuccessful(){
        db.setTransactionSuccessful();
    }
    public void endTransaction(){
        db.endTransaction();
    }

    public long insert(String webApp, String cipherText, String salt, String iv){
        ContentValues values = new ContentValues();
        values.put(DbEntry.COL_WEBAPP_NAME, webApp);
        values.put(DbEntry.COL_CIPHER_TEXT, cipherText);
        values.put(DbEntry.COL_SALT, salt);
        values.put(DbEntry.COL_IV, iv);

        return db.insert(DbEntry.TABLE_NAME, null, values);
    }

    public boolean deleteRow(long rowid){
        return db.delete(DbEntry.TABLE_NAME, DbEntry._ID + "=" +rowid, null) > 0;
    }

    public Cursor getAll(){
        return db.query(DbEntry.TABLE_NAME, new String[]{DbEntry._ID, DbEntry.COL_WEBAPP_NAME,
                DbEntry.COL_CIPHER_TEXT, DbEntry.COL_SALT, DbEntry.COL_IV}, DbEntry._ID + ">" + 1, null, null, null, null);
    }

    public Cursor get(long rowId) throws SQLException{
        Cursor mCursor = db.query(true, DbEntry.TABLE_NAME, new String[]{DbEntry._ID, DbEntry.COL_WEBAPP_NAME,
                        DbEntry.COL_CIPHER_TEXT, DbEntry.COL_SALT, DbEntry.COL_IV}, DbEntry._ID + "=" + rowId,
                null, null, null, null, null);

        if (mCursor != null)
            mCursor.moveToFirst();

        return mCursor;
    }

    public boolean update(long rowId, String webApp, String cipherText, String salt, String iv){
        ContentValues values = new ContentValues();
        values.put(DbEntry.COL_WEBAPP_NAME, webApp);
        values.put(DbEntry.COL_CIPHER_TEXT, cipherText);
        values.put(DbEntry.COL_SALT, salt);
        values.put(DbEntry.COL_IV, iv);

        return db.update(DbEntry.TABLE_NAME, values, DbEntry._ID + "=" + rowId, null) > 0;
    }



}
