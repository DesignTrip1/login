package com.example.design.community;

import android.util.Log;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FirestoreManager {
    private static final String TAG = "FirestoreManager";
    private static FirestoreManager instance;
    private FirebaseFirestore db;
    private static final String COLLECTION_POSTS = "posts";

    private FirestoreManager() {
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized FirestoreManager getInstance() {
        if (instance == null) {
            instance = new FirestoreManager();
        }
        return instance;
    }

    public interface OnPostsLoadedListener {
        void onPostsLoaded(ArrayList<Post> posts);
        void onError(Exception e);
    }

    public interface OnPostAddedListener {
        void onPostAdded(boolean success, String postId);
    }

    public interface OnPostUpdatedListener {
        void onPostUpdated(boolean success);
    }

    // 게시물 삭제 완료 시 호출될 콜백 인터페이스 추가
    public interface OnPostDeletedListener {
        void onPostDeleted(boolean success);
    }


    public void addPost(Post post, OnPostAddedListener listener) {
        db.collection(COLLECTION_POSTS)
                .add(post)
                .addOnSuccessListener(documentReference -> {
                    String postId = documentReference.getId();
                    post.setId(postId);
                    documentReference.set(post)
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "DocumentSnapshot added with ID: " + postId);
                                listener.onPostAdded(true, postId);
                            })
                            .addOnFailureListener(e -> {
                                Log.w(TAG, "Error updating document with ID: " + postId, e);
                                listener.onPostAdded(false, null);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding document", e);
                    listener.onPostAdded(false, null);
                });
    }

    public void getPosts(OnPostsLoadedListener listener) {
        db.collection(COLLECTION_POSTS)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Post> posts = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Post post = document.toObject(Post.class);
                            post.setId(document.getId());
                            posts.add(post);
                        }
                        listener.onPostsLoaded(posts);
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                        listener.onError(task.getException());
                    }
                });
    }

    public void getPost(String postId, OnPostLoadedListener listener) {
        db.collection(COLLECTION_POSTS).document(postId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Post post = documentSnapshot.toObject(Post.class);
                        if (post != null) {
                            post.setId(documentSnapshot.getId());
                        }
                        listener.onPostLoaded(post);
                    } else {
                        listener.onPostLoaded(null);
                    }
                })
                .addOnFailureListener(e -> listener.onError(e));
    }

    public interface OnPostLoadedListener {
        void onPostLoaded(Post post);
        void onError(Exception e);
    }

    // FirestoreManager.java
    public void updatePost(Post post, OnPostUpdatedListener listener) {
        // 이 로그는 post.getId()가 null이든 아니든 항상 호출됩니다.
        Log.d(TAG, "updatePost called for post ID: " + post.getId());

        if (post.getId() == null) {
            listener.onPostUpdated(false);
            Log.w(TAG, "updatePost failed: Post ID is null."); // ID가 null인 경우 추가 로그
            return;
        }

        DocumentReference postRef = db.collection(COLLECTION_POSTS).document(post.getId());

        Map<String, Object> updates = new HashMap<>();
        updates.put("likeCount", post.getLikeCount());
        updates.put("comments", post.getComments());
        // 이 부분이 핵심: likedUsers Set을 ArrayList로 변환하여 Firestore에 저장
        updates.put("likedUsers", new ArrayList<>(post.getLikedUsers()));

        postRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot successfully updated! Post ID: " + post.getId()); // 성공 로그에 Post ID 추가
                    listener.onPostUpdated(true);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error updating document for Post ID: " + post.getId(), e); // 실패 로그에 Post ID 추가
                    listener.onPostUpdated(false);
                });
    }

    // 게시물 삭제 메서드 추가
    public void deletePost(String postId, OnPostDeletedListener listener) {
        if (postId == null) {
            listener.onPostDeleted(false);
            return;
        }

        db.collection(COLLECTION_POSTS).document(postId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    listener.onPostDeleted(true);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error deleting document", e);
                    listener.onPostDeleted(false);
                });
    }
}