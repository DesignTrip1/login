package com.example.design.group;

import java.util.ArrayList;
import java.util.List;

public class GroupItem {
    public String groupId;
    public String groupName;
    public String creatorId; // ⭐ 추가: 그룹 생성자 ID
    public List<String> members; // ⭐ 추가: 그룹 멤버들의 사용자 ID 리스트

    // Firestore에서 데이터를 객체로 변환하기 위해선 기본 생성자가 필요합니다.
    public GroupItem() {
        // Default constructor required for calls to DataSnapshot.toObject(GroupItem.class)
        // members 리스트는 null이 아닌 빈 리스트로 초기화하는 것이 좋습니다.
        this.members = new ArrayList<>();
    }

    // 모든 필드를 포함하는 주 생성자 (가장 완전한 형태)
    public GroupItem(String groupId, String groupName, String creatorId, List<String> members) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.creatorId = creatorId;
        // members 리스트는 null 체크 후 새로운 ArrayList로 생성하여 외부 변경으로부터 보호합니다.
        this.members = members != null ? new ArrayList<>(members) : new ArrayList<>();
    }

    // 그룹 생성 시 groupId를 바로 알 수 없을 때 (예: Firestore에 추가 전) 사용하는 생성자
    // 이 생성자는 실제 그룹 생성 로직에서 바로 GroupItem 객체를 만들 때 유용할 수 있습니다.
    // 이 경우, groupId는 객체 생성 후 따로 설정하거나, Firestore에서 반환받아 설정하게 됩니다.
    public GroupItem(String groupName, String creatorId, List<String> members) {
        // 이 생성자에서는 groupId를 알 수 없으므로, null 또는 빈 문자열로 초기화
        this.groupId = null; // 또는 ""
        this.groupName = groupName;
        this.creatorId = creatorId;
        this.members = members != null ? new ArrayList<>(members) : new ArrayList<>();
    }

    // ⭐ 제거된 생성자: public GroupItem(String groupId, String groupName, List<String> members)
    // 이 생성자는 위의 '모든 필드를 포함하는 주 생성자'에서 creatorId를 null로 전달하여 대체 가능합니다.
    // 예: new GroupItem(groupId, groupName, null, members);

    // GroupItem의 getter와 setter 메서드 (Firestore 매핑을 위해 필요)
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }
}