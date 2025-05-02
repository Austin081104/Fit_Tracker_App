package com.example.fit_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {
    private EditText nameInput, emailInput, passwordInput, ageInput, weightInput, heightInput;
    private Button registerBtn;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        nameInput = findViewById(R.id.editTextName);
        emailInput = findViewById(R.id.editTextEmail);
        passwordInput = findViewById(R.id.editTextPassword);
        ageInput = findViewById(R.id.editTextAge);
        weightInput = findViewById(R.id.editTextWeight);
        heightInput = findViewById(R.id.editTextHeight);
        registerBtn = findViewById(R.id.buttonRegister);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        registerBtn.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String name = nameInput.getText().toString();
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String age = ageInput.getText().toString();
        String weight = weightInput.getText().toString();
        String height = heightInput.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email & Password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String userId = mAuth.getCurrentUser().getUid();

                    Map<String, Object> user = new HashMap<>();
                    user.put("name", name);
                    user.put("email", email);
                    user.put("age", age);
                    user.put("weight", weight);
                    user.put("height", height);

                    db.collection("Users").document(userId).set(user)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, Login.class));
                                finish();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_SHORT).show());

                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Registration Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}