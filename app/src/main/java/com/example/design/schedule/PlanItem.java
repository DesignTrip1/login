package com.example.design.schedule;

// PropertyName 어노테이션은 Firestore 필드명과 자바 필드명이 다를 때 사용하지만,
// 지금은 일치시키므로 필수는 아닙니다. 하지만 명시적으로 두는 것은 좋은 습관입니다.
// import com.google.firebase.firestore.PropertyName; // 필요하다면 추가

public class PlanItem {
    private String id; // Firestore 문서 ID (객체 생성 후 setId로 설정)
    private String title;
    private String startDate; // 시작일 (YYYY-MM-DD)
    private String endDate;   // 종료일 (YYYY-MM-DD)
    // ⭐ 필드 이름을 'group'으로 통일
    // @PropertyName("group") // 만약 자바 변수명을 groupId로 유지하고 싶다면 이렇게 사용 (지금은 group으로 변경)
    private String group;

    public PlanItem() {
        // Firestore가 객체를 역직렬화할 때 필요한 기본 생성자
    }

    // 모든 필드를 포함하는 생성자
    public PlanItem(String id, String title, String startDate, String endDate, String group) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.group = group; // ⭐ 'group' 필드 사용
    }

    // Firestore에서 읽어올 때 toObject()가 사용하는 생성자 (id는 나중에 setId로 설정)
    public PlanItem(String title, String startDate, String endDate, String group) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.group = group; // ⭐ 'group' 필드 사용
    }

    // Getter와 Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPeriod() { return startDate + " ~ " + endDate; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    // ⭐ 'groupId' 대신 'group' 필드의 Getter와 Setter
    public String getGroup() { return group; }
    public void setGroup(String group) { this.group = group; }
}