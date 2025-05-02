package com.example.fit_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    private EditText emailInput, passwordInput;
    private Button loginBtn;
    private FirebaseAuth mAuth;
    private TextView registerLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailInput = findViewById(R.id.editTextEmail);
        passwordInput = findViewById(R.id.editTextPassword);
        loginBtn = findViewById(R.id.buttonLogin);
        registerLink = findViewById(R.id.textViewRegisterLink);

        mAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(v -> loginUser());

        registerLink.setOnClickListener(v ->
                startActivity(new Intent(this, Registration.class)));
    }

    private void loginUser() {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email & Password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Login Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}