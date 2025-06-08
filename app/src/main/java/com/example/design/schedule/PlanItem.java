package com.example.design.schedule;

public class PlanItem {
    private String title;
    private String startDate;
    private String endDate;
    private String groupName;  // ✅ 그룹명 추가

    public PlanItem(String title, String startDate, String endDate, String groupName) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.groupName = groupName;
    }

    public String getTitle() {
        return title;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getPeriod() {
        return startDate + " ~ " + endDate;
    }
}
