package com.example.design;

public class PlanItem {
    private String title;
    private String period;

    public PlanItem(String title, String period) {
        this.title = title;
        this.period = period;
    }

    public String getTitle() {
        return title;
    }

    public String getPeriod() {
        return period;
    }
}
