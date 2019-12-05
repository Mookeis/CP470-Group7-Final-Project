package com.cp470group7.launcherapp.MusicStreamerBast9100;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cp470group7.launcherapp.R;

import java.util.ArrayList;
import java.util.List;

public class bast9100 extends AppCompatActivity {

    MusicAdapter musicAdapter;
    public ArrayList<String> MusicItems;
    MusicDBItemHelper database;
    ListView listView;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bast9100);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        musicAdapter = new MusicAdapter( this );
        MusicItems = new ArrayList<String>();

        database = new MusicDBItemHelper(this);
        database.open();

        listView = findViewById(R.id.ListOfItems);

        listView.setAdapter(musicAdapter);

        UpdateDB();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getBaseContext(),"Streaming Not Yet Implemented",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        database.open();

        super.onResume();
    }

    @Override
    protected void onStart() {
        database.open();

        super.onStart();
    }

    @Override
    protected void onPause() {
        database.close();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        database.close();
        super.onDestroy();
    }

    public boolean onCreateOptionsMenu(Menu m)
    {
        getMenuInflater().inflate(R.menu.bast9100_content_layout,m);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem mi)
    {
        int selection = mi.getItemId();
        switch (selection)
        {
            case R.id.action_add:
                intent = new Intent(bast9100.this, AddMusicStream.class);
                startActivity(intent);
                break;
            case R.id.action_delete:
                intent = new Intent(bast9100.this, DeleteMusicItem.class);
                startActivity(intent);
                break;
            case R.id.action_about:
                AlertDialog.Builder builder = new AlertDialog.Builder(bast9100.this);
                // 2. Chain together various setter methods to set the dialog characteristics
                builder.setMessage(R.string.bastdialog_message) //Add a dialog message to strings.xml

                        .setTitle(R.string.bastdialog_title)
                        .setNegativeButton(R.string.bastclose, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        })
                        .show();
                    break;
        }
        return true;
    }

    private class MusicAdapter extends ArrayAdapter<String>
    {
        public MusicAdapter(Context ctx) {
            super(ctx, 0);
        }
        @Override
        public int getCount()
        {
            return MusicItems.size();
        }
        @Override
        public String getItem(int position)
        {
            return MusicItems.get(position);
        }
        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater inflater = bast9100.this.getLayoutInflater();
            View result = null ;
            result = inflater.inflate(R.layout.music_item_layout, null);

            TextView musicItem = (TextView)result.findViewById(R.id.musicItem);
            musicItem.setText(   getItem(position)  ); // get the string at position
            return result;
        }

    }
    private void UpdateDB()
    {
        List<MusicStreamItem> dbItems = database.getAllItem();

        MusicStreamItem tmp;

        for(int i = 0; i < dbItems.size();i++)
        {
            tmp = dbItems.get(i);
            MusicItems.add(tmp.toString());
        }
    }

}
