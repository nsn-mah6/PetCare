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
import com.example.petcare.data.entity.User;
import com.example.petcare.data.repository.PetCareRepository;

public class RegistrationPage extends AppCompatActivity {
    EditText email, password, cpassword;
    TextView login;
    Button register;
    PetCareRepository repository;

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

                repository.getUserByEmail(Email, new PetCareRepository.Callback<User>() {
                    @Override
                    public void onResult(User existingUser) {
                        if (existingUser != null) {
                            runOnUiThread(() -> Toast.makeText(RegistrationPage.this, "User already exists with this email", Toast.LENGTH_SHORT).show());
                        } else {
                            String fullName = Email.split("@")[0]; // Use prefix as Name for prototype
                            fullName = fullName.substring(0, 1).toUpperCase() + fullName.substring(1);
                            User newUser = new User(fullName, Email, Password);
                            repository.insertUser(newUser, new PetCareRepository.Callback<Long>() {
                                @Override
                                public void onResult(Long newId) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(RegistrationPage.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegistrationPage.this, LoginPage.class);
                                        startActivity(intent);
                                        finish();
                                    });
                                }
                            });
                        }
                    }
                });
            }
        });
    }
}