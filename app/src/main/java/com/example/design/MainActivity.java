package com.example.design;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private SliderAdapter sliderAdapter;
    private LinearLayout indicatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 슬라이드 이미지 리스트
        viewPager = findViewById(R.id.viewPager);
        indicatorLayout = findViewById(R.id.indicatorLayout);

        List<Integer> imageList = new ArrayList<>();
        imageList.add(R.drawable.jeju);
        imageList.add(R.drawable.busan);
        imageList.add(R.drawable.jeonju);

        sliderAdapter = new SliderAdapter(imageList);
        viewPager.setAdapter(sliderAdapter);

        // 점 Indicator 셋업
        setupIndicators(imageList.size());

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                setCurrentIndicator(position);
            }
        });

        // 지도 버튼
        Button btnOpenMap = findViewById(R.id.btnOpenMap);
        btnOpenMap.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            startActivity(intent);
        });

        // 아이콘 버튼
        ImageButton btnPlan = findViewById(R.id.btnPlan);
        ImageButton btnCommunity = findViewById(R.id.btnCommunity);
        ImageButton btnRoulette = findViewById(R.id.btnRoulette);
        ImageButton btnGroup = findViewById(R.id.btnGroup);

        btnPlan.setOnClickListener(v -> startActivity(new Intent(this, AddScheduleActivity.class)));
        btnCommunity.setOnClickListener(v -> startActivity(new Intent(this, CommunityActivity.class)));
        btnRoulette.setOnClickListener(v -> startActivity(new Intent(this, RouletteActivity.class)));
        btnGroup.setOnClickListener(v -> startActivity(new Intent(this, GroupActivity.class)));
    }

    private void setupIndicators(int count) {
        indicatorLayout.removeAllViews();
        for (int i = 0; i < count; i++) {
            ImageView dot = new ImageView(this);
            dot.setImageDrawable(getDrawable(R.drawable.indicator_inactive));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            indicatorLayout.addView(dot, params);
        }

        if (count > 0) {
            ((ImageView) indicatorLayout.getChildAt(0))
                    .setImageDrawable(getDrawable(R.drawable.indicator_active));
        }
    }

    private void setCurrentIndicator(int index) {
        int count = indicatorLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            ImageView dot = (ImageView) indicatorLayout.getChildAt(i);
            if (i == index) {
                dot.setImageDrawable(getDrawable(R.drawable.indicator_active));
            } else {
                dot.setImageDrawable(getDrawable(R.drawable.indicator_inactive));
            }
        }
    }
}
