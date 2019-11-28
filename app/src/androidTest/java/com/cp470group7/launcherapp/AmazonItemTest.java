package com.cp470group7.launcherapp;

import android.net.Uri;

import com.cp470group7.launcherapp.pricemanager.AmazonItem;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AmazonItemTest {

    @Test
    public void generateListingPageLink() {
        assertNotNull(AmazonItem.generateListingPageLink("B07J2Q4SWZ"));
    }

    @Test
    public void getASIN() {
        assertEquals(AmazonItem.getASIN(Uri.parse("https://www.amazon.ca/dp/B07J2Q4SWZ")), "B07J2Q4SWZ");
    }
}