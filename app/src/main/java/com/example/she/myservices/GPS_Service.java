package com.example.she.myservices;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;


public class GPS_Service extends Service {
    public static final String LOCATION_UPDATE_ACTION = "location_update";
    private LocationListener gpsListener;
    private LocationManager locationManager;

    private static final long MIN_TIME_BETWEEN_UPDATES = 5000; // 5 seconds
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 0 meters
    private static final float MIN_REQUIRED_ACCURACY = 200.0f; // Minimum required accuracy in meters

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        gpsListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (isLocationAcceptable(location)) {
                    sendLocationBroadcast(location);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // Handle status changes if needed.
            }

            @Override
            public void onProviderEnabled(String provider) {
                // Handle when the GPS provider is enabled.
            }

            @Override
            public void onProviderDisabled(String provider) {
                // Handle when the GPS provider is disabled.
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        // Check for location permission before requesting updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BETWEEN_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES,
                        gpsListener
                );

            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (isLocationAcceptable(lastKnownLocation)) {
                sendLocationBroadcast(lastKnownLocation);
            }
        }

    }

    @Override
    public void onDestroy() {
        if (locationManager != null) {
            locationManager.removeUpdates(gpsListener);
        }
        super.onDestroy();
    }

    private boolean isLocationAcceptable(Location location) {
        return location != null && location.getAccuracy() <= MIN_REQUIRED_ACCURACY;
    }

    private void sendLocationBroadcast(Location location) {
        Intent i = new Intent(LOCATION_UPDATE_ACTION);
        i.putExtra("Latitude", String.valueOf(location.getLatitude()));
        i.putExtra("Longitude", String.valueOf(location.getLongitude()));
        sendBroadcast(i);
    }
}
