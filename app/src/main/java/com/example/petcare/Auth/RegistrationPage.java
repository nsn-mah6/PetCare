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

import com.example.petcare.R;
import com.example.petcare.data.repository.PetCareRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegistrationPage extends AppCompatActivity {
    EditText email, password, cpassword;
    TextView login;
    Button register;
    PetCareRepository repository;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration_page);

        email = findViewById(R.id.register_txt_email);
        password = findViewById(R.id.register_txt_password);
        cpassword = findViewById(R.id.register_txt_Cpassword);
        register = findViewById(R.id.register_btn);
        login = findViewById(R.id.register_txt_login);

        repository = new PetCareRepository(this);
        firebaseAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginPage.class);
                startActivity(intent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = email.getText().toString().trim();
                String Password = password.getText().toString().trim();
                String CPassword = cpassword.getText().toString().trim();

                if (Email.isEmpty()){
                    Toast.makeText(RegistrationPage.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Password.isEmpty()){
                    Toast.makeText(RegistrationPage.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Password.length() < 6){
                    Toast.makeText(RegistrationPage.this, "Password too Short (min 6 characters)", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Password.equals(CPassword)){
                    Toast.makeText(RegistrationPage.this, "Passwords do not Match", Toast.LENGTH_SHORT).show();
                    return;
                }

                register.setEnabled(false);

                String initialName = Email.split("@")[0];
                final String fullName = initialName.substring(0, 1).toUpperCase() + initialName.substring(1);

                firebaseAuth.createUserWithEmailAndPassword(Email, Password)
                        .addOnCompleteListener(RegistrationPage.this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                if (firebaseUser != null) {
                                    String uid = firebaseUser.getUid();

                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(fullName)
                                            .build();
                                    firebaseUser.updateProfile(profileUpdates);

                                    // Save in Firebase Database
                                    repository.saveUserToFirebaseDatabase(uid, fullName, Email);

                                    // Sync in Room DB for local relations
                                    repository.syncFirebaseUser(uid, fullName, Email, localUser -> runOnUiThread(() -> {
                                        Toast.makeText(RegistrationPage.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegistrationPage.this, LoginPage.class);
                                        startActivity(intent);
                                        finish();
                                    }));
                                } else {
                                    register.setEnabled(true);
                                    Toast.makeText(RegistrationPage.this, "Registration failed: User missing", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                register.setEnabled(true);
                                String errorMsg = (task.getException() != null) ? task.getException().getMessage() : "Registration Failed";
                                Toast.makeText(RegistrationPage.this, errorMsg, Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }
}