package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnCreate;
    Button btnExit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        EdgeToEdge.enable ( this );
        setContentView ( R.layout.activity_main );
        btnCreate = findViewById(R.id.btn_create);
        btnExit = findViewById(R.id.btn_exit);
        btnCreate.setOnClickListener ( v -> {
            Intent intent = new Intent(MainActivity.this, FormActivity.class);
            startActivity(intent);
        } );

        // Nastavení posluchače pro tlačítko "Ukončit"
        btnExit.setOnClickListener( v -> {
            // Ukončení aplikace
            finish();
        } );
    }
}