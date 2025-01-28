package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {

    Button btnCreate;
    Button btnHistorie;
    Button btnExit;

    Button btnPorucha;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );

        sharedPreferences = getSharedPreferences("theme_prefs", MODE_PRIVATE);
        boolean isDarkTheme = sharedPreferences.getBoolean("dark_theme", false);
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        EdgeToEdge.enable ( this );
        setContentView ( R.layout.activity_main );
        Button btnToggleTheme = findViewById(R.id.btn_toggle_theme);
        btnToggleTheme.setOnClickListener(v -> {
            boolean currentTheme = sharedPreferences.getBoolean("dark_theme", false);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            if (currentTheme) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor.putBoolean("dark_theme", false);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor.putBoolean("dark_theme", true);
            }

            editor.apply();
        });

        btnCreate = findViewById ( R.id.btn_create );
        btnExit = findViewById ( R.id.btn_exit );
        btnHistorie = findViewById ( R.id.btn_history );
        btnPorucha = findViewById ( R.id.btn_create_porucha );
        btnCreate.setOnClickListener ( v -> {
            Intent intent = new Intent ( MainActivity.this, FormActivity.class );
            startActivity ( intent );
        } );

        // Nastavení posluchače pro tlačítko "Ukončit"
        btnExit.setOnClickListener ( v -> {
            // Ukončení aplikace
            finish ();
        } );

        btnHistorie.setOnClickListener ( v -> {
            Intent intent = new Intent (MainActivity.this, HistoryActivity.class);
            startActivity ( intent );
        } );

        btnPorucha.setOnClickListener ( v -> {
            Intent intent = new Intent (MainActivity.this, FailuresActivity.class);
            startActivity ( intent );
        } );
        }
    }