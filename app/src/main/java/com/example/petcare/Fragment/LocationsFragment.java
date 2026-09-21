package com.example.petcare.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.petcare.AddLocation;
import com.example.petcare.Adapter.LocationAdapter;
import com.example.petcare.MapActivity;
import com.example.petcare.R;
import com.example.petcare.data.entity.Location;
import com.example.petcare.data.repository.PetCareRepository;
import com.example.petcare.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class LocationsFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private FloatingActionButton fabAdd;
    private PetCareRepository repository;
    private SessionManager sessionManager;
    private final List<Location> locationList = new ArrayList<>();
    private LocationAdapter adapter;

    public LocationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_locations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_locations);
        tvEmpty = view.findViewById(R.id.tv_empty_locations);
        fabAdd = view.findViewById(R.id.fab_add_location);

        repository = new PetCareRepository(requireContext());
        sessionManager = new SessionManager(requireContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LocationAdapter(locationList, requireContext(), this::loadLocations);
        recyclerView.setAdapter(adapter);
        adapter.attachToRecyclerView(recyclerView);

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddLocation.class);
            startActivity(intent);
        });

        view.findViewById(R.id.btn_open_map).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MapActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadLocations();
    }

    private void loadLocations() {
        repository.getLocationsForUser(sessionManager.getUserId(), locations -> {
            if (getActivity() == null) return;
            getActivity().runOnUiThread(() -> {
                locationList.clear();
                if (locations != null) {
                    locationList.addAll(locations);
                }
                adapter.notifyDataSetChanged();

                if (locationList.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            });
        });
    }
}