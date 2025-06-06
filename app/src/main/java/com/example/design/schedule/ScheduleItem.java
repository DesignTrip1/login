package com.example.design.schedule;

public class ScheduleItem {
    private String startTime;
    private String endTime;
    private String place;
    private String memo;

    public ScheduleItem(String startTime, String endTime, String place, String memo) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.place = place;
        this.memo = memo;
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
