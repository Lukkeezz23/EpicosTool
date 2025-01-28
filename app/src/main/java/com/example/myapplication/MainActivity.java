package com.example.myapplication;

import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager2.widget.ViewPager2;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TextView tvPageCounter;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EdgeToEdge.enable(this);

        viewPager = findViewById(R.id.viewPager);
        tvPageCounter = findViewById(R.id.tvPageCounter);
        Button btnToggleTheme = findViewById(R.id.btn_toggle_theme);
        ImageView arrowLeft = findViewById(R.id.arrowLeft);
        ImageView arrowRight = findViewById(R.id.arrowRight);

        // Nastavení adapteru s fragmenty
        MenuPagerAdapter adapter = new MenuPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Nastavení celkového počtu fragmentů
        int totalFragments = adapter.getItemCount();
        tvPageCounter.setText("1 / " + totalFragments);

        // Posluchač změn stránky pro aktualizaci čítače
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tvPageCounter.setText((position + 1) + " / " + totalFragments);
            }
        });

        // Ovládání šipek
        arrowLeft.setOnClickListener(v -> viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true));
        arrowRight.setOnClickListener(v -> viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true));

        // Přepínání světlého a tmavého režimu
        btnToggleTheme.setOnClickListener(v -> {
            boolean isDarkMode = sharedPreferences.getBoolean("dark_theme", false);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor.putBoolean("dark_theme", false);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor.putBoolean("dark_theme", true);
            }
            editor.apply();
        });

        // Nastavení profesionálního gradientového pozadí
        setProfessionalBackground();
    }

    // Profesionální gradientové pozadí
    private void setProfessionalBackground() {
        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{0xFF1E1E1E, 0xFF3D3D3D} // Gradient barvy
        );
        //findViewById(R.id.mainLayout).setBackground(gradient);
    }
}
