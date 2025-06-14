package com.example.design.group;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.design.R; // R 파일 import (리소스 ID 사용 위함)

import java.util.ArrayList;
import java.util.List;

public class GroupDetailActivity extends AppCompatActivity {

    private String groupId;
    private GroupItem currentGroupItem; // 그룹의 모든 상세 정보를 담을 GroupItem 객체

    private TextView textGroupName;
    private LinearLayout layoutMembers;

    private Button btnLeaveGroup; // '그룹 탈퇴' 버튼
    private Button btnDeleteGroup; // '그룹 삭제' 버튼
    // private Button btnEditMembers; // ⭐ 제거됨: 그룹원 수정 버튼

    private GroupRepository groupRepository;

    private String currentUserId;

    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_USER_ID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail); // ⭐ 레이아웃 파일 이름이 activity_group_detail.xml인지 확인합니다.

        groupRepository = new GroupRepository(this);

        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        currentUserId = prefs.getString(KEY_USER_ID, null);

        Log.d("GroupDetailActivity", "Loaded currentUserId: " + (currentUserId != null ? currentUserId : "NULL"));

        if (currentUserId == null || currentUserId.isEmpty()) {
            Toast.makeText(this, "사용자 ID를 불러올 수 없습니다. 로그인이 필요합니다.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        textGroupName = findViewById(R.id.textGroupName);
        layoutMembers = findViewById(R.id.layoutMembers);
        btnLeaveGroup = findViewById(R.id.btnLeaveGroup);
        btnDeleteGroup = findViewById(R.id.btnDeleteGroup);
        // btnEditMembers = findViewById(R.id.btnEditMembers); // ⭐ 제거됨: findViewById

        groupId = getIntent().getStringExtra("groupId"); // 전달받은 groupId 사용

        if (groupId == null || groupId.isEmpty()) {
            Toast.makeText(this, "그룹 ID를 찾을 수 없습니다.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        loadGroupDetails(); // 그룹 상세 정보를 Firestore에서 로드합니다.

        textGroupName.setOnClickListener(v -> showEditGroupNameDialog());

        btnLeaveGroup.setOnClickListener(v -> {
            // ⭐ 그룹 탈퇴 로직
            new AlertDialog.Builder(GroupDetailActivity.this)
                    .setTitle("그룹 탈퇴")
                    .setMessage("정말로 '" + (currentGroupItem != null ? currentGroupItem.groupName : "이 그룹") + "' 그룹을 탈퇴하시겠습니까? 탈퇴하면 다시 가입해야 합니다.")
                    .setPositiveButton("탈퇴", (dialog, which) -> {
                        groupRepository.leaveGroup(groupId, currentUserId, new GroupRepository.FirestoreCallback<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                                Toast.makeText(GroupDetailActivity.this, "'" + (currentGroupItem != null ? currentGroupItem.groupName : "이 그룹") + "' 그룹을 성공적으로 탈퇴했습니다.", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK); // 호출한 액티비티에 변경 사항 알림
                                finish(); // 현재 액티비티 종료
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(GroupDetailActivity.this, "그룹 탈퇴 실패: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                Log.e("GroupDetailActivity", "그룹 탈퇴 실패: " + e.getMessage());
                            }
                        });
                    })
                    .setNegativeButton("취소", null)
                    .show();
        });

        btnDeleteGroup.setOnClickListener(v -> {
            // ⭐ 그룹 삭제 로직
            new AlertDialog.Builder(GroupDetailActivity.this)
                    .setTitle("그룹 삭제")
                    .setMessage("'" + (currentGroupItem != null ? currentGroupItem.groupName : "이 그룹") + "' 그룹을 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.")
                    .setPositiveButton("삭제", (dialog, which) -> {
                        // 그룹장만 그룹 삭제 가능하도록 조건 추가
                        if (currentGroupItem != null && currentUserId.equals(currentGroupItem.creatorId)) {
                            groupRepository.deleteGroup(groupId, new GroupRepository.FirestoreCallback<Void>() {
                                @Override
                                public void onSuccess(Void result) {
                                    Toast.makeText(GroupDetailActivity.this, "'" + (currentGroupItem != null ? currentGroupItem.groupName : "이 그룹") + "' 그룹이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                    setResult(RESULT_OK);
                                    finish();
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Log.e("GroupDetailActivity", "그룹 삭제 실패: " + e.getMessage());
                                    Toast.makeText(GroupDetailActivity.this, "그룹 삭제 실패: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            Toast.makeText(GroupDetailActivity.this, "그룹 삭제는 그룹장만 가능합니다.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("취소", null)
                    .show();
        });

        // ⭐ 제거됨: btnEditMembers.setOnClickListener
    }

    // Firestore에서 그룹 상세 정보를 불러오는 메서드
    private void loadGroupDetails() {
        if (currentUserId == null || currentUserId.isEmpty()) {
            Toast.makeText(this, "사용자 ID가 없어 그룹 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        groupRepository.loadCurrentGroup(currentUserId, new GroupRepository.FirestoreCallback<GroupItem>() {
            @Override
            public void onSuccess(GroupItem groupItem) {
                if (groupItem != null && groupItem.getGroupId().equals(groupId)) {
                    currentGroupItem = groupItem; // GroupItem 객체를 필드에 저장

                    textGroupName.setText(currentGroupItem.groupName);
                    refreshMemberList(); // UI 갱신

                    // 버튼 가시성 제어 (그룹장 여부에 따라)
                    if (currentGroupItem.creatorId != null && currentGroupItem.creatorId.equals(currentUserId)) {
                        btnDeleteGroup.setVisibility(View.VISIBLE); // 그룹장에게 삭제 버튼 보임
                        btnLeaveGroup.setVisibility(View.GONE);     // 그룹장은 보통 탈퇴보다 삭제를 함
                        // btnEditMembers.setVisibility(View.VISIBLE); // ⭐ 제거됨: 멤버 수정 버튼
                    } else {
                        btnDeleteGroup.setVisibility(View.GONE);    // 그룹장이 아니면 삭제 버튼 숨김
                        btnLeaveGroup.setVisibility(View.VISIBLE);  // 그룹장이 아니면 탈퇴 버튼 보임
                        // btnEditMembers.setVisibility(View.GONE); // ⭐ 제거됨: 멤버 수정 버튼
                    }
                } else if (groupItem != null && !groupItem.getGroupId().equals(groupId)) {
                    // 사용자가 다른 그룹에 속해 있으나, 전달된 groupId와 다를 경우
                    Toast.makeText(GroupDetailActivity.this, "현재 속한 그룹 정보와 일치하지 않습니다.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    // 그룹 정보를 찾을 수 없거나, 사용자가 어떤 그룹에도 속해 있지 않은 경우
                    Toast.makeText(GroupDetailActivity.this, "그룹 정보를 찾을 수 없거나, 그룹에 속해있지 않습니다.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(GroupDetailActivity.this, "그룹 정보를 불러오는데 실패했습니다: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("GroupDetailActivity", "그룹 상세 정보 로드 실패 (loadCurrentGroup): " + e.getMessage());
                finish();
            }
        });
    }

    private void refreshMemberList() {
        layoutMembers.removeAllViews();

        List<String> displayMembers = (currentGroupItem != null && currentGroupItem.members != null) ?
                currentGroupItem.members : new ArrayList<>();

        if (displayMembers.isEmpty()) {
            TextView noMembersText = new TextView(this);
            noMembersText.setText("그룹에 구성원이 없습니다.");
            noMembersText.setTextSize(16f);
            noMembersText.setPadding(16, 16, 16, 16);
            noMembersText.setTextColor(Color.GRAY);
            layoutMembers.addView(noMembersText);
        } else {
            for (String member : displayMembers) {
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
    }

    private void showEditGroupNameDialog() {
        if (currentGroupItem == null) {
            Toast.makeText(this, "그룹 정보가 로드되지 않았습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 그룹장만 그룹명 수정 가능하도록 조건 추가
        if (!currentUserId.equals(currentGroupItem.creatorId)) {
            Toast.makeText(this, "그룹명 수정은 그룹장만 가능합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(currentGroupItem.groupName);

        new AlertDialog.Builder(this)
                .setTitle("그룹명 수정")
                .setView(input)
                .setPositiveButton("확인", (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if (!newName.isEmpty() && !newName.equals(currentGroupItem.groupName)) {
                        // Firestore에 그룹명 업데이트 로직 호출
                        groupRepository.updateGroupName(groupId, newName, new GroupRepository.FirestoreCallback<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                                // UI 업데이트
                                currentGroupItem.setGroupName(newName); // GroupItem 객체 업데이트
                                textGroupName.setText(newName); // TextView 업데이트
                                Toast.makeText(GroupDetailActivity.this, "그룹명이 성공적으로 업데이트되었습니다.", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(GroupDetailActivity.this, "그룹명 업데이트 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("GroupDetailActivity", "그룹명 업데이트 실패: " + e.getMessage());
                            }
                        });
                    } else if (newName.isEmpty()) {
                        Toast.makeText(this, "그룹명은 비워둘 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }

    // ⭐ 제거됨: showEditMembersDialog() 메소드
}