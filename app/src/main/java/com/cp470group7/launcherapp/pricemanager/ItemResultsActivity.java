package com.cp470group7.launcherapp.pricemanager;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.FragmentTransaction;

import com.cp470group7.launcherapp.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import toan.android.floatingactionmenu.FloatingActionButton;
import toan.android.floatingactionmenu.FloatingActionsMenu;

import static com.cp470group7.launcherapp.pricemanager.AmazonItem.debugAmazonItem;
import static com.cp470group7.launcherapp.pricemanager.AmazonItem.getASIN;
import static com.cp470group7.launcherapp.pricemanager.ItemSearchActivity.API_KEY;
import static com.cp470group7.launcherapp.pricemanager.ItemSearchActivity.convertStreamToString;

/**
 * Results Activity to display results of an Amazon Item Search
 * @see android.app.Activity
 */
@SuppressWarnings("deprecation")
public class ItemResultsActivity extends AppCompatActivity implements ItemListingFragment.OnListFragmentInteractionListener {

    protected Toolbar resultsToolbar;
    protected SearchView resultsSearchView;
    protected LinearLayout imageLinearLayout;
    protected FloatingActionsMenu menu;
    protected FloatingActionButton retailerButton;
    protected FloatingActionButton watchListButton;
    protected ProgressBar resultsProgressBar;
    protected FrameLayout productRetailerFrame;

    protected TextView itemNameTextView;
    protected TextView descriptionTextView;
    protected TextView availabilityTextView;
    protected TextView brandTextView;
    protected TextView totalReviewsTextView;
    protected TextView categoryTextView;
    protected TextView priceTextView;
    protected TextView shippingTextView;
    protected TextView averageRatingTextView;
    protected TextView ASINTextView;
    protected TextView urlTextView;

    protected String ACTIVITY_NAME = "ItemResultsActivity";
    protected static String DEVELOPER_TOKEN = "434a60337dc73bbb235ddc5c6612a887";
    protected SQLiteDatabase watchlistDb;

