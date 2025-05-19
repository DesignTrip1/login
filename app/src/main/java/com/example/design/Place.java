package com.example.design;

public class Place {
    private int imageResId;
    private String title;

    public Place(int imageResId, String title) {
        this.imageResId = imageResId;
        this.title = title;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getTitle() {
        return title;
    }
}
