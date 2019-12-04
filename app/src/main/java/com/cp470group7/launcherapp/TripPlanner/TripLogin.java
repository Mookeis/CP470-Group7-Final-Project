package com.cp470group7.launcherapp.TripPlanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cp470group7.launcherapp.R;

public class TripLogin extends AppCompatActivity {
    EditText loginEdit;
    Button loginButton;
    SharedPreferences sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_login);

        loginEdit = findViewById(R.id.tripLoginEdit);
        loginButton = findViewById(R.id.tripLoginButton);
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        String defEmail = getResources().getString(R.string.defEmail);
        String email = sharedPref.getString(String.valueOf(R.string.SavedEmail),defEmail);
        loginEdit.setText(email);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailTXT = loginEdit.getText().toString();
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(String.valueOf(R.string.SavedEmail),emailTXT);
                editor.commit();

                Intent intent = new Intent(TripLogin.this,TripMapsActivity.class);
                startActivity(intent);
            }
        });
    }
}
