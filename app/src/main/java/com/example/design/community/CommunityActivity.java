package com.example.design.community;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.design.R;

import java.util.ArrayList;

public class CommunityActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_WRITE_POST = 1001;

    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private ArrayList<Post> postList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("커뮤니티");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PostAdapter(this, postList);
        recyclerView.setAdapter(adapter);

        // ✅ 툴바에 가려지지 않도록 ItemDecoration 추가
        recyclerView.addItemDecoration(new PostItemDecoration());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_community, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_write) {
            Intent intent = new Intent(this, WritePostActivity.class);
            startActivityForResult(intent, REQUEST_CODE_WRITE_POST);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_WRITE_POST && resultCode == RESULT_OK && data != null) {
            String title = data.getStringExtra("title");
            String content = data.getStringExtra("content");

            if (title != null && content != null) {
                postList.add(new Post(title, content));
                adapter.notifyItemInserted(postList.size() - 1);
                recyclerView.scrollToPosition(postList.size() - 1);
            }
        }
    }
}
