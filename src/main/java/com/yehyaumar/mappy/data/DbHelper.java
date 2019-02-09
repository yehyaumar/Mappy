package com.yehyaumar.mappy.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.yehyaumar.mappy.data.DbContract.*;

/**
 * Created by YehyaUmar on 8/6/2017.
 *
 */

class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION=1;
    private static final String DATABASE_NAME = "passes.db";

    DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TABLE = "CREATE TABLE " + DbEntry.TABLE_NAME
                + " (" + DbEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DbEntry.COL_WEBAPP_NAME + " TEXT NOT NULL, "
                + DbEntry.COL_CIPHER_TEXT + " TEXT NOT NULL, "
                + DbEntry.COL_SALT + " TEXT NOT NULL, "
                + DbEntry.COL_IV + " TEXT)";
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DbEntry.TABLE_NAME);
        onCreate(db);
    }
}
