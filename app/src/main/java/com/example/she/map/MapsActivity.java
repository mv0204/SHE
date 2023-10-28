package com.example.she.map;

import android.Manifest;
import android.content.pm.PackageManager;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.she.R;
import com.example.she.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;


import java.io.IOException;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private final int REQUEST_CODE_PERMISSION = 1001;
    private ActivityMapsBinding binding;
    private final String[] REQUIRED_PERMISSIONS = {android.Manifest.permission.ACCESS_FINE_LOCATION};
    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private double lat, lon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location= binding.searchView.getQuery().toString();
                List<Address> addressList=null;
                if(location!=null){
                    Geocoder geocoder=new Geocoder(MapsActivity.this);
                    try {
                        addressList=geocoder.getFromLocationName(location,5);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    for (int i = 0; i < addressList.size(); i++) {
                        Address address=addressList.get(i);
                        LatLng latLng=new LatLng(address.getLatitude(),address.getLongitude());
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(latLng).title(location));

                    }

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        binding.hospital.setOnClickListener(view -> {
            StringBuilder stringBuilder=new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            stringBuilder.append("location="+lat+","+lon);
            stringBuilder.append("&radius=2000");
            stringBuilder.append("&type="+"hospital");
            stringBuilder.append("&sensor=true");
            stringBuilder.append("&key="+"AIzaSyAmarxKRNs4lJ6CoAftVyYaXxN4qHY6aHw");
            String url=stringBuilder.toString();
            Object fetchData[]=new Object[2];
            fetchData[0]=mMap;
            fetchData[1]=url;
            FetchNearbyData fetchNearbyData=new FetchNearbyData();
            fetchNearbyData.execute(fetchData);
        });
        binding.myLocation.setOnClickListener(view -> {
            getMyLocation();
        });
        binding.policeStation.setOnClickListener(view -> {
            StringBuilder stringBuilder=new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            stringBuilder.append("location="+lat+","+lon);
            stringBuilder.append("&radius=4000");
            stringBuilder.append("&type=police");
            stringBuilder.append("&sensor=true");
            stringBuilder.append("&key="+"AIzaSyAmarxKRNs4lJ6CoAftVyYaXxN4qHY6aHw");
            String url=stringBuilder.toString();
            Object fetchData[]=new Object[2];
            fetchData[0]=mMap;
            fetchData[1]=url;
            FetchNearbyData fetchNearbyData=new FetchNearbyData();
            fetchNearbyData.execute(fetchData);
        });
        binding.pharmacy.setOnClickListener(view -> {
            StringBuilder stringBuilder=new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            stringBuilder.append("location="+lat+","+lon);
            stringBuilder.append("&radius=2000");
            stringBuilder.append("&type=pharmacy");
            stringBuilder.append("&sensor=true");
            stringBuilder.append("&key="+"AIzaSyAmarxKRNs4lJ6CoAftVyYaXxN4qHY6aHw");
            String url=stringBuilder.toString();
            Object fetchData[]=new Object[2];
            fetchData[0]=mMap;
            fetchData[1]=url;
            FetchNearbyData fetchNearbyData=new FetchNearbyData();
            fetchNearbyData.execute(fetchData);
        });
        binding.myLocation.setOnClickListener(view -> {
            getMyLocation();
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Check and request location permission
        if (checkLocationPermission()) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            // Get user's current location
            getMyLocation();
        } else {
            requestLocationPermission();
        }
    }


    private boolean checkLocationPermission() {
        int permissionState = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSION);
    }


    private void getMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            Toast.makeText(this, "please first allow maps permission ", Toast.LENGTH_SHORT).show();
            return;
        }
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 60000)
                .setWaitForAccurateLocation(true)
                .setMinUpdateIntervalMillis(50000)
                .build();


        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) {
                    Toast.makeText(MapsActivity.this, "Current Location is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        Toast.makeText(MapsActivity.this, "Location is :" + location.getLatitude() + " , " + location.getLongitude(), Toast.LENGTH_SHORT).show();

                    }
                }
            }

        };
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        Task<Location> task = fusedLocationClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                lat = location.getLatitude();
                lon = location.getLongitude();
                LatLng latLng = new LatLng(lat, lon);
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getMyLocation();
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
