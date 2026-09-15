package com.example.petcare.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.petcare.Auth.LoginPage;
import com.example.petcare.R;
import com.example.petcare.data.entity.CareTask;
import com.example.petcare.data.repository.PetCareRepository;
import com.example.petcare.utils.SessionManager;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    private TextView tvEmail, tvWorkoutCount, tvCompletedCount;
    private ImageView ivProfile;
    private Button btnLogout, btnEditProfile, btnNotifications;
    private SessionManager sessionManager;
    private PetCareRepository repository;

    private final ActivityResultLauncher<Intent> profileImagePicker = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    if (selectedImage != null) {
                        try {
                            requireContext().getContentResolver().takePersistableUriPermission(selectedImage, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } catch (Exception ignored) {}
                        sessionManager.updateProfileImage(selectedImage.toString());
                        ivProfile.setImageURI(selectedImage);
                        Toast.makeText(getContext(), "Profile Photo Updated Successfully", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvEmail = view.findViewById(R.id.tv_profile_email);
        tvWorkoutCount = view.findViewById(R.id.tv_workout_count);
        tvCompletedCount = view.findViewById(R.id.tv_completed_count);
        ivProfile = view.findViewById(R.id.iv_profile_icon);
        btnLogout = view.findViewById(R.id.btn_logout);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        btnNotifications = view.findViewById(R.id.btn_notifications);

        sessionManager = new SessionManager(requireContext());
        repository = new PetCareRepository(requireContext());

        tvEmail.setText(sessionManager.getUserEmail());
        
        String savedPhoto = sessionManager.getProfileImage();
        if (!savedPhoto.isEmpty()) {
            try {
                ivProfile.setImageURI(Uri.parse(savedPhoto));
                ivProfile.setBackground(null); // Remove circle background if image is set
            } catch (Exception e) {
                ivProfile.setImageResource(R.drawable.outline_account_circle_24);
            }
        }

        ivProfile.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Change Profile Photo")
                    .setMessage("Choose an image from your gallery for your account.")
                    .setPositiveButton("Select Photo", (dialog, which) -> {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");
                        profileImagePicker.launch(intent);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        btnEditProfile.setOnClickListener(v -> {
            EditText input = new EditText(getContext());
            input.setText(sessionManager.getUserName());
            input.setHint("Enter Full Name");
            input.setPadding(48, 32, 48, 32);
            new AlertDialog.Builder(getContext())
                .setTitle("Setup Profile Name")
                .setView(input)
                .setPositiveButton("Save Name", (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if(!newName.isEmpty()){
                        sessionManager.createLoginSession(sessionManager.getUserId(), newName, sessionManager.getUserEmail());
                        Toast.makeText(getContext(), "Welcome, " + newName + "!", Toast.LENGTH_SHORT).show();
                        loadStats();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
        });

        btnNotifications.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Care Reminders")
                    .setMessage("Manage how you receive alerts for pet routines.")
                    .setPositiveButton("Settings", (dialog, which) -> Toast.makeText(getContext(), "Notification settings opened", Toast.LENGTH_SHORT).show())
                    .setNegativeButton("Close", null)
                    .show();
        });

        btnLogout.setOnClickListener(v -> {
            sessionManager.logoutUser();
            Intent intent = new Intent(getActivity(), LoginPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        loadStats();
    }

    private void loadStats() {
        repository.getPetsForUser(sessionManager.getUserId(), pets -> {
            if (getActivity() == null) return;
            int petCount = (pets != null) ? pets.size() : 0;
            repository.getTasksForUser(sessionManager.getUserId(), tasks -> {
                if (getActivity() == null) return;
                int totalTasks = (tasks != null) ? tasks.size() : 0;
                int completedTasks = 0;
                if (tasks != null) {
                    for (CareTask t : tasks) {
                        if (t.isCompleted()) completedTasks++;
                    }
                }
                int finalCompletedTasks = completedTasks;
                getActivity().runOnUiThread(() -> {
                    tvWorkoutCount.setText("Managing: " + petCount + " Pets");
                    tvCompletedCount.setText("Checklist: " + finalCompletedTasks + " / " + totalTasks + " Complete");
                });
            });
        });
    }
}