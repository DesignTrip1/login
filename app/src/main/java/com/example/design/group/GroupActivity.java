package com.example.design.group;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.design.R;

import java.util.*;

public class GroupActivity extends AppCompatActivity {

    private LinearLayout layoutGroups;
    private LinearLayout layoutFriends;
    private Set<String> friendSet = new HashSet<>();
    private List<String> groupList = new ArrayList<>();
    private Map<String, Set<String>> groupMembers = new HashMap<>();

    private TextView groupHeader;
    private TextView friendHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        Button btnCreateGroup = findViewById(R.id.btnCreateGroup);
        ImageButton btnAddFriend = findViewById(R.id.btnAddFriend);

        layoutGroups = findViewById(R.id.layoutGroups);
        layoutFriends = findViewById(R.id.layoutFriends);

        btnAddFriend.setOnClickListener(v -> showAddFriendDialog());
        btnCreateGroup.setOnClickListener(v -> showCreateGroupDialog());

        // 헤더 초기화
        groupHeader = new TextView(this);
        groupHeader.setText("📁 생성된 그룹 (0)");
        groupHeader.setTextSize(18f);
        groupHeader.setTextColor(Color.BLACK);
        groupHeader.setTypeface(null, android.graphics.Typeface.BOLD);
        groupHeader.setPadding(16, 32, 16, 8);
        layoutGroups.addView(groupHeader);

        friendHeader = new TextView(this);
        friendHeader.setText("👥 친구 목록 (0)");
        friendHeader.setTextSize(18f);
        friendHeader.setTextColor(Color.BLACK);
        friendHeader.setTypeface(null, android.graphics.Typeface.BOLD);
        friendHeader.setPadding(16, 32, 16, 8);
        layoutFriends.addView(friendHeader);
    }

    private void showAddFriendDialog() {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        new AlertDialog.Builder(this)
                .setTitle("친구 추가")
                .setMessage("친구 ID를 입력하세요")
                .setView(input)
                .setPositiveButton("추가", (dialog, which) -> {
                    String friendId = input.getText().toString().trim();
                    if (!friendId.isEmpty() && !friendSet.contains(friendId)) {
                        friendSet.add(friendId);
                        GroupManager.getInstance().addFriend(friendId);  // GroupManager 에도 추가
                        addFriendView(friendId);
                        updateFriendHeader();
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }

    private void addFriendView(String friendId) {
        TextView textView = new TextView(this);
        textView.setText(friendId);
        textView.setTextSize(16f);
        textView.setPadding(16, 16, 16, 16);
        textView.setTextColor(Color.BLACK);

        View divider = new View(this);
        divider.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        divider.setBackgroundColor(Color.LTGRAY);

        layoutFriends.addView(textView);
        layoutFriends.addView(divider);
    }

    private void showCreateGroupDialog() {
        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(30, 30, 30, 30);

        EditText inputGroupName = new EditText(this);
        inputGroupName.setHint("그룹 이름");
        dialogLayout.addView(inputGroupName);

        List<CheckBox> checkBoxes = new ArrayList<>();
        for (String friendId : friendSet) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(friendId);
            checkBox.setTextColor(Color.BLACK);
            checkBoxes.add(checkBox);
            dialogLayout.addView(checkBox);
        }

        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(dialogLayout);

        new AlertDialog.Builder(this)
                .setTitle("그룹 생성")
                .setView(scrollView)
                .setPositiveButton("생성", (dialog, which) -> {
                    String groupName = inputGroupName.getText().toString().trim();
                    if (!groupName.isEmpty() && !groupList.contains(groupName)) {
                        groupList.add(groupName);

                        // ✅ 그룹 구성원 저장
                        Set<String> selectedMembers = new HashSet<>();
                        for (CheckBox cb : checkBoxes) {
                            if (cb.isChecked()) {
                                selectedMembers.add(cb.getText().toString());
                            }
                        }

                        groupMembers.put(groupName, selectedMembers);

                        // ✅ 추가 → GroupManager 에도 그룹 저장 (AddScheduleActivity 쪽에서 사용할 수 있도록)
                        GroupManager.getInstance().addGroup(groupName);

                        addGroupView(groupName);
                        updateGroupHeader();
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }

    private void addGroupView(String groupName) {
        TextView groupView = new TextView(this);
        groupView.setText(groupName);
        groupView.setTextSize(16f);
        groupView.setPadding(16, 16, 16, 16);
        groupView.setTextColor(Color.BLACK);

        groupView.setOnClickListener(v -> {
            Intent intent = new Intent(GroupActivity.this, GroupDetailActivity.class);
            intent.putExtra("groupName", groupName);

            ArrayList<String> members = new ArrayList<>(groupMembers.get(groupName));
            intent.putStringArrayListExtra("memberList", members);

            startActivityForResult(intent, 1234);  // 그룹 상세 화면 요청
        });

        View divider = new View(this);
        divider.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        divider.setBackgroundColor(Color.LTGRAY);

        layoutGroups.addView(groupView);
        layoutGroups.addView(divider);
    }

    private void updateGroupHeader() {
        groupHeader.setText("📁 생성된 그룹 (" + groupList.size() + ")");
    }

    private void updateFriendHeader() {
        friendHeader.setText("👥 친구 목록 (" + friendSet.size() + ")");
    }

    private void refreshGroupList() {
        layoutGroups.removeAllViews();
        layoutGroups.addView(groupHeader);

        for (String groupName : groupList) {
            addGroupView(groupName);
        }

        updateGroupHeader();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234 && resultCode == RESULT_OK && data != null) {
            String deleteGroupName = data.getStringExtra("deleteGroupName");
            if (deleteGroupName != null && groupList.contains(deleteGroupName)) {
                groupList.remove(deleteGroupName);
                groupMembers.remove(deleteGroupName);
                refreshGroupList();  // 그룹 리스트 갱신
            }
        }
    }
}
