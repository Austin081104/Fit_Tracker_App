package com.example.fit_tracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.*;

public class ProgressFragment extends Fragment {

    private View root;
    private String userId;
    private FirebaseFirestore db;

    private TextView stepsValue, caloriesValue, workoutValue, sleepValue, waterValue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_progress, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return root;  // Avoid crash if user is not logged in

        userId = user.getUid();
        db = FirebaseFirestore.getInstance();

        stepsValue = root.findViewById(R.id.stepsValue);
        caloriesValue = root.findViewById(R.id.caloriesValue);
        workoutValue = root.findViewById(R.id.workoutValue);
        sleepValue = root.findViewById(R.id.sleepValue);
        waterValue = root.findViewById(R.id.waterValue);

        if (stepsValue == null || caloriesValue == null || workoutValue == null ||
                sleepValue == null || waterValue == null || getActivity() == null) {
            return root;  // Prevent crash due to missing views or context
        }

        loadAllProgress();
        return root;
    }

    private void loadAllProgress() {
        loadWeeklySteps();
        loadWeeklySleep();
        loadWeeklyWater();
        loadWorkoutSummary();
        loadCaloriesSummary();
    }

    private void loadWeeklySteps() {
        CollectionReference ref = db.collection("Users").document(userId).collection("steps");
        fetchWeeklySum(ref, "steps", false, value -> stepsValue.setText(String.valueOf(value)));
    }

    private void loadWeeklySleep() {
        CollectionReference ref = db.collection("Users").document(userId).collection("SleepLogs");
        fetchWeeklySum(ref, "hours", true, value -> sleepValue.setText(value + " hrs"));
    }

    private void loadWeeklyWater() {
        CollectionReference ref = db.collection("Users").document(userId).collection("WaterLogs");
        fetchWeeklySum(ref, "amount", true, value -> {
            float liters = value / 1000f;
            waterValue.setText(String.format(Locale.getDefault(), "%.1f L", liters));
        });
    }



    private void loadWorkoutSummary() {
        if (getActivity() == null) return;
        SharedPreferences prefs = getActivity().getSharedPreferences("WorkoutData_" + userId, 0);
        long durationSec = prefs.getLong("totalDuration", 0);
        int totalMins = (int) (durationSec / 60);
        workoutValue.setText(totalMins + " min");
    }

    private void loadCaloriesSummary() {
        if (getActivity() == null) return;
        SharedPreferences prefs = getActivity().getSharedPreferences("WorkoutData_" + userId, 0);
        int calories = prefs.getInt("totalCalories", 0);
        caloriesValue.setText(String.valueOf(calories));
    }

    private void fetchWeeklySum(CollectionReference ref, String field, boolean useDateField, ValueCallback callback) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        List<String> last7Days = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            last7Days.add(sdf.format(cal.getTime()));
            cal.add(Calendar.DAY_OF_YEAR, -1);
        }

        ref.get().addOnSuccessListener(snapshot -> {
            float total = 0;
            for (QueryDocumentSnapshot doc : snapshot) {
                String dateKey = useDateField ? doc.getString("date") : doc.getId();
                if (dateKey != null && last7Days.contains(dateKey) && doc.contains(field)) {
                    Double value = doc.getDouble(field);
                    if (value != null) total += value;
                }
            }
            callback.onResult((int) total);
        }).addOnFailureListener(e -> callback.onResult(0));
    }




    interface ValueCallback {
        void onResult(int value);
    }
}
