package com.example.theexplorer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.test.rule.ActivityTestRule;

import com.example.theexplorer.ui.scan.ZXingScannerScan;
import com.example.theexplorer.ui.search.SearchActivity;

import org.junit.Rule;
import org.junit.Test;

public class ZXingScannerScanTest {

    @Rule
    public ActivityTestRule<ZXingScannerScan> rule = new ActivityTestRule<>(ZXingScannerScan.class);

    @Test
    public void scanQRcode() {
        ZXingScannerScan activity = rule.getActivity();
        Button scan_button = activity.findViewById(R.id.scan_button);

        activity.scanQRcode();
    }


    @Test
    public void getLocation() {
        ZXingScannerScan activity = rule.getActivity();
        TextView address_text_view = activity.findViewById(R.id.address_text_view);

        String location = activity.getLocation();
        Log.e("..00..00..00..", "getLocation: " + location);

        address_text_view.setText(location);
    }

}