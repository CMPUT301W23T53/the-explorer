package com.example.theexplorer.ui.map;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.theexplorer.databinding.FragmentMapBinding;
import com.example.theexplorer.services.NewUserService;
import com.example.theexplorer.services.QRCode;
import com.example.theexplorer.services.UserService;
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

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        binding.map.onCreate(savedInstanceState);
        binding.map.getMapAsync(this);

        return root;
    }

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
                        mMap.addMarker(new MarkerOptions().position(nearby));
                    }
                }
            });
            mMap.setMyLocationEnabled(true);
            createLocationRequest();
            setLocationCallback();

        }

    }


    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

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

