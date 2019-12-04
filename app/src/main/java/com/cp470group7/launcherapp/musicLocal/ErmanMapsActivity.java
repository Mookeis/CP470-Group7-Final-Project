package com.cp470group7.launcherapp.musicLocal;



import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cp470group7.launcherapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class ErmanMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_INT = 150;
    private GoogleMap mMap;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private String json;
    private String[] venues = new String[4];
    private double[][] coordinates = new double[4][2];
    private String[] description = new String[4];
    private String[] events = new String[4];
    private String checkFav = "False";
    private SQLiteDatabase database;
    private ErmanFavDatabaseHelper dbH;
    private String[] allItems = { ErmanFavDatabaseHelper.KEY_ID,
            ErmanFavDatabaseHelper.KEY_MESSAGE };
    private Cursor cursor;
    private ArrayList<String> array;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_erman_maps);
        toolbar = findViewById(R.id.ErmantoolbarID);
        toolbar.setVisibility(View.INVISIBLE);
        toolbar.setTitle("musicLocal");
        toolbar.setTitleTextColor(Color.WHITE);
        progressBar = (ProgressBar)findViewById(R.id.ErmanprogressBar);
        progressBar.setVisibility(View.VISIBLE);
        VenueQuery vq = new VenueQuery();
        vq.execute();

        setSupportActionBar(toolbar);
        dbH = new ErmanFavDatabaseHelper(this);
        database = dbH.getWritableDatabase();
        array = new ArrayList<>();
        cursor = database.query(dbH.TABLE_NAME,allItems, null, null, null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast() ) {
            array.add(cursor.getString(cursor.getColumnIndex(ErmanFavDatabaseHelper.KEY_MESSAGE)));
            cursor.moveToNext();
        }
        cursor.close();
    }

    public boolean onCreateOptionsMenu(Menu m){
        getMenuInflater().inflate(R.menu.erman_toolbar_menu, m );
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem mi){
        switch (mi.getItemId()){
            case R.id.Erman_action_one:
                Intent intent = new Intent(ErmanMapsActivity.this,ErmanFavouritesActivity.class);
                startActivity(intent);
                break;
            case R.id.Erman_settings:
                //Build custom dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(ErmanMapsActivity.this);
                LayoutInflater inflater = ErmanMapsActivity.this.getLayoutInflater();
                View view = inflater.inflate(R.layout.erman_custom_dialog, null);
                builder.setView(view);
                // Add positive button (which doesn't do anything but get rid of the dialog box)
                builder.setPositiveButton(R.string.Ermanok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Snackbar.make(findViewById(android.R.id.content).getRootView(), getResources().getString(R.string.ErmanThankYouText), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
                // Create the AlertDialog
                AlertDialog dialog2 = builder.create();
                dialog2.show();
                break;
        }
        return true;
    }

    private class VenueQuery extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                //I wasn't able to get the app to find the venues close to the user's location
                //So, I made a preset json object at the link below and the app parses the json to get some venues
                URL url = new URL("https://api.myjson.com/bins/16p9us");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                InputStream in = conn.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                StringBuffer buffer = new StringBuffer();
                String line = "";
                int count = 0;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    count = count + 10;
                    publishProgress(count);
                }
                publishProgress(100);
                in.close();
                json = buffer.toString();

                try{
                    JSONObject jsonO = new JSONObject(json);
                    //Parse json get venue names
                    JSONArray array = jsonO.getJSONArray("venues");
                    for(int i = 0; i < 4; i++){
                        venues[i] = array.getString(i);
                    }
                    //Parse json get coordinates
                    array = jsonO.getJSONArray("coordinates");
                    int index = 0;
                    for(int i = 0; i < 4; i++){
                        for(int j = 0; j < 2; j++){
                            coordinates[i][j] = array.getDouble(index);
                            index = index + 1;
                        }

                    }
                    //Parse json get venue descriptions
                    array = jsonO.getJSONArray("description");
                    for(int i = 0; i < 4; i++){
                        description[i] = array.getString(i);
                    }

                    //Parse json get venue events
                    array = jsonO.getJSONArray("Events");
                    for(int i = 0; i < 4; i++){
                        events[i] = array.getString(i);
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }

        //update progress bar
        @Override
        protected void onProgressUpdate(Integer... values){
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String a){
            //get rid of progress bar and put in the tool bar
            progressBar.setVisibility(View.INVISIBLE);
            toolbar.setVisibility(View.VISIBLE);
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.Ermanmap);
            mapFragment.getMapAsync(ErmanMapsActivity.this);
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            //Attempt to create custom maps style
            //Maps style made using Google Maps styling Wizard: (https://mapstyle.withgoogle.com/)
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.erman_map_style));
            if (!success) {
                Log.e("ErmanMapsActivity", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("ErmanMapsActivity", "Can't find style. Error: ", e);
        }

        mMap = googleMap;
        //Set current location:
        //Get permission to access device location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&  ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_INT);
            }

        }
        //Permission Granted
        else{
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

        //Enable map zoom controls
        mMap.getUiSettings().setZoomControlsEnabled(true);
        //Add markers for the venues onto the map

        for (int i = 0; i < venues.length; i++) {
            //get coordinates
            LatLng lat = new LatLng(coordinates[i][0], coordinates[i][1]);
            //make marker set title to venue name
            MarkerOptions marker = new MarkerOptions().position(lat).title(venues[i]);
            //change colour of marker to violet
            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
            mMap.addMarker(marker);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(lat));
        }


        //on click listener for when the user clicks on the title of the marker
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                String venueName = marker.getTitle();
                String event = "";
                String descript = "";
                int index = 0;
                //Get the index of the name of the venue in the array, which is used to get the events and description
                for(int i = 0; i < venues.length; i++){
                    if(venues[i].equals(venueName)){
                        index = i;
                        break;
                    }
                }

                event = events[index];
                descript = description[index];
                String[] send = new String[3];
                send[0] = venueName;
                send[1] = descript;
                send[2] = event;


                Intent intent = new Intent(ErmanMapsActivity.this,ErmanMarkerActivity.class);
                intent.putExtra("Response", send);
                startActivityForResult(intent,10);
                //if marker activity returns true, add the current venue to the database
                if(checkFav.equals("True")){
                    ContentValues values = new ContentValues();
                    values.put(ErmanFavDatabaseHelper.KEY_MESSAGE,venueName);
                    long insertId = database.insert(ErmanFavDatabaseHelper.TABLE_NAME, null,
                            values);
                    Cursor cursor = database.query(ErmanFavDatabaseHelper.TABLE_NAME,
                            allItems, ErmanFavDatabaseHelper.KEY_ID + " = " + insertId, null,
                            null, null, null);
                    cursor.moveToFirst();
                    cursor.close();
                    array.add(venueName);
                }

                checkFav = "False";

            }
        });






    }
    //get the response message from marker activity to determine whether to add current venue to favourites
    @SuppressLint("MissingSuperCall")
    protected void onActivityResult(int requestCode, int responseCode, Intent data) {
        if (requestCode == 10){
            if(responseCode == RESULT_OK){
                String messagePassed = data.getStringExtra("Response");
                checkFav = messagePassed;
            }

        }
    }


}
