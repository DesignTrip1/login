package com.example.design;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.design.community.CommunityActivity;
import com.example.design.group.GroupActivity;
import com.example.design.recommend.MainImageSliderAdapter;
import com.example.design.roulette.RouletteActivity;
import com.example.design.recommend.FullscreenSliderActivity;
import com.example.design.schedule.PlanActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private MainImageSliderAdapter mainImageSliderAdapter;
    private LinearLayout indicatorLayout;
    private Map<Integer, List<Integer>> relatedImagesMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 뷰 연결
        viewPager = findViewById(R.id.viewPager);
        indicatorLayout = findViewById(R.id.indicatorLayout);

        // 메인 슬라이더 데이터 및 관련 이미지 설정
        List<Integer> mainSliderImages = new ArrayList<>();
        mainSliderImages.add(R.drawable.mainslide_jeju);
        mainSliderImages.add(R.drawable.mainslide_busan);
        mainSliderImages.add(R.drawable.mainslide_jeonju);

        relatedImagesMap = new HashMap<>();
        relatedImagesMap.put(R.drawable.mainslide_jeju, Arrays.asList(
                R.drawable.mainslide_jeju,
                R.drawable.mainslide_jeju1,
                R.drawable.mainslide_jeju2
        ));
        relatedImagesMap.put(R.drawable.mainslide_busan, Arrays.asList(
                R.drawable.mainslide_busan,
                R.drawable.mainslide_busan1,
                R.drawable.mainslide_busan2
        ));
        relatedImagesMap.put(R.drawable.mainslide_jeonju, Arrays.asList(
                R.drawable.mainslide_jeonju,
                R.drawable.mainslide_jeonju1,
                R.drawable.mainslide_jeonju2
        ));

        mainImageSliderAdapter = new MainImageSliderAdapter(this, mainSliderImages);
        viewPager.setAdapter(mainImageSliderAdapter);

        mainImageSliderAdapter.setOnImageClickListener((clickedImageResId, position) -> {
            List<Integer> imagesToShowInFullscreen = relatedImagesMap.get(clickedImageResId);

            if (imagesToShowInFullscreen != null && !imagesToShowInFullscreen.isEmpty()) {
                Intent intent = new Intent(MainActivity.this, FullscreenSliderActivity.class);
                intent.putIntegerArrayListExtra("imageList", new ArrayList<>(imagesToShowInFullscreen));
                intent.putExtra("currentPosition", 0);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "슬라이드 클릭: " + clickedImageResId, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "관련 이미지가 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        setupIndicators(mainSliderImages.size());

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

        // 로그아웃 버튼 클릭 처리 ⭐ 추가 부분 ⭐
        ImageButton btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> showLogoutDialog());
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

    // ⭐ 로그아웃 다이얼로그 및 기능 ⭐
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("로그아웃")
                .setMessage("정말 로그아웃 하시겠습니까?")
                .setPositiveButton("확인", (dialog, which) -> logout())
                .setNegativeButton("취소", null)
                .show();
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();

        Intent intent = new Intent(MainActivity.this, com.example.design.login.LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
