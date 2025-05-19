package com.example.design;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AddScheduleActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        Button btnSelectRegion = findViewById(R.id.btnSelectRegion);
        btnSelectRegion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 다음 액티비티(지역 선택 화면)로 이동 예정
                // 예시: Intent intent = new Intent(AddScheduleActivity.this, SelectRegionActivity.class);
                // startActivity(intent);
            }
        });
    }
}
