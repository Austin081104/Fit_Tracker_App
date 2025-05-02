package com.example.fit_tracker;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SleepHistoryAdapter extends RecyclerView.Adapter<SleepHistoryAdapter.SleepViewHolder> {

    private Context context;
    private ArrayList<SleepEntry> sleepList;
    private FirebaseFirestore db;
    private String userId;

    // New: callback interface to refresh sleep data
    public interface OnSleepChangedListener {
        void onSleepChanged();
    }

    private OnSleepChangedListener listener;

    public SleepHistoryAdapter(Context context, ArrayList<SleepEntry> sleepList, OnSleepChangedListener listener) {
        this.context = context;
        this.sleepList = sleepList;
        this.listener = listener;
        this.db = FirebaseFirestore.getInstance();
        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public SleepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sleep_entry, parent, false);
        return new SleepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SleepViewHolder holder, int position) {
        SleepEntry entry = sleepList.get(position);
        holder.tvDate.setText(entry.getDate());
        holder.tvHours.setText(String.format("%.1f hrs", entry.getHours()));

        holder.btnEdit.setOnClickListener(v -> showEditDialog(entry));
        holder.btnDelete.setOnClickListener(v -> deleteEntry(entry));
    }

    @Override
    public int getItemCount() {
        return sleepList.size();
    }

    class SleepViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvHours;
        Button btnEdit, btnDelete;

        public SleepViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvHours = itemView.findViewById(R.id.tvSleepHours);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    private void showEditDialog(SleepEntry entry) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_sleep, null);
        EditText etEditHours = view.findViewById(R.id.etEditHours);
        etEditHours.setText(String.valueOf(entry.getHours()));

        new AlertDialog.Builder(context)
                .setTitle("Edit Hours")
                .setView(view)
                .setPositiveButton("Update", (dialog, which) -> {
                    String newHoursStr = etEditHours.getText().toString();
                    if (!newHoursStr.isEmpty()) {
                        try {
                            double newHours = Double.parseDouble(newHoursStr);
                            double totalHours = 0;
                            for (SleepEntry e : sleepList) {
                                if (e.getDate().equals(entry.getDate()) && !e.getId().equals(entry.getId())) {
                                    totalHours += e.getHours();
                                }
                            }
                            if (totalHours + newHours > 24) {
                                Toast.makeText(context, "Total sleep hours cannot exceed 24 in a day", Toast.LENGTH_LONG).show();
                                return;
                            }

                            entry.setHours(newHours);
                            db.collection("Users").document(userId).collection("SleepLogs")
                                    .document(entry.getId()).set(entry)
                                    .addOnSuccessListener(aVoid -> {
                                        notifyDataSetChanged();
                                        Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show();
                                        listener.onSleepChanged(); // Refresh totals
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show());
                        } catch (NumberFormatException e) {
                            Toast.makeText(context, "Invalid input", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteEntry(SleepEntry entry) {
        db.collection("Users").document(userId).collection("SleepLogs")
                .document(entry.getId()).delete()
                .addOnSuccessListener(aVoid -> {
                    sleepList.remove(entry);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                    listener.onSleepChanged(); // Refresh totals
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show());
    }
}
