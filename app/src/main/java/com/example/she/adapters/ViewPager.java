package com.example.she.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.she.fragments.HelpChatGptFragment;
import com.example.she.fragments.HelpDangerFragment;
import com.example.she.fragments.HelpMapFragment;
import com.example.she.fragments.HelpSafeButtonFragment;


public class ViewPager extends FragmentPagerAdapter {
    public ViewPager(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new HelpDangerFragment();
            case 1: return new HelpMapFragment();
            case 2: return new HelpSafeButtonFragment();
            case 3: return new HelpChatGptFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
