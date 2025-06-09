package com.example.design.group;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent; // Intent import 추가 (혹시 몰라)
import android.content.SharedPreferences;
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
import java.util.Collections; // Collections import 추가 (정렬용)

public class GroupDetailActivity extends AppCompatActivity {

    private String groupId;
    private String groupName;
    private List<String> memberList; // 현재 그룹의 멤버 목록

    private TextView textGroupName;
    private LinearLayout layoutMembers;

    private Button btnEditMembers;
    private Button btnDeleteGroup;

    private GroupRepository groupRepository;

    private String currentUserId; // 현재 사용자 ID 필드 (SharedPreferences용)

    private static final String PREF_NAME = "MyPrefs"; // SharedPreferences 이름 (GroupRepository와 동일)
    private static final String KEY_USER_ID = "userId"; // SharedPreferences 키 (GroupRepository와 동일)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        groupRepository = new GroupRepository(this); // GroupRepository 초기화 (context 전달)

        // SharedPreferences에서 currentUserId 가져오기
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        currentUserId = prefs.getString(KEY_USER_ID, null);

        Log.d("GroupDetailActivity", "Loaded currentUserId: " + (currentUserId != null ? currentUserId : "NULL"));

        // currentUserId가 없으면 액티비티 진행 불가
        if (currentUserId == null || currentUserId.isEmpty()) {
            Toast.makeText(this, "사용자 ID를 불러올 수 없습니다. 로그인이 필요합니다.", Toast.LENGTH_LONG).show();
            finish(); // 사용자 ID 없으면 액티비티 종료
            return;
        }

        textGroupName = findViewById(R.id.textGroupName);
        layoutMembers = findViewById(R.id.layoutMembers);
        btnEditMembers = findViewById(R.id.btnEditMembers);
        btnDeleteGroup = findViewById(R.id.btnDeleteGroup);

        // 인텐트에서 그룹 ID, 그룹 이름, 멤버 리스트 받기
        groupId = getIntent().getStringExtra("groupId");
        groupName = getIntent().getStringExtra("groupName");
        memberList = getIntent().getStringArrayListExtra("memberList");
        if (memberList == null) memberList = new ArrayList<>();

