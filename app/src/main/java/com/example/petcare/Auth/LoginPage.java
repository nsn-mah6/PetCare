package com.example.petcare.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.petcare.MainActivity;
import com.example.petcare.R;
import com.example.petcare.data.repository.PetCareRepository;
import com.example.petcare.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity {
    EditText email, password;
    TextView register, forgotpassword;
    Button login;
    PetCareRepository repository;
    SessionManager sessionManager;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);

        email = findViewById(R.id.login_txt_email);
        password = findViewById(R.id.login_txt_password);
        register = findViewById(R.id.login_txt_register);
        login = findViewById(R.id.login_btn);
        forgotpassword = findViewById(R.id.login_txt_forgotPassword);

        repository = new PetCareRepository(this);
        sessionManager = new SessionManager(this);
        firebaseAuth = FirebaseAuth.getInstance();

        // Check if user is already logged in with Firebase & SessionManager
        if (firebaseAuth.getCurrentUser() != null && sessionManager.isLoggedIn()) {
            Intent intent = new Intent(LoginPage.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegistrationPage.class);
                startActivity(intent);
            }
        });

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ForgotPage.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = email.getText().toString().trim();
                String Password = password.getText().toString().trim();

                if (Email.isEmpty()){
                    Toast.makeText(LoginPage.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Password.isEmpty()){
                    Toast.makeText(LoginPage.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                login.setEnabled(false);

                firebaseAuth.signInWithEmailAndPassword(Email, Password)
                        .addOnCompleteListener(LoginPage.this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                if (firebaseUser != null) {
                                    String uid = firebaseUser.getUid();
                                    String displayName = firebaseUser.getDisplayName();
                                    if (displayName == null || displayName.isEmpty()) {
                                        String initialName = Email.split("@")[0];
                                        displayName = initialName.substring(0, 1).toUpperCase() + initialName.substring(1);
                                    }
                                    String finalName = displayName;

                                    repository.syncFirebaseUser(uid, finalName, Email, localUser -> runOnUiThread(() -> {
                                        sessionManager.createLoginSession(localUser.getUserId(), localUser.getFullName(), localUser.getEmail());
                                        Toast.makeText(LoginPage.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(LoginPage.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }));
                                } else {
                                    login.setEnabled(true);
                                    Toast.makeText(LoginPage.this, "Login failed: User record empty", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                login.setEnabled(true);
                                String errorMsg = (task.getException() != null) ? task.getException().getMessage() : "Invalid Email or Password";
                                Toast.makeText(LoginPage.this, errorMsg, Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }
}