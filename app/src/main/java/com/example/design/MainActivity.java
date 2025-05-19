package com.example.design;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PlaceAdapter adapter;
    private List<Place> placeList;
    private ViewPager2 viewPager;
    private SliderAdapter sliderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 지도 버튼
        Button btnOpenMap = findViewById(R.id.btnOpenMap);
        btnOpenMap.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, kakaoapi.class);
            startActivity(intent);
        });

        // 아이콘 버튼들
        ImageButton btnPlan = findViewById(R.id.btnPlan);
        ImageButton btnCommunity = findViewById(R.id.btnCommunity);
        ImageButton btnRoulette = findViewById(R.id.btnRoulette);
        ImageButton btnGroup = findViewById(R.id.btnGroup);

        btnPlan.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddScheduleActivity.class);
            startActivity(intent);
        });

        btnCommunity.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CommunityActivity.class);
            startActivity(intent);
        });

        btnRoulette.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RouletteActivity.class);
            startActivity(intent);
        });

        btnGroup.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GroupActivity.class);
            startActivity(intent);
        });

        // RecyclerView 셋업
        viewPager = findViewById(R.id.viewPager);

        List<Integer> imageList = new ArrayList<>();
        imageList.add(R.drawable.jeju);
        imageList.add(R.drawable.busan);
        imageList.add(R.drawable.jeonju);

        sliderAdapter = new SliderAdapter(imageList);
        viewPager.setAdapter(sliderAdapter);
    }
}
