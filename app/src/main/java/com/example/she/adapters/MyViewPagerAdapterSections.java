package com.example.she.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.she.fragments.DiscriminationFragment;
import com.example.she.fragments.EmploymentOppertunityFragment;
import com.example.she.fragments.EqualPayFragment;
import com.example.she.fragments.MaternityFragment;
import com.example.she.fragments.RightSpeechFragment;
import com.example.she.fragments.SectionAcidAttackFragment;
import com.example.she.fragments.SectionBuyingMinorFragment;
import com.example.she.fragments.SectionDomesticViolenceFragment;

public class MyViewPagerAdapterSections extends FragmentStateAdapter {
    public MyViewPagerAdapterSections(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            case 0: return new EmploymentOppertunityFragment();
            case 1: return new MaternityFragment();
            case 2: return new RightSpeechFragment();
            case 3: return new DiscriminationFragment();
            case 4: return new EqualPayFragment();
            case 5: return new SectionAcidAttackFragment();
            case 6: return new SectionDomesticViolenceFragment();
            case 7: return new SectionBuyingMinorFragment();
        }

        return null;
    }

    @Override
    public int getItemCount() {
        return 8;
    }
}