    protected Intent changeActivityIntent;
    protected Bundle searchBundle;
    protected String deviceMode;
    protected String itemASIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_results);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        itemNameTextView = findViewById(R.id.itemNameTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        availabilityTextView = findViewById(R.id.availabilityTextView);
        brandTextView = findViewById(R.id.brandTextView);
        totalReviewsTextView = findViewById(R.id.totalReviewsTextView);
        categoryTextView = findViewById(R.id.categoryTextView);
        priceTextView = findViewById(R.id.priceTextView);
        shippingTextView = findViewById(R.id.shippingTextView);
        averageRatingTextView = findViewById(R.id.averageRatingTextView);
        ASINTextView = findViewById(R.id.ASINTextView);
        urlTextView = findViewById(R.id.urlTextView);
        retailerButton = findViewById(R.id.retailerFab);
        watchListButton = findViewById(R.id.watchFab);
        menu = findViewById(R.id.fab_menu);
        imageLinearLayout = findViewById(R.id.imageLinearLayout);
        resultsToolbar = findViewById(R.id.listingsToolbar);
        resultsProgressBar = findViewById(R.id.resultProgressBar);
        productRetailerFrame = findViewById(R.id.productRetailersFrame);

        menu.bringToFront();
        setSupportActionBar(resultsToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        resultsToolbar.setBackgroundColor(Color.parseColor("#80000000"));

        final Bundle bundle = getIntent().getExtras();
        updateUiText(bundle);

        deviceMode = getDeviceMode(productRetailerFrame);
        if(deviceMode.equals("tablet")){
            ItemListingFragment itemListingFragment = new ItemListingFragment();
            ArrayList<ItemListing> listings = getIntent().getParcelableArrayListExtra("Listings");
            Bundle listingsBundle = new Bundle();
            listingsBundle.putParcelableArrayList("Listings", listings);
            itemListingFragment.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.productRetailersFrame, itemListingFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

        if(!deviceMode.equals("tablet")) {
            retailerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bundle != null) {
                        AmazonItem item = bundle.getParcelable("Item");
                        resultsProgressBar.setVisibility(View.VISIBLE);
                        resultsProgressBar.bringToFront();
                        if (item != null) {
                            searchBundle = new Bundle();
                            changeActivityIntent = new Intent(ItemResultsActivity.this, ListingsActivity.class);
                            SellerRequest sr = new SellerRequest(AmazonItem.generateListingPageLink(item.getItemASIN()));
                            sr.execute();
                        }
                    }
                }
            });
        }

        watchListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(ACTIVITY_NAME, "Watchlist Button Pressed");
                new AlertDialog.Builder(ItemResultsActivity.this)
                        .setTitle("Confirm")
                        .setMessage("Please confirm watchlist add")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AmazonItem item = null;
                                if(bundle != null) {
                                    item = bundle.getParcelable("Item");
                                }
                                Gson gson = new Gson();
                                String watchListJSON = gson.toJson(item, AmazonItem.class);
                                Log.d(ACTIVITY_NAME, watchListJSON);
                                WatchlistDatabase initWatchlist = new WatchlistDatabase(getApplicationContext());
                                watchlistDb = initWatchlist.getWritableDatabase();
                                ContentValues cv = new ContentValues();
                                if(item != null) {
                                    cv.put(WatchlistDatabase.ASIN_NAME, item.getItemASIN());
                                    cv.put(WatchlistDatabase.AMAZONITEM_NAME, watchListJSON);
                                }
                                String getAmazonItemSQL = null;
                                if(item != null) {
                                    getAmazonItemSQL = "SELECT * FROM " + WatchlistDatabase.TABLE_NAME + " WHERE " + WatchlistDatabase.ASIN_NAME + "='" + item.getItemASIN() +"'";
                                }
                                try {
                                    Cursor c = watchlistDb.rawQuery(getAmazonItemSQL, new String[]{});
                                    if(!(c.moveToFirst()) || c.getCount() == 0){
                                        watchlistDb.insert(WatchlistDatabase.TABLE_NAME, "NullPlaceholder", cv);
                                        createCustomBackgroundToast("Item added to watchlist", Color.DKGRAY, Color.WHITE);
                                    }else{
                                        Log.i(ACTIVITY_NAME, "Item in db already");
                                        createCustomBackgroundToast("Item in watchlist already", Color.DKGRAY, Color.WHITE);
                                    }
                                    c.close();
                                }catch(Exception e){
                                    Log.e(ACTIVITY_NAME, "");
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
    }

    /**
     * Creates and displays a custom toast object with specified message, background colour and text colour
     * @param message a string giving the message to display in the toast
     * @param backgroundColor a colour integer to set the toast background
     * @param textColor a colour integer to set the toast text colour
     * @see Toast
     */
    private void createCustomBackgroundToast(String message, int backgroundColor, int textColor){
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        View view = toast.getView();
        view.getBackground().setColorFilter(backgroundColor, PorterDuff.Mode.SRC_IN);

        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(textColor);

        toast.show();
    }

    /**
     * Updates activity views and text from bundle items
     * @param bundle contains parcelable Amazon item
     */
    private void updateUiText(Bundle bundle){
        if (bundle != null) {
            AmazonItem item = bundle.getParcelable("Item");
            if (item != null) {
                try {
                    String[] imageURLs = item.getImages();
                    int countImages = imageURLs.length;
                    for(int i = 0; i < countImages; ++i){
                        String imageURL = item.getImages()[i];
                        String filePath = new URL(imageURL).getFile();
                        String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);
                        ImageRequest im = new ImageRequest(new URL(item.getImages()[i]), fileName);
                        im.execute();
                    }
                }catch(MalformedURLException e){
                    e.printStackTrace();
                }
                String averageRating = "<b>Average Rating:</b><br>" + item.getAverageRating();
                String itemDescription = "<b>Item Description:</b><br>" + item.getDescription();
                String itemAvailability = "<b>Availability:</b><br>" + item.getAvailable();
                String itemBrand = "<b>Brand:</b><br>" + item.getBrand();
                String itemASIN = "<b>ASIN:</b><br>" + item.getItemASIN();
                String itemTotalReviews = "<b>Total Reviews:</b><br>" + item.getTotalReviews();
                String itemCategory = "<b>Category:</b><br>" + item.getCategory();
                String itemPrice = "<b>Price:</b><br>" + item.getPrice();
                String itemShipping = "<b>Shipping Costs:</b><br>";
                if(item.getShipping().equals("null")){
                    itemShipping = itemShipping + "None";
                }else{
                    itemShipping = itemShipping + item.getShipping();
                }
                String itemURL = "<b>URL:</b><br>" + item.getUrl();
                itemNameTextView.setText(item.getName());
                itemNameTextView.setMovementMethod(new ScrollingMovementMethod());
                descriptionTextView.setText(Html.fromHtml(itemDescription));
                availabilityTextView.setText(Html.fromHtml(itemAvailability));
                brandTextView.setText(Html.fromHtml(itemBrand));
                totalReviewsTextView.setText(Html.fromHtml(itemTotalReviews));
                categoryTextView.setText(Html.fromHtml(itemCategory));
                priceTextView.setText(Html.fromHtml(itemPrice));
                shippingTextView.setText(Html.fromHtml(itemShipping));
                averageRatingTextView.setText(Html.fromHtml(averageRating));
                ASINTextView.setText(Html.fromHtml(itemASIN));
                urlTextView.setText(Html.fromHtml(itemURL));
            }
        }
    }

    /**
     * Checks if fragmentContainer is within the activity to determine device type
     * @param fragmentContainer container that is only visible on certain devices
     * @return                  the type of device that is being used
     */
    public static String getDeviceMode(FrameLayout fragmentContainer){
        String deviceMode = null;
        if(fragmentContainer == null){
            deviceMode = "phone";
        }else{
            deviceMode = "tablet";
        }
        return deviceMode;
    }

    @Override
    public void onListFragmentInteraction(ItemListing item) {

    }

    /**
     * Hides the keyboard if on display
     * @see InputMethodManager
     */
    public void hideKeyboard(){
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputManager != null) {
            if(getCurrentFocus() != null) {
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * Class for handling API search for item query information
     */
    public class ScrapeRequest extends AsyncTask<String, Integer, String>{

        private URL itemURL;
        private boolean error = false;
        private AmazonItem searchItem;

        /**
         * Constructor for API handling class to get AmazonItem data
         * @param url absolute URL of item to scrape data from
         * @see AmazonItem
         */
        private ScrapeRequest(URL url){
            itemURL = url;
        }

        @Override
        protected void onPostExecute(String a) {
            resultsProgressBar.setVisibility(View.INVISIBLE);
            if(error) {
                String errorMessage = "Invalid URL; please try again";
                Toast errorToast = Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG);
                errorToast.show();
            }else {
                changeActivityIntent = new Intent(ItemResultsActivity.this, ItemResultsActivity.class);
                searchBundle = new Bundle();
                searchBundle.putParcelable("Item", searchItem);
                if(deviceMode.equals("tablet")){
                    resultsProgressBar.setVisibility(View.VISIBLE);
                    SellerRequest sellerRequest = new SellerRequest(AmazonItem.generateListingPageLink(itemASIN));
                    sellerRequest.execute();
                }else{
                    changeActivityIntent.putExtras(searchBundle);
                    finish();
                    startActivityForResult(changeActivityIntent, 10);
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            resultsProgressBar.setProgress(values[0]);
        }

        @Override
        protected String doInBackground(String... strings){
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
                Log.d(ACTIVITY_NAME, response);
                if(response.contains("\"error\":")){
                    Pattern p = Pattern.compile("\"([^\"]*)\"");
                    Matcher m = p.matcher(response);
                    while (m.find()) {
                        if(error){
                            break;
                        }
                        if(m.group().equals("\"error\"")){
                            error = true;
                        }

                    }
                }
                try {
                    JSONObject itemJSON = new JSONObject(response);
                    JSONArray imageJSON = itemJSON.getJSONArray("images");
                    String[] images = new String[imageJSON.length()];
                    for(int i = 0; i < imageJSON.length(); i++) {
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
                }catch (JSONException err){
                    Log.d(ACTIVITY_NAME, err.toString());
                }
                is.close();
                publishProgress(100);
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * Class for handling API call for listing information for item
     */
    public class SellerRequest extends AsyncTask<String, Integer, String> {

        protected URL listingsURL;
        protected ArrayList<ItemListing> listings = new ArrayList<>();
        protected boolean error = false;

        /**
         * Constructor for API handling class to get AmazonItem listing data
         * @param listingsURL absolute URL to item listings page
         * @see ItemListing
         * @see AmazonItem
         */
        public SellerRequest(URL listingsURL){
            this.listingsURL = listingsURL;
        }

        @Override
        protected void onPostExecute(String a) {
            resultsProgressBar.setVisibility(View.INVISIBLE);
            if(error) {
                String errorMessage = "Request to server failed; please try again";
                createCustomBackgroundToast(errorMessage, Color.DKGRAY, Color.WHITE);
            }else {
                searchBundle.putParcelableArrayList("Listings", listings);
                changeActivityIntent.putExtras(searchBundle);
                finish();
                startActivityForResult(changeActivityIntent, 10);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            resultsProgressBar.setProgress(values[0]);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL diffURL = new URL("https://api.diffbot.com/v3/article?token="+ DEVELOPER_TOKEN +"&url=" + listingsURL);
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
                for(int i = 0; i < objectArray.length(); ++i){
                     JSONObject json = objectArray.getJSONObject(i);
                     listingDetails[i] = json;
                }
                ArrayList<JSONObject> conditions = new ArrayList<>();
                ArrayList<JSONObject> sellers = new ArrayList<>();
                ArrayList<JSONObject> prices = new ArrayList<>();
                ArrayList<String> conditionStrings = new ArrayList<>();
                ArrayList<String> sellerStrings = new ArrayList<>();
                ArrayList<String> priceStrings = new ArrayList<>();
                for(JSONObject json : listingDetails){
                    Iterator<String> keys = json.keys();
                    while(keys.hasNext()){
                        String key = keys.next();
                        if(key.equals("Condition Info")){
                            JSONArray conditionArray = json.getJSONArray("Condition Info");
                            for(int i = 0; i < conditionArray.length(); ++i){
                                conditions.add(conditionArray.getJSONObject(i));
                            }
                        }else if(key.equals("Seller Info")){
                            JSONArray sellerArray = json.getJSONArray("Seller Info");
                            for(int i = 0; i < sellerArray.length(); ++i){
                                sellers.add(sellerArray.getJSONObject(i));
                            }
                        }else if(key.equals("Price Info")){
                            JSONArray priceArray = json.getJSONArray("Price Info");
                            for(int i = 0; i < priceArray.length(); ++i){
                                prices.add(priceArray.getJSONObject(i));
                            }
                        }
                    }
                }
                publishProgress(75);

                for(JSONObject condition : conditions){
                    Log.i(ACTIVITY_NAME, condition.getString("Condition"));
                    conditionStrings.add(condition.getString("Condition"));
                }
                for(JSONObject seller : sellers){
                    Log.i(ACTIVITY_NAME, seller.getString("Seller"));
                    if(seller.getString("Seller").isEmpty()){
                        sellerStrings.add("Amazon");
                    }else {
                        sellerStrings.add(seller.getString("Seller"));
                    }
                }
                for(JSONObject price : prices){
                    Log.i(ACTIVITY_NAME, price.getString("Price"));
                    priceStrings.add(price.getString("Price"));
                }

                if(priceStrings.size() == sellerStrings.size() && sellerStrings.size() == conditionStrings.size()){
                    Log.i(ACTIVITY_NAME, "Listing Info arrays same size");
                    for(int i = 0; i < priceStrings.size(); ++i){
                        listings.add(new ItemListing(sellerStrings.get(i), priceStrings.get(i), conditionStrings.get(i)));
                    }
                }else{
                    error = true;
                }
                publishProgress(100);
            }catch(Exception e){
                Log.i(ACTIVITY_NAME, "Server Request Error");
                error = true;

            }
            return null;
        }
    }

    /**
     * Class for handling image request for item
     */
    public class ImageRequest extends AsyncTask<String, Integer, String> {

        URL imageURL;
        Bitmap itemImage;
        String fileName;

        /**
         * Constructor for image handling class
         * @param imageURL absolute URL to image file to download
         * @param fileName name of image file to download
         * @see Bitmap
         */
        ImageRequest(URL imageURL, String fileName){
            this.imageURL = imageURL;
            this.fileName = fileName;
        }

        @Override
        protected void onPostExecute(String a) {
            int width = 400, height = 400;
            itemImage = Bitmap.createScaledBitmap(itemImage, width, height, true);
            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), itemImage);
            final float roundPx = (float) width * 0.1f;
            roundedBitmapDrawable.setCornerRadius(roundPx);
            ImageView img = new ImageView(getApplicationContext());
            img.setImageDrawable(roundedBitmapDrawable);
            img.setScaleType(ImageView.ScaleType.FIT_XY);
            imageLinearLayout.addView(img);
            View divider = new View(new ContextThemeWrapper(getApplicationContext(), R.style.Divider), null, 0);
            divider.setLayoutParams(new LinearLayout.LayoutParams(5, height));
            imageLinearLayout.addView(divider);
        }

        private Bitmap getImage(URL url) {
            HttpsURLConnection connection = null;
            try {
                connection = (HttpsURLConnection) url.openConnection();
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    return BitmapFactory.decodeStream(connection.getInputStream());
                } else
                    return null;
            } catch (Exception e) {
                return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            File file = getBaseContext().getFileStreamPath(fileName);
            if(file.exists()) {
                FileInputStream fis = null;
                try {
                    fis = openFileInput(fileName);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Log.i(ACTIVITY_NAME, "Found the file locally");
                itemImage = BitmapFactory.decodeStream(fis);
            }else {
                itemImage = getImage(imageURL);
                try {
                    FileOutputStream outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                    itemImage.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                    Log.i(ACTIVITY_NAME, "Downloaded the file from the Internet");
                    outputStream.flush();
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        getMenuInflater().inflate(R.menu.result_toolbar_menu, m);
        resultsSearchView = (SearchView) m.findItem(R.id.app_bar_search).getActionView();

        resultsSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(ACTIVITY_NAME, "Search Query Submit");
                hideKeyboard();
                String searchText = resultsSearchView.getQuery().toString();
                if(ItemSearchActivity.validateInputURL(searchText)) {
                    itemASIN = getASIN(Uri.parse(searchText));
                    final String urlString = "https://get.scrapehero.com/amz/product-details/?asin=" + itemASIN + "&x-api-key=" + API_KEY;
                    URL url = null;
                    try {
                        url = new URL(urlString);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    resultsProgressBar.setVisibility(View.VISIBLE);
                    WatchlistDatabase initWatchlist = new WatchlistDatabase(getApplicationContext());
                    watchlistDb = initWatchlist.getWritableDatabase();
                    String getAmazonItemSQL = "SELECT * FROM " + WatchlistDatabase.TABLE_NAME + " WHERE " + WatchlistDatabase.ASIN_NAME + "='" + itemASIN+"'";
                    try {
                        Cursor c = watchlistDb.rawQuery(getAmazonItemSQL, new String[]{});
                        if(!(c.moveToFirst()) || c.getCount() == 0){
                            Log.i(ACTIVITY_NAME, "Request item from internet");
                            ScrapeRequest sr = new ScrapeRequest(url);
                            sr.execute();

                        }else{
                            Log.i(ACTIVITY_NAME, "Found item in watchlist database");
                            changeActivityIntent = new Intent(ItemResultsActivity.this, ItemResultsActivity.class);
                            searchBundle = new Bundle();
                            Gson gson = new Gson();
                            AmazonItem searchItem = gson.fromJson(c.getString(c.getColumnIndex(WatchlistDatabase.AMAZONITEM_NAME)), AmazonItem.class);
                            searchBundle.putParcelable("Item", searchItem);
                            if(deviceMode.equals("tablet")){
                                resultsProgressBar.setVisibility(View.VISIBLE);
                                SellerRequest sellerRequest = new SellerRequest(AmazonItem.generateListingPageLink(itemASIN));
                                sellerRequest.execute();
                            }else {
                                changeActivityIntent.putExtras(searchBundle);
                                finish();
                                startActivityForResult(changeActivityIntent, 10);
                            }
                        }
                        c.close();
                    }catch(SQLiteException e){
                        e.printStackTrace();
                    }

                }else{
                    Toast errorToast = Toast.makeText(getApplicationContext(), "Invalid URL; please try again", Toast.LENGTH_LONG);
                    errorToast.show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
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
