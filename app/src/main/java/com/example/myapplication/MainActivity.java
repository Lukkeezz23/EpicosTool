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

        // ✅ Opravená inicializace SharedPreferences
        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        // ✅ Opravené nastavení adapteru s fragmenty
        MenuPagerAdapter adapter = new MenuPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // ✅ Nastavení celkového počtu fragmentů
        int totalFragments = adapter.getItemCount();
        tvPageCounter.setText("1 / " + totalFragments);

        // ✅ Posluchač změn stránky pro aktualizaci čítače
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tvPageCounter.setText((position + 1) + " / " + totalFragments);
            }
        });

        // ✅ Ovládání šipek (zabránění přetečení)
        arrowLeft.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem > 0) {
                viewPager.setCurrentItem(currentItem - 1, true);
            }
        });

        arrowRight.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem < totalFragments - 1) {
                viewPager.setCurrentItem(currentItem + 1, true);
            }
        });

        // ✅ Opravené přepínání světlého a tmavého režimu
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

        // ✅ Profesionální gradientové pozadí
        setProfessionalBackground();
    }

    // ✅ Profesionální gradientové pozadí
    private void setProfessionalBackground() {
        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{0xFFB0B0B0, 0xFFE0E0E0} // Gradient barvy
        );
        findViewById(R.id.mainLayout).setBackground(gradient);
    }
}
