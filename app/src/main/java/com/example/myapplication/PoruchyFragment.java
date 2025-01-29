package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class PoruchyFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_poruchy, container, false);

        Button btn1 = view.findViewById(R.id.btn1); // Zápis poruchy
        Button btn2 = view.findViewById(R.id.btn2); // Přehled poruch

        btn1.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ZapisPoruchyActivity.class);
            startActivity(intent);
        });

        btn2.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PrehledPoruchActivity.class);
            startActivity(intent);
        });

        return view;
    }
}

