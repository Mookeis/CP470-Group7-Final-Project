package com.cp470group7.launcherapp;

import android.content.Context;
import android.widget.FrameLayout;

import androidx.test.core.app.ApplicationProvider;

import com.cp470group7.launcherapp.pricemanager.ItemResultsActivity;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;


public class ItemResultsActivityTest {

    @Test
    public void getDeviceModeTablet() {
        Context context = ApplicationProvider.getApplicationContext();
        FrameLayout f = new FrameLayout(context);
        assertEquals(ItemResultsActivity.getDeviceMode(f), "tablet");
    }

    @Test
    public void getDeviceModePhone() {
        assertEquals(ItemResultsActivity.getDeviceMode(null), "phone");
    }

}