package com.example.petcare;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.petcare.data.entity.CareTask;
import com.example.petcare.data.repository.PetCareRepository;

public class Create_Task_Activity extends AppCompatActivity {
    EditText etName, etCategory, etFrequency, etTime, etSupplies, etInstructions;
    Button btnSave;
    PetCareRepository repository;
    int petId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_task);

        petId = getIntent().getIntExtra("PET_ID", -1);
        repository = new PetCareRepository(this);

        etName = findViewById(R.id.et_task_name);
        etCategory = findViewById(R.id.et_task_category);
        etFrequency = findViewById(R.id.et_task_frequency);
        etTime = findViewById(R.id.et_task_time);
        etSupplies = findViewById(R.id.et_task_supplies);
        etInstructions = findViewById(R.id.et_task_instructions);
        btnSave = findViewById(R.id.btn_save_task);

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String category = etCategory.getText().toString().trim();
            String frequency = etFrequency.getText().toString().trim();
            String time = etTime.getText().toString().trim();
            String supplies = etSupplies.getText().toString().trim();
            String instructions = etInstructions.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter a task name", Toast.LENGTH_SHORT).show();
                return;
            }

            CareTask task = new CareTask(
                    petId,
                    name,
                    category.isEmpty() ? "Feeding" : category,
                    frequency.isEmpty() ? "Daily" : frequency,
                    time.isEmpty() ? "08:00 AM" : time,
                    supplies,
                    instructions,
                    "", // notes
                    "", // image
                    false,
                    System.currentTimeMillis()
            );

            repository.insertTask(task, result -> runOnUiThread(() -> {
                Toast.makeText(Create_Task_Activity.this, "Care Task Created Successfully", Toast.LENGTH_SHORT).show();
                finish();
            }));
        });
    }
}