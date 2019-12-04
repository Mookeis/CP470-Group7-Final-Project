package com.cp470group7.launcherapp.musicLocal;



import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
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
import com.google.android.gms.maps.model.MarkerOptions;

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


    }

    public boolean onCreateOptionsMenu(Menu m){
        getMenuInflater().inflate(R.menu.erman_toolbar_menu, m );
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem mi){
        switch (mi.getItemId()){
            case R.id.Erman_action_one:
                Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.ErmantempText), Toast.LENGTH_LONG);
                toast.show();
                break;
            case R.id.Erman_settings:
                AlertDialog.Builder builder = new AlertDialog.Builder(ErmanMapsActivity.this);
                LayoutInflater inflater = ErmanMapsActivity.this.getLayoutInflater();
                View view = inflater.inflate(R.layout.erman_custom_dialog, null);
                builder.setView(view);
                // Add positive button (which doesn't do anything but get rid of the dialog box)
                builder.setPositiveButton(R.string.Ermanok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
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

        @Override
        protected void onProgressUpdate(Integer... values){
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String a){
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






    }
}
