package com.cp470group7.launcherapp.pricemanager;

import android.os.Parcel;
import android.os.Parcelable;

/*
@startuml
class ItemListing{
#String sellerName
#String itemPrice
#String itemCondition
+describeContents()
+writeToParcel()
---Getters---
+getSellerName()
+getItemPrice()
+getItemCondition()
---Setters---
+setSellerName()
+setItemPrice()
+setItemCondition()
}
ItemListing <|-- Parcelable
Parcelable : +createFromParcel()
Parcelable : +newArray()
Parcelable <|-- Parcel
@enduml
*/
public class ItemListing implements Parcelable {
    protected String sellerName, itemPrice, itemCondition;

    /**
     * Constructor for item listing object row
     * @param sellerName name of listing seller
     * @param itemPrice price of listing
     * @param itemCondition condition of listing
     */
    public ItemListing(String sellerName, String itemPrice, String itemCondition){
        this.sellerName = sellerName;
        this.itemPrice = itemPrice;
        this.itemCondition = itemCondition;
    }

    /**
     * Parcelable listing constructor
     * @param in parcel
     */
    protected ItemListing(Parcel in) {
        this.sellerName = in.readString();
        this.itemPrice = in.readString();
        this.itemCondition = in.readString();
    }

    public static final Creator<ItemListing> CREATOR = new Creator<ItemListing>() {
        @Override
        public ItemListing createFromParcel(Parcel in) {
            return new ItemListing(in);
        }

        @Override
        public ItemListing[] newArray(int size) {
            return new ItemListing[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sellerName);
        dest.writeString(itemPrice);
        dest.writeString(itemCondition);
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemCondition() {
        return itemCondition;
    }

    public void setItemCondition(String itemCondition) {
        this.itemCondition = itemCondition;
    }
}
