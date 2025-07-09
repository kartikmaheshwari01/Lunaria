package com.example.lunarphase;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

public class AsteroidsFragment extends Fragment {

    RecyclerView recyclerExplore;

    public AsteroidsFragment() {}

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
                new ExploreModel("Ceres", getString(R.string.asteroid_ceres), R.drawable.asteriod_ceres),
                new ExploreModel("Vesta", getString(R.string.asteroid_vesta), R.drawable.asteriod_vesta),
                new ExploreModel("Pallas", getString(R.string.asteroid_pallas), R.drawable.asteriod_pallas),
                new ExploreModel("Hygiea", getString(R.string.asteroid_hygiea), R.drawable.asteriod_hygiea),
                new ExploreModel("Eros", getString(R.string.asteroid_eros), R.drawable.asteriod_eros),
                new ExploreModel("Bennu", getString(R.string.asteroid_bennu), R.drawable.asteriod_bennu),
                new ExploreModel("Ryugu", getString(R.string.asteroid_ryugu), R.drawable.asteriod_ryugu)
        );

        ExploreItemAdapter adapter = new ExploreItemAdapter(planetList);

        recyclerExplore.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        recyclerExplore.setAdapter(adapter);
    }
}