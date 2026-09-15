package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.petcare.Auth.LoginPage;
import com.example.petcare.Fragment.HomeFragment;
import com.example.petcare.Fragment.MypetFragment;
import com.example.petcare.Fragment.ProfileFragment;
import com.example.petcare.Fragment.LocationsFragment;
import com.example.petcare.Fragment.TasksFragment;
import com.example.petcare.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, LoginPage.class);
            startActivity(intent);
            finish();
            return;
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();
            if (id == R.id.home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.workout) {
                selectedFragment = new MypetFragment();
            } else if (id == R.id.tasks) {
                selectedFragment = new TasksFragment();
            } else if (id == R.id.locations) {
                selectedFragment = new LocationsFragment();
            } else if (id == R.id.profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });

        // Set default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }
    }
}