        if (groupId == null || groupId.isEmpty()) {
            Toast.makeText(this, "그룹 ID를 찾을 수 없습니다.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        textGroupName.setText(groupName);

        // 초기 멤버 리스트 표시
        refreshMemberList();

        // 그룹명 수정 (여기는 그룹명 업데이트 기능을 제거했으므로 Firestore 연동 없음)
        textGroupName.setOnClickListener(v -> showEditGroupNameDialog());

        // 멤버 수정 클릭 리스너
        btnEditMembers.setOnClickListener(v -> {
            // GroupRepository를 통해 친구 목록을 비동기로 가져옵니다.
            // currentUserId는 이미 GroupDetailActivity에서 가지고 있으므로,
            // GroupRepository.getAllFriends()로 전달합니다.
            groupRepository.getAllFriends(currentUserId, new GroupRepository.FirestoreCallback<Set<String>>() {
                @Override
                public void onSuccess(Set<String> allFriends) {
                    showEditMembersDialog(allFriends); // 친구 목록을 다이얼로그에 전달
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(GroupDetailActivity.this, "친구 목록을 불러오는데 실패했습니다: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("GroupDetailActivity", "친구 목록 불러오기 실패: " + e.getMessage());
                }
            });
        });

        // 그룹 삭제 → 팝업 추가!!
        btnDeleteGroup.setOnClickListener(v -> {
            new AlertDialog.Builder(GroupDetailActivity.this)
                    .setTitle("그룹 삭제")
                    .setMessage("'" + groupName + "' 그룹을 삭제하시겠습니까?")
                    .setPositiveButton("삭제", (dialog, which) -> {
                        groupRepository.deleteGroup(groupId, new GroupRepository.FirestoreCallback<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                                Toast.makeText(GroupDetailActivity.this, "'" + groupName + "' 그룹이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK); // 그룹 목록 갱신을 위해 RESULT_OK 설정
                                finish();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.e("GroupDetailActivity", "그룹 삭제 실패: " + e.getMessage());
                                // GroupRepository에서 이미 토스트 메시지를 처리했으므로 여기서는 로그만 남김
                            }
                        });
                    })
                    .setNegativeButton("취소", null)
                    .show();
        });
    }

    private void refreshMemberList() {
        layoutMembers.removeAllViews();

        if (memberList.isEmpty()) {
            TextView noMembersText = new TextView(this);
            noMembersText.setText("그룹에 구성원이 없습니다.");
            noMembersText.setTextSize(16f);
            noMembersText.setPadding(16, 16, 16, 16);
            noMembersText.setTextColor(Color.GRAY);
            layoutMembers.addView(noMembersText);
        } else {
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
                    if (!newName.isEmpty() && !newName.equals(groupName)) {
                        // Firestore에 반영하지 않고 UI에만 임시 적용하는 로직
                        groupName = newName;
                        textGroupName.setText(groupName);
                        Toast.makeText(GroupDetailActivity.this, "그룹명이 성공적으로 변경되었습니다. (Firestore에는 반영되지 않음)", Toast.LENGTH_SHORT).show();

                    } else if (newName.isEmpty()) {
                        Toast.makeText(this, "그룹명은 비워둘 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }

    private void showEditMembersDialog(Set<String> allFriendsSet) {
        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(30, 30, 30, 30);

        List<CheckBox> checkBoxes = new ArrayList<>();

        // 1. 현재 사용자(자기 자신)를 위한 체크박스 추가
        // currentUserId가 유효한지 다시 확인
        if (currentUserId != null && !currentUserId.isEmpty()) {
            CheckBox selfCheckBox = new CheckBox(this);
            selfCheckBox.setText("나 (" + currentUserId + ")"); // '나' 라는 텍스트와 함께 사용자 ID 표시
            selfCheckBox.setTextColor(Color.BLACK);
            selfCheckBox.setChecked(true); // 자기 자신은 항상 체크된 상태로 시작
            selfCheckBox.setEnabled(false); // 자기 자신은 체크 해제 불가능하도록 설정 (그룹 탈퇴는 다른 방식으로)
            checkBoxes.add(selfCheckBox);
            dialogLayout.addView(selfCheckBox);

            // 자기 자신 체크박스 아래 구분선
            View dividerForSelf = new View(this);
            LinearLayout.LayoutParams dividerParamsSelf = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 1);
            dividerForSelf.setLayoutParams(dividerParamsSelf);
            dividerForSelf.setBackgroundColor(Color.LTGRAY);
            dialogLayout.addView(dividerForSelf);
        } else {
            // 이 상황은 onCreate에서 이미 처리되었지만, 만약을 위해 로그 추가
            Log.e("GroupDetailActivity", "currentUserId is null when trying to build members dialog.");
        }


        // 2. 친구 목록 추가
        // 친구 목록이 비어 있는 경우 메시지 (자기 자신은 항상 있으므로 "친구"만 해당)
        if (allFriendsSet.isEmpty() && (currentUserId == null || currentUserId.isEmpty())) {
            // currentUserId도 없고 친구도 없으면 완전히 비어있다는 메시지
            TextView noFriendsText = new TextView(this);
            noFriendsText.setText("추가할 친구가 없습니다. 먼저 친구를 추가해 주세요.");
            noFriendsText.setTextColor(Color.GRAY);
            dialogLayout.addView(noFriendsText);
        } else {
            List<String> sortedFriends = new ArrayList<>(allFriendsSet);
            Collections.sort(sortedFriends); // 이름순 정렬

            for (String friendId : sortedFriends) {
                // 자기 자신은 이미 추가했으므로 친구 목록에서 제외
                if (friendId.equals(currentUserId)) {
                    continue;
                }

                CheckBox checkBox = new CheckBox(this);
                checkBox.setText(friendId);
                checkBox.setTextColor(Color.BLACK);
                if (memberList.contains(friendId)) {
                    checkBox.setChecked(true); // 기존에 포함된 멤버는 체크 상태로 표시
                }
                checkBoxes.add(checkBox); // 리스트에 추가 (저장 시 사용)
                dialogLayout.addView(checkBox); // UI에 추가

                // 각 친구 체크박스 아래 구분선
                View divider = new View(this);
                LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 1);
                divider.setLayoutParams(dividerParams);
                divider.setBackgroundColor(Color.LTGRAY);
                dialogLayout.addView(divider);
            }
        }

        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(dialogLayout);

        new AlertDialog.Builder(this)
                .setTitle("그룹 구성원 수정")
                .setView(scrollView)
                .setPositiveButton("저장", (dialog, which) -> {
                    List<String> newSelectedMembers = new ArrayList<>();

                    // ⭐ 현재 사용자(자기 자신)를 항상 새 멤버 리스트에 추가
                    // currentUserId가 유효할 경우에만 추가
                    if (currentUserId != null && !currentUserId.isEmpty()) {
                        newSelectedMembers.add(currentUserId);
                    }

                    // 선택된 친구들을 새 멤버 리스트에 추가
                    for (CheckBox cb : checkBoxes) {
                        // '나' 체크박스는 이미 위에서 처리했으므로 건너뜁니다.
                        // 이 조건은 UI에 표시된 텍스트를 기준으로 하므로 정확해야 합니다.
                        if (cb.getText().toString().equals("나 (" + currentUserId + ")")) {
                            continue;
                        }
                        if (cb.isChecked()) {
                            newSelectedMembers.add(cb.getText().toString());
                        }
                    }

                    // ⭐ Firestore에 그룹 멤버 업데이트 요청
                    groupRepository.updateGroupMembers(groupId, newSelectedMembers, new GroupRepository.FirestoreCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            memberList.clear();
                            memberList.addAll(newSelectedMembers); // UI의 멤버 리스트 업데이트
                            refreshMemberList(); // 화면 갱신
                            Toast.makeText(GroupDetailActivity.this, "그룹 구성원이 성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            // GroupRepository에서 이미 토스트 메시지 처리
                            Log.e("GroupDetailActivity", "그룹 구성원 업데이트 실패: " + e.getMessage());
                        }
                    });
                })
                .setNegativeButton("취소", null)
                .show();
    }
}