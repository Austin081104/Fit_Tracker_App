package com.example.fit_tracker;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

public class StretchActivity extends AppCompatActivity {

    private List<Stretch> stretchList;
    private int index = 0;
    private boolean isBreak = false;

    private TextView stretchTitle, stretchDescription, timerText;
    private ImageView stretchImage;
    private Button startButton, nextButton, backButton;
    private CountDownTimer timer;
    private MediaPlayer mediaPlayer;

    private static final int STRETCH_DURATION = 30_000; // 30 seconds
    private static final int BREAK_DURATION = 10_000;   // 10 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stretch);

        // UI references
        stretchTitle = findViewById(R.id.stretchTitle);
        stretchDescription = findViewById(R.id.stretchDescription);
        stretchImage = findViewById(R.id.stretchImage);
        timerText = findViewById(R.id.timerText);
        startButton = findViewById(R.id.startButton);
        nextButton = findViewById(R.id.nextButton);
        backButton = findViewById(R.id.backButton);

        // Stretch data
        stretchList = Arrays.asList(
                new Stretch("Touch your toes and hold.", R.drawable.stretch1),
                new Stretch("Reach for the sky and hold.", R.drawable.stretch2),
                new Stretch("Stretch arms across chest.", R.drawable.stretch3),
                new Stretch("Neck stretch: tilt sideways.", R.drawable.stretch4),
                new Stretch("Hamstring stretch seated.", R.drawable.stretch5)
        );

        // Background music
        mediaPlayer = MediaPlayer.create(this, R.raw.stretch_music);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        // Start button
        startButton.setOnClickListener(v -> {
            startButton.setEnabled(false);
            timerText.setVisibility(TextView.VISIBLE);
            index = 0;
            isBreak = false;
            showNext();
        });

        // Next button
        nextButton.setOnClickListener(v -> {
            if (timer != null) timer.cancel();
            isBreak = false;
            index = Math.min(index + 1, stretchList.size() - 1);
            showNext();
        });

        // Back button
        backButton.setOnClickListener(v -> {
            if (timer != null) timer.cancel();
            isBreak = false;
            index = Math.max(index - 1, 0);
            showNext();
        });
    }

    private void showNext() {
        if (index >= stretchList.size()) {
            stretchDescription.setText("Well done! Stretching complete.");
            stretchImage.setImageResource(R.drawable.stretch1);
            timerText.setText("");
            startButton.setText("Restart");
            startButton.setEnabled(true);
            return;
        }

        if (isBreak) {
            stretchDescription.setText("Take a short break...");
            stretchImage.setImageResource(R.drawable.stretch1); // You can use a break image
            startTimer(BREAK_DURATION, "Break...");
        } else {
            Stretch current = stretchList.get(index);
            stretchDescription.setText(current.description);
            stretchImage.setImageResource(current.imageRes);
            startTimer(STRETCH_DURATION, "Stretching...");
        }
    }

    private void startTimer(long duration, String label) {
        timer = new CountDownTimer(duration, 1000) {
            long seconds = duration / 1000;

            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText(label + " " + seconds-- + "s");
            }

            @Override
            public void onFinish() {
                if (isBreak) {
                    index++;
                }
                isBreak = !isBreak;
                showNext();
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        if (timer != null) timer.cancel();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    // Stretch data model
    private static class Stretch {
        String description;
        int imageRes;

        Stretch(String desc, int img) {
            description = desc;
            imageRes = img;
        }
    }
}
