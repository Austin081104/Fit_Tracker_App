package com.example.fit_tracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class WaterHistoryAdapter extends RecyclerView.Adapter<WaterHistoryAdapter.ViewHolder> {

    private Context context;
    private ArrayList<WaterEntry> waterList;

    public WaterHistoryAdapter(Context context, ArrayList<WaterEntry> waterList) {
        this.context = context;
        this.waterList = waterList;
    }

    @NonNull
    @Override
    public WaterHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_water_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WaterHistoryAdapter.ViewHolder holder, int position) {
        WaterEntry entry = waterList.get(position);
        holder.tvDate.setText("Date: " + entry.getDate());
        holder.tvAmount.setText("Amount: " + entry.getAmount() + " ml");
    }

    @Override
    public int getItemCount() {
        return waterList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvWaterDate);
            tvAmount = itemView.findViewById(R.id.tvWaterAmount);
        }
    }
}
