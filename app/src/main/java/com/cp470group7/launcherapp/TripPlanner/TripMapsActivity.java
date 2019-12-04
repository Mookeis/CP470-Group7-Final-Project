package com.cp470group7.launcherapp.TripPlanner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.cp470group7.launcherapp.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class TripMapsActivity extends AppCompatActivity {

    private RadioButton uniButton;
    private RadioButton museumButton;
    private RadioButton commButton;
    private RadioButton libButton;
    private Toolbar tool;
    private String jsonOverall;
    private ProgressBar pB;
    private Button date;

    private String[] names = new String[4];
    private String[] descriptions = new String[4];
    private String[] coord = new String[4];
    private int[] rating = new int[4];
    private String temp;
    private String[] fav = new String[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_maps);
        LocationQ lq = new LocationQ();
        lq.execute();


        tool = findViewById(R.id.stuartToolBarID);
        tool.setVisibility(View.VISIBLE);
        tool.setTitle(R.string.tripPlannerTitle);
        tool.setTitleTextColor(getResources().getColor(R.color.white));

        setSupportActionBar(tool);
        date = findViewById(R.id.stuartDateSet);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast2 = Toast.makeText(getApplicationContext(),getResources().getString(R.string.stuartTry2),Toast.LENGTH_LONG);
                toast2.show();
            }
        });

        uniButton = findViewById(R.id.uniButton);
        uniButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(TripMapsActivity.this);
                LayoutInflater inflater = TripMapsActivity.this.getLayoutInflater();
                View content = (inflater.inflate(R.layout.stuart_info_dialog, null));
                builder1.setView(content).setPositiveButton(R.string.stuartAdd, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        temp = "Name: " + names[0] + " added to favourites";
                        Snackbar.make(findViewById(R.id.stuartLAY),temp,Snackbar.LENGTH_LONG).setAction("Action",null).show();
                    }
                }).setNegativeButton(R.string.stuartCancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                TextView name = (TextView)content.findViewById(R.id.stuartNameChange);
                name.setText(names[0]);
                TextView desc = (TextView)content.findViewById(R.id.stuartDesChange);
                desc.setText(descriptions[0]);
                TextView coor = (TextView)content.findViewById(R.id.stuartCoordChange);
                coor.setText(coord[0]);
                RatingBar rate = (RatingBar)content.findViewById(R.id.stuartRatingBar);
                rate.setRating(rating[0]);

                AlertDialog dialog1 = builder1.create();
                dialog1.show();
                if (uniButton.isChecked()){
                    uniButton.setChecked(false);
                }else{
                    uniButton.setChecked(true);
                }

            }
        });

        museumButton = findViewById(R.id.musuemButton);
        museumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(TripMapsActivity.this);
                LayoutInflater inflater = TripMapsActivity.this.getLayoutInflater();
                View content = (inflater.inflate(R.layout.stuart_info_dialog, null));
                builder1.setView(content).setPositiveButton(R.string.stuartAdd, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        temp = "Name: " + names[1] + " added to favourites";
                        Snackbar.make(findViewById(R.id.stuartLAY),temp,Snackbar.LENGTH_LONG).setAction("Action",null).show();
                    }
                }).setNegativeButton(R.string.stuartCancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                TextView name = (TextView)content.findViewById(R.id.stuartNameChange);
                name.setText(names[1]);
                TextView desc = (TextView)content.findViewById(R.id.stuartDesChange);
                desc.setText(descriptions[1]);
                TextView coor = (TextView)content.findViewById(R.id.stuartCoordChange);
                coor.setText(coord[1]);
                RatingBar rate = (RatingBar)content.findViewById(R.id.stuartRatingBar);
                rate.setRating(rating[1]);

                AlertDialog dialog1 = builder1.create();
                dialog1.show();
                if (museumButton.isChecked()){
                    museumButton.setChecked(false);
                }else{
                    museumButton.setChecked(true);
                }
            }
        });

        commButton = findViewById(R.id.commButton);
        commButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(TripMapsActivity.this);
                LayoutInflater inflater = TripMapsActivity.this.getLayoutInflater();
                View content = (inflater.inflate(R.layout.stuart_info_dialog, null));
                builder1.setView(content).setPositiveButton(R.string.stuartAdd, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        temp = "Name: " + names[3] + " added to favourites";
                        Snackbar.make(findViewById(R.id.stuartLAY),temp,Snackbar.LENGTH_LONG).setAction("Action",null).show();
                    }
                }).setNegativeButton(R.string.stuartCancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                TextView name = (TextView)content.findViewById(R.id.stuartNameChange);
                name.setText(names[3]);
                TextView desc = (TextView)content.findViewById(R.id.stuartDesChange);
                desc.setText(descriptions[3]);
                TextView coor = (TextView)content.findViewById(R.id.stuartCoordChange);
                coor.setText(coord[3]);
                RatingBar rate = (RatingBar)content.findViewById(R.id.stuartRatingBar);
                rate.setRating(rating[3]);

                AlertDialog dialog1 = builder1.create();
                dialog1.show();
                if (commButton.isChecked()){
                    commButton.setChecked(false);
                }else{
                    commButton.setChecked(true);
                }
            }
        });

        libButton = findViewById(R.id.libButton);
        libButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(TripMapsActivity.this);
                LayoutInflater inflater = TripMapsActivity.this.getLayoutInflater();
                View content = (inflater.inflate(R.layout.stuart_info_dialog, null));
                builder1.setView(content).setPositiveButton(R.string.stuartAdd, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        temp = "Name: " + names[2] + " added to favourites";
                        Snackbar.make(findViewById(R.id.stuartLAY),temp,Snackbar.LENGTH_LONG).setAction("Action",null).show();
                    }
                }).setNegativeButton(R.string.stuartCancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                TextView name = (TextView)content.findViewById(R.id.stuartNameChange);
                name.setText(names[2]);
                TextView desc = (TextView)content.findViewById(R.id.stuartDesChange);
                desc.setText(descriptions[2]);
                TextView coor = (TextView)content.findViewById(R.id.stuartCoordChange);
                coor.setText(coord[2]);
                RatingBar rate = (RatingBar)content.findViewById(R.id.stuartRatingBar);
                rate.setRating(rating[2]);

                AlertDialog dialog1 = builder1.create();
                dialog1.show();
                if (libButton.isChecked()){
                    libButton.setChecked(false);
                }else{
                    libButton.setChecked(true);
                }
            }
        });
    }

    public boolean onCreateOptionsMenu (Menu m){
        getMenuInflater().inflate(R.menu.stuart_toolbar_menu, m );
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem mi) {
        CharSequence text;
        int idMi = mi.getItemId();

        switch(idMi){
            case R.id.stuart_action_two:
                Toast toast = Toast.makeText(getApplicationContext(),getResources().getString(R.string.stuartToast),Toast.LENGTH_LONG);
                toast.show();
            break;
            case R.id.stuart_action_one:
                Toast toast1 = Toast.makeText(getApplicationContext(),getResources().getString(R.string.stuartTry),Toast.LENGTH_LONG);
                toast1.show();
            break;
            case R.id.stuart_settings:
                //show custom dialog to help user use the application
                AlertDialog.Builder builder1 = new AlertDialog.Builder(TripMapsActivity.this);
                LayoutInflater inflater = TripMapsActivity.this.getLayoutInflater();
                builder1.setView(inflater.inflate(R.layout.stuart_dialog, null)).setPositiveButton(R.string.stuartReturn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       //closes the dialog box
                    }
                });
                AlertDialog dialog1 = builder1.create();
                dialog1.show();
        }
    return true;
    }
    private class LocationQ extends AsyncTask<String,Integer,String>{
        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL("https://api.myjson.com/bins/ldcsk");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                InputStream in = conn.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                StringBuffer buffer = new StringBuffer();
                String l = "";
                int j = 1;
                while ((l = br.readLine()) != null) {
                    publishProgress(j*10);
                    j++;
                    buffer.append(l + "\n");
                }
                in.close();
                jsonOverall = buffer.toString();
                Log.i("memes",jsonOverall);
                try{
                    JSONObject json = new JSONObject(jsonOverall);
                    JSONArray jsonA = json.getJSONArray("Locations");
                    //get name of locations
                    for(int i = 0; i < 4; i++){
                        names[i] = jsonA.getString(i);
                    }
                    if(names[0] != null){
                        Log.i("MEMES",names[0]);
                    }else{
                        Log.i("MEMES","hey");
                    }
                    //get description
                    jsonA = json.getJSONArray("Description");
                    for(int i = 0; i < 4; i++){
                        descriptions[i] = jsonA.getString(i);
                    }
                    //get coord
                    jsonA = json.getJSONArray("Coordinates");
                    for(int i = 0; i < 4; i++){
                        coord[i] = jsonA.getString(i);
                    }
                    //get rating
                    jsonA = json.getJSONArray("Rating");
                    for(int i = 0; i < 4; i++){
                        rating[i] = jsonA.getInt(i);
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
            return "";
        }
    }


}
