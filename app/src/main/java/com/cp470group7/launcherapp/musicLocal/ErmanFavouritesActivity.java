package com.cp470group7.launcherapp.musicLocal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cp470group7.launcherapp.R;

import java.util.ArrayList;

public class ErmanFavouritesActivity extends AppCompatActivity {
    private Cursor cursor;
    private SQLiteDatabase database;
    private ErmanFavDatabaseHelper dbH;
    private ListView FavList;
    private ArrayList<String> favArray;
    private FavAdapter messageAdapter;
    private String[] allItems = { ErmanFavDatabaseHelper.KEY_ID,
            ErmanFavDatabaseHelper.KEY_MESSAGE };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_erman_favourites);

        FavList = (ListView)findViewById(R.id.ErmanFavListView);
        dbH = new ErmanFavDatabaseHelper(this);
        database = dbH.getWritableDatabase();
        cursor = database.query(dbH.TABLE_NAME,allItems, null, null, null, null, null);
        FavList = (ListView)findViewById(R.id.ErmanFavListView);
        favArray = new ArrayList<>();
        messageAdapter =new FavAdapter( this );
        FavList.setAdapter(messageAdapter);

        //add elements in database to array
        cursor.moveToFirst();
        while(!cursor.isAfterLast() ) {
            favArray.add(cursor.getString(cursor.getColumnIndex(ErmanFavDatabaseHelper.KEY_MESSAGE)));
            cursor.moveToNext();
        }
        cursor.close();
        FavList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            //when an element is clicked, open details activity while sending the venue name and id there
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String msg = messageAdapter.getItem(i);
                long id = messageAdapter.getItemId(i);
                Bundle args = new Bundle();
                args.putString("title",msg);
                args.putLong("id",id);


                Intent intent = new Intent(ErmanFavouritesActivity.this,ErmanFavDetails.class);
                intent.putExtras(args);
                startActivityForResult(intent,10);

            }
        });

    }

    //If delete favourite was pressed on details activity, remove element with the returned id
    @SuppressLint("MissingSuperCall")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10){
            if (resultCode == RESULT_OK) {
                long id2 = 0;
                long id =  data.getLongExtra("Response",id2);
                database.delete(ErmanFavDatabaseHelper.TABLE_NAME,ErmanFavDatabaseHelper.KEY_ID + "=" + id,null);
                finish();
                startActivity(getIntent());
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbH.close();
    }

    private class FavAdapter extends ArrayAdapter<String> {
        public FavAdapter(Context ctx) {
            super(ctx, 0);
        }

        public int getCount(){
            return favArray.size();
        }

        public String getItem(int position){
            return favArray.get(position);
        }

        public long getItemId(int position){
            Cursor temp = database.query(dbH.TABLE_NAME,allItems, null, null, null, null, null);
            temp.moveToPosition(position);
            return temp.getLong(temp.getColumnIndex(ErmanFavDatabaseHelper.KEY_ID));
        }

        public View getView(int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = ErmanFavouritesActivity.this.getLayoutInflater();
            View result = null ;
            result = inflater.inflate(R.layout.erman_favourites, null);
            TextView message = (TextView)result.findViewById(R.id.ErmanFavouritesTitle);
            message.setText(getItem(position));
            return result;

        }
    }
}
