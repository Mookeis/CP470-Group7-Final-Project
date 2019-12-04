package com.cp470group7.launcherapp.musicLocal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cp470group7.launcherapp.R;

import org.w3c.dom.Text;

public class ErmanMarkerActivity extends AppCompatActivity {
private TextView title,descript,event;
private Button favButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_erman_marker);

        title = (TextView)(findViewById(R.id.ErmanMarkerTextView1));
        descript = (TextView)(findViewById(R.id.ErmanMarkerTextView2));
        event = (TextView)(findViewById(R.id.ErmanMarkerTextView4));
        favButton = (Button)findViewById(R.id.ErmanFavButton);

        //Set the title, description and events from the sent text through intent
        Bundle bundle = getIntent().getExtras();
        String[] sent = bundle.getStringArray("Response");
        title.setText(sent[0]);
        descript.setText(sent[1]);
        event.setText(sent[2]);

        //If favourites button is pressed, return True
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.ErmanAddedToFavText), Toast.LENGTH_LONG);
                toast.show();
                Intent resultIntent = new Intent(  );
                resultIntent.putExtra("Response", "True");
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

    }


}
