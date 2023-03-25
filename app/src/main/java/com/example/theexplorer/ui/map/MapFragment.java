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

/**
 * Get the user's current location and show it on the Map. And it also show the nearby QRCode location on the map.
 * This method won't returns anything. When showing this fragment it will get the users' current location
 * and show it with nearby location an the map.
 */
public class MapFragment extends Fragment implements LocationListener {

    private FragmentMapBinding binding;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationManager locationManager;

    View mView;
    private MapView mMapView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MapViewModel mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);

        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mMapView= binding.map;
        getCurrentLocation();
        return root;
    }
    /**
     * Trying to get the system services and request to update Location
     * <p>
     * This method won't returns anything. When call this function it will try to get system service to get location.
     * And if it has the permission to get the location, it will ask onLocationChanged() to show it.
     * @return      null
     */
    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        try {
            locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,10,5,this);
        }catch (Exception e){
            e.printStackTrace();
        }}
    /**
     * Get the current location and show it on the map
     * <p>
     * This method won't returns anything. When call this function it will get the current location and set the mapView.
     * When mapView is ready it will return the map with a mark at current location
     * @param  location  the location for current location
     * @return  null
     */
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