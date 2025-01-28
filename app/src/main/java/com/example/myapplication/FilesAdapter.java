package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class FilesAdapter extends ArrayAdapter<String> {

    public FilesAdapter(Context context, List<String> fileNames) {
        super(context, 0, fileNames); // Předání seznamu názvů souborů
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        // Získání názvu souboru pro aktuální pozici
        String fileName = getItem(position);

        // Nastavení názvu souboru do TextView
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(fileName);

        return convertView;
    }
}
