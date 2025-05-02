// WorkoutCategoryActivity.java
package com.example.fit_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;
import androidx.appcompat.app.AppCompatActivity;

public class WorkoutCategoryActivity extends AppCompatActivity {

    GridView gridView;
    String[] categories = {"Chest","Back","Shoulder", "Biceps", "Legs","Triceps"};
    int[] images = {R.drawable.chest,R.drawable.back,R.drawable.shoulder, R.drawable.biceps, R.drawable.leg,R.drawable.tricep};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_category);

        gridView = findViewById(R.id.gridView);

        CategoryAdapter adapter = new CategoryAdapter(this, categories, images);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(this, StartWorkoutActivity.class);
            intent.putExtra("category", categories[i]);
            startActivity(intent);
        });
    }
}
