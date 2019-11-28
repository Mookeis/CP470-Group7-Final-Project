package com.cp470group7.launcherapp.pricemanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.cp470group7.launcherapp.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import toan.android.floatingactionmenu.FloatingActionButton;
import toan.android.floatingactionmenu.FloatingActionsMenu;

import static com.cp470group7.launcherapp.pricemanager.AmazonItem.debugAmazonItem;
import static com.cp470group7.launcherapp.pricemanager.AmazonItem.getASIN;


@SuppressWarnings("deprecation")
public class ItemSearchActivity extends AppCompatActivity implements AmazonItemFragment.OnListFragmentInteractionListener {

    protected EditText searchBar;
    protected Button searchButton;
    protected Toolbar mainToolbar;
    protected ProgressBar searchProgressBar;
    protected static int SEARCH_RESULT_CODE = 15;
    protected static String ACTIVITY_NAME = "ItemSearchActivity";
    protected static String API_KEY = "y5Cs6io2mLZNvoHFT63RwpItTm3jTTZL";
    protected SQLiteDatabase watchlistDb;
    protected FrameLayout watchlistFrame;
    protected Intent changeActivityIntent;
    protected Bundle bundle;
    protected String deviceMode;
    protected String itemASIN;
    public static DisplayMetrics displayMetrics;
    public static float dpHeight;
    public static float dpWidth;

