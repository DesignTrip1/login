package com.example.design.recommend;

import android.os.Bundle;
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
        setContentView(R.layout.activity_fullscreen_slider);

        viewPager = findViewById(R.id.fullscreenViewPager);

        // 이미지 추가 예시
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
