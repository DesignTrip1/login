package com.example.design.schedule;

public class PlanItem {
    private String title;
    private String startDate;
    private String endDate;

    public PlanItem(String title, String startDate, String endDate) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
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

    public String getPeriod() {
        return startDate + " ~ " + endDate;
    }
}
