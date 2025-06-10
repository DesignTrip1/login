package com.example.design.roulette;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.design.R;
import com.example.design.recommend.SliderAdapter;
import com.example.design.roulette.RouletteData.TravelDestination; // 추가

import java.util.ArrayList;
import java.util.List;

public class RouletteSliderActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private SliderAdapter sliderAdapter;
    private List<TravelDestination> destinationList = new ArrayList<>(); // 이미지 리스트 대신 여행지 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_slider);

        viewPager = findViewById(R.id.fullscreenViewPager);
        String region = getIntent().getStringExtra("region");

        if (region != null) {
            // RouletteData에서 해당 지역의 여행지 목록을 가져옵니다.
            destinationList = RouletteData.getDestinationsForRegion(region);
        } else {
            // region이 null일 경우 기본값 (예: 제주도)
            destinationList = RouletteData.getDestinationsForRegion("default");
        }

        sliderAdapter = new SliderAdapter(this, destinationList);
        viewPager.setAdapter(sliderAdapter);

        // 슬라이드 클릭 리스너 설정
        sliderAdapter.setOnItemClickListener(destinationName -> {
            // 선택된 여행지 이름을 가지고 PlaceListActivity를 시작합니다.
            Intent intent = new Intent(RouletteSliderActivity.this, PlaceListActivity.class);
            intent.putExtra("destinationName", destinationName);
            startActivity(intent);
        });

        // 액션바 설정 (선택 사항)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(region + " 추천 여행지"); // 예: "전라북도 추천 여행지"
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // 뒤로가기 버튼 처리
        return true;
    }
}