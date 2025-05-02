package com.example.fit_tracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.*;

public class StartWorkoutActivity extends AppCompatActivity {

    TextView workoutName, timerText, caloriesText, setsText, durationText;
    ImageView workoutImage;
    Button pauseResumeBtn, completeBtn, prevBtn, nextBtn, finishEarlyBtn, saveWorkoutBtn, startWorkoutBtn;
    CountDownTimer timer;
    int calories = 0, sets = 0, totalSets = 0;
    boolean isPaused = false, onBreak = false, workoutCompleted = false, workoutStarted = false;
    long timeRemaining = 60000, totalWorkoutDuration = 0, sessionStartTime = 0;
    List<Workout> workoutList = new ArrayList<>();
    int currentIndex = 0;
    String category = "";

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String uid;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_workout);

        // Enable the back button in the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Workout in Progress");
        }

        // UI
        workoutName = findViewById(R.id.workoutName);
        timerText = findViewById(R.id.timerText);
        caloriesText = findViewById(R.id.caloriesText);
        setsText = findViewById(R.id.setsText);
        workoutImage = findViewById(R.id.workoutImage);
        pauseResumeBtn = findViewById(R.id.pauseResumeBtn);
        completeBtn = findViewById(R.id.completeBtn);
        prevBtn = findViewById(R.id.prevBtn);
        nextBtn = findViewById(R.id.nextBtn);
        finishEarlyBtn = findViewById(R.id.finishEarlyBtn);
        durationText = findViewById(R.id.durationText);
        saveWorkoutBtn = findViewById(R.id.saveWorkoutBtn);
        startWorkoutBtn = findViewById(R.id.startWorkoutBtn);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        sharedPreferences = getSharedPreferences("WorkoutData_" + uid, MODE_PRIVATE);

        setupWorkoutList();

        // Initial visibility
        saveWorkoutBtn.setVisibility(View.GONE);
        pauseResumeBtn.setVisibility(View.GONE);
        completeBtn.setVisibility(View.GONE);
        prevBtn.setVisibility(View.GONE);
        nextBtn.setVisibility(View.GONE);
        finishEarlyBtn.setVisibility(View.GONE);

        startWorkoutBtn.setOnClickListener(v -> {
            startWorkoutBtn.setVisibility(View.GONE);
            pauseResumeBtn.setVisibility(View.VISIBLE);
            completeBtn.setVisibility(View.VISIBLE);
            prevBtn.setVisibility(View.VISIBLE);
            nextBtn.setVisibility(View.VISIBLE);
            finishEarlyBtn.setVisibility(View.VISIBLE);
            sessionStartTime = System.currentTimeMillis();
            workoutStarted = true;
            startWorkout();
        });

        pauseResumeBtn.setOnClickListener(v -> {
            if (!workoutStarted || onBreak) return;
            if (isPaused) {
                startTimer(timeRemaining);
                pauseResumeBtn.setText("Pause");
                isPaused = false;
            } else {
                if (timer != null) timer.cancel();
                pauseResumeBtn.setText("Resume");
                isPaused = true;
            }
        });

        completeBtn.setOnClickListener(v -> {
            if (onBreak) {
                Toast.makeText(this, "Currently on break.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (sets < 5) {
                calories += 15;
                sets++;
                totalSets++;
                updateStats();
                if (sets >= 5) {
                    Toast.makeText(this, "All sets completed!", Toast.LENGTH_SHORT).show();
                    completeBtn.setEnabled(false);
                } else {
                    workoutCompleted = true;
                    startBreak();
                }
            } else {
                Toast.makeText(this, "Max 5 sets allowed", Toast.LENGTH_SHORT).show();
                completeBtn.setEnabled(false);
            }
        });

        prevBtn.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                sets = 0;
                workoutCompleted = false;
                startWorkout();
            }
        });

        nextBtn.setOnClickListener(v -> {
            if (currentIndex < workoutList.size() - 1) {
                currentIndex++;
                sets = 0;
                workoutCompleted = false;
                startWorkout();
            }
        });

        finishEarlyBtn.setOnClickListener(v -> endWorkout());

        saveWorkoutBtn.setOnClickListener(v -> {
            saveWorkoutData();
        });
    }

    // Handle ActionBar back button
    @Override
    public boolean onSupportNavigateUp() {
        finish(); // Close activity and return to previous
        return true;
    }

    private void setupWorkoutList() {
        category = getIntent().getStringExtra("category");
        if (category == null) return;

        switch (category) {
            case "Chest":
                workoutList = Arrays.asList(
                        new Workout("Push Ups", "Standard push-ups", R.drawable.pushup),
                        new Workout("Incline Push", "Incline variation", R.drawable.inclinepushup),
                        new Workout("Decline Push", "Push-ups with feet elevated", R.drawable.declinepushup),
                        new Workout("Chest Press", "Using dumbbells", R.drawable.chestpress),
                        new Workout("Chest Fly", "Open/close arms with dumbbells", R.drawable.chestfly));
                break;

            case "Biceps":
                workoutList = Arrays.asList(
                        new Workout("Bicep Curls", "Lift dumbbells alternately", R.drawable.bicepcurls),
                        new Workout("Hammer Curls", "Palms face inward", R.drawable.hammercurls),
                        new Workout("Concentration Curls", "Seated curl", R.drawable.concentrationcurls),
                        new Workout("Barbell Curls", "Use barbell", R.drawable.barbellcurl),
                        new Workout("Cable Curls", "Cable machine reps", R.drawable.cablecurl));
                break;

            case "Back":
                workoutList = Arrays.asList(
                        new Workout("Pull-Ups", "Bodyweight exercise for lats", R.drawable.pullup),
                        new Workout("Deadlifts", "Barbell lift for overall back", R.drawable.deadlift),
                        new Workout("Bent-Over Rows", "Dumbbell or barbell rows", R.drawable.bentover),
                        new Workout("Lat Pulldown", "Cable machine targeting lats", R.drawable.latpull),
                        new Workout("Seated Cable Rows", "Rowing motion with cable", R.drawable.seated));
                break;

            case "Shoulder":
                workoutList = Arrays.asList(
                        new Workout("Overhead Press", "Dumbbell or barbell shoulder press", R.drawable.preeup),
                        new Workout("Lateral Raises", "Side raise to target delts", R.drawable.lateralraise),
                        new Workout("Front Raises", "Raise weights in front", R.drawable.frontraises),
                        new Workout("Reverse Flys", "Target rear delts", R.drawable.revrese),
                        new Workout("Arnold Press", "Rotating dumbbell press", R.drawable.arnoldpress));
                break;

            case "Legs":
                workoutList = Arrays.asList(
                        new Workout("Squats", "Bodyweight or dumbbell squats", R.drawable.squats),
                        new Workout("Lunges", "Forward or backward lunges", R.drawable.lunges),
                        new Workout("Calf Raises", "Stand on toes and raise heels", R.drawable.calfraises),
                        new Workout("Wall Sit", "Hold seated position", R.drawable.wallsit),
                        new Workout("Glute Bridges", "Bridge for glutes", R.drawable.glutebridges));
                break;

            case "Triceps":
                workoutList = Arrays.asList(
                        new Workout("Tricep Dips", "Use chair or bench", R.drawable.tricepdips),
                        new Workout("Overhead Tricep Extension", "Dumbbell overhead", R.drawable.overheadtri),
                        new Workout("Close-Grip Pushups", "Hands close", R.drawable.close),
                        new Workout("Tricep Kickbacks", "With dumbbells", R.drawable.kickbacks),
                        new Workout("Diamond Pushups", "Hands form diamond", R.drawable.diamondpushups));
                break;
        }
    }

    private void startWorkout() {
        if (timer != null) timer.cancel();
        isPaused = false;
        pauseResumeBtn.setText("Pause");
        workoutCompleted = false;
        completeBtn.setEnabled(true);
        sets = 0;
        updateStats();

        if (currentIndex < workoutList.size()) {
            Workout current = workoutList.get(currentIndex);
            workoutName.setText(current.getName());
            workoutImage.setImageResource(current.getImageRes());
            timeRemaining = 60000;
            onBreak = false;
            startTimer(timeRemaining);
        } else {
            endWorkout();
        }
    }

    private void startTimer(long duration) {
        timer = new CountDownTimer(duration, 1000) {
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
                timerText.setText("Time: " + millisUntilFinished / 1000 + "s");
            }

            public void onFinish() {
                if (!onBreak) {
                    if (sets < 5) {
                        workoutName.setText("Time's up! Tap Complete.");
                        pauseResumeBtn.setVisibility(View.GONE);
                        completeBtn.setEnabled(true);
                    } else {
                        workoutName.setText("All sets done! Tap Next.");
                        completeBtn.setEnabled(false);
                        pauseResumeBtn.setVisibility(View.GONE);
                    }
                } else {
                    onBreak = false;
                    workoutName.setText("Resuming...");
                    startWorkout();
                }
            }
        }.start();
    }

    private void startBreak() {
        if (timer != null) timer.cancel();
        isPaused = false;
        pauseResumeBtn.setText("Pause");
        onBreak = true;
        workoutName.setText("Break Time! 🛌");
        timeRemaining = 60000;
        workoutCompleted = false;
        workoutImage.setImageResource(R.drawable.breakimg);
        startTimer(timeRemaining);
    }

    private void updateStats() {
        caloriesText.setText("Calories: " + calories);
        setsText.setText("Sets: " + totalSets);
    }

    private void endWorkout() {
        if (timer != null) timer.cancel();

        long endTime = System.currentTimeMillis();
        totalWorkoutDuration = (endTime - sessionStartTime) / 1000;

        workoutName.setText("Workout Complete! 🏅");
        timerText.setVisibility(View.GONE);
        pauseResumeBtn.setVisibility(View.GONE);
        completeBtn.setVisibility(View.GONE);
        prevBtn.setVisibility(View.GONE);
        nextBtn.setVisibility(View.GONE);
        finishEarlyBtn.setVisibility(View.GONE);
        durationText.setText("Duration: " + totalWorkoutDuration + " sec");
        durationText.setVisibility(View.VISIBLE);
        saveWorkoutBtn.setVisibility(View.VISIBLE);

        int prevCalories = sharedPreferences.getInt("totalCalories", 0);
        long prevDuration = sharedPreferences.getLong("totalDuration", 0);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("totalCalories", prevCalories + calories);
        editor.putLong("totalDuration", prevDuration + totalWorkoutDuration);
        editor.putString("date", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        editor.apply();

        Toast.makeText(this, "Saved " + calories + " cal, " + totalWorkoutDuration + " sec", Toast.LENGTH_LONG).show();
    }

    private void saveWorkoutData() {
        if (sets == 0) {
            Toast.makeText(this, "Complete at least one set.", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference userRef = db.collection("Users").document(uid);
        String workoutTitle = currentIndex < workoutList.size() ? workoutList.get(currentIndex).getName() : "Workout";

        Map<String, Object> workoutDetails = new HashMap<>();
        workoutDetails.put("name", workoutTitle);
        workoutDetails.put("bodyPart", category);
        workoutDetails.put("totalSets", totalSets);
        workoutDetails.put("calories", calories);
        workoutDetails.put("duration", totalWorkoutDuration);
        workoutDetails.put("completedAt", System.currentTimeMillis());

        userRef.collection("Workouts").add(workoutDetails)
                .addOnSuccessListener(doc -> Toast.makeText(this, "Workout saved", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show());
    }
}
