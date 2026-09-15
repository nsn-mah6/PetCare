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
import com.example.petcare.Adapter.PetAdapter;
import com.example.petcare.Create_Pets;
import com.example.petcare.R;
import com.example.petcare.data.entity.Pet;
import com.example.petcare.data.repository.PetCareRepository;
import com.example.petcare.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private TextView txtWelcome, tvNoWorkouts;
    private RecyclerView recyclerPets;
    private FloatingActionButton fabAddPet;
    private PetCareRepository repository;
    private SessionManager sessionManager;
    private final List<Pet> petList = new ArrayList<>();
    private PetAdapter adapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtWelcome = view.findViewById(R.id.txt_welcome);
        tvNoWorkouts = view.findViewById(R.id.tv_no_workouts);
        recyclerPets = view.findViewById(R.id.recycler_pets);
        fabAddPet = view.findViewById(R.id.fab_add_workout);

        repository = new PetCareRepository(requireContext());
        sessionManager = new SessionManager(requireContext());

        txtWelcome.setText("Hello, " + sessionManager.getUserName() + " 👋");

        recyclerPets.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PetAdapter(petList, requireContext());
        recyclerPets.setAdapter(adapter);

        fabAddPet.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Create_Pets.class);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPets();
    }

    private void loadPets() {
        repository.getPetsForUser(sessionManager.getUserId(), new PetCareRepository.Callback<List<Pet>>() {
            @Override
            public void onResult(List<Pet> pets) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    petList.clear();
                    if (pets != null) {
                        petList.addAll(pets);
                    }
                    adapter.notifyDataSetChanged();

                    if (petList.isEmpty()) {
                        tvNoWorkouts.setVisibility(View.VISIBLE);
                        recyclerPets.setVisibility(View.GONE);
                    } else {
                        tvNoWorkouts.setVisibility(View.GONE);
                        recyclerPets.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }
}