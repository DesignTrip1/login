package com.example.design.community;

import java.util.ArrayList;
import java.util.HashSet;

public class Post {
    private static int autoIncrementId = 0;

    private int id;                     // 게시글 고유 ID
    private String title;               // 제목
    private String content;             // 내용
    private int likeCount;              // 좋아요 수
    private ArrayList<String> comments; // 댓글 리스트

    // ✅ 좋아요 누른 사용자 ID 저장 (중복 방지용)
    private HashSet<String> likedUsers;

    // 생성자 (id 자동 생성, 제목, 내용)
    public Post(String title, String content) {
        this(autoIncrementId++, title, content);
    }

    // 생성자 (id, 제목, 내용)
    public Post(int id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.likeCount = 0;
        this.comments = new ArrayList<>();
        this.likedUsers = new HashSet<>();
    }

    // id getter
    public int getId() {
        return id;
    }

    // 제목 getter
    public String getTitle() {
        return title;
    }

    // 내용 getter
    public String getContent() {
        return content;
    }

    // 좋아요 수 getter/setter
    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    // ✅ 사용자 ID 기반 좋아요 추가 (중복 방지)
    public boolean like(String userId) {
        if (!likedUsers.contains(userId)) {
            likedUsers.add(userId);
            likeCount++;
            return true;
        }
        return false;
    }

    // ✅ 해당 사용자가 이미 좋아요 눌렀는지 확인
    public boolean hasLiked(String userId) {
        return likedUsers.contains(userId);
    }

    // 댓글 리스트 getter
    public ArrayList<String> getComments() {
        return comments;
    }

    // 댓글 추가 메서드
    public void addComment(String comment) {
        comments.add(comment);
    }
}
