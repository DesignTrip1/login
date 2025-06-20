package com.example.design.community;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast; // Toast는 여전히 다른 용도로 사용될 수 있어 임포트는 유지

import androidx.appcompat.app.AppCompatActivity;

import com.example.design.R;

import java.util.ArrayList;

public class CommentActivity extends AppCompatActivity {

    private static final String TAG = "CommentActivity";

    private EditText editComment;
    private Button btnSubmit;
    private ListView commentListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> commentList;
    private String postId;
    private Post currentPost;
    private FirestoreManager firestoreManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        firestoreManager = FirestoreManager.getInstance();

        // Intent에서 postId 가져오기
        postId = getIntent().getStringExtra("postId");

        if (postId == null) {
            Log.e(TAG, "Error: postId is null. Finishing activity.");
            Toast.makeText(this, "잘못된 게시글입니다.", Toast.LENGTH_SHORT).show(); // 오류 토스트는 유지
            finish();
            return;
        }

        editComment = findViewById(R.id.editComment);
        btnSubmit = findViewById(R.id.btnSubmit);
        commentListView = findViewById(R.id.commentListView);

        // 게시글과 댓글 로드
        loadPostAndComments();

        btnSubmit.setOnClickListener(v -> {
            String comment = editComment.getText().toString().trim();
            if (!comment.isEmpty()) {
                if (currentPost != null) {
                    // Post 객체의 comments 리스트에 직접 댓글 추가
                    // 이 부분이 수정된 부분입니다.
                    currentPost.getComments().add(comment);

                    // Firestore에 게시물 업데이트 (댓글 포함)
                    firestoreManager.updatePost(currentPost, success -> {
                        // 액티비티가 종료되거나 파괴되지 않았을 때만 UI 업데이트
                        if (!isFinishing() && !isDestroyed()) {
                            if (success) {
                                adapter.notifyDataSetChanged(); // UI 갱신
                                editComment.setText(""); // 입력 필드 초기화
                                // 댓글 추가 성공 Toast 메시지 제거 (CommunityActivity의 실시간 리스너가 반영)
                                // TODO: 필요시, 다른 방식으로 댓글 추가 성공을 알릴 수 있습니다.
                            } else {
                                // 댓글 추가 실패 시, 추가했던 댓글을 다시 제거하여 UI와 데이터 일치
                                Log.e(TAG, "Failed to add comment to Firestore.");
                                currentPost.getComments().remove(comment); // 실패 시 댓글 되돌리기
                                // TODO: 필요시, 다른 방식으로 댓글 추가 실패를 알릴 수 있습니다.
                            }
                        } else {
                            Log.d(TAG, "Activity finished or destroyed, skipping UI update for comment.");
                        }
                    });
                }
            } else {
                // 댓글 내용이 비어있을 때 토스트 메시지 표시
                if (!isFinishing() && !isDestroyed()) {
                    Toast.makeText(this, "댓글을 입력하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadPostAndComments() {
        // Firestore에서 게시물 데이터를 로드
        firestoreManager.getPost(postId, new FirestoreManager.OnPostLoadedListener() {
            @Override
            public void onPostLoaded(Post post) {
                // 액티비티가 종료되거나 파괴되지 않았을 때만 UI 업데이트
                if (!isFinishing() && !isDestroyed()) {
                    if (post != null) {
                        currentPost = post; // 현재 게시물 객체 저장
                        commentList = currentPost.getComments(); // 게시물에서 댓글 리스트 가져오기
                        // ArrayAdapter 설정 및 ListView에 연결
                        adapter = new ArrayAdapter<>(CommentActivity.this, android.R.layout.simple_list_item_1, commentList);
                        commentListView.setAdapter(adapter);
                    } else {
                        // postId에 해당하는 게시물을 찾지 못했을 경우
                        Log.e(TAG, "Post with ID " + postId + " not found.");
                        Toast.makeText(CommentActivity.this, "게시글을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                        finish(); // 액티비티 종료
                    }
                } else {
                    Log.d(TAG, "Activity finished or destroyed, skipping UI update for post load.");
                }
            }

            @Override
            public void onError(Exception e) {
                // 게시물 로드 중 오류 발생 시
                if (!isFinishing() && !isDestroyed()) {
                    Log.e(TAG, "Error loading post with ID " + postId + ": " + e.getMessage(), e);
                    Toast.makeText(CommentActivity.this, "게시글을 불러오는 데 실패했습니다: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish(); // 액티비티 종료
                } else {
                    Log.d(TAG, "Activity finished or destroyed, skipping error toast.");
                }
            }
        });
    }
}