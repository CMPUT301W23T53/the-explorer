package com.example.theexplorer.ui.map;

import static android.content.Context.LOCATION_SERVICE;

import android.annotation.SuppressLint;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.theexplorer.databinding.FragmentMapBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

public class MapFragment extends Fragment implements LocationListener {

    private FragmentMapBinding binding;
    private GoogleMap mMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationManager locationManager;

    View mView;
    MapView mMapView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MapViewModel mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);

        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mMapView= binding.map;
        getCurrentLocation();
        return root;
    }
    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        try {
            locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,10,5,this);
        }catch (Exception e){
            e.printStackTrace();
        }}
    @Override
    public void onLocationChanged(@NonNull Location location) {
        Toast.makeText(getActivity(), ""+location.getLatitude()+","+location.getLongitude(), Toast.LENGTH_SHORT).show();
        try {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            double currentLat = location.getLatitude();
            double currentLong = location.getLongitude();
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull GoogleMap googleMap) {
                    mMap = googleMap;
                    LatLng sydney = new LatLng(currentLat, currentLong);
                    mMap.addMarker(new MarkerOptions().position(sydney).title("Current Location"));

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney,100));
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}