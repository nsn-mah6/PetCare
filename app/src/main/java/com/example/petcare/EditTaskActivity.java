package com.example.petcare;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.petcare.data.entity.CareTask;
import com.example.petcare.data.repository.PetCareRepository;

public class EditTaskActivity extends AppCompatActivity {
    EditText etName, etCategory, etFrequency, etTime, etSupplies, etInstructions;
    Button btnSave;
    TextView tvTitle;
    PetCareRepository repository;
    int taskId;
    CareTask currentTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_task);

        taskId = getIntent().getIntExtra("TASK_ID", -1);
        repository = new PetCareRepository(this);

        tvTitle = findViewById(R.id.tv_title);
        etName = findViewById(R.id.et_task_name);
        etCategory = findViewById(R.id.et_task_category);
        etFrequency = findViewById(R.id.et_task_frequency);
        etTime = findViewById(R.id.et_task_time);
        etSupplies = findViewById(R.id.et_task_supplies);
        etInstructions = findViewById(R.id.et_task_instructions);
        btnSave = findViewById(R.id.btn_save_task);

        tvTitle.setText("Edit Care Routine");
        btnSave.setText("Update Care Task");

        repository.getTaskById(taskId, task -> {
            if (task == null) return;
            currentTask = task;
            runOnUiThread(() -> {
                etName.setText(task.getTaskName());
                etCategory.setText(task.getCategory());
                etFrequency.setText(task.getFrequency());
                etTime.setText(task.getTime());
                etSupplies.setText(task.getRequiredSupplies());
                etInstructions.setText(task.getInstructions());
            });
        });

        btnSave.setOnClickListener(v -> {
            if (currentTask == null) return;
            String name = etName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter a task name", Toast.LENGTH_SHORT).show();
                return;
            }

            currentTask.setTaskName(name);
            currentTask.setCategory(etCategory.getText().toString().trim());
            currentTask.setFrequency(etFrequency.getText().toString().trim());
            currentTask.setTime(etTime.getText().toString().trim());
            currentTask.setRequiredSupplies(etSupplies.getText().toString().trim());
            currentTask.setInstructions(etInstructions.getText().toString().trim());

            repository.updateTask(currentTask, () -> runOnUiThread(() -> {
                Toast.makeText(EditTaskActivity.this, "Care Routine Updated Successfully", Toast.LENGTH_SHORT).show();
                finish();
            }));
        });
    }
}