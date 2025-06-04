package com.example.design.community;

import java.util.ArrayList;

public class PostRepository {
    private static PostRepository instance;
    private ArrayList<Post> posts;

    private PostRepository() {
        posts = new ArrayList<>();
        // 예시로 초기 게시글 추가 (id 포함)
        posts.add(new Post(0, "첫 번째 글", "내용입니다."));
        posts.add(new Post(1, "두 번째 글", "두 번째 내용입니다."));
    }

    public static PostRepository getInstance() {
        if (instance == null) {
            instance = new PostRepository();
        }
        return instance;
    }

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public void addPost(String title, String content) {
        int newId = posts.size(); // 간단하게 id는 리스트 크기 사용
        posts.add(new Post(newId, title, content));
    }

    public Post getPost(int id) {
        for (Post post : posts) {
            if (post.getId() == id) {
                return post;
            }
        }
        return null;
    }
}
