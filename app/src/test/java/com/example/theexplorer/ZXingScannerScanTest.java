package com.example.theexplorer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import com.example.theexplorer.ui.scan.ZXingScannerScan;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Collections;

public class ZXingScannerScanTest {

    @Mock
    private LocationManager locationManager;
    @Mock
    private ZXingScannerScan activity;
    @Mock
    private Location location;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getLocation_shouldSetAddressText() throws IOException {
        // Given
        when(activity.getSystemService(ZXingScannerScan.LOCATION_SERVICE)).thenReturn(locationManager);

        // Simulate location update
        when(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)).thenReturn(location);
        activity.onLocationChanged(location);

        // Simulate geocoding result
        Geocoder geocoder = mock(Geocoder.class);

        when(geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1))
                .thenReturn(Collections.singletonList(mock(Address.class)));
        activity.onLocationChanged(location);
    }

}

