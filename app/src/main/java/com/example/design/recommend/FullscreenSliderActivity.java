package com.example.design.recommend; // 패키지 경로 확인

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.design.R;
import com.example.design.recommend.SimpleImageSliderAdapter; // SimpleImageSliderAdapter 임포트

import java.util.ArrayList;
import java.util.List;

public class FullscreenSliderActivity extends AppCompatActivity {

    private ViewPager2 fullscreenViewPager;
    private SimpleImageSliderAdapter simpleImageSliderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 전체 화면 설정 (필요에 따라 주석 해제)
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //         WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // getSupportActionBar().hide();

        setContentView(R.layout.activity_fullscreen_slider); // 레이아웃 파일 이름 확인

        fullscreenViewPager = findViewById(R.id.fullscreenViewPager); // ViewPager2 ID 확인

        // Intent로부터 이미지 리소스 ID 목록과 현재 위치를 가져옵니다.
        // 이 리스트가 null이 아님을 보장해야 합니다.
        List<Integer> selectedImages = getIntent().getIntegerArrayListExtra("imageList");
        int currentPosition = getIntent().getIntExtra("currentPosition", 0);

        // ⭐ 'default_placeholder_image' 관련 코드 블록이 제거되었습니다. ⭐
        // 따라서 selectedImages가 null인 경우를 호출하는 쪽에서 처리해야 합니다.
        // 예를 들어, 호출 전에 null 체크를 하거나, 항상 빈 리스트라도 넘겨주도록 합니다.
        if (selectedImages == null) {
            selectedImages = new ArrayList<>(); // null 방지를 위해 빈 리스트로 초기화 (또는 오류 처리)
        }


        // SimpleImageSliderAdapter 초기화
        simpleImageSliderAdapter = new SimpleImageSliderAdapter(this, selectedImages);
        fullscreenViewPager.setAdapter(simpleImageSliderAdapter);

        // 특정 위치에서 슬라이드 시작
        if (selectedImages.size() > 0) { // 리스트가 비어있지 않을 때만 설정
            fullscreenViewPager.setCurrentItem(currentPosition, false);
        }


        // 액션바 설정
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("이미지 보기");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // 뒤로가기 버튼 처리
        return true;
    }
}