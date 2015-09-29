package com.example.yhuang.photofeed.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.apache.commons.logging.Log;

import java.util.ArrayList;

/**
 * Created by yhuang on 9/17/2015.
 */
public class PhotoHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    static final String DATABASE_NAME = "photo.db";

    public PhotoHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PhotoContract.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(PhotoContract.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public static void SyncWithDatabase(PhotoHelper mDbHelper, ArrayList<String> items) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String[] projection = {
                PhotoContract.PhotoEntry._ID,
                PhotoContract.PhotoEntry.PHOTO_SOURCE_LINK
        };

        String sortOrder =
                PhotoContract.PhotoEntry._ID + " DESC";

        Cursor cursor = db.query(
                PhotoContract.PhotoEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        if (cursor.moveToFirst()) {
            do {
                items.add(cursor.getString(cursor.getColumnIndex(PhotoContract.PhotoEntry.PHOTO_SOURCE_LINK)));
            }
            while (cursor.moveToNext());
        }
    }

    public static void WriteIntoDatabase(PhotoHelper mDbHelper, String link) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PhotoContract.PhotoEntry.PHOTO_SOURCE_LINK, link);
        Cursor c = db.rawQuery(
                "SELECT * FROM " + PhotoContract.PhotoEntry.TABLE_NAME, null
        );

        boolean flag = false;

        while(c.moveToNext())
        {
            if(c.getString(c.getColumnIndex(PhotoContract.PhotoEntry.PHOTO_SOURCE_LINK)).equals(link))
            {
                flag = true;
            }
        }

        if (!flag) {
            db.insert(
                    PhotoContract.PhotoEntry.TABLE_NAME,
                    null,
                    values);
        }
    }

    public static void DeleteItemDatabase(PhotoHelper mDbHelper, String link) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = PhotoContract.PhotoEntry.PHOTO_SOURCE_LINK + " LIKE ?";
        String[] selectionArgs = { link };
        db.delete(PhotoContract.PhotoEntry.TABLE_NAME, selection, selectionArgs);
    }

}
