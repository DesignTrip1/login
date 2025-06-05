package com.example.design.community;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.design.R;

public class PostDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        TextView titleText = findViewById(R.id.detailTitle);
        TextView contentText = findViewById(R.id.detailContent);

        // 전달받은 데이터
        String title = getIntent().getStringExtra("title");
        String content = getIntent().getStringExtra("content");

        titleText.setText(title);
        contentText.setText(content);
    }
}
