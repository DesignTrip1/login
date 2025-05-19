package com.example.design;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;

public class MapActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // 툴바에 뒤로가기(Up) 버튼 활성화
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // ← 버튼 표시
            getSupportActionBar().setTitle("지도"); // 제목도 바꿔줄 수 있음
        }
    }

    // 뒤로가기 버튼 눌렀을 때 동작 처리
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // 현재 액티비티 종료 → 메인화면으로 돌아감
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
