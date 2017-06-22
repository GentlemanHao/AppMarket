package com.greatest.appmarket.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.greatest.appmarket.bean.DownloadRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WangHao on 2017.04.13  0013.
 */

public class DownloadDao {

    private DownloadOpenHelper downloadOpenHelper;

    private static DownloadDao downloadDao;

    private DownloadDao(Context context) {
        downloadOpenHelper = new DownloadOpenHelper(context);
    }

    public static DownloadDao getInstance(Context context) {
        if (downloadDao == null) {
            downloadDao = new DownloadDao(context);
        }
        return downloadDao;
    }

    // 添加
    public void insert(String name, String packagename, String time, String iconurl, String size) {
        SQLiteDatabase database = downloadOpenHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("packagename", packagename);
        values.put("time", time);
        values.put("iconurl", iconurl);
        values.put("size", size);

        database.insert("downloadrecord", null, values);
        database.close();
    }

    // 删除
    public void delete(String name) {
        SQLiteDatabase database = downloadOpenHelper.getWritableDatabase();

        database.delete("downloadrecord", "name = ?", new String[]{name});

        database.close();
    }

    // 查询
    public List<DownloadRecord> findAll() {
        SQLiteDatabase database = downloadOpenHelper.getWritableDatabase();

        Cursor cursor = database.query("downloadrecord", new String[]{"name", "packagename", "time", "iconurl", "size"}, null, null, null, null,
                "_id desc");
        List<DownloadRecord> downloadRecordList = new ArrayList<DownloadRecord>();
        while (cursor.moveToNext()) {
            DownloadRecord downloadRecord = new DownloadRecord();
            downloadRecord.name = cursor.getString(0);
            downloadRecord.packagename = cursor.getString(1);
            downloadRecord.time = cursor.getString(2);
            downloadRecord.iconurl = cursor.getString(3);
            downloadRecord.size = cursor.getString(4);
            downloadRecordList.add(downloadRecord);
        }
        cursor.close();
        database.close();

        return downloadRecordList;
    }
}
