package com.example.design.group;

public class FriendItem {
    private String userId;
    private boolean isSelected = false;

    public FriendItem(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
