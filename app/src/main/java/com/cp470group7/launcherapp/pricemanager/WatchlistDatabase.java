package com.cp470group7.launcherapp.pricemanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class WatchlistDatabase extends SQLiteOpenHelper {

    private static String ACTIVTIY_NAME = "WatchlistDatabase";
    private static String DATABASE_NAME = "Watchlist.db";
    private static int VERSION_NUM = 1;
    static String TABLE_NAME = "watchlist_table";
    static String ASIN_NAME = "asin";
    static String AMAZONITEM_NAME = "amazonitem";

    protected SQLiteDatabase db;

    WatchlistDatabase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUM);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(ACTIVTIY_NAME, "Watchlist db created");
        String createTableSQL = "create table " + TABLE_NAME + " (" + ASIN_NAME + " TEXT PRIMARY KEY, " + AMAZONITEM_NAME + " TEXT)";
        db.execSQL(createTableSQL);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(ACTIVTIY_NAME, "Database updated from " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
