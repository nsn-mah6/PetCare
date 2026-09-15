package com.example.petcare;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.petcare.data.entity.Pet;
import com.example.petcare.data.repository.PetCareRepository;
import com.example.petcare.utils.SessionManager;

public class Create_Pets extends AppCompatActivity {
    EditText etName, etSpecies, etBreed, etAge, etWeight, etDescription;
    Button btnSave, btnAddImage;
    ImageView ivPreview;
    PetCareRepository repository;
    SessionManager sessionManager;
    String imageUriString = "";

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    if (selectedImage != null) {
                        try {
                            getContentResolver().takePersistableUriPermission(selectedImage, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } catch (Exception ignored) {}
                        imageUriString = selectedImage.toString();
                        ivPreview.setImageURI(selectedImage);
                        findViewById(R.id.card_image_preview).setVisibility(View.VISIBLE);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_pets);

        etName = findViewById(R.id.et_pet_name);
        etSpecies = findViewById(R.id.et_pet_species);
        etBreed = findViewById(R.id.et_pet_breed);
        etAge = findViewById(R.id.et_pet_age);
        etWeight = findViewById(R.id.et_pet_weight);
        etDescription = findViewById(R.id.et_pet_description);
        btnSave = findViewById(R.id.btn_save_workout);
        btnAddImage = findViewById(R.id.btn_add_image);
        ivPreview = findViewById(R.id.iv_workout_image);

        repository = new PetCareRepository(this);
        sessionManager = new SessionManager(this);

        btnAddImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String species = etSpecies.getText().toString().trim();
            String breed = etBreed.getText().toString().trim();
            String ageStr = etAge.getText().toString().trim();
            String weightStr = etWeight.getText().toString().trim();
            String diet = etDescription.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter pet name", Toast.LENGTH_SHORT).show();
                return;
            }

            int age = 0;
            try { age = Integer.parseInt(ageStr); } catch (Exception ignored) {}
            double weight = 0;
            try { weight = Double.parseDouble(weightStr); } catch (Exception ignored) {}

            Pet newPet = new Pet(
                    sessionManager.getUserId(),
                    name,
                    species,
                    breed,
                    age,
                    weight,
                    diet, // using description as diet for prototype
                    "", // allergies
                    "", // toys
                    "", // vaccination
                    imageUriString
            );

            repository.insertPet(newPet, result -> runOnUiThread(() -> {
                Toast.makeText(Create_Pets.this, "Pet Added Successfully", Toast.LENGTH_SHORT).show();
                finish();
            }));
        });
    }
}