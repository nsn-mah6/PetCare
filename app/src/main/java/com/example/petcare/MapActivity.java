package com.example.petcare;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.petcare.data.entity.Location;
import com.example.petcare.data.repository.PetCareRepository;
import com.example.petcare.utils.SessionManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private PetCareRepository repository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        repository = new PetCareRepository(this);
        sessionManager = new SessionManager(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        findViewById(R.id.fab_back).setOnClickListener(v -> finish());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        repository.getLocationsForUser(sessionManager.getUserId(), locations -> {
            if (locations != null && !locations.isEmpty()) {
                runOnUiThread(() -> {
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (Location loc : locations) {
                        LatLng pos = new LatLng(loc.getLatitude(), loc.getLongitude());
                        mMap.addMarker(new MarkerOptions()
                                .position(pos)
                                .title(loc.getName())
                                .snippet(loc.getType()));
                        builder.include(pos);
                    }

                    LatLngBounds bounds = builder.build();
                    int padding = 200; 
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
                });
            }
        });
    }
}