package com.example.design.community;

// import com.google.firebase.firestore.Exclude; // ✨ 이 임포트는 더 이상 필요하지 않을 수 있습니다. 다른 필드에 @Exclude가 없다면 제거하세요.
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
    private Set<String> likedUsers = new HashSet<>(); // ✨ 필드 선언 시 초기화 유지
    private String authorId;

    public Post() {
        // Firestore 역직렬화에 필요한 public no-argument 생성자
        // likedUsers는 이미 필드 선언 시 초기화되므로 여기서의 명시적 초기화는 불필요
        this.comments = new ArrayList<>();
    }

    // authorId를 포함한 새 생성자
    public Post(String title, String content, String authorId) {
        this.title = title;
        this.content = content;
        this.likeCount = 0;
        this.comments = new ArrayList<>();
        // likedUsers는 이미 필드 선언 시 초기화되므로 여기서의 명시적 초기화는 불필요
        this.authorId = authorId;
    }

    // --- Getters ---
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public int getLikeCount() { return likeCount; }
    public ArrayList<String> getComments() { return comments; }
    public String getAuthorId() { return authorId; }

    // ✨ 중요: @Exclude 어노테이션을 제거합니다.
    // Firestore의 CustomClassMapper가 'likedUsers' 필드를 찾도록 허용합니다.
    // setter에 @PropertyName이 있으므로, 이 getter는 Firestore의 직렬화에 직접 사용되지 않습니다.
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