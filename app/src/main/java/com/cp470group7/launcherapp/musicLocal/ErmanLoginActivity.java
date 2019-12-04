package com.cp470group7.launcherapp.musicLocal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cp470group7.launcherapp.R;

public class ErmanLoginActivity extends AppCompatActivity {
    Button loginButton;
    EditText emailEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_erman_login);
        emailEditText = (EditText)findViewById(R.id.ErmanloginEmailEditText);

        //save email so it will show up again after the app is reponed
        SharedPreferences getSP = getSharedPreferences("DefaultEmail", Context.MODE_PRIVATE);
        String defaultEmail = getResources().getString(R.string.ErmanDefaultEmailText);
        String emailText = getSP.getString(String.valueOf(R.string.ErmanSavedEmailText),defaultEmail);
        emailEditText.setText(emailText);

        loginButton = (Button)findViewById(R.id.ErmanloginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailText = emailEditText.getText().toString();
                SharedPreferences setSP = getSharedPreferences("DefaultEmail",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = setSP.edit();
                editor.putString(String.valueOf(R.string.ErmanSavedEmailText),emailText);
                editor.commit();

                Intent intent = new Intent(ErmanLoginActivity.this,ErmanMapsActivity.class);
                startActivity(intent);
            }
        });
    }
}

