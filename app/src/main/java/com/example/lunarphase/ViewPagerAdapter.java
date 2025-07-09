package com.example.lunarphase;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new EventsFragment();
            case 2:
                return new FactsFragment();
            case 3:
                return new ExploreFragment();
            case 4:
                return new MoreFragment();
            default:
                return new HomeFragment();
        }
    }
    @Override
    public int getItemCount() {
        return 5;
    }
}
