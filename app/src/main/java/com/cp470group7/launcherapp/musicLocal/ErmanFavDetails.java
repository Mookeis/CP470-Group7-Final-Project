package com.cp470group7.launcherapp.musicLocal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cp470group7.launcherapp.R;

public class ErmanFavDetails extends AppCompatActivity {
private TextView title, idview;
private Button delete;
private String venueName;
private long id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_erman_fav_details);

        title = (TextView)findViewById(R.id.ErmanTitleDetailsTextView);
        idview = (TextView)findViewById(R.id.ErmanIDDetailsTextView);
        delete = (Button)findViewById(R.id.ErmandeleteFavButton);
        Bundle args = this.getIntent().getExtras();

        venueName = args.getString("title");
        id = args.getLong("id");

        title.setText(venueName);
        idview.setText("ID: ".concat(Long.toString(id)));

        //return id of current venue to delete it from the favourites list
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent(  );
                resultIntent.putExtra("Response", id);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}
