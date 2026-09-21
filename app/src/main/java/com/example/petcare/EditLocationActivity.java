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
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationTokenSource;

public class EditLocationActivity extends AppCompatActivity implements OnMapReadyCallback {
    EditText etName, etAddress, etType;
    Button btnGetCurrentLocation, btnSave;
    TextView tvCoordinates, tvTitle;
    PetCareRepository repository;
    SessionManager sessionManager;
    FusedLocationProviderClient fusedLocationClient;
    double latitude = 0.0;
    double longitude = 0.0;
    int locationId;
    Location currentLocation;
    private final CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_location);

        locationId = getIntent().getIntExtra("LOCATION_ID", -1);
        
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        tvTitle = findViewById(R.id.tv_location_header_title);
        etName = findViewById(R.id.et_location_name_val);
        etType = findViewById(R.id.et_location_type_val);
        etAddress = findViewById(R.id.et_address_val);
        btnGetCurrentLocation = findViewById(R.id.btn_get_current_location_action);
        btnSave = findViewById(R.id.btn_save_location_final);
        tvCoordinates = findViewById(R.id.tv_coordinates_val);

        repository = new PetCareRepository(this);
        sessionManager = new SessionManager(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        if (tvTitle != null) tvTitle.setText("Edit Saved Location");
        btnSave.setText("Update Location Details");

        repository.getLocationById(locationId, loc -> {
            if (loc == null) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
                    finish();
                });
                return;
            }
            currentLocation = loc;
            runOnUiThread(() -> {
                etName.setText(loc.getName());
                etType.setText(loc.getType());
                etAddress.setText(loc.getAddress());
                latitude = loc.getLatitude();
                longitude = loc.getLongitude();
                tvCoordinates.setText(String.format("Lat: %.4f, Lng: %.4f", latitude, longitude));
                findViewById(R.id.card_coordinates_display).setVisibility(View.VISIBLE);
                findViewById(R.id.card_map_container).setVisibility(View.VISIBLE);
                updateMapMarker();
            });
        });

        btnGetCurrentLocation.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, 100);
            } else {
                fetchLocation();
            }
        });

        btnSave.setOnClickListener(v -> {
            if (currentLocation == null) {
                Toast.makeText(this, "Still loading data...", Toast.LENGTH_SHORT).show();
                return;
            }

            String name = etName.getText().toString().trim();
            String type = etType.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter a location name", Toast.LENGTH_SHORT).show();
                return;
            }

            currentLocation.setName(name);
            currentLocation.setType(type.isEmpty() ? "Pet Clinic" : type);
            currentLocation.setAddress(address.isEmpty() ? "Captured via GPS" : address);
            currentLocation.setLatitude(latitude);
            currentLocation.setLongitude(longitude);

            repository.updateLocation(currentLocation, () -> runOnUiThread(() -> {
                Toast.makeText(EditLocationActivity.this, "Location Updated Successfully", Toast.LENGTH_SHORT).show();
                finish();
            }));
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        updateMapMarker();
    }

    private void updateMapMarker() {
        if (mMap != null && latitude != 0.0) {
            LatLng latLng = new LatLng(latitude, longitude);
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Spot"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Toast.makeText(this, "Fetching current location...", Toast.LENGTH_SHORT).show();

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.getToken())
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        tvCoordinates.setText(String.format("Lat: %.4f, Lng: %.4f", latitude, longitude));
                        findViewById(R.id.card_coordinates_display).setVisibility(View.VISIBLE);
                        findViewById(R.id.card_map_container).setVisibility(View.VISIBLE);
                        updateMapMarker();
                        Toast.makeText(this, "Location detected!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancellationTokenSource.cancel();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchLocation();
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}