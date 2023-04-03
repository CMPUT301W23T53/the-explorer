package com.example.theexplorer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.app.Activity;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;





/**
 * US 06.01.01: As a player, I want to see a map of geo-locations of nearby QR codes.
 * Assumes at least 1 code has been scanned to show off the information required in each US.
 */
public class MapTest {
    private Solo solo;


    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }
    /**
     * Gets the Activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }

    /**
     * US 01.02.01: As a player, I want to see what QR codes I have added to my account.
     *
     * This test case is assuming the user has at least 1 code added to show the QR list view.
     */
    @Test
    public void checkMap() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.navigation_map));
        MapView mapView = (MapView) solo.getView(R.id.map);

        assertNotNull(mapView);
        solo.getCurrentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        // Use the GoogleMap object
                        assertNotNull(googleMap);
                        assertNotNull(googleMap.getCameraPosition());
                        // Verify the controller is not null
                        assertTrue(googleMap.getUiSettings().isZoomControlsEnabled());
                        assertTrue(googleMap.getUiSettings().isMyLocationButtonEnabled());
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(new LatLng(37.7749, -122.4194))
                                .title("San Francisco");
                        Marker marker = googleMap.addMarker(markerOptions);
                        assertNotNull(marker);
                        // Remove the marker when the test is done
                        marker.remove();
                    }
                });
            }
        });



    }
    /**
     * Closes the activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }


}
