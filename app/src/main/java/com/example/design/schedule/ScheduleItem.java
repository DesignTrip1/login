// ScheduleItem.java
package com.example.design.schedule;

public class ScheduleItem {
    private String id; // ⭐ 추가
    private String startTime;
    private String endTime;
    private String place;
    private String memo;

    // ⭐ id를 포함하는 생성자 추가
    public ScheduleItem(String id, String startTime, String endTime, String place, String memo) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.place = place;
        this.memo = memo;
    }

    // 기존 생성자 (id 없이 객체 생성 시)
    public ScheduleItem(String startTime, String endTime, String place, String memo) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.place = place;
        this.memo = memo;
    }

    // ⭐ Getter for id
    public String getId() {
        return id;
    }

    // ⭐ Setter for id
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