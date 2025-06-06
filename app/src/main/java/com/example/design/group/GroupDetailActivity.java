package com.example.design.group;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.design.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GroupDetailActivity extends AppCompatActivity {

    private String groupName;
    private List<String> memberList;

    private TextView textGroupName;
    private LinearLayout layoutMembers;

    private Button btnEditMembers;
    private Button btnDeleteGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        textGroupName = findViewById(R.id.textGroupName);
        layoutMembers = findViewById(R.id.layoutMembers);

        btnEditMembers = findViewById(R.id.btnEditMembers);
        btnDeleteGroup = findViewById(R.id.btnDeleteGroup);

        // 인텐트에서 그룹 이름과 멤버 리스트 받기
        groupName = getIntent().getStringExtra("groupName");
        memberList = getIntent().getStringArrayListExtra("memberList");
        if (memberList == null) memberList = new ArrayList<>();

        textGroupName.setText(groupName);

        // 초기 멤버 리스트 표시
        refreshMemberList();

        // 그룹명 수정
        textGroupName.setOnClickListener(v -> showEditGroupNameDialog());

        // 멤버 수정
        btnEditMembers.setOnClickListener(v -> showEditMembersDialog());

        // 그룹 삭제 → 팝업 추가!!
        btnDeleteGroup.setOnClickListener(v -> {
            new AlertDialog.Builder(GroupDetailActivity.this)
                    .setTitle("그룹 삭제")
                    .setMessage("'" + groupName + "' 그룹을 삭제하시겠습니까?")
                    .setPositiveButton("삭제", (dialog, which) -> {
                        // 그룹 이름을 결과로 넘김
                        getIntent().putExtra("deleteGroupName", groupName);
                        setResult(RESULT_OK, getIntent());
                        finish();
                    })
                    .setNegativeButton("취소", null)
                    .show();
        });
    }

    private void refreshMemberList() {
        layoutMembers.removeAllViews();

        for (String member : memberList) {
            TextView textView = new TextView(this);
            textView.setText(member);
            textView.setTextSize(16f);
            textView.setPadding(16, 16, 16, 16);
            textView.setTextColor(Color.BLACK);

            View divider = new View(this);
            LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 1);
            divider.setLayoutParams(dividerParams);
            divider.setBackgroundColor(Color.LTGRAY);

            layoutMembers.addView(textView);
            layoutMembers.addView(divider);
        }
    }

    private void showEditGroupNameDialog() {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(groupName);

        new AlertDialog.Builder(this)
                .setTitle("그룹명 수정")
                .setView(input)
                .setPositiveButton("확인", (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        groupName = newName;
                        textGroupName.setText(groupName);
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }

    private void showEditMembersDialog() {
        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(30, 30, 30, 30);

        // 전체 친구 목록 가져오기 (GroupManager 사용)
        Set<String> allFriends = GroupManager.getInstance().getAllFriends();

        List<CheckBox> checkBoxes = new ArrayList<>();
        for (String friendId : allFriends) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(friendId);
            checkBox.setTextColor(Color.BLACK);
            if (memberList.contains(friendId)) {
                checkBox.setChecked(true);  // 기존에 포함된 멤버는 체크 상태로 표시
            }
            checkBoxes.add(checkBox);
            dialogLayout.addView(checkBox);
        }

        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(dialogLayout);

        new AlertDialog.Builder(this)
                .setTitle("그룹 구성원 수정")
                .setView(scrollView)
                .setPositiveButton("저장", (dialog, which) -> {
                    memberList.clear();
                    for (CheckBox cb : checkBoxes) {
                        if (cb.isChecked()) {
                            memberList.add(cb.getText().toString());
                        }
                    }
                    refreshMemberList();  // 수정 후 갱신
                })
                .setNegativeButton("취소", null)
                .show();
    }
}
