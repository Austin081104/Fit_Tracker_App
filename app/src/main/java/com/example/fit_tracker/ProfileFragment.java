package com.example.fit_tracker;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class    ProfileFragment extends Fragment {

    private TextView textViewName, textViewEmail, textViewAge, textViewWeight, textViewHeight;
    private ImageView imageViewProfile;
    private FirebaseFirestore db;
    private FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textViewName = view.findViewById(R.id.textViewName);
        textViewEmail = view.findViewById(R.id.textViewEmail);
        textViewAge = view.findViewById(R.id.textViewAge);
        textViewWeight = view.findViewById(R.id.textViewWeight);
        textViewHeight = view.findViewById(R.id.textViewHeight);
        imageViewProfile = view.findViewById(R.id.imageViewProfile);
        Button btnEdit = view.findViewById(R.id.btnEditProfile);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if (user != null) {
            db.collection("Users").document(user.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            textViewName.setText(documentSnapshot.getString("name"));
                            textViewEmail.setText("Email: " + documentSnapshot.getString("email"));
                            textViewAge.setText("Age: " + documentSnapshot.getString("age"));
                            textViewWeight.setText("Weight: " + documentSnapshot.getString("weight") + " kg");
                            textViewHeight.setText("Height: " + documentSnapshot.getString("height") + " cm");
                        }
                    });
        }

        btnEdit.setOnClickListener(v -> showEditDialog());
        Button btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // clears backstack
            startActivity(intent);
        });

    }

    private void showEditDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_edit_profile, null);

        EditText editName = dialogView.findViewById(R.id.editName);
        EditText editAge = dialogView.findViewById(R.id.editAge);
        EditText editWeight = dialogView.findViewById(R.id.editWeight);
        EditText editHeight = dialogView.findViewById(R.id.editHeight);

        editName.setText(textViewName.getText().toString());
        editAge.setText(textViewAge.getText().toString().replace("Age: ", ""));
        editWeight.setText(textViewWeight.getText().toString().replace("Weight: ", "").replace(" kg", ""));
        editHeight.setText(textViewHeight.getText().toString().replace("Height: ", "").replace(" cm", ""));

        new AlertDialog.Builder(getContext())
                .setTitle("Edit Profile")
                .setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    String name = editName.getText().toString();
                    String age = editAge.getText().toString();
                    String weight = editWeight.getText().toString();
                    String height = editHeight.getText().toString();

                    updateProfile(name, age, weight, height);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateProfile(String name, String age, String weight, String height) {
        if (user == null) return;

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("name", name);
        updatedData.put("age", age);
        updatedData.put("weight", weight);
        updatedData.put("height", height);

        db.collection("Users").document(user.getUid()).update(updatedData)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                    textViewName.setText(name);
                    textViewAge.setText("Age: " + age);
                    textViewWeight.setText(" Weight: " + weight + " kg");
                    textViewHeight.setText("Height: " + height + " cm");
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