    protected FloatingActionsMenu menu;
    protected FloatingActionButton watchlistButton;
    protected TextView watchlistTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_search);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        displayMetrics = this.getResources().getDisplayMetrics();

        dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        watchlistTitle = findViewById(R.id.watchlistTitle);
        watchlistFrame = findViewById(R.id.watchListFrame);
        searchBar = findViewById(R.id.linkSearchBar);
        searchButton = findViewById(R.id.linkSearchButton);
        searchProgressBar = findViewById(R.id.searchProgressBar);
        mainToolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Amazon Price Manager");
        }
        mainToolbar.setBackgroundColor(Color.parseColor("#80000000"));

        deviceMode = ItemResultsActivity.getDeviceMode(watchlistFrame);

        if(deviceMode.equals("tablet")) {
            inflateWatchlistFrame();
        }else{
            watchlistButton = findViewById(R.id.watchlistFab);
            menu = findViewById(R.id.fab_menu);
            menu.bringToFront();
            watchlistButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    inflateWatchlistFrame();
                }
            });
        }

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                String searchText = searchBar.getText().toString();
                if (validateInputURL(searchText)) {
                    itemASIN = getASIN(Uri.parse(searchText));
                    final String urlString = "https://get.scrapehero.com/amz/product-details/?asin=" + itemASIN + "&x-api-key=" + API_KEY;
                    URL url = null;
                    try {
                        url = new URL(urlString);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    searchProgressBar.setVisibility(View.VISIBLE);
                    WatchlistDatabase initWatchlist = new WatchlistDatabase(getApplicationContext());
                    watchlistDb = initWatchlist.getWritableDatabase();
                    String getAmazonItemSQL = "SELECT * FROM " + WatchlistDatabase.TABLE_NAME + " WHERE " + WatchlistDatabase.ASIN_NAME + "='" + itemASIN + "'";
                    try {
                        Cursor c = watchlistDb.rawQuery(getAmazonItemSQL, new String[]{});
                        if (!(c.moveToFirst()) || c.getCount() == 0) {
                            Log.i(ACTIVITY_NAME, "Request item from internet");
                            ScrapeRequest sr = new ScrapeRequest(url);
                            sr.execute();

                        } else {
                            Log.i(ACTIVITY_NAME, "Found item in watchlist database");
                            changeActivityIntent = new Intent(ItemSearchActivity.this, ItemResultsActivity.class);
                            bundle = new Bundle();
                            Gson gson = new Gson();
                            AmazonItem searchItem = gson.fromJson(c.getString(c.getColumnIndex(WatchlistDatabase.AMAZONITEM_NAME)), AmazonItem.class);
                            bundle.putParcelable("Item", searchItem);
                            if (deviceMode.equals("tablet")) {
                                searchProgressBar.setVisibility(View.VISIBLE);
                                SellerRequest sellerRequest = new SellerRequest(AmazonItem.generateListingPageLink(itemASIN));
                                sellerRequest.execute();
                            } else {
                                changeActivityIntent.putExtras(bundle);
                                startActivityForResult(changeActivityIntent, SEARCH_RESULT_CODE);
                            }
                        }
                        c.close();
                    } catch (SQLiteException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast errorToast = Toast.makeText(getApplicationContext(), "Invalid URL; please try again", Toast.LENGTH_LONG);
                    errorToast.show();
                }
            }
        });
    }

    /**
     * Method to inflate watchlist from watchlistDB if device is a tablet and there is items in the database
     * if device is phone then change activity to watchlist
     */
    private void inflateWatchlistFrame() {
        WatchlistDatabase initWatchlist = new WatchlistDatabase(getApplicationContext());
        watchlistDb = initWatchlist.getWritableDatabase();
        String getAmazonItemSQL = "SELECT * FROM " + WatchlistDatabase.TABLE_NAME;
        try {
            Cursor c = watchlistDb.rawQuery(getAmazonItemSQL, new String[]{});
            if (!(c.moveToFirst()) || c.getCount() == 0) {
                Log.d(ACTIVITY_NAME, "No items in watchlist; leaving frame empty");
                if(deviceMode.equals("tablet")) {
                    watchlistTitle.setVisibility(View.INVISIBLE);
                }
            } else {
                Log.i(ACTIVITY_NAME, "Items found in watchlist; inflating frame");
                Bundle bundle = new Bundle();
                Gson gson = new Gson();
                ArrayList<AmazonItem> watchlist = new ArrayList<>();
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    AmazonItem watchItem = gson.fromJson(c.getString(c.getColumnIndex(WatchlistDatabase.AMAZONITEM_NAME)), AmazonItem.class);
                    watchlist.add(watchItem);
                    c.moveToNext();
                }
                c.close();
                bundle.putParcelableArrayList("Items", watchlist);
                if(deviceMode.equals("tablet")) {
                    watchlistTitle.setVisibility(View.VISIBLE);
                    AmazonItemFragment amazonItemFragment = new AmazonItemFragment();
                    amazonItemFragment.setArguments(bundle);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.watchListFrame, amazonItemFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }else{
                    Intent changeActivityIntent = new Intent(ItemSearchActivity.this, WatchlistActivity.class);
                    changeActivityIntent.putExtras(bundle);
                    startActivityForResult(changeActivityIntent, 10);
                }

            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onListFragmentInteraction(AmazonItem item) {

    }

    public class ScrapeRequest extends AsyncTask<String, Integer, String> {

        private URL itemURL;
        private boolean error = false;
        private AmazonItem searchItem;

        /**
         * @param url item URL
         * @see ItemResultsActivity.ScrapeRequest
         */
        private ScrapeRequest(URL url) {
            itemURL = url;
        }

        @Override
        protected void onPostExecute(String a) {
            searchProgressBar.setVisibility(View.INVISIBLE);
            if (error) {
                String errorMessage = "Invalid URL; please try again";
                Toast errorToast = Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG);
                errorToast.show();
            } else {
                changeActivityIntent = new Intent(ItemSearchActivity.this, ItemResultsActivity.class);
                bundle = new Bundle();
                bundle.putParcelable("Item", searchItem);
                if (deviceMode.equals("tablet")) {
                    searchProgressBar.setVisibility(View.VISIBLE);
                    SellerRequest sellerRequest = new SellerRequest(AmazonItem.generateListingPageLink(itemASIN));
                    sellerRequest.execute();
                } else {
                    changeActivityIntent.putExtras(bundle);
                    startActivityForResult(changeActivityIntent, SEARCH_RESULT_CODE);
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            searchProgressBar.setProgress(values[0]);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                HttpURLConnection conn = (HttpURLConnection) itemURL.openConnection();
                publishProgress(25);
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                publishProgress(50);
                InputStream is = conn.getInputStream();
                publishProgress(75);
                String response = convertStreamToString(is);
                if (response.contains("\"error\":")) {
                    Pattern p = Pattern.compile("\"([^\"]*)\"");
                    Matcher m = p.matcher(response);
                    while (m.find()) {
                        if (error) {
                            break;
                        }
                        if (m.group().equals("\"error\"")) {
                            error = true;
                        }

                    }
                }
                try {
                    JSONObject itemJSON = new JSONObject(response);
                    JSONArray imageJSON = itemJSON.getJSONArray("images");
                    String[] images = new String[imageJSON.length()];
                    for (int i = 0; i < imageJSON.length(); i++) {
                        images[i] = imageJSON.getString(i);
                    }

                    String itemName = itemJSON.getString("name");
                    String itemDescription = itemJSON.getString("small_description");
                    String availability = itemJSON.getString("availability_status");
                    URL itemURL = new URL(itemJSON.getString("url"));
                    String itemBrand = itemJSON.getString("brand");
                    int itemTotalReviews = Integer.parseInt(itemJSON.getString("total_reviews").replaceAll(",", ""));
                    String itemCategory = itemJSON.getString("productCategory");
                    JSONObject productInformation = itemJSON.getJSONObject("product_information");
                    String itemASIN = productInformation.getString("ASIN");
                    String itemPrice = itemJSON.getString("price");
                    String itemShipping = itemJSON.getString("shipping_charge");
                    float itemAvgRating = Float.parseFloat(itemJSON.getString("average_rating"));
                    searchItem = new AmazonItem(itemName, itemDescription, availability, images, itemURL, itemBrand, itemTotalReviews, itemCategory, itemASIN, itemPrice, itemShipping, itemAvgRating);
                    debugAmazonItem(searchItem);
                } catch (JSONException err) {
                    Log.d(ACTIVITY_NAME, err.toString());
                }
                is.close();
                publishProgress(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class SellerRequest extends AsyncTask<String, Integer, String> {

        protected URL listingsURL;
        protected ArrayList<ItemListing> listings = new ArrayList<>();
        protected boolean error = false;

        /**
         * @param listingsURL listing URL
         * @see ItemResultsActivity.SellerRequest
         */
        public SellerRequest(URL listingsURL) {
            this.listingsURL = listingsURL;
        }

        @Override
        protected void onPostExecute(String a) {
            searchProgressBar.setVisibility(View.INVISIBLE);
            if (error) {
                String errorMessage = "Request to server failed; please try again";
                createCustomBackgroundToast(errorMessage, Color.DKGRAY, Color.WHITE);
            } else {
                bundle.putParcelableArrayList("Listings", listings);
                changeActivityIntent.putExtras(bundle);
                recreate();
                startActivityForResult(changeActivityIntent, SEARCH_RESULT_CODE);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            searchProgressBar.setProgress(values[0]);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL diffURL = new URL("https://api.diffbot.com/v3/article?token=" + ItemResultsActivity.DEVELOPER_TOKEN + "&url=" + listingsURL);
                HttpURLConnection conn = (HttpURLConnection) diffURL.openConnection();
                publishProgress(25);
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                String response = convertStreamToString(is);
                Log.i(ACTIVITY_NAME, response);
                publishProgress(50);

                JSONObject listingJSON = new JSONObject(response);
                JSONArray objectArray = listingJSON.getJSONArray("objects");
                JSONObject[] listingDetails = new JSONObject[objectArray.length()];
                for (int i = 0; i < objectArray.length(); ++i) {
                    JSONObject json = objectArray.getJSONObject(i);
                    listingDetails[i] = json;
                }
                ArrayList<JSONObject> conditions = new ArrayList<>();
                ArrayList<JSONObject> sellers = new ArrayList<>();
                ArrayList<JSONObject> prices = new ArrayList<>();
                ArrayList<String> conditionStrings = new ArrayList<>();
                ArrayList<String> sellerStrings = new ArrayList<>();
                ArrayList<String> priceStrings = new ArrayList<>();
                for (JSONObject json : listingDetails) {
                    Iterator<String> keys = json.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        if (key.equals("Condition Info")) {
                            JSONArray conditionArray = json.getJSONArray("Condition Info");
                            for (int i = 0; i < conditionArray.length(); ++i) {
                                conditions.add(conditionArray.getJSONObject(i));
                            }
                        } else if (key.equals("Seller Info")) {
                            JSONArray sellerArray = json.getJSONArray("Seller Info");
                            for (int i = 0; i < sellerArray.length(); ++i) {
                                sellers.add(sellerArray.getJSONObject(i));
                            }
                        } else if (key.equals("Price Info")) {
                            JSONArray priceArray = json.getJSONArray("Price Info");
                            for (int i = 0; i < priceArray.length(); ++i) {
                                prices.add(priceArray.getJSONObject(i));
                            }
                        }
                    }
                }
                publishProgress(75);

                for (JSONObject condition : conditions) {
                    Log.i(ACTIVITY_NAME, condition.getString("Condition"));
                    conditionStrings.add(condition.getString("Condition"));
                }
                for (JSONObject seller : sellers) {
                    Log.i(ACTIVITY_NAME, seller.getString("Seller"));
                    if (seller.getString("Seller").isEmpty()) {
                        sellerStrings.add("Amazon");
                    } else {
                        sellerStrings.add(seller.getString("Seller"));
                    }
                }
                for (JSONObject price : prices) {
                    Log.i(ACTIVITY_NAME, price.getString("Price"));
                    priceStrings.add(price.getString("Price"));
                }

                if (priceStrings.size() == sellerStrings.size() && sellerStrings.size() == conditionStrings.size()) {
                    Log.i(ACTIVITY_NAME, "Listing Info arrays same size");
                    for (int i = 0; i < priceStrings.size(); ++i) {
                        listings.add(new ItemListing(sellerStrings.get(i), priceStrings.get(i), conditionStrings.get(i)));
                    }
                } else {
                    error = true;
                }
                publishProgress(100);
            } catch (Exception e) {
                Log.i(ACTIVITY_NAME, "Server Request Error");
                error = true;

            }
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        getMenuInflater().inflate(R.menu.item_search_toolbar_menu, m);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        int selected_id = mi.getItemId();
        if(selected_id == R.id.aboutButton){
            Log.d("MainToolbar", "About selected");
            Snackbar.make(findViewById(android.R.id.content), "Ver. 1 " + getResources().getString(R.string.amazonPriceManagerAuthorName), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }else if(selected_id == R.id.infoButton){
            Log.d("MainToolbar", "Info selected");
            AlertDialog.Builder builder = new AlertDialog.Builder(ItemSearchActivity.this)
                    .setTitle("Information")
                    .setMessage(getResources().getString(R.string.helpText))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            final AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        return true;
    }

    /**
     * Hides keyboard if on display
     */
    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            if (getCurrentFocus() != null) {
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * Converts an input stream to a string
     *
     * @param is input stream
     * @return inputstream in string format
     */
    public static String convertStreamToString(InputStream is) {
        try {
            return new java.util.Scanner(is).useDelimiter("\\A").next();
        } catch (java.util.NoSuchElementException e) {
            return "";
        }
    }

    /**
     * Validates whether URL is a valid URL
     *
     * @param url input URL string
     * @return true/false
     */
    public static boolean validateInputURL(String url) {
        boolean isValid = true;
        try {
            URL u = new URL(url);
        } catch (MalformedURLException e) {
            isValid = false;
        }
        return isValid;
    }

    /**
     * Generates and displays a custom toast message
     *
     * @param message         toast message
     * @param backgroundColor toast background colour
     * @param textColor       toast text colour
     */
    private void createCustomBackgroundToast(String message, int backgroundColor, int textColor) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        View view = toast.getView();
        view.getBackground().setColorFilter(backgroundColor, PorterDuff.Mode.SRC_IN);

        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(textColor);

        toast.show();
    }
}
