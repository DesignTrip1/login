package com.example.design.community;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.example.design.R;

public class CommunityActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("커뮤니티");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    // 메뉴를 인플레이트 (툴바 메뉴에 추가)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_community, menu);
        return true;
    }

    // 메뉴 아이템 클릭 처리
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_write) {
            // + 버튼 클릭 시 게시글 작성 화면으로 이동
            Intent intent = new Intent(this, WritePostActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
