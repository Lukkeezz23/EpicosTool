package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DokumentyFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dokumenty, container, false);

        // Načtení tlačítka
        Button btnShowDocuments = view.findViewById(R.id.btnShowDocuments);

        // Klikací akce
        btnShowDocuments.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FilesActivity.class);
            intent.putExtra("AUTO_LOAD", true); // 🔹 Přidání informace o stisku tlačítka
            startActivity(intent);
        });

        return view;
    }
}

