package com.example.fit_tracker;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class WaterActivity extends AppCompatActivity {

    private EditText etWaterAmount;
    private Button btnAddWater;
    private TextView tvGoal;
    private ProgressBar progressBar;
    private RecyclerView recyclerViewWater;
    private BarChart barChart;

    private FirebaseFirestore db;
    private CollectionReference waterRef;
    private String userId;

    private ArrayList<WaterEntry> waterList;
    private WaterAdapter waterAdapter;
    private final int dailyGoal = 6000;
    private int totalToday = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water);

        etWaterAmount = findViewById(R.id.etWaterAmount);
        btnAddWater = findViewById(R.id.btnAddWater);
        tvGoal = findViewById(R.id.tvGoal);
        progressBar = findViewById(R.id.progressBar);
        recyclerViewWater = findViewById(R.id.recyclerViewWater);
        barChart = findViewById(R.id.barChartWater);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        waterRef = db.collection("Users").document(userId).collection("WaterLogs");

        waterList = new ArrayList<>();
        waterAdapter = new WaterAdapter(this, waterList);
        recyclerViewWater.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewWater.setAdapter(waterAdapter);

        btnAddWater.setOnClickListener(v -> addWater());

        fetchWaterData();
    }

    private void addWater() {
        String amountStr = etWaterAmount.getText().toString();
        if (TextUtils.isEmpty(amountStr)) {
            etWaterAmount.setError("Enter water amount");
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        if (totalToday + amount > dailyGoal) {
            int allowed = dailyGoal - totalToday;
            Toast.makeText(this, "Daily limit exceeded! You can only add " + allowed + "ml more today.", Toast.LENGTH_LONG).show();
            return;
        }

        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String id = waterRef.document().getId();
        WaterEntry entry = new WaterEntry(id, userId, currentDate, amount);

        progressBar.setVisibility(View.VISIBLE);
        waterRef.document(id).set(entry).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                Toast.makeText(this, "Water data added", Toast.LENGTH_SHORT).show();
                etWaterAmount.setText("");
                fetchWaterData();
            } else {
                Toast.makeText(this, "Failed to add data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchWaterData() {
        progressBar.setVisibility(View.VISIBLE);
        waterList.clear();
        totalToday = 0;
        progressBar.setProgress(totalToday);
        progressBar.setMax(dailyGoal);

        waterRef.get().addOnCompleteListener(task -> {
            progressBar.setVisibility(View.VISIBLE);
            if (task.isSuccessful()) {
                String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    WaterEntry entry = doc.toObject(WaterEntry.class);
                    waterList.add(entry);
                    if (entry.getDate().equals(today)) {
                        totalToday += entry.getAmount();
                    }
                }

                waterAdapter.notifyDataSetChanged();

                progressBar.setProgress(totalToday);
                tvGoal.setText("Today's Intake: " + totalToday + " / " + dailyGoal + " ml");

                showBarChart(waterList);
            } else {
                Toast.makeText(this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showBarChart(List<WaterEntry> entries) {
        Map<String, Integer> dailyTotals = new TreeMap<>();

        for (int i = 6; i >= 0; i--) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -i);
            String date = new SimpleDateFormat("MM-dd", Locale.getDefault()).format(cal.getTime());
            dailyTotals.put(date, 0);
        }

        for (WaterEntry entry : entries) {
            try {
                String entryDate = entry.getDate();
                if (entryDate != null && !entryDate.isEmpty()) {
                    SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date parsedDate = originalFormat.parse(entryDate);
                    if (parsedDate != null) {
                        String label = new SimpleDateFormat("MM-dd", Locale.getDefault()).format(parsedDate);
                        if (dailyTotals.containsKey(label)) {
                            int total = (int) (dailyTotals.get(label) + entry.getAmount());
                            dailyTotals.put(label, total);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        int index = 0;

        for (Map.Entry<String, Integer> item : dailyTotals.entrySet()) {
            barEntries.add(new BarEntry(index, item.getValue()));
            labels.add(item.getKey());
            index++;
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Water Intake (ml)");
        barDataSet.setColor(Color.parseColor("#2196F3"));
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(12f);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.9f);

        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return labels.get((int) value % labels.size());
            }
        });
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setGranularity(250f);
        yAxis.setAxisMinimum(0f);

        barChart.setFitBars(true);
        barChart.invalidate();
    }
}
