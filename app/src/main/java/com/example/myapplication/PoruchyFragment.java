package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class PoruchyFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_poruchy, container, false);

        Button btn1 = view.findViewById(R.id.btn1);
        Button btn2 = view.findViewById(R.id.btn2);
        Button btn3 = view.findViewById(R.id.btn3);

        btn1.setOnClickListener(v -> Toast.makeText(getActivity(), "Poruchy - Akce 1", Toast.LENGTH_SHORT).show());
        btn2.setOnClickListener(v -> Toast.makeText(getActivity(), "Poruchy - Akce 2", Toast.LENGTH_SHORT).show());
        btn3.setOnClickListener(v -> Toast.makeText(getActivity(), "Poruchy - Akce 3", Toast.LENGTH_SHORT).show());

        return view;
    }
}
