package com.example.petcare.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.petcare.Adapter.PetAdapter;
import com.example.petcare.R;
import com.example.petcare.data.entity.Pet;
import com.example.petcare.data.repository.PetCareRepository;
import com.example.petcare.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class MypetFragment extends Fragment {
    private RecyclerView recyclerView;
    private LinearLayout layoutEmpty;
    private PetCareRepository repository;
    private SessionManager sessionManager;
    private final List<Pet> petList = new ArrayList<>();
    private PetAdapter adapter;

    public MypetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mypet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_all_workouts);
        layoutEmpty = view.findViewById(R.id.layout_empty_state);

        repository = new PetCareRepository(requireContext());
        sessionManager = new SessionManager(requireContext());

        // Use clean LinearLayoutManager layout style
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PetAdapter(petList, requireContext());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAllPets();
    }

    private void loadAllPets() {
        repository.getPetsForUser(sessionManager.getUserId(), pets -> {
            if (getActivity() == null) return;
            getActivity().runOnUiThread(() -> {
                petList.clear();
                if (pets != null) {
                    petList.addAll(pets);
                }
                adapter.notifyDataSetChanged();

                if (petList.isEmpty()) {
                    layoutEmpty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    layoutEmpty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            });
        });
    }
}