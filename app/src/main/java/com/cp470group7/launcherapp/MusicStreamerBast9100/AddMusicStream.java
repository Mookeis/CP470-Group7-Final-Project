package com.cp470group7.launcherapp.MusicStreamerBast9100;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.cp470group7.launcherapp.R;

public class AddMusicStream extends AppCompatActivity {

    Spinner spinner;
    MusicDBItemHelper database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_music_stream);

        spinner = findViewById(R.id.AddSpinner);

        database = new MusicDBItemHelper(this);
        database.open();
    }


    public void addEntry(View view){
        String MusicName = (String)spinner.getSelectedItem();
        MusicStreamItem item = new MusicStreamItem(MusicName);
        database.InsertItem(item);
    }

    @Override
    protected void onResume() {
        database.open();
        super.onResume();
    }

    @Override
    protected void onStart() {
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
}
