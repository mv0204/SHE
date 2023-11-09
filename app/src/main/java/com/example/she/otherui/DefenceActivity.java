package com.example.she.otherui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.example.she.R;
import com.example.she.adapters.MyViewPagerAdapter;
import com.example.she.databinding.ActivityDefenceBinding;

public class DefenceActivity extends AppCompatActivity {
    ViewPager2 viewPager2;
    ActivityDefenceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityDefenceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        binding.cardView2.setOnClickListener(view -> {
//            startActivity(new Intent(DefenceActivity.this,DefenceRightsActivity.class));
//        });
//        binding.cardView2.setOnClickListener(view -> {
//            startActivity(new Intent(DefenceActivity.this,DefenceSectionsActivity.class));
//        });


//        viewPager2=findViewById(R.id.pager);
//        MyViewPagerAdapter adapter=new MyViewPagerAdapter(getSupportFragmentManager(),getLifecycle());
//        viewPager2.setAdapter(adapter);

    }
}