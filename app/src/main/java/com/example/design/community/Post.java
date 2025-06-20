// Post.java
package com.example.design.community;

import com.google.firebase.firestore.PropertyName;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Post {
    private String id;
    private String title;
    private String content;
    private int likeCount;
    private ArrayList<String> comments;
    private Set<String> likedUsers = new HashSet<>(); // 필드 선언 시 초기화 유지
    private String authorId;

    public Post() {
        // Firestore 역직렬화에 필요한 public no-argument 생성자
        this.comments = new ArrayList<>();
    }

    // authorId를 포함한 새 생성자
    public Post(String title, String content, String authorId) {
        this.title = title;
        this.content = content;
        this.likeCount = 0;
        this.comments = new ArrayList<>();
        this.authorId = authorId;
    }

    // --- Getters ---
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public int getLikeCount() { return likeCount; }
    public ArrayList<String> getComments() { return comments; }
    public String getAuthorId() { return authorId; }

    // *** 중요: Firestore 직렬화를 위해 likedUsers Set을 ArrayList로 변환하여 반환 ***
    // Firestore는 Set을 직접 직렬화할 수 없으므로, 이 getter가 List를 반환하도록 합니다.
    @PropertyName("likedUsers") // Firestore에 저장될 필드 이름을 명시적으로 지정
    public List<String> getLikedUsersList() { // 메서드 이름 변경하여 혼동 방지
        return new ArrayList<>(likedUsers); // HashSet의 요소를 새 ArrayList로 변환하여 반환
    }

    // 이전에 있던 getLikedUsers() Set<String> 버전을 삭제하거나 @Exclude 처리 (이 경우 이름을 다르게 하세요)
    // Firestore는 @PropertyName이 붙은 getter/setter를 우선하고, 없으면 일반 getter/setter를 사용하므로
    // getLikedUsersList()를 추가하면 기존 getLikedUsers()가 자동으로 무시될 가능성이 높지만,
    // 명시적으로 문제를 없애려면 아래 getLikedUsers()를 제거하거나 @Exclude 처리하는 것이 좋습니다.
    // 여기서는 getLikedUsersList()가 우선되므로, 기존 getLikedUsers()를 제거하지 않아도 큰 문제는 없지만,
    // 혼동을 피하기 위해 제거하거나 이름을 변경하는 것을 권장합니다.
    // 만약 기존 getLikedUsers()를 유지하려면, @Exclude를 붙여 Firestore 직렬화에서 제외해야 합니다.
    // @Exclude
    // public Set<String> getLikedUsers() {
    //    return likedUsers;
    // }


    // --- Setters ---
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }
    public void setComments(ArrayList<String> comments) { this.comments = comments; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }

    // *** 중요: Firestore가 데이터베이스에서 'likedUsers' 배열을 역직렬화할 때 이 setter를 사용합니다. ***
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