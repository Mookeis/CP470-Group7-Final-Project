package com.cp470group7.launcherapp.pricemanager;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

/*
@startuml
class AmazonItem{
# String name
# String description
# String available
# String[] images
# URL url
    # String brand
    # int totalReviews
    # String category
    # String ASIN
    # String price
    # String shipping
    # float averageRating
---
+generateListingPageLink()
+getASIN()
+describeContents()
+writeToParcel()
---Getters---
+getName()
+getDescription()
+getAvailable()
+getImages()
+getUrl()
+getBrand()
+getTotalReviews()
+getCategory()
+getItemASIN()
+getPrice()
+getShipping()
+getAverageRating()
---Setters---
+setName()
+setDescription()
+setAvailable()
+setImages()
+setUrl()
+setBrand()
+setTotalReviews()
+setCategory()
+setItemASIN()
+setPrice()
+setShipping()
+setAverageRating()
}
AmazonItem <|-- Parcelable
Parcelable : +createFromParcel()
Parcelable : +newArray()
Parcelable <|-- Parcel
@enduml
*/

/**
 * Object class for Amazon item to contain item information from API
 * @see Object
 */
public class AmazonItem implements Parcelable {
    protected String name;
    protected String description;
    protected String available;
    protected String[] images;
    protected URL url;
    protected String brand;
    protected int totalReviews;
    protected String category;
    protected String ASIN;
    protected String price;
    protected String shipping;
    protected float averageRating;

    /**
     * Constructor for filling AmazonItem data from API data
     * @param itemName Amazon item name
     * @param itemDescription Amazon item description
     * @param itemAvailable Amazon item availability
     * @param itemImages Amazon item image list
     * @param itemURL URL to Amazon item
     * @param itemBrand Amazon item brand
     * @param itemReviews Total reviews for Amazon item
     * @param itemCategory Category of Amazon item
     * @param itemASIN Amazon item unique ID
     * @param itemPrice Amazon item price
     * @param itemShipping Amazon item shipping cost
     * @param itemAvgRating Average rating of Amazon item
     */
    AmazonItem(String itemName, String itemDescription, String itemAvailable, String[] itemImages, URL itemURL, String itemBrand, int itemReviews, String itemCategory, String itemASIN, String itemPrice, String itemShipping, float itemAvgRating){
        name = itemName;
        description = itemDescription;
        available = itemAvailable;
        images = itemImages;
        url = itemURL;
        brand = itemBrand;
        totalReviews = itemReviews;
        category = itemCategory;
        ASIN = itemASIN;
        price = itemPrice;
        shipping = itemShipping;
        averageRating = itemAvgRating;
    }

    /**
     * Generates the unique item listing page from the unique item ID ASIN
     * @param ASIN unique item ID
     * @return listings URL
     */
    public static URL generateListingPageLink(String ASIN){
        URL listingPage = null;
        try {
            listingPage = new URL("https://www.amazon.ca/gp/offer-listing/" + ASIN + "/");
        }catch(MalformedURLException e){
            e.printStackTrace();
        }
        return listingPage;
    }

    /**
     * Extracts the unique item ASIN from an Amazon item link
     * @param url absolute URL to item minimized URL format (https://amazon.REGION_EXT/dp/ASIN)
     * @return unique ASIN
     */
    public static String getASIN(Uri url){
        String path = url.getPath();
        String ASIN = "";
        if(path != null) {
            String[] pathList = path.split("/");
            for(String id : pathList){
                if(id.startsWith("B0")){
                    ASIN = id;
                }
            }
        }
        return ASIN;
    }

    /**
     *
     * @return item name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name item name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return item description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description item description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return item availability
     */
    public String getAvailable() {
        return available;
    }

    /**
     *
     * @param available item availability
     */
    public void setAvailable(String available) {
        this.available = available;
    }

    /**
     *
     * @return item image list
     */
    public String[] getImages() {
        return images;
    }

    /**
     *
     * @param images item image list
     */
    public void setImages(String[] images) {
        this.images = images;
    }

    /**
     *
     * @return item URL
     */
    public URL getUrl() {
        return url;
    }

    /**
     *
     * @param url item URL
     */
    public void setUrl(URL url) {
        this.url = url;
    }

    /**
     *
     * @return item brand
     */
    public String getBrand() {
        return brand;
    }

    /**
     *
     * @param brand item brand
     */
    public void setBrand(String brand) {
        this.brand = brand;
    }

    /**
     *
     * @return item total reviews
     */
    public int getTotalReviews() {
        return totalReviews;
    }

    /**
     *
     * @param totalReviews item total reviews
     */
    public void setTotalReviews(int totalReviews) {
        this.totalReviews = totalReviews;
    }

    /**
     *
     * @return item category
     */
    public String getCategory() {
        return category;
    }

    /**
     *
     * @param category item category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     *
     * @return item ASIN
     */
    public String getItemASIN() {
        return ASIN;
    }

    /**
     *
     * @param ASIN item ASIN
     */
    public void setASIN(String ASIN) {
        this.ASIN = ASIN;
    }

    /**
     *
     * @return item price
     */
    public String getPrice() {
        return price;
    }

    /**
     *
     * @param price item price
     */
    public void setPrice(String price) {
        this.price = price;
    }

    /**
     *
     * @return item shipping cost
     */
    public String getShipping() {
        return shipping;
    }

    /**
     *
     * @param shipping item shipping cost
     */
    public void setShipping(String shipping) {
        this.shipping = shipping;
    }

    /**
     *
     * @return item average rating
     */
    public float getAverageRating() {
        return averageRating;
    }

    /**
     *
     * @param averageRating item average rating
     */
    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }

    /**
     * Prints an Amazon item for debugging purposes
     * @param item an Amazon item
     */
    static void debugAmazonItem(AmazonItem item){
        Log.d("AmazonItem", item.getName() + " " + item.getItemASIN() + " " + item.getDescription() + " " + item.getPrice());
    }

    /**
     * Amazon item constructor for parcelable input
     * @param in parcel
     */
    public AmazonItem(Parcel in){
        name = in.readString();
        description = in.readString();
        available = in.readString();
        images = in.createStringArray();
        url = (URL) in.readValue(URL.class.getClassLoader());
        brand = in.readString();
        totalReviews = in.readInt();
        category = in.readString();
        ASIN = in.readString();
        price = in.readString();
        shipping = in.readString();
        averageRating = in.readFloat();
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(available);
        dest.writeStringArray(images);
        dest.writeValue(url);
        dest.writeString(brand);
        dest.writeInt(totalReviews);
        dest.writeString(category);
        dest.writeString(ASIN);
        dest.writeString(price);
        dest.writeString(shipping);
        dest.writeFloat(averageRating);
    }

    public static final Creator CREATOR = new Creator() {
        public AmazonItem createFromParcel(Parcel in){
            return new AmazonItem(in);
        }

        public AmazonItem[] newArray(int size){
            return new AmazonItem[size];
        }
    };
}
