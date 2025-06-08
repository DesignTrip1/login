package com.example.design.recommend;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.design.R;

import java.util.ArrayList;
import java.util.List;

public class FullscreenSliderActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private SliderAdapter sliderAdapter;
    private List<Integer> busanImages = new ArrayList<>();
    private List<Integer> jejuImages = new ArrayList<>();
    private List<Integer> jeonjuImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 상태바까지 숨기는 전체화면 설정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_fullscreen_slider);

        // 시스템 UI 숨기기 (네비게이션 바, 상태바 포함)
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );

        viewPager = findViewById(R.id.fullscreenViewPager);

        // 이미지 추가
        jejuImages.add(R.drawable.jeju);
        jejuImages.add(R.drawable.jeju1);
        jejuImages.add(R.drawable.jeju2);
        busanImages.add(R.drawable.busan);
        busanImages.add(R.drawable.busan1);
        busanImages.add(R.drawable.busan2);
        jeonjuImages.add(R.drawable.jeonju);
        jeonjuImages.add(R.drawable.jeonju1);
        jeonjuImages.add(R.drawable.jeonju2);

        int type = getIntent().getIntExtra("imageType", 0);
        List<Integer> selectedImages;

        switch (type) {
            case 0:
                selectedImages = jejuImages;
                break;
            case 1:
                selectedImages = busanImages;
                break;
            case 2:
                selectedImages = jeonjuImages;
                break;
            default:
                selectedImages = new ArrayList<>();
        }

        sliderAdapter = new SliderAdapter(this, selectedImages);
        viewPager.setAdapter(sliderAdapter);
    }
}
