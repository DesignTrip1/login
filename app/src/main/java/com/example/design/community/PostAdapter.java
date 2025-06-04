package com.example.design.community;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.design.R;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private ArrayList<Post> posts;
    private Context context;

    public PostAdapter(Context context, ArrayList<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.titleText.setText(post.getTitle());
        holder.contentText.setText(post.getContent());
        holder.likeCountText.setText(String.valueOf(post.getLikeCount()));

        // 댓글 버튼 클릭 → 댓글 화면으로 이동
        holder.commentButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentActivity.class);
            intent.putExtra("postIndex", post.getId());
            context.startActivity(intent);
        });

        // 좋아요 버튼 클릭
        holder.likeButton.setOnClickListener(v -> {
            String userId = "device_user"; // 로그인 기능 없으면 임시 ID

            if (!post.hasLiked(userId)) {
                if (post.like(userId)) {
                    holder.likeCountText.setText(String.valueOf(post.getLikeCount()));
                    Toast.makeText(context, "좋아요를 눌렀습니다.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "이미 좋아요를 누르셨습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView contentText;
        TextView likeCountText;
        Button commentButton;
        Button likeButton;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.textViewTitle);   // ✅ 수정
            contentText = itemView.findViewById(R.id.textViewContent); // ✅ 수정
            likeCountText = itemView.findViewById(R.id.likeCountText); // ✅ 수정
            commentButton = itemView.findViewById(R.id.btnComment);
            likeButton = itemView.findViewById(R.id.btnLike);
        }
    }
}
