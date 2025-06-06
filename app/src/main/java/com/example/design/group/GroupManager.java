package com.example.design.group;

import java.util.HashSet;
import java.util.Set;

public class GroupManager {

    private static GroupManager instance;
    private Set<String> allFriends = new HashSet<>();

    private GroupManager() {}

    public static GroupManager getInstance() {
        if (instance == null) {
            instance = new GroupManager();
        }
        return instance;
    }

    public void addFriend(String friendId) {
        allFriends.add(friendId);
    }

    public Set<String> getAllFriends() {
        return allFriends;
    }
}
