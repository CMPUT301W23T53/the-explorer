package com.example.theexplorer;

import android.view.LayoutInflater;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.theexplorer.databinding.FragmentMapBinding;
import com.example.theexplorer.ui.map.MapFragment;
import com.google.android.gms.maps.GoogleMap;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public class MapFragmentTest {
    MainActivity mainActivity;
    MapFragment mapFragment;

    private FragmentMapBinding binding;
    GoogleMap googleMap;
    @Before
    public void setUp() {
        mainActivity = Robolectric.setupActivity(MainActivity.class);
        mapFragment= new MapFragment();
        startFragment(mapFragment);

        binding = FragmentMapBinding.inflate(LayoutInflater.from(RuntimeEnvironment.application), null, false);

        googleMap = Mockito.mock(GoogleMap.class);
    }
    @Test
    public void testMainActivity() {
        Assert.assertNotNull(mainActivity);
    }
    private void startFragment( MapFragment fragment ) {
        FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(fragment, null );
        fragmentTransaction.commit();
    }






}
