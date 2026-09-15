package com.example.petcare.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.petcare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPage extends AppCompatActivity {
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_page);

        EditText email=findViewById(R.id.forgot_txt_email);
        TextView textView = findViewById(R.id.forgot_txt_login);
        Button fb_btn=findViewById(R.id.forgot_btn);
        firebaseAuth=FirebaseAuth.getInstance();

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginPage.class);
                startActivity(intent);
            }
        });

        fb_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = email.getText().toString().trim();

                if (Email.isEmpty()){
                    Toast.makeText(ForgotPage.this, "Enter Email", Toast.LENGTH_SHORT).show();
                }
                firebaseAuth.sendPasswordResetEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ForgotPage.this, "Password reset link has been sent to your email.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            }
        });
    }
}