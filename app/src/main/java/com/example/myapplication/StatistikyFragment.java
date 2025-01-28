package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class StatistikyFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistiky, container, false);

        // Připojení tlačítek
        Button btnExport = view.findViewById(R.id.btnExport);
        Button btnCharts = view.findViewById(R.id.btnCharts);
        Button btnAnalysis = view.findViewById(R.id.btnAnalysis);

        // Kliknutí na tlačítko Export
        btnExport.setOnClickListener(v -> {
            // TODO: Implementace exportu dat
            Toast.makeText(getContext(), "Export probíhá...", Toast.LENGTH_SHORT).show();
        });

        // Kliknutí na tlačítko Grafy
        btnCharts.setOnClickListener(v -> {
            // TODO: Otevření grafického zobrazení
            Toast.makeText(getContext(), "Otevírám grafy...", Toast.LENGTH_SHORT).show();
        });

        // Kliknutí na tlačítko Analýzy
        btnAnalysis.setOnClickListener(v -> {
            // TODO: Otevření analytického pohledu
            Toast.makeText(getContext(), "Spouštím analýzu...", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
