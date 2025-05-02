package com.example.fit_tracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class WaterAdapter extends RecyclerView.Adapter<WaterAdapter.WaterViewHolder> {

    private Context context;
    private ArrayList<WaterEntry> waterList;

    public WaterAdapter(Context context, ArrayList<WaterEntry> waterList) {
        this.context = context;
        this.waterList = waterList;
    }

    @NonNull
    @Override
    public WaterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_water_entry, parent, false);
        return new WaterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WaterViewHolder holder, int position) {
        WaterEntry entry = waterList.get(position);
        holder.tvDate.setText(entry.getDate());
        holder.tvAmount.setText(String.format("%.0f ml", entry.getAmount()));
    }

    @Override
    public int getItemCount() {
        return waterList.size();
    }

    public static class WaterViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvAmount;

        public WaterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvWaterDate);
            tvAmount = itemView.findViewById(R.id.tvWaterAmount);
        }
    }
}
