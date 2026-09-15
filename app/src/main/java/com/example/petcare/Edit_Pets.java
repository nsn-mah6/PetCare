package com.example.petcare;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.petcare.data.entity.Pet;
import com.example.petcare.data.repository.PetCareRepository;

public class Edit_Pets extends AppCompatActivity {
    EditText etName, etSpecies, etBreed, etAge, etWeight, etDiet;
    Button btnUpdate;
    PetCareRepository repository;
    int petId;
    Pet currentPet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_pets);

        petId = getIntent().getIntExtra("PET_ID", -1);
        repository = new PetCareRepository(this);

        etName = findViewById(R.id.et_pet_name);
        etSpecies = findViewById(R.id.et_pet_species);
        etBreed = findViewById(R.id.et_pet_breed);
        etAge = findViewById(R.id.et_pet_age);
        etWeight = findViewById(R.id.et_pet_weight);
        etDiet = findViewById(R.id.et_pet_diet);
        btnUpdate = findViewById(R.id.btn_update_pet);

        repository.getPetById(petId, pet -> {
            if (pet == null) return;
            currentPet = pet;
            runOnUiThread(() -> {
                etName.setText(pet.getName());
                etSpecies.setText(pet.getSpecies());
                etBreed.setText(pet.getBreed());
                etAge.setText(String.valueOf(pet.getAge()));
                etWeight.setText(String.valueOf(pet.getWeight()));
                etDiet.setText(pet.getDietaryPreferences());
            });
        });

        btnUpdate.setOnClickListener(v -> {
            if (currentPet == null) return;

            String name = etName.getText().toString().trim();
            String species = etSpecies.getText().toString().trim();
            String breed = etBreed.getText().toString().trim();
            String ageStr = etAge.getText().toString().trim();
            String weightStr = etWeight.getText().toString().trim();
            String diet = etDiet.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter pet name", Toast.LENGTH_SHORT).show();
                return;
            }

            int age = 0;
            try { age = Integer.parseInt(ageStr); } catch (Exception ignored) {}
            double weight = 0;
            try { weight = Double.parseDouble(weightStr); } catch (Exception ignored) {}

            currentPet.setName(name);
            currentPet.setSpecies(species);
            currentPet.setBreed(breed);
            currentPet.setAge(age);
            currentPet.setWeight(weight);
            currentPet.setDietaryPreferences(diet);

            repository.updatePet(currentPet, () -> runOnUiThread(() -> {
                Toast.makeText(Edit_Pets.this, "Pet Details Updated", Toast.LENGTH_SHORT).show();
                finish();
            }));
        });
    }
}