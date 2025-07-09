package com.example.lunarphase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class PlanetsFragment extends Fragment {

    private RecyclerView recyclerExplore;

    public PlanetsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_content, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerExplore = view.findViewById(R.id.recyclerExplore);

        List<ExploreModel> planetList = Arrays.asList(
                new ExploreModel("Mercury", getString(R.string.Mercury), R.drawable.mercury_planet),
                new ExploreModel("Venus", getString(R.string.Venus), R.drawable.planet_venus),
                new ExploreModel("Earth", getString(R.string.Earth), R.drawable.planet_earth),
                new ExploreModel("Mars", getString(R.string.Mars), R.drawable.planet_mars),
                new ExploreModel("Jupiter", getString(R.string.Jupiter), R.drawable.planet_jupiter),
                new ExploreModel("Saturn", getString(R.string.Saturn), R.drawable.planet_saturn),
                new ExploreModel("Uranus", getString(R.string.Uranus), R.drawable.planet_uranus),
                new ExploreModel("Neptune", getString(R.string.Neptune), R.drawable.planet_neptune),
                new ExploreModel("Pluto", getString(R.string.Pluto), R.drawable.planet_pluto)
        );

        ExploreItemAdapter adapter = new ExploreItemAdapter(planetList);

        recyclerExplore.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        recyclerExplore.setAdapter(adapter);
    }
}
