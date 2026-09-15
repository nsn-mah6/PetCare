package com.example.petcare.Fragment;

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
import com.example.petcare.Adapter.TaskAdapter;
import com.example.petcare.R;
import com.example.petcare.data.entity.CareTask;
import com.example.petcare.data.repository.PetCareRepository;
import com.example.petcare.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class TasksFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private PetCareRepository repository;
    private SessionManager sessionManager;
    private final List<CareTask> taskList = new ArrayList<>();
    private TaskAdapter adapter;

    public TasksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tasks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_all_tasks);
        tvEmpty = view.findViewById(R.id.tv_empty_tasks);

        repository = new PetCareRepository(requireContext());
        sessionManager = new SessionManager(requireContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TaskAdapter(taskList, requireContext(), this::loadAllTasks);
        recyclerView.setAdapter(adapter);
        adapter.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAllTasks();
    }

    private void loadAllTasks() {
        repository.getTasksForUser(sessionManager.getUserId(), tasks -> {
            if (getActivity() == null) return;
            getActivity().runOnUiThread(() -> {
                taskList.clear();
                if (tasks != null) {
                    taskList.addAll(tasks);
                }
                adapter.notifyDataSetChanged();

                if (taskList.isEmpty()) {
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