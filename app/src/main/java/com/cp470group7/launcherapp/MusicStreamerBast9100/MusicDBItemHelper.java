package com.cp470group7.launcherapp.MusicStreamerBast9100;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MusicDBItemHelper {
    private SQLiteDatabase database;
    private musicStreamDB dbHelper;
    private String[] allItems = {musicStreamDB.KEY_ID, musicStreamDB.KEY_NAME , musicStreamDB.KEY_LINK};
    private String defaultLink = "StreamLink";

    private static final String TAG = "myItemDB ";

    MusicDBItemHelper(Context context) {
        dbHelper = new musicStreamDB(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }


    public MusicStreamItem createItem(MusicStreamItem item) {
        ContentValues values = new ContentValues();
        values.put(musicStreamDB.KEY_NAME, item.getMusicName());
        long insertId = database.insert(musicStreamDB.TABLE_ITEMS, null,
                values);


        Cursor cursor = database.query(musicStreamDB.TABLE_ITEMS,
                allItems, musicStreamDB.KEY_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        MusicStreamItem newItem = cursorToItem(cursor);

        cursor.close();
        return newItem;
    }

    public List<MusicStreamItem> getAllItem() {
        List<MusicStreamItem> items = new ArrayList<>();

        Cursor cursor = database.query(musicStreamDB.TABLE_ITEMS,
                allItems, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MusicStreamItem item = cursorToItem(cursor);
            Log.i(TAG, "get item = " + cursorToItem(cursor).toString());
            items.add(item);
            cursor.moveToNext();
        }
        Log.d("CHAT_ACTIVITY", "Cursor’s  column count =" + cursor.getColumnCount() );
        // Make sure to close the cursor
        cursor.close();
        return items;
    }

    private MusicStreamItem cursorToItem(Cursor cursor) {
        MusicStreamItem item = new MusicStreamItem();
        item.setId(cursor.getLong(0));
        item.setMusicName(cursor.getString(1));
        return item;
    }

    public void InsertItem(MusicStreamItem Item)
    {
        database.execSQL("INSERT INTO " + dbHelper.TABLE_ITEMS + "("+ dbHelper.KEY_NAME + ","+ dbHelper.KEY_LINK+ ")" +
                " VALUES(\"" + Item.getMusicName() + "\",\"" + defaultLink +"\");");
    }
}
