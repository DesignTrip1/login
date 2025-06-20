package com.example.design.community;

import android.content.Intent;
import android.content.SharedPreferences; // SharedPreferences import
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.design.R;

public class WritePostActivity extends AppCompatActivity {

    private static final String TAG = "WritePostActivity";
    private EditText editTitle, editContent;
    private Button btnSubmit;
    private FirestoreManager firestoreManager; // FirestoreManager 인스턴스
    private SharedPreferences sharedPreferences; // SharedPreferences 인스턴스

    private static final String PREF_NAME = "MyPrefs"; // CommunityActivity와 동일한 이름
    private static final String KEY_USER_ID = "userId"; // CommunityActivity와 동일한 키

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        editTitle = findViewById(R.id.editTitle);
        editContent = findViewById(R.id.editContent);
        btnSubmit = findViewById(R.id.btnSubmit);

        firestoreManager = FirestoreManager.getInstance(); // FirestoreManager 초기화
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE); // SharedPreferences 초기화

        btnSubmit.setOnClickListener(v -> {
            String title = editTitle.getText().toString().trim();
            String content = editContent.getText().toString().trim();

            if (TextUtils.isEmpty(title)) {
                Toast.makeText(this, "제목을 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(content)) {
                Toast.makeText(this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 현재 로그인된 사용자 ID 가져오기
            String authorId = sharedPreferences.getString(KEY_USER_ID, null);
            if (authorId == null) {
                Toast.makeText(this, "로그인 정보가 없습니다. 다시 로그인 해주세요.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Author ID is null. User might not be logged in.");
                setResult(RESULT_CANCELED); // 로그인 정보가 없으면 취소
                finish();
                return;
            }

            // 새 게시물 객체 생성 (authorId 포함)
            Post newPost = new Post(title, content, authorId);

            Log.d(TAG, "Attempting to add post to Firestore...");
            // Firestore에 게시물 추가
            firestoreManager.addPost(newPost, (success, postId) -> {
                if (success) {
                    Toast.makeText(WritePostActivity.this, "게시글이 성공적으로 작성되었습니다.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Post added successfully with ID: " + postId);
                    setResult(RESULT_OK); // 성공 시 RESULT_OK만 반환
                    finish();
                } else {
                    Toast.makeText(WritePostActivity.this, "게시글 작성에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to add post to Firestore.");
                    setResult(RESULT_CANCELED); // 실패 시 RESULT_CANCELED 반환
                    finish();
                }
            });
        });
    }
}