package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class VyrobaFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vyroba, container, false);

        // Načtení tlačítek správným způsobem
        Button btnCreate = view.findViewById(R.id.btnCreate);
        Button btnHistory = view.findViewById(R.id.btnHistory);

        btnCreate.setOnClickListener ( v -> {
            Toast.makeText(getActivity(), "Vytvořit kliknuto", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), FormActivity.class);
            startActivity(intent);
        } );

        btnHistory.setOnClickListener ( v -> {
            Toast.makeText(getActivity(), "Vytvořit kliknuto", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), HistoryActivity.class);
            startActivity(intent);
        } );
        return view;
    }
}
