package com.example.design;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.design.community.CommunityActivity;
import com.example.design.group.GroupActivity;
import com.example.design.recommend.MainImageSliderAdapter; // MainImageSliderAdapter 임포트
import com.example.design.roulette.RouletteActivity;
// import com.example.design.roulette.RouletteData; // 이 액티비티에서는 더 이상 직접 RouletteData 사용 안함
import com.example.design.recommend.FullscreenSliderActivity; // FullscreenSliderActivity 임포트 ⭐
import com.example.design.schedule.PlanActivity;

import java.util.ArrayList;
import java.util.Arrays; // Arrays.asList() 사용을 위해 추가
import java.util.HashMap; // HashMap 사용을 위해 추가
import java.util.List;
import java.util.Map; // Map 사용을 위해 추가

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private MainImageSliderAdapter mainImageSliderAdapter;
    private LinearLayout indicatorLayout;

    // ⭐ 메인 슬라이드 이미지 ID와 해당 이미지에 대한 '관련 사진' 리스트를 매핑하는 맵 ⭐
    private Map<Integer, List<Integer>> relatedImagesMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 뷰 연결
        viewPager = findViewById(R.id.viewPager);
        indicatorLayout = findViewById(R.id.indicatorLayout);

        // --- 메인 슬라이더 데이터 및 관련 이미지 설정 ---
        // 1. 메인 슬라이더에 표시할 이미지 리스트
        List<Integer> mainSliderImages = new ArrayList<>();
        mainSliderImages.add(R.drawable.jeju);
        mainSliderImages.add(R.drawable.busan);
        mainSliderImages.add(R.drawable.jeonju);
        // 필요한 경우 더 많은 메인 슬라이더 이미지 추가 (예: R.drawable.seoul, R.drawable.gangwon 등)

        // 2. 각 메인 슬라이더 이미지에 대한 '관련 사진' 리스트 정의
        // 이 부분은 사용자가 직접 어떤 사진들을 보여주고 싶은지 설정해야 합니다.
        relatedImagesMap = new HashMap<>();
        // 제주도 관련 이미지들
        relatedImagesMap.put(R.drawable.jeju, Arrays.asList(
                R.drawable.jeju,   // res/drawable 에 추가해야 할 이미지들
                R.drawable.jeju1,
                R.drawable.jeju2
                // 더 많은 제주도 관련 이미지 추가
        ));
        // 부산 관련 이미지들
        relatedImagesMap.put(R.drawable.busan, Arrays.asList(
                R.drawable.busan,
                R.drawable.busan1,
                R.drawable.busan2
                // 더 많은 부산 관련 이미지 추가
        ));
        // 전주 관련 이미지들
        relatedImagesMap.put(R.drawable.jeonju, Arrays.asList(
                R.drawable.jeonju,
                R.drawable.jeonju1,
                R.drawable.jeonju2
                // 더 많은 전주 관련 이미지 추가
        ));
        // ⭐ 이 이미지들은 모두 res/drawable 폴더에 존재해야 합니다. ⭐

        // MainImageSliderAdapter 초기화 (단순 이미지 리스트 전달)
        mainImageSliderAdapter = new MainImageSliderAdapter(this, mainSliderImages);
        viewPager.setAdapter(mainImageSliderAdapter);

        // MainImageSliderAdapter의 클릭 리스너 설정
        mainImageSliderAdapter.setOnImageClickListener((clickedImageResId, position) -> { // ⭐ 변경 ⭐
            // 클릭된 이미지에 해당하는 관련 이미지 리스트를 가져옴
            List<Integer> imagesToShowInFullscreen = relatedImagesMap.get(clickedImageResId);

            if (imagesToShowInFullscreen != null && !imagesToShowInFullscreen.isEmpty()) {
                Intent intent = new Intent(MainActivity.this, FullscreenSliderActivity.class);
                // 관련 이미지 리스트를 Intent에 담아 FullscreenSliderActivity로 전달
                intent.putIntegerArrayListExtra("imageList", new ArrayList<>(imagesToShowInFullscreen));
                // 클릭된 이미지가 relatedImagesMap의 첫 번째 이미지와 일치한다면, 시작 위치는 0
                // 아니면 relatedImagesMap.get(clickedImageResId).indexOf(clickedImageResId) 등으로 조정 가능하지만
                // 일반적으로 전체 화면 슬라이더는 첫 이미지부터 보여주는 경우가 많으므로 0으로 설정
                intent.putExtra("currentPosition", 0); // FullscreenSliderActivity의 시작 위치
                startActivity(intent);
                Toast.makeText(MainActivity.this, "슬라이드 클릭: " + clickedImageResId, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "관련 이미지가 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        // --- 수정 끝 ---

        setupIndicators(mainSliderImages.size()); // 메인 슬라이더 이미지 크기 반영

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                setCurrentIndicator(position);
            }
        });

        // 지도 이미지 클릭 시 지도 액티비티 실행
        ImageView imgMap = findViewById(R.id.imgMap);
        imgMap.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, kakaoapi.class);
            startActivity(intent);
        });

        // 아이콘 버튼들
        ImageButton btnPlan = findViewById(R.id.btnPlan);
        ImageButton btnCommunity = findViewById(R.id.btnCommunity);
        ImageButton btnRoulette = findViewById(R.id.btnRoulette);
        ImageButton btnGroup = findViewById(R.id.btnGroup);

        btnPlan.setOnClickListener(v -> startActivity(new Intent(this, PlanActivity.class)));
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