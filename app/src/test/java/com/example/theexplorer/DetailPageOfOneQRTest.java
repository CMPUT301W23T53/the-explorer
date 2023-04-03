package com.example.theexplorer;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;

import android.os.Bundle;
import android.widget.TextView;

import com.example.theexplorer.ui.home.DetailPageOfOneQR;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class DetailPageOfOneQRTest {
    private DetailPageOfOneQR activity;
    @Before
    public void setUp() throws Exception {
        activity = Robolectric.buildActivity(DetailPageOfOneQR.class)
                .create()
                .resume()
                .get();
    }



    @Test
    public void testOnCreate() throws Exception {
        Bundle savedInstanceState = mock(Bundle.class);
        savedInstanceState.putString(anyString(),anyString());
        activity.onCreate(savedInstanceState);
        activity.setContentView(R.layout.activity_detail_page_of_one_qr);
        TextView name = activity.findViewById(R.id.qr_name);
        TextView id = activity.findViewById(R.id.qr_id);
        TextView score = activity.findViewById(R.id.qr_score_for_detail);
        TextView location = activity.findViewById(R.id.qr_location);

        assertNotNull(name);
        assertNotNull(id);
        assertNotNull(score);
        assertNotNull(location);
    }
}


