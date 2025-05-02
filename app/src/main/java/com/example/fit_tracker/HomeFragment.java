package com.example.fit_tracker;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.*;
import android.view.animation.*;
import android.widget.*;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.*;

public class HomeFragment extends Fragment implements SensorEventListener {

    private TextView tvSteps, tvCalories, tvWorkout, tvQuote, tvWelcome, tvProgress;
    private ImageView ivStepsIcon, ivCaloriesIcon, ivWorkoutIcon, ivSleep, ivWater, ivStretch;
    private ProgressBar progressBar;
    private CardView statsCard, quoteCard, watercard, sleepcard, stretchcard;
    private Button btnStartWorkout;
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private boolean isSensorPresent = false;
    private int previousTotalSteps = -1;

    private final int stepTarget = 8000;
    private final int calorieTarget = 300;
    private final int durationTarget = 30;

    private SharedPreferences stepPrefs, workoutPrefs;
    private String uid;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return view;
        uid = user.getUid();

        tvSteps = view.findViewById(R.id.tvSteps);
        tvCalories = view.findViewById(R.id.tvCalories);
        tvWorkout = view.findViewById(R.id.tvWorkout);
        tvQuote = view.findViewById(R.id.tvQuote);
        tvWelcome = view.findViewById(R.id.tvWelcome);
        tvProgress = view.findViewById(R.id.tvProgress);
        progressBar = view.findViewById(R.id.progressBar);
        statsCard = view.findViewById(R.id.statsCard);
        quoteCard = view.findViewById(R.id.quoteCard);
        btnStartWorkout = view.findViewById(R.id.btnStartWorkout);
        watercard = view.findViewById(R.id.cardwater);
        sleepcard = view.findViewById(R.id.cardsleep);
        stretchcard = view.findViewById(R.id.cardstrech);
        ivStepsIcon = view.findViewById(R.id.steps);
        ivCaloriesIcon = view.findViewById(R.id.calorie);
        ivWorkoutIcon = view.findViewById(R.id.workout);
        ivSleep = view.findViewById(R.id.sleep);
        ivWater = view.findViewById(R.id.water);
        ivStretch = view.findViewById(R.id.strech);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 101);
        }

        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        stepPrefs = requireContext().getSharedPreferences("StepPrefs_" + uid, MODE_PRIVATE);
        workoutPrefs = requireContext().getSharedPreferences("WorkoutData_" + uid, MODE_PRIVATE);

        String savedWorkoutDate = workoutPrefs.getString("date", "");
        if (!todayDate.equals(savedWorkoutDate)) {
            SharedPreferences.Editor editor = workoutPrefs.edit();
            editor.putInt("totalCalories", 0);
            editor.putLong("totalDuration", 0);
            editor.putString("date", todayDate);
            editor.apply();
        }

        int totalCalories = workoutPrefs.getInt("totalCalories", 0);
        long totalDuration = workoutPrefs.getLong("totalDuration", 0);
        int totalMinutes = (int) (totalDuration / 60);

        tvCalories.setText(String.valueOf(totalCalories));
        tvWorkout.setText(totalMinutes + " min");

        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        loadStepData();

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isSensorPresent = true;
        } else {
            Toast.makeText(getContext(), "Step Counter Sensor not available!", Toast.LENGTH_SHORT).show();
        }

        resetSteps();

        animateFadeIn(tvWelcome, 0);
        animateFadeIn(statsCard, 300);
        animateFadeIn(progressBar, 500);
        animateFadeIn(quoteCard, 700);
        animateFadeIn(tvQuote, 900);
        animateIconRotationWithDelay(ivStepsIcon, 300);
        animateIconRotationWithDelay(ivCaloriesIcon, 600);
        animateIconRotationWithDelay(ivWorkoutIcon, 900);
        animateIconRotationWithDelay(ivWater, 1000);
        animateIconRotationWithDelay(ivSleep, 1200);
        animateIconRotationWithDelay(ivStretch, 1400);

        Animation bounce = AnimationUtils.loadAnimation(requireContext(), R.anim.bounce);
        btnStartWorkout.setOnClickListener(v -> {
            v.startAnimation(bounce);
            startActivity(new Intent(getActivity(), WorkoutCategoryActivity.class));
        });

        watercard.setOnClickListener(v -> startActivity(new Intent(getActivity(), WaterActivity.class)));
        sleepcard.setOnClickListener(v -> startActivity(new Intent(getActivity(), SleepActivity.class)));
        stretchcard.setOnClickListener(v -> startActivity(new Intent(getActivity(), StretchActivity.class)));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isSensorPresent) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isSensorPresent) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int currentTotalSteps = (int) event.values[0];

            if (previousTotalSteps == -1) {
                previousTotalSteps = currentTotalSteps;
                saveSensorBase(currentTotalSteps);
            }

            int stepsSinceLastUpdate = currentTotalSteps - previousTotalSteps;
            if (stepsSinceLastUpdate < 0) stepsSinceLastUpdate = 0;

            tvSteps.setText(String.valueOf(stepsSinceLastUpdate));
            saveStepData(stepsSinceLastUpdate);
            saveStepsToFirestore(stepsSinceLastUpdate);

            int stepPercent = Math.min(100, (stepsSinceLastUpdate * 100) / stepTarget);
            progressBar.setProgress(stepPercent);
            tvProgress.setText(stepPercent + "% Completed");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void loadStepData() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String savedDate = stepPrefs.getString("date_" + uid, "");
        previousTotalSteps = stepPrefs.getInt("sensorBase_" + uid, -1);

        if (!today.equals(savedDate)) {
            previousTotalSteps = -1;
            SharedPreferences.Editor editor = stepPrefs.edit();
            editor.putString("date_" + uid, today);
            editor.remove("sensorBase_" + uid);
            editor.apply();
        }
    }

    private void saveSensorBase(int base) {
        SharedPreferences.Editor editor = stepPrefs.edit();
        editor.putInt("sensorBase_" + uid, base);
        editor.apply();
    }

    private void saveStepData(int steps) {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        SharedPreferences.Editor editor = stepPrefs.edit();
        editor.putInt("steps_" + uid, steps);
        editor.putString("date_" + uid, today);
        editor.apply();
    }

    private void resetSteps() {
        tvSteps.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Long press to reset steps", Toast.LENGTH_SHORT).show());

        tvSteps.setOnLongClickListener(v -> {
            previousTotalSteps = -1;  // force rebase
            tvSteps.setText("0");
            SharedPreferences.Editor editor = stepPrefs.edit();
            editor.remove("sensorBase_" + uid);
            editor.putInt("steps_" + uid, 0);
            editor.apply();
            return true;
        });
    }

    private void saveStepsToFirestore(int currentSteps) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        FirebaseFirestore.getInstance().collection("Users")
                .document(user.getUid())
                .collection("steps")
                .document(today)
                .set(new StepData(currentSteps, stepTarget));
    }

    private void animateFadeIn(View view, long delay) {
        AlphaAnimation fade = new AlphaAnimation(0.0f, 1.0f);
        fade.setDuration(500);
        fade.setStartOffset(delay);
        fade.setFillAfter(true);
        view.startAnimation(fade);
    }

    private void animateIconRotationWithDelay(ImageView imageView, long delayMillis) {
        new Handler().postDelayed(() -> {
            if (isAdded()) {
                Animation rotate = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate);
                imageView.startAnimation(rotate);
            }
        }, delayMillis);
    }
}
