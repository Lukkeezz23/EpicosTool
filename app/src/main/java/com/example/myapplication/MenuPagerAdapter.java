package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MenuPagerAdapter extends FragmentStateAdapter {
    public MenuPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new VyrobaFragment();
            case 1: return new PoruchyFragment();
            case 2: return new StatistikyFragment();
            case 3: return new DokumentyFragment ();
            case 4: return new NastaveniFragment ();
            default: return new VyrobaFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5; // Počet fragmentů
    }
}
