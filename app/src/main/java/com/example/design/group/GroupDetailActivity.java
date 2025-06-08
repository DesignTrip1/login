package com.example.design.group;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.design.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GroupDetailActivity extends AppCompatActivity {

    private String groupId; // <-- 그룹 ID 추가 (삭제에 필요)
    private String groupName;
    private List<String> memberList;

    private TextView textGroupName;
    private LinearLayout layoutMembers;

    private Button btnEditMembers;
    private Button btnDeleteGroup;

    private GroupRepository groupRepository; // <-- GroupRepository 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        // GroupRepository 초기화
        groupRepository = new GroupRepository(this); // <-- 이 부분 추가

        textGroupName = findViewById(R.id.textGroupName);
        layoutMembers = findViewById(R.id.layoutMembers);

        btnEditMembers = findViewById(R.id.btnEditMembers);
        btnDeleteGroup = findViewById(R.id.btnDeleteGroup);

        // 인텐트에서 그룹 ID, 그룹 이름, 멤버 리스트 받기
        groupId = getIntent().getStringExtra("groupId"); // <-- 그룹 ID 받기
        groupName = getIntent().getStringExtra("groupName");
        memberList = getIntent().getStringArrayListExtra("memberList");
        if (memberList == null) memberList = new ArrayList<>();

        // groupId가 없는 경우는 치명적인 오류이므로 확인 로직 필요
        if (groupId == null || groupId.isEmpty()) {
            Toast.makeText(this, "그룹 ID를 찾을 수 없습니다.", Toast.LENGTH_LONG).show();
            finish(); // 그룹 ID 없이 진행할 수 없으므로 액티비티 종료
            return;
        }

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
                        // !!!! 이 부분에서 GroupRepository를 사용하여 Firestore에서 그룹을 삭제해야 합니다. !!!!
                        groupRepository.deleteGroup(groupId, new GroupRepository.FirestoreCallback<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                                // Firestore 삭제 성공
                                Toast.makeText(GroupDetailActivity.this, "'" + groupName + "' 그룹이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                // GroupActivity로 돌아가서 그룹 목록을 갱신하도록 RESULT_OK 설정
                                setResult(RESULT_OK);
                                finish(); // GroupDetailActivity 종료
                            }

                            @Override
                            public void onFailure(Exception e) {
                                // Firestore 삭제 실패 (GroupRepository에서 Toast 메시지 처리)
                                // Logcat에 오류를 기록하여 디버깅에 도움
                                Log.e("GroupDetailActivity", "그룹 삭제 실패: " + e.getMessage());
                            }
                        });
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
