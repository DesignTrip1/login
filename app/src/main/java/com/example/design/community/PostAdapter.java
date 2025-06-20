package com.example.design.community;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.design.R;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private ArrayList<Post> posts;
    private Context context;
    private FirestoreManager firestoreManager;
    private String currentLoggedInUserId; // 현재 로그인된 사용자 ID를 저장할 필드

    // 생성자 수정: currentLoggedInUserId를 받도록 변경
    public PostAdapter(Context context, ArrayList<Post> posts, String currentLoggedInUserId) {
        this.context = context;
        this.posts = posts;
        this.firestoreManager = FirestoreManager.getInstance();
        this.currentLoggedInUserId = currentLoggedInUserId; // 필드 초기화
    }

    // (선택 사항) 외부에서 현재 사용자 ID를 업데이트할 수 있는 메서드
    public void updateCurrentUserId(String userId) {
        this.currentLoggedInUserId = userId;
        notifyDataSetChanged(); // UI 업데이트를 위해 전체 새로고침
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

        // **좋아요 버튼 초기 상태 설정**
        if (currentLoggedInUserId != null && post.hasLiked(currentLoggedInUserId)) {
            holder.likeButton.setText("♥ 좋아요 취소");
            holder.likeButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.darker_gray)));
        } else {
            holder.likeButton.setText("♥ 좋아요");
            holder.likeButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.holo_red_light)));
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("title", post.getTitle());
            intent.putExtra("content", post.getContent());
            // 필요한 경우 postId도 전달하여 PostDetailActivity에서 해당 게시물 정보를 로드하도록 할 수 있습니다.
            // intent.putExtra("postId", post.getId());
            context.startActivity(intent);
        });

        holder.commentButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentActivity.class);
            intent.putExtra("postId", post.getId());
            context.startActivity(intent);
        });

        holder.likeButton.setOnClickListener(v -> {
            // 이 로그는 좋아요 버튼이 클릭될 때마다 항상 호출됩니다.
            Log.d("PostAdapter", "Like button clicked. currentLoggedInUserId: " + currentLoggedInUserId + ", Post ID: " + post.getId());

            // 로그인 여부 확인
            if (currentLoggedInUserId == null) {
                Toast.makeText(context, "로그인 후 좋아요를 누를 수 있습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Post 객체의 toggleLike 메서드를 호출하여 좋아요 상태 변경
            boolean liked = post.toggleLike(currentLoggedInUserId);

            // UI 즉시 업데이트 (좋아요 수, 버튼 텍스트/색상)
            holder.likeCountText.setText(String.valueOf(post.getLikeCount()));
            if (liked) { // 좋아요가 눌린 상태
                holder.likeButton.setText("♥ 좋아요 취소");
                holder.likeButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.darker_gray)));
            } else { // 좋아요가 취소된 상태
                holder.likeButton.setText("♥ 좋아요");
                holder.likeButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.holo_red_light)));
            }

            // Firestore에 변경된 Post 객체 업데이트
            firestoreManager.updatePost(post, success -> {
                if (!success) {
                    // Firestore 업데이트 실패 시, 앱 내의 좋아요 상태를 되돌림 (롤백)
                    post.toggleLike(currentLoggedInUserId); // 상태 되돌리기
                    holder.likeCountText.setText(String.valueOf(post.getLikeCount())); // UI도 되돌리기

                    // 버튼 텍스트/색상도 되돌리기
                    if (post.hasLiked(currentLoggedInUserId)) {
                        holder.likeButton.setText("♥ 좋아요 취소");
                        holder.likeButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.darker_gray)));
                    } else {
                        holder.likeButton.setText("♥ 좋아요");
                        holder.likeButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.holo_red_light)));
                    }
                    Toast.makeText(context, "좋아요 업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    // 이 위치의 로그는 이제 필요 없습니다. (위에서 이미 찍었으므로)
                } else {
                    // 좋아요 업데이트 성공 시
                    Log.d("PostAdapter", "Firestore update successful for Post ID: " + post.getId());
                }
            });
        });

        // 게시물 길게 눌러서 삭제 기능 (작성자만 삭제 가능하도록 권한 확인)
        holder.itemView.setOnLongClickListener(v -> {
            if (currentLoggedInUserId == null) {
                Toast.makeText(context, "로그인해야 게시글을 삭제할 수 있습니다.", Toast.LENGTH_SHORT).show();
                return true;
            }

            if (post.getAuthorId() != null && post.getAuthorId().equals(currentLoggedInUserId)) {
                new AlertDialog.Builder(context)
                        .setTitle("게시글 삭제")
                        .setMessage("이 게시글을 삭제하시겠습니까?")
                        .setPositiveButton("삭제", (dialog, which) -> {
                            firestoreManager.deletePost(post.getId(), success -> {
                                if (success) {
                                    Toast.makeText(context, "게시글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                    removePost(position);
                                } else {
                                    Toast.makeText(context, "게시글 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        })
                        .setNegativeButton("취소", null)
                        .show();
            } else {
                Toast.makeText(context, "게시글은 작성자만 삭제할 수 있습니다.", Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void removePost(int position) {
        if (position >= 0 && position < posts.size()) {
            posts.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, posts.size());
        }
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView contentText;
        TextView likeCountText;
        Button commentButton;
        Button likeButton;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.textViewTitle);
            contentText = itemView.findViewById(R.id.textViewContent);
            likeCountText = itemView.findViewById(R.id.likeCountText);
            commentButton = itemView.findViewById(R.id.btnComment);
            likeButton = itemView.findViewById(R.id.btnLike);
        }
    }
}