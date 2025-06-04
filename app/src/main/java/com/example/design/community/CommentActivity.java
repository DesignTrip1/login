package com.example.design.community;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.design.R;

import java.util.ArrayList;

public class CommentActivity extends AppCompatActivity {

    private EditText editComment;
    private Button btnSubmit;
    private ListView commentListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        int postId = getIntent().getIntExtra("postIndex", -1);
        Post post = PostRepository.getInstance().getPost(postId);

        if (post == null) {
            Toast.makeText(this, "잘못된 게시글입니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        editComment = findViewById(R.id.editComment);
        btnSubmit = findViewById(R.id.btnSubmit);
        commentListView = findViewById(R.id.commentListView);

        commentList = post.getComments();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, commentList);
        commentListView.setAdapter(adapter);

        btnSubmit.setOnClickListener(v -> {
            String comment = editComment.getText().toString().trim();
            if (!comment.isEmpty()) {
                post.addComment(comment);
                adapter.notifyDataSetChanged();
                editComment.setText("");
            } else {
                Toast.makeText(this, "댓글을 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
