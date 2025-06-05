package com.example.design;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class DayPagerAdapter extends FragmentStateAdapter {

    private final List<String> dayTitles;

    public DayPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<String> dayTitles) {
        super(fragmentActivity);
        this.dayTitles = dayTitles;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return DayFragment.newInstance(dayTitles.get(position));
    }

    @Override
    public int getItemCount() {
        return dayTitles.size();
    }
}
