package com.example.she.otherui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.she.R;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}