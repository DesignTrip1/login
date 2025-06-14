// ScheduleItem.java
package com.example.design.schedule;

public class ScheduleItem {
    private String id; // Firestore 문서 ID 추가
    private String startTime;
    private String endTime;
    private String place;
    private String memo;

    // Firestore에서 객체를 역직렬화할 때 필요한 기본 생성자
    public ScheduleItem() {
    }

    public ScheduleItem(String startTime, String endTime, String place, String memo) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.place = place;
        this.memo = memo;
    }

    public ScheduleItem(String id, String startTime, String endTime, String place, String memo) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.place = place;
        this.memo = memo;
    }

    // Getter and Setter for ID
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getPlace() {
        return place;
    }

    public String getMemo() {
        return memo;
    }

    public String getTimeRangeText() {
        return startTime + " ~ " + endTime;
    }
}