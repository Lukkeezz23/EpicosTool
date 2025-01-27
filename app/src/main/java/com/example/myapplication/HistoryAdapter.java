package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private final List<List<Object>> data;

    public HistoryAdapter(List<List<Object>> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        // Každý řádek z tabulky Google Sheets
        List<Object> row = data.get(position);

        // První řádek: Datum / Směna / Seřizovač
        String date = row.size() > 0 ? row.get(0).toString() : "N/A";
        String shift = row.size() > 0 ? row.get(1).toString() : "N/A";
        String operator = row.size() > 0 ? row.get(3).toString() : "N/A";
        holder.textDateShiftOperator.setText(String.format("%s / %s / %s", date, shift, operator));

        // Druhý řádek: Palet: x / NOK: x
        String palet = row.size() > 3 ? row.get(3).toString() : "0";
        String nok = row.size() > 4 ? row.get(4).toString() : "0";
        holder.textPaletNok.setText(String.format("Palet: %s / NOK: %s", palet, nok));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void updateData(List<List<Object>> newData) {
        data.clear();
        data.addAll(newData);
        notifyDataSetChanged();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView textDateShiftOperator;
        TextView textPaletNok;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textDateShiftOperator = itemView.findViewById(R.id.textDateShiftOperator);
            textPaletNok = itemView.findViewById(R.id.textPaletNok);
        }
    }
}
