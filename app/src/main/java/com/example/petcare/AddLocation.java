package com.example.petcare;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.petcare.data.entity.Location;
import com.example.petcare.data.repository.PetCareRepository;
import com.example.petcare.utils.SessionManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class AddLocation extends AppCompatActivity {
    EditText etName, etAddress, etType;
    Button btnGetCurrentLocation, btnSave;
    TextView tvCoordinates;
    PetCareRepository repository;
    SessionManager sessionManager;
    FusedLocationProviderClient fusedLocationClient;
    double latitude = 0.0;
    double longitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_location);
        
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        etName = findViewById(R.id.et_location_name_val);
        etType = findViewById(R.id.et_location_type_val);
        etAddress = findViewById(R.id.et_address_val);
        btnGetCurrentLocation = findViewById(R.id.btn_get_current_location_action);
        btnSave = findViewById(R.id.btn_save_location_final);
        tvCoordinates = findViewById(R.id.tv_coordinates_val);

        repository = new PetCareRepository(this);
        sessionManager = new SessionManager(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnGetCurrentLocation.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            } else {
                fetchLocation();
            }
        });

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String type = etType.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter a location name", Toast.LENGTH_SHORT).show();
                return;
            }

            Location location = new Location(
                    sessionManager.getUserId(),
                    name,
                    type.isEmpty() ? "Pet Clinic" : type,
                    address.isEmpty() ? "Captured via GPS" : address,
                    latitude,
                    longitude
            );

            repository.insertLocation(location, id -> runOnUiThread(() -> {
                Toast.makeText(AddLocation.this, "Location Saved Successfully", Toast.LENGTH_SHORT).show();
                finish();
            }));
        });
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        tvCoordinates.setText("Lat: " + latitude + ", Lng: " + longitude);
                        findViewById(R.id.card_coordinates_display).setVisibility(View.VISIBLE);
                        Toast.makeText(this, "Coordinates updated from GPS", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Unable to fetch location. Using default coordinates.", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (SecurityException e) {
                Toast.makeText(this, "Location error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchLocation();
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }
}