package com.example.yhuang.photofeed.Database;

import android.provider.BaseColumns;

/**
 * Created by yhuang on 9/17/2015.
 */
public class PhotoContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public PhotoContract() {}

    public static final String TEXT_TYPE = " TEXT";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + PhotoEntry.TABLE_NAME + " (" +
                    PhotoEntry._ID + " INTEGER PRIMARY KEY," +
                    PhotoEntry.PHOTO_SOURCE_LINK + TEXT_TYPE +
            " )";
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PhotoEntry.TABLE_NAME;

    /* Inner class that defines the table contents */
    public static abstract class PhotoEntry implements BaseColumns {
        public static final String TABLE_NAME = "Photo";
        public static final String PHOTO_SOURCE_LINK = "Source_Link";
    }
}
