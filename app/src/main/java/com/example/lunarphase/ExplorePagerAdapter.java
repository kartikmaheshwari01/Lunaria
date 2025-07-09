package com.example.lunarphase;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ExplorePagerAdapter extends FragmentStateAdapter {

    public ExplorePagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PlanetsFragment(); // Will use ExploreItemFragment internally
            case 1:
                return new MoonsFragment();
            case 2:
                return new AsteroidsFragment();
            default:
                return new PlanetsFragment();
        }
    }
    @Override
    public int getItemCount() {
        return 3;
    }
}
