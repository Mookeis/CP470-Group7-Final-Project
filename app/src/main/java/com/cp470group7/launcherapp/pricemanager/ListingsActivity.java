package com.cp470group7.launcherapp.pricemanager;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.cp470group7.launcherapp.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class ListingsActivity extends AppCompatActivity implements ItemListingFragment.OnListFragmentInteractionListener {

    protected Toolbar listingsToolbar;
    protected static final String ACTIVITY_NAME = "ListingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listings);
        listingsToolbar = findViewById(R.id.listingsToolbar);

        ItemListingFragment itemListingFragment = new ItemListingFragment();
        ArrayList<ItemListing> listings = getIntent().getParcelableArrayListExtra("Listings");
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("Listings", listings);
        itemListingFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.listingsFrame, itemListingFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        setSupportActionBar(listingsToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Item Listings");
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        listingsToolbar.setBackgroundColor(Color.parseColor("#80000000"));
    }

    @Override
    public void onListFragmentInteraction(ItemListing item) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        getMenuInflater().inflate(R.menu.listings_toolbar_menu, m);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        int selected_id = mi.getItemId();
        if(selected_id == R.id.aboutButton){
            Log.d(ACTIVITY_NAME, "About selected");
            Snackbar.make(findViewById(android.R.id.content), "Ver. 1 " + getResources().getString(R.string.amazonPriceManagerAuthorName), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }else if(selected_id == android.R.id.home){
            Log.d(ACTIVITY_NAME, "Back Selected");
            finish();
        }
        return true;
    }
}
