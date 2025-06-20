package com.example.design.community;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName; // 이 임포트가 있는지 확인
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List; // 이 임포트가 있는지 확인
import java.util.Set;

public class Post {
    private String id;
    private String title;
    private String content;
    private int likeCount;
    private ArrayList<String> comments;
    private Set<String> likedUsers; // 내부 표현은 Set
    private String authorId;

    public Post() {
        // Firestore 역직렬화에 필요한 public no-argument 생성자
        this.likedUsers = new HashSet<>(); // NullPointerException 방지를 위해 항상 초기화
        this.comments = new ArrayList<>();
    }

    // authorId를 포함한 새 생성자
    public Post(String title, String content, String authorId) {
        this.title = title;
        this.content = content;
        this.likeCount = 0;
        this.comments = new ArrayList<>();
        this.likedUsers = new HashSet<>(); // NullPointerException 방지를 위해 항상 초기화
        this.authorId = authorId;
    }

    // --- Getters ---
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public int getLikeCount() { return likeCount; }
    public ArrayList<String> getComments() { return comments; }
    public String getAuthorId() { return authorId; }

    // --- 중요: Firestore가 DB에 직렬화할 때 이 getter를 호출합니다. ---
    // @Exclude가 붙지 않은 likedUsers 관련 getter는 이것뿐이어야 합니다.
    // Firestore 필드 이름이 'likedUsers'라면 이 메서드가 그에 대응해야 합니다.
    // 다른 getter에 @PropertyName을 사용했다면 일관성이 있어야 합니다.
    // 하지만 FirestoreManager에서 명시적으로 `new ArrayList<>(post.getLikedUsers())`를 사용하므로,
    // 직렬화를 위해 `getLikedUsersList()`가 엄격하게 필요하지는 않지만, 명확하게 할 수 있습니다.

    // 이 getter는 앱 내부에서 사용됩니다. @Exclude는 Firestore가 직접 매핑하는 것을 막습니다.
    @Exclude
    public Set<String> getLikedUsers() {
        return likedUsers;
    }


    // --- Setters ---
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }
    public void setComments(ArrayList<String> comments) { this.comments = comments; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }

    // *** 중요: Firestore가 데이터베이스에서 'likedUsers' 배열을 역직렬화할 때 이 setter를 사용합니다. ***
    // Firestore는 배열을 List로 전달하므로, 반드시 List<String>을 받아야 합니다.
    // Firestore의 필드 이름이 'likedUsers'라면, 메서드 이름은 'setLikedUsers'와 일치해야 합니다.
    // Firestore에 'likedUsersList'가 있고 Java에 'likedUsers'가 있다면, 여기에 @PropertyName("likedUsersList")를 사용해야 합니다.
    // 제공된 문서 구조에 "likedUsers"가 배열로 표시되어 있으므로, 이 setter 이름이 올바릅니다.
    @PropertyName("likedUsers") // "likedUsers" 필드에 이 setter를 사용하도록 Firestore에 명시적으로 알림
    public void setLikedUsers(List<String> likedUsersList) {
        if (likedUsersList != null) {
            this.likedUsers = new HashSet<>(likedUsersList);
        } else {
            this.likedUsers = new HashSet<>();
        }
    }

    // --- 비즈니스 로직 ---
    public boolean toggleLike(String userId) {
        if (likedUsers.contains(userId)) {
            likedUsers.remove(userId);
            likeCount--;
            return false; // 좋아요 취소됨
        } else {
            likedUsers.add(userId);
            likeCount++;
            return true; // 좋아요 눌림
        }
    }

    public boolean hasLiked(String userId) {
        return likedUsers.contains(userId);
    }
}