package com.example.design.community;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.design.R;

import java.util.ArrayList;

public class CommunityActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_WRITE_POST = 1001;
    private static final String TAG = "CommunityActivity";

    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private ArrayList<Post> postList = new ArrayList<>();
    private FirestoreManager firestoreManager;

    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_USER_ID = "userId";
    private SharedPreferences sharedPreferences;
    private String currentLoggedInUserId; // 현재 로그인된 사용자 ID를 저장할 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("커뮤니티");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        firestoreManager = FirestoreManager.getInstance();
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // 현재 로그인된 사용자 ID 가져오기
        currentLoggedInUserId = sharedPreferences.getString(KEY_USER_ID, null);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new PostItemDecoration());

        // PostAdapter를 초기화할 때 현재 로그인된 사용자 ID를 전달
        adapter = new PostAdapter(this, postList, currentLoggedInUserId); // currentLoggedInUserId 추가
        recyclerView.setAdapter(adapter);

        loadPostsFromFirestore();
    }

    private void loadPostsFromFirestore() {
        firestoreManager.getPosts(new FirestoreManager.OnPostsLoadedListener() {
            @Override
            public void onPostsLoaded(ArrayList<Post> posts) {
                postList.clear();
                postList.addAll(posts);
                adapter.notifyDataSetChanged();
                Log.d(TAG, "Posts loaded: " + posts.size());
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error loading posts: " + e.getMessage());
                Toast.makeText(CommunityActivity.this, "게시글 로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
        } else if (item.getItemId() == R.id.action_write) { // R.id.action_write로 변경되었음을 확인
            // 게시글 작성 시 현재 로그인된 사용자 ID를 다시 확인
            currentLoggedInUserId = sharedPreferences.getString(KEY_USER_ID, null);
            if (currentLoggedInUserId == null) {
                Toast.makeText(this, "게시글을 작성하려면 로그인해야 합니다.", Toast.LENGTH_SHORT).show();
                return true;
            }

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
                // 게시글 작성 시 authorId로 현재 로그인된 사용자 ID를 사용
                String authorId = currentLoggedInUserId;

                // 새로운 게시물을 Firestore에 추가 (authorId 포함)
                Post newPost = new Post(title, content, authorId);
                firestoreManager.addPost(newPost, (success, postId) -> {
                    if (success) {
                        Toast.makeText(CommunityActivity.this, "게시글이 성공적으로 작성되었습니다.", Toast.LENGTH_SHORT).show();
                        loadPostsFromFirestore();
                    } else {
                        Toast.makeText(CommunityActivity.this, "게시글 작성에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        // 액티비티로 돌아올 때마다 사용자 ID를 다시 가져오고 어댑터를 업데이트 (혹시 로그인/로그아웃 상태 변경 시)
        currentLoggedInUserId = sharedPreferences.getString(KEY_USER_ID, null);
        // 어댑터에 현재 로그인된 사용자 ID를 업데이트
        if (adapter != null) {
            adapter.updateCurrentUserId(currentLoggedInUserId); // PostAdapter의 updateCurrentUserId 호출
        }
        loadPostsFromFirestore(); // 게시물 목록 새로고침
    }
}