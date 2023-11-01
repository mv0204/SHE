package com.example.she.otherui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.she.R;
import com.example.she.chat.ChatsHome;
import com.example.she.contact.ContactsDisplayActivity;
import com.example.she.databinding.ActivityMainBinding;
import com.example.she.db.DmModel;
import com.example.she.db.MyDbHandler;
import com.example.she.gpt.ChatLaytoutGPT;
import com.example.she.map.MapsActivity;
import com.example.she.models.UserModel;
import com.example.she.myservices.GPS_Service;
import com.example.she.myservices.MyAccelerometerService;
import com.example.she.utils.AndroidUtils;
import com.example.she.utils.FirebaseUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 100;
    ArrayList<DmModel> list;
    SmsManager smsManager;
    double lat, lon;
    boolean canCallMethod = true;
    BroadcastReceiver locationReceiver;
    BroadcastReceiver shakeReceiver;
    MyDbHandler db;
    UserModel currentUser;
    FusedLocationProviderClient fusedLocationProviderClient;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // fetching name and profile from FireBaseDB
        FirebaseUtils.getCurrentUserDetailsFromDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                String profile = snapshot.child("profile").getValue().toString();
                binding.textViewUserName.setText("Hi, " + name);
                AndroidUtils.loadImage(Uri.parse(profile), binding.imageViewUserProfile, MainActivity.this);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // requesting permissions
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        startService(new Intent(getApplicationContext(), GPS_Service.class));
        startService(new Intent(getApplicationContext(), MyAccelerometerService.class));
        locationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {                                 // broadcast receiver to get location updates
                lat = Double.parseDouble(intent.getStringExtra("Latitude"));
                lon = Double.parseDouble(intent.getStringExtra("Longitude"));
            }
        };

        shakeReceiver = new BroadcastReceiver() {
            boolean isShakeHandled = false;

            @Override
            public void onReceive(Context context, Intent intent) {

                if (canCallMethod) {
                    // Call your method logic here
                    sendMessage();

                    // Disable button and set a delay
                    canCallMethod = false;

                    new Handler().postDelayed(() -> {
                        canCallMethod = true;
                    }, 5000); // 5000 milliseconds (5 seconds)
                }


            }
        };                                              // broadcast receiver to get shake updates

        ContextCompat.registerReceiver(this, locationReceiver, new IntentFilter(GPS_Service.LOCATION_UPDATE_ACTION), ContextCompat.RECEIVER_EXPORTED);                              // registering receiver for location updates

        ContextCompat.registerReceiver(this, shakeReceiver, new IntentFilter(MyAccelerometerService.SHAKE_UPDATE_ACTION), ContextCompat.RECEIVER_EXPORTED);                      // registering receiver for shake updates

        try {
            db = new MyDbHandler(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        binding.profileL.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });
        binding.helplineLayout.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), HelplineActivity.class));
        });
        binding.mapL.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
        });
        binding.contactsL.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), ContactsDisplayActivity.class));
        });
        binding.safeL.setOnClickListener(view -> {
            list = db.viewData();
            for (DmModel d : list) {
                String n = d.getKEY_NAME();
                String p = d.getKEY_PHONE();
                smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(p, null, "Hello " + n + " , I have reached my destination safely (●'◡'●)", null, null);
            }

        });
        binding.alertL.setOnClickListener(view -> {
            sendMessage();
        });
        binding.chatBot.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), ChatLaytoutGPT.class));

        });
        binding.logoutBtn.setOnClickListener(v -> {
            Dialog dialog = new Dialog(MainActivity.this, R.style.dialogue);
            dialog.setContentView(R.layout.dialog_layout);
            Button no, yes;
            yes = dialog.findViewById(R.id.yesbnt);
            no = dialog.findViewById(R.id.nobnt);
            yes.setOnClickListener(v1 -> {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            });
            no.setOnClickListener(v2 -> dialog.dismiss());
            dialog.show();
        });
        binding.chatBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ChatsHome.class));
        });


    }

    private void askPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendMessage();
            } else {
                Toast.makeText(this, "Required permissions", Toast.LENGTH_SHORT).show();
            }
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void sendMessage() {
        smsManager = SmsManager.getDefault();
        if (isInternetConnection()) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Give Permissions", Toast.LENGTH_SHORT).show();
                askPermissions();
                return;
            }
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    List<Address> addresses = null;
                    smsManager = SmsManager.getDefault();
                    MyDbHandler db = new MyDbHandler(getApplicationContext());


                    try {
                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    list = db.viewData();

                    for (DmModel d : list) {
                        String p = d.getKEY_PHONE();
                        smsManager.sendTextMessage(p, null, "Location is :" + addresses.get(0).getAddressLine(0) + "\nCoordinates are:(" + addresses.get(0).getLatitude() + "," + addresses.get(0).getLongitude() + ")", null, null);
                    }
                    Toast.makeText(this, "Message Send", Toast.LENGTH_SHORT).show();


                }
            });


        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Give Permissions", Toast.LENGTH_SHORT).show();
                askPermissions();
                return;
            }


            smsManager = SmsManager.getDefault();
            MyDbHandler db = new MyDbHandler(getApplicationContext());
            list = db.viewData();
            for (DmModel d : list) {
                String p = d.getKEY_PHONE();
                smsManager.sendTextMessage(p, null, "coordinates are :\nLat: " + lat + "\nLon: " + lon + "\nAnd you can check location here :\n" + " https://maps.google.com/maps?q=" + lat + "," + lon, null, null);
            }
            Toast.makeText(this, "message send ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the receivers to avoid memory leaks
        if (locationReceiver != null) {
            unregisterReceiver(locationReceiver);
        }
        if (shakeReceiver != null) {
            unregisterReceiver(shakeReceiver);
        }
    }

    public boolean isInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnectedOrConnecting();
        }
        return false;
    }
}