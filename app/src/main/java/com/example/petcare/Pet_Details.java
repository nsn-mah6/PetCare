package com.example.petcare;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petcare.Adapter.TaskAdapter;
import com.example.petcare.data.entity.CareTask;
import com.example.petcare.data.entity.Pet;
import com.example.petcare.data.repository.PetCareRepository;

import java.util.ArrayList;
import java.util.List;

public class Pet_Details extends AppCompatActivity {
    private ImageView ivImage;
    private TextView tvName, tvSpecies, tvBreed, tvAge, tvWeight, tvDiet;
    private Button btnAddTask, btnDelegate, btnEdit, btnDelete;
    private RecyclerView recyclerTasks;
    private PetCareRepository repository;
    private int petId;
    private Pet currentPet;
    private final List<CareTask> taskList = new ArrayList<>();
    private TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pet_details);

        petId = getIntent().getIntExtra("PET_ID", -1);
        repository = new PetCareRepository(this);

        ivImage = findViewById(R.id.iv_details_image);
        tvName = findViewById(R.id.tv_details_name);
        tvSpecies = findViewById(R.id.tv_details_species);
        tvBreed = findViewById(R.id.tv_details_breed);
        tvAge = findViewById(R.id.tv_details_age);
        tvWeight = findViewById(R.id.tv_details_weight);
        tvDiet = findViewById(R.id.tv_details_diet);
        btnAddTask = findViewById(R.id.btn_add_task);
        btnDelegate = findViewById(R.id.btn_delegate_care);
        btnEdit = findViewById(R.id.btn_edit_pet);
        btnDelete = findViewById(R.id.btn_delete_pet);
        recyclerTasks = findViewById(R.id.recycler_tasks);

        recyclerTasks.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(taskList, this, this::loadTasks);
        recyclerTasks.setAdapter(taskAdapter);
        taskAdapter.attachToRecyclerView(recyclerTasks);

        btnAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(Pet_Details.this, Create_Task_Activity.class);
            intent.putExtra("PET_ID", petId);
            startActivity(intent);
        });

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(Pet_Details.this, Edit_Pets.class);
            intent.putExtra("PET_ID", petId);
            startActivity(intent);
        });

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(Pet_Details.this)
                    .setTitle("Delete Pet")
                    .setMessage("Are you sure you want to delete " + (currentPet != null ? currentPet.getName() : "this pet") + "?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        if (currentPet != null) {
                            repository.deletePet(currentPet, () -> runOnUiThread(() -> {
                                Toast.makeText(Pet_Details.this, "Pet Deleted", Toast.LENGTH_SHORT).show();
                                finish();
                            }));
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        btnDelegate.setOnClickListener(v -> {
            if (taskList.isEmpty()) {
                Toast.makeText(this, "No care tasks to delegate. Please add a care task first.", Toast.LENGTH_SHORT).show();
                return;
            }
            StringBuilder builder = new StringBuilder();
            builder.append("PetCare — ").append(currentPet.getName()).append(" Care Schedule\n\n");
            
            for (CareTask t : taskList) {
                builder.append("TASK: ").append(t.getTaskName()).append("\n");
                builder.append("TIME: ").append(t.getTime()).append(" (").append(t.getFrequency()).append(")\n");
                
                if (t.getRequiredSupplies() != null && !t.getRequiredSupplies().isEmpty()) {
                    builder.append("SUPPLIES: ").append(t.getRequiredSupplies()).append("\n");
                }
                
                if (t.getInstructions() != null && !t.getInstructions().isEmpty()) {
                    builder.append("INSTRUCTIONS: ").append(t.getInstructions()).append("\n");
                }
                builder.append("----------------------------\n");
            }
            
            builder.append("\nPlease follow ").append(currentPet.getName()).append("'s care routine. Thank you!");

            Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
            smsIntent.setData(Uri.parse("smsto:"));
            smsIntent.putExtra("sms_body", builder.toString());
            try {
                startActivity(smsIntent);
            } catch (Exception e) {
                Toast.makeText(this, "SMS application not available.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPetDetails();
        loadTasks();
    }

    private void loadPetDetails() {
        repository.getPetById(petId, pet -> {
            if (pet == null) return;
            currentPet = pet;
            runOnUiThread(() -> {
                tvName.setText(pet.getName());
                tvSpecies.setText("Species: " + pet.getSpecies());
                tvBreed.setText("Breed: " + pet.getBreed());
                tvAge.setText("Age: " + pet.getAge() + " years");
                tvWeight.setText("Weight: " + pet.getWeight() + " kg");
                tvDiet.setText("Diet Preferences: " + pet.getDietaryPreferences());

                if (pet.getImageUri() != null && !pet.getImageUri().isEmpty()) {
                    try {
                        ivImage.setImageURI(Uri.parse(pet.getImageUri()));
                    } catch (Exception e) {
                        ivImage.setImageResource(R.drawable.cat);
                    }
                } else {
                    ivImage.setImageResource(R.drawable.cat);
                }
            });
        });
    }

    private void loadTasks() {
        repository.getTasksForPet(petId, tasks -> runOnUiThread(() -> {
            taskList.clear();
            if (tasks != null) {
                taskList.addAll(tasks);
            }
            taskAdapter.notifyDataSetChanged();
        }));
    }
}