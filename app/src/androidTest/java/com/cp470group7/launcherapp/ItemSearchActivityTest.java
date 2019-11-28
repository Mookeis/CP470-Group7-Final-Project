package com.cp470group7.launcherapp;

import com.cp470group7.launcherapp.pricemanager.ItemSearchActivity;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

public class ItemSearchActivityTest {

    @Test
    public void convertStreamToString() {
        String initialString = "test_text";
        InputStream targetStream = new ByteArrayInputStream(initialString.getBytes());
        assertEquals(ItemSearchActivity.convertStreamToString(targetStream), "test_text");
    }

    @Test
    public void validateInputURL() {
        assertTrue(ItemSearchActivity.validateInputURL("https://www.amazon.ca/Crucial-1TB-NAND-NVMe-PCIe/dp/B07J2Q4SWZ"));
    }
}