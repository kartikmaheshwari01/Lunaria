package com.example.lunarphase;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ExploreFragment extends Fragment {

    private ViewPager2 viewPagerExplore;
    private TabLayout tabLayoutExplore;

    public ExploreFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayoutExplore = view.findViewById(R.id.tabLayoutExplore);
        viewPagerExplore = view.findViewById(R.id.viewPagerExplore);

        viewPagerExplore.setAdapter(new ExplorePagerAdapter(this));

        new TabLayoutMediator(tabLayoutExplore, viewPagerExplore, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Planets"); break;
                case 1: tab.setText("Moons"); break;
                case 2: tab.setText("Asteroids"); break;
            }
        }).attach();
    }
}