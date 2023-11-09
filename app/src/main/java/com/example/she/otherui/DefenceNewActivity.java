package com.example.she.otherui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.example.she.R;
import com.example.she.adapters.MyViewPagerAdapter;

public class DefenceNewActivity extends AppCompatActivity {
    ViewPager2 viewPager2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defence_new);
        viewPager2 = findViewById(R.id.pager);
        MyViewPagerAdapter adapter = new MyViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager2.setAdapter(adapter);
    }
}