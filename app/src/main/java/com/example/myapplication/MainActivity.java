package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnCreate;
    Button btnHistorie;
    Button btnExit;

    Button btnPorucha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        EdgeToEdge.enable ( this );
        setContentView ( R.layout.activity_main );
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