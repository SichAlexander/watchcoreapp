package com.boost.watchcore.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.boost.watchcore.db.WatchContract.WatchEntry;

/**
 * Created by BruSD on 28.04.2015.
 */
public class WatchDBHelper extends SQLiteOpenHelper {


    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "watch.db";

    public WatchDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_WATCH_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + WatchEntry.TABLE_NAME);

        onCreate(db);
    }

    final String SQL_CREATE_WATCH_TABLE = "CREATE TABLE " + WatchEntry.TABLE_NAME + " (" +
            // Why AutoIncrement here, and not above?
            // Unique keys will be auto-generated in either case.  But for weather
            // forecasting, it's reasonable to assume the user will want information
            // for a certain date and all dates *following*, so the forecast data
            // should be sorted accordingly.
            WatchEntry.COLUMN_WATCH_ID + " INTEGER PRIMARY KEY, " +
            // the ID of the location entry associated with this weather data
            WatchEntry.COLUMN_WATCH_NAME + " TEXT NOT NULL, " +
            WatchEntry.COLUMN_WATCH_ICON  + " TEXT, " +
            WatchEntry.COLUMN_WATCH_IMAGE_FOLDER_LINK  + " TEXT, " +
            WatchEntry.COLUMN_WATCH_DESCRIPTION  + " TEXT, " +
            WatchEntry.COLUMN_WATCH_TIME_STAMP  + " INTEGER NOT NULL, " +
            WatchEntry.COLUMN_WATCH_PACKAGE_NAME  + " TEXT NOT NULL, " +
            WatchEntry.COLUMN_WATCH_ACTION_NAME  + " TEXT, " +

            WatchEntry.COLUMN_WATCH_IS_FREE + " INTEGER NOT NULL);";

}
