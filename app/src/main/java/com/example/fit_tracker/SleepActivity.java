package com.example.fit_tracker;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class SleepActivity extends AppCompatActivity {

    private EditText etSleepHours;
    private Button btnAddSleep;
    private TextView tvAverageSleep, tvSleepProgress;
    private ProgressBar progressBarSleep;
    private RecyclerView recyclerViewSleep;

    private FirebaseFirestore db;
    private CollectionReference sleepRef;

    private ArrayList<SleepEntry> sleepList;
    private SleepHistoryAdapter sleepAdapter;

    private String userId;
    private static final double SLEEP_GOAL_HOURS = 8.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);

        etSleepHours = findViewById(R.id.etSleepHours);
        btnAddSleep = findViewById(R.id.btnAddSleep);
        tvAverageSleep = findViewById(R.id.tvAverageSleep);
        tvSleepProgress = findViewById(R.id.tvSleepProgress); // TextView for % progress
        progressBarSleep = findViewById(R.id.progressBarSleep);
        recyclerViewSleep = findViewById(R.id.recyclerViewSleep);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        sleepRef = db.collection("Users").document(userId).collection("SleepLogs");

        sleepList = new ArrayList<>();
        sleepAdapter = new SleepHistoryAdapter(this, sleepList, this::fetchSleepData);

        recyclerViewSleep.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSleep.setAdapter(sleepAdapter);

        btnAddSleep.setOnClickListener(v -> addSleep());

        fetchSleepData();
    }

    private void addSleep() {
        String hoursStr = etSleepHours.getText().toString();
        if (TextUtils.isEmpty(hoursStr)) {
            etSleepHours.setError("Enter hours");
            return;
        }

        double enteredHours;
        try {
            enteredHours = Double.parseDouble(hoursStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
            return;
        }

        if (enteredHours > 24) {
            Toast.makeText(this, "Sleep hours cannot exceed 24", Toast.LENGTH_LONG).show();
            return;
        }

        double totalHoursToday = 0;
        String currentDate = getTodayDate();
        for (SleepEntry entry : sleepList) {
            if (entry.getDate().equals(currentDate)) {
                totalHoursToday += entry.getHours();
            }
        }

        if (totalHoursToday + enteredHours > 24) {
            Toast.makeText(this, "Total sleep hours cannot exceed 24 in a day", Toast.LENGTH_LONG).show();
            return;
        }

        String id = sleepRef.document().getId();
        SleepEntry entry = new SleepEntry(id, userId, currentDate, enteredHours);

        progressBarSleep.setVisibility(View.VISIBLE);
        sleepRef.document(id).set(entry).addOnCompleteListener(task -> {
            progressBarSleep.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                Toast.makeText(SleepActivity.this, "Sleep data added", Toast.LENGTH_SHORT).show();
                etSleepHours.setText("");
                fetchSleepData();
            } else {
                Toast.makeText(SleepActivity.this, "Failed to add data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchSleepData() {
        progressBarSleep.setVisibility(View.VISIBLE);
        sleepList.clear();
        sleepRef.get().addOnCompleteListener(task -> {
            progressBarSleep.setVisibility(View.VISIBLE);
            if (task.isSuccessful()) {
                double todayTotal = 0;
                String today = getTodayDate();

                for (QueryDocumentSnapshot doc : task.getResult()) {
                    SleepEntry entry = doc.toObject(SleepEntry.class);
                    sleepList.add(entry);

                    if (entry.getDate().equals(today)) {
                        todayTotal += entry.getHours();
                    }
                }

                sleepAdapter.notifyDataSetChanged();

                // Show total for today
                tvAverageSleep.setText("Today: " + String.format(Locale.getDefault(), "%.1f", todayTotal) + " hrs");

                int progress = (int) ((todayTotal / SLEEP_GOAL_HOURS) * 100);
                progressBarSleep.setProgress(Math.min(progress, 100));
                tvSleepProgress.setText(Math.min(progress, 100) + "% Completed");

            } else {
                Toast.makeText(this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteSleepEntry(SleepEntry entry) {
        sleepRef.document(entry.getId()).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Sleep entry deleted", Toast.LENGTH_SHORT).show();
                    fetchSleepData();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show());
    }

    private String getTodayDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    public ArrayList<SleepEntry> getSleepList() {
        return sleepList;
    }
}
