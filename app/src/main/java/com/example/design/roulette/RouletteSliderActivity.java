package com.example.design.roulette;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.design.R;
import com.example.design.recommend.SliderAdapter;

import java.util.ArrayList;
import java.util.List;

public class RouletteSliderActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private SliderAdapter sliderAdapter;
    private List<Integer> imageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_slider);

        viewPager = findViewById(R.id.fullscreenViewPager);
        String region = getIntent().getStringExtra("region");

        if (region != null) {
            switch (region) {
                case "전라북도":
                    imageList.add(R.drawable.jb1);
                    imageList.add(R.drawable.jb2);
                    imageList.add(R.drawable.jb3);
                    break;
                case "제주도":
                    imageList.add(R.drawable.jj1);
                    imageList.add(R.drawable.jj2);
                    imageList.add(R.drawable.jj3);
                    break;
                case "경상북도":
                    imageList.add(R.drawable.gb1);
                    imageList.add(R.drawable.gb2);
                    imageList.add(R.drawable.gb3);
                    break;
                case "경상남도":
                    imageList.add(R.drawable.gn1);
                    imageList.add(R.drawable.gn2);
                    imageList.add(R.drawable.gn3);
                    break;
                case "충청남도":
                    imageList.add(R.drawable.cn1);
                    imageList.add(R.drawable.cn2);
                    imageList.add(R.drawable.cn3);
                    break;
                case "충청북도":
                    imageList.add(R.drawable.cb1);
                    imageList.add(R.drawable.cb2);
                    imageList.add(R.drawable.cb3);
                    break;
                case "전라남도":
                    imageList.add(R.drawable.jn1);
                    imageList.add(R.drawable.jn2);
                    imageList.add(R.drawable.jn3);
                    break;
                case "경기도":
                    imageList.add(R.drawable.gg1);
                    imageList.add(R.drawable.gg2);
                    imageList.add(R.drawable.gg3);
                    break;
                case "강원도":
                    imageList.add(R.drawable.gy1);
                    imageList.add(R.drawable.gy2);
                    imageList.add(R.drawable.gy3);
                    break;
                default:
                    imageList.add(R.drawable.jeju);  // fallback
                    break;
            }
        } else {
            // region이 null일 경우 예외 방지
            imageList.add(R.drawable.jeju);
        }

        sliderAdapter = new SliderAdapter(this, imageList);
        viewPager.setAdapter(sliderAdapter);
    }
}
