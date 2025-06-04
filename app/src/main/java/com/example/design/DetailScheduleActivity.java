package com.example.design;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.SimpleDateFormat;
import java.util.*;

public class DetailScheduleActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private DayPagerAdapter dayPagerAdapter;
    private List<String> dayTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_schedule);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        // ✅ PlanItem에서 start/end 날짜 받아오기 (예시: intent로 받았다고 가정)
        Intent intent = getIntent();
        String startDate = intent.getStringExtra("startDate");
        String endDate = intent.getStringExtra("endDate");

        dayTitles = generateDayList(startDate, endDate);
        dayPagerAdapter = new DayPagerAdapter(this, dayTitles);
        viewPager.setAdapter(dayPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(dayTitles.get(position));
        }).attach();
    }

    private List<String> generateDayList(String start, String end) {
        List<String> result = new ArrayList<>();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            Date startDate = sdf.parse(start);
            Date endDate = sdf.parse(end);

            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);

            int dayIndex = 1;
            while (!cal.getTime().after(endDate)) {
                result.add("Day " + dayIndex);
                cal.add(Calendar.DATE, 1);
                dayIndex++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
