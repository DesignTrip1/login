package com.example.design.group;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GroupManager {

    private static GroupManager instance;
    private Set<String> allFriends = new HashSet<>();
    private List<String> groupList = new ArrayList<>(); // ✅ 그룹 목록 추가

    private GroupManager() {}

    public static GroupManager getInstance() {
        if (instance == null) {
            instance = new GroupManager();
        }
        return instance;
    }

    // ✅ 친구 추가
    public void addFriend(String friendId) {
        allFriends.add(friendId);
    }

    public Set<String> getAllFriends() {
        return allFriends;
    }

    // ✅ 그룹 추가
    public void addGroup(String groupName) {
        groupList.add(groupName);
    }

    public List<String> getGroupList() {
        return new ArrayList<>(groupList); // 복사본 리턴
    }
}
