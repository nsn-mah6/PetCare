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
import com.example.petcare.data.entity.User;
import com.example.petcare.data.repository.PetCareRepository;
import com.example.petcare.utils.SessionManager;

public class LoginPage extends AppCompatActivity {
    EditText email, password;
    TextView register, forgotpassword;
    Button login;
    PetCareRepository repository;
    SessionManager sessionManager;

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

        // Check if user is already logged in
        if (sessionManager.isLoggedIn()) {
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
                Toast.makeText(LoginPage.this, "Password reset prototype: Use Room Database login.", Toast.LENGTH_SHORT).show();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = email.getText().toString().trim();
                String Password = password.getText().toString().trim();

                if (Email.isEmpty()){
                    Toast.makeText(LoginPage.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                }
                else if (Password.isEmpty()){
                    Toast.makeText(LoginPage.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                }
                else {
                    repository.login(Email, Password, new PetCareRepository.Callback<User>() {
                        @Override
                        public void onResult(User user) {
                            runOnUiThread(() -> {
                                if (user != null) {
                                    sessionManager.createLoginSession(user.getUserId(), user.getFullName(), user.getEmail());
                                    Toast.makeText(LoginPage.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginPage.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(LoginPage.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }
        });
    }
}