package com.example.she.otherui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.WindowManager;

import com.cuberto.liquid_swipe.LiquidPager;
import com.example.she.R;
import com.example.she.adapters.ViewPager;

public class HelpActivity extends AppCompatActivity {
    LiquidPager pager;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        pager = findViewById(R.id.pagerLiquidSwipe);
        viewPager = new ViewPager(getSupportFragmentManager(), 1);
        pager.setAdapter(viewPager);

    }
}