package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Button btnZapisy = view.findViewById(R.id.btnZapisy); // Zápis poruchy
        Button btnPrehledy = view.findViewById(R.id.btnPrehledy); // Přehled poruch

        btnZapisy.setOnClickListener( v -> {
            Intent intent = new Intent(getActivity(), ZapisPoruchyActivity.class);
            startActivity(intent);
        });

        btnPrehledy.setOnClickListener( v -> {
            Intent intent = new Intent(getActivity(), PrehledPoruchActivity.class);
            startActivity(intent);
        });

        return view;
    }
}

