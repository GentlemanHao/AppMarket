package com.greatest.appmarket.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by WangHao on 2017.04.13  0013.
 */

public class DownloadOpenHelper extends SQLiteOpenHelper {
    public DownloadOpenHelper(Context context) {
        super(context, "downloadrecord.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table downloadrecord (_id integer primary key autoincrement , name varchar(20), packagename varchar(20),time varchar(20),size varchar(20),iconurl varchar(50));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
