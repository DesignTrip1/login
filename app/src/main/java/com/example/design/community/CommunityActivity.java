package com.example.design.community;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
        Log.d(TAG, "Current Logged In User ID: " + currentLoggedInUserId);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new PostItemDecoration()); // 아이템 간 간격 추가

        adapter = new PostAdapter(this, postList, currentLoggedInUserId); // 사용자 ID를 어댑터에 전달
        recyclerView.setAdapter(adapter);

        loadPostsFromFirestore(); // 초기 게시물 로드

        // 기존에 R.id.fabAddPost에 대한 리스너 설정 부분이 여기에 있었으나,
        // 해당 ID가 레이아웃에 없으므로 제거됩니다.
        // 게시글 작성은 이제 메뉴 아이템 (action_write)을 통해서만 이루어집니다.
    }

    // 게시물 작성 후 결과 처리 (Firestore 직접 업데이트로 변경)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult called. RequestCode: " + requestCode + ", ResultCode: " + resultCode);

        if (requestCode == REQUEST_CODE_WRITE_POST && resultCode == RESULT_OK) {
            Log.d(TAG, "WritePostActivity returned RESULT_OK. Reloading posts from Firestore.");
            // WritePostActivity에서 게시물을 이미 Firestore에 추가했으므로,
            // CommunityActivity는 그저 최신 목록을 다시 불러오기만 하면 됩니다.
            loadPostsFromFirestore();
        } else if (requestCode == REQUEST_CODE_WRITE_POST && resultCode == RESULT_CANCELED) {
            Log.d(TAG, "WritePostActivity returned RESULT_CANCELED.");
            Toast.makeText(this, "게시글 작성이 취소되거나 실패했습니다.", Toast.LENGTH_SHORT).show();
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

    private void loadPostsFromFirestore() {
        Log.d(TAG, "Loading posts from Firestore...");
        firestoreManager.getPosts(new FirestoreManager.OnPostsLoadedListener() {
            @Override
            public void onPostsLoaded(ArrayList<Post> posts) {
                if (!isFinishing() && !isDestroyed()) { // 액티비티가 유효한 상태인지 확인
                    Log.d(TAG, "Posts loaded successfully. Count: " + posts.size());
                    postList.clear();
                    postList.addAll(posts);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Activity finished or destroyed, skipping UI update.");
                }
            }

            @Override
            public void onError(Exception e) {
                if (!isFinishing() && !isDestroyed()) { // 액티비티가 유효한 상태인지 확인
                    Log.e(TAG, "Error loading posts: " + e.getMessage(), e);
                    Toast.makeText(CommunityActivity.this, "게시글 로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "Activity finished or destroyed, skipping error toast.");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_community, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) { // 뒤로가기 버튼 클릭 시
            onBackPressed(); // 현재 액티비티 종료
            return true;
        } else if (id == R.id.action_write) { // R.id.action_write 메뉴 아이템 처리
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
}