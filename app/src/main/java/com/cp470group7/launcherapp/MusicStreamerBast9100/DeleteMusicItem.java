package com.cp470group7.launcherapp.MusicStreamerBast9100;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.cp470group7.launcherapp.R;

public class DeleteMusicItem extends AppCompatActivity {

    MusicDBItemHelper database;
    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_music_item);
        spinner = findViewById(R.id.DeleteSpinner);

        database = new MusicDBItemHelper(this);
        database.open();

    }

    public void deleteEntry(View view){
        Toast.makeText(getBaseContext(),"Deleting Not Yet Implemented",Toast.LENGTH_SHORT).show();
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
