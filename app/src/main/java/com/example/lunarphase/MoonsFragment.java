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

public class MoonsFragment extends Fragment {

    private RecyclerView recyclerExplore;

    public MoonsFragment() {}

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
                new ExploreModel("Luna",getString(R.string.moon_luna), R.drawable.phase_full),
                new ExploreModel("Phobos", getString(R.string.moon_phobos), R.drawable.phobos_moon),
                new ExploreModel("Europa", getString(R.string.moon_europa), R.drawable.europa_moon),
                new ExploreModel("Io", getString(R.string.moon_io), R.drawable.io_moon),
                new ExploreModel("Ganymede", getString(R.string.moon_ganymede), R.drawable.ganymede_moon),
                new ExploreModel("Callisto", getString(R.string.moon_callisto), R.drawable.callisto_moon),
                new ExploreModel("Titan", getString(R.string.moon_titan), R.drawable.titan_moon),
                new ExploreModel("Enceladus", getString(R.string.moon_enceladus), R.drawable.enceladus_moon),
                new ExploreModel("Triton", getString(R.string.moon_triton), R.drawable.triton_moon),
                new ExploreModel("Charon", getString(R.string.moon_charon), R.drawable.charon_moon)
        );

        ExploreItemAdapter adapter = new ExploreItemAdapter(planetList);

        recyclerExplore.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        recyclerExplore.setAdapter(adapter);
    }
}