package com.cp470group7.launcherapp.MusicStreamerBast9100;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class musicStreamDB extends SQLiteOpenHelper {
    public static String DATABASE_NAME = "musicStreams.db";
    public static int VERSION_NUM = 1;

    public static final String TABLE_ITEMS = "MUSICSTREAMS";
    public static final String KEY_ID = "ID";
    public static final String KEY_LINK = "STREAMLINK";
    public static final String KEY_NAME = "STREAMNAME";

    private static final String DATABASE_CREATE = "create table "+ TABLE_ITEMS +
            "(" + KEY_ID+ " integer primary key autoincrement, "
            + KEY_NAME+ " text not null," + KEY_LINK + " text not null);";

    musicStreamDB(Context ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        InitialInsert(db);
        super.onConfigure(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(musicStreamDB.class.getName(),
                "Upgrading database from version " + oldVersion + " to "+ newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
        InitialInsert(db);
    }

    private void InitialInsert(SQLiteDatabase db)
    {
        DefaultMusicStreamData defaultMusicStreamData = new DefaultMusicStreamData();
        int i = 0;
        while(i < (defaultMusicStreamData.count() - 1))
        {
            db.execSQL("INSERT INTO " + TABLE_ITEMS + "("+KEY_NAME +"," + KEY_LINK +")" +
                    " VALUES(\"" + defaultMusicStreamData.defualtMusic[i] + "\",\"" + defaultMusicStreamData.defualtMusic[i+1] +"\");");
            i++;
        }
    }
}

