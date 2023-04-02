package com.example.theexplorer.ui.map;


import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.theexplorer.databinding.FragmentMapBinding;
import com.example.theexplorer.services.NewUserService;
import com.example.theexplorer.services.QRCode;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import android.app.Activity;

import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FragmentMapBinding binding;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;


    /**
     * Inflates the map fragment layout and initializes the GoogleMap object.
     *
     * @param inflater           the LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState the saved state of the fragment.
     * @return the root view of the fragment.
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        binding.map.onCreate(savedInstanceState);
        binding.map.getMapAsync(this);

        return root;
    }

    /**
     * Handles the initialization of the GoogleMap object and the display of nearby QR codes on the map.
     *
     * @param googleMap the GoogleMap object used for displaying the map.
     */
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        LatLng lastLocation = getLastLocation();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 15));
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            double currentLat = lastLocation.latitude;
            double currentLong = lastLocation.longitude;
            NewUserService newUserService = new NewUserService();
            newUserService.getNearbyQRCodes(currentLat, currentLong, 0.3).addOnSuccessListener(new OnSuccessListener<List<QRCode>>() {
                @Override
                public void onSuccess(List<QRCode> qrCodes) {
                    for (QRCode qrCode : qrCodes) {
                        LatLng nearby = new LatLng(qrCode.getLatitude(), qrCode.getLongitude());
                        //mMap.addMarker(new MarkerOptions().position(nearby));
                        mMap.addMarker(new MarkerOptions().position(nearby).title(qrCode.getQRName()));
                    }
                }
            });
            mMap.setMyLocationEnabled(true);
            createLocationRequest();
            setLocationCallback();

        }

    }

    /**
     * Creates a LocationRequest with specified interval and accuracy, sets the created LocationRequest to locationRequest variable.
     * The interval between location updates is set to 10000 milliseconds and fastest interval to 5000 milliseconds.
     * The priority is set to high accuracy to provide the most accurate location possible.
     */
    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Sets up a LocationCallback to handle location updates.
     * When a new location is received, the function will convert the location to a LatLng object, save it as last location,
     * animate the camera to the new location on the map, and remove location updates.
     * @throws SecurityException if ACCESS_FINE_LOCATION or ACCESS_COARSE_LOCATION permission is not granted
     */
    private void setLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    saveLastLocation(currentLocation);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                    fusedLocationClient.removeLocationUpdates(locationCallback);
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.map.onResume();
    }

    @Override
    public void onPause() {
        binding.map.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        binding.map.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.map.onLowMemory();
    }

    private void saveLastLocation(LatLng location) {
        Activity activity = getActivity();
        if (activity != null) {
            SharedPreferences sharedPreferences = activity.getSharedPreferences("last_location", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat("latitude", (float) location.latitude);
            editor.putFloat("longitude", (float) location.longitude);
            editor.apply();
        }
    }


    private LatLng getLastLocation() {
        Activity activity = getActivity();
        if (activity != null) {
            SharedPreferences sharedPreferences = activity.getSharedPreferences("last_location", Context.MODE_PRIVATE);
            float latitude = sharedPreferences.getFloat("latitude", 0);
            float longitude = sharedPreferences.getFloat("longitude", 0);
            return new LatLng(latitude, longitude);
        }
        return new LatLng(0, 0);
    }



}

