package com.example.design.group;



import android.app.AlertDialog;

import android.content.Intent;

import android.content.SharedPreferences;

import android.graphics.Color;

import android.os.Bundle;

import android.text.InputType;

import android.util.Log;

import android.view.Gravity;

import android.view.View;

import android.widget.Button;

import android.widget.CheckBox;

import android.widget.EditText;

import android.widget.ImageButton;

import android.widget.LinearLayout;

import android.widget.ScrollView;

import android.widget.TextView;

import android.widget.Toast;

import java.util.HashMap;

import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;



import com.example.design.R; // R.layout.activity_group에 접근하기 위해 필요

import com.example.design.login.LoginActivity; // 로그인 액티비티로 이동하기 위해 필요

import com.google.firebase.firestore.DocumentSnapshot;



import java.util.ArrayList;

import java.util.HashSet;

import java.util.List;

import java.util.Map;

import java.util.Set;



public class GroupActivity extends AppCompatActivity {

    private LinearLayout layoutGroups;

    private LinearLayout layoutFriends;

    private LinearLayout layoutInvitations;

    private LinearLayout layoutFriendRequests; // 친구 요청 레이아웃



    private Set<String> friendSet = new HashSet<>();

    private List<GroupItem> groupList = new ArrayList<>();

// groupMembers 맵은 GroupDetailActivity로 멤버 목록을 전달할 때 사용됩니다.

// GroupRepository에서 그룹 로드 시 멤버 목록을 다시 가져와 이 맵을 업데이트해야 합니다.

    private Map<String, Set<String>> groupMembers = new HashMap<>();



    private TextView groupHeader;

    private TextView friendHeader;

    private TextView invitationHeader;



    private GroupRepository groupRepository; // 새롭게 추가된 리포지토리 인스턴스

    private String currentUserId;

    private SharedPreferences sharedPreferences;



    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_group); // activity_group.xml 레이아웃 사용



// GroupRepository 초기화: Activity context를 전달

        groupRepository = new GroupRepository(this);



        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        currentUserId = sharedPreferences.getString("userId", null);



// 로그인 여부 확인 및 리다이렉트

        if (currentUserId == null) {

            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(this, LoginActivity.class));

            finish();

            return;

        }



// UI 요소 초기화 및 이벤트 리스너 설정

        ImageButton btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());



        Button btnCreateGroup = findViewById(R.id.btnCreateGroup);

        ImageButton btnAddFriend = findViewById(R.id.btnAddFriend);



        layoutGroups = findViewById(R.id.layoutGroups);

        layoutFriends = findViewById(R.id.layoutFriends);

        layoutInvitations = findViewById(R.id.layoutInvitations);

        layoutFriendRequests = findViewById(R.id.layoutFriendRequests);



        btnAddFriend.setOnClickListener(v -> showAddFriendDialog());

        btnCreateGroup.setOnClickListener(v -> showCreateGroupDialog());



// 헤더 TextView 초기화 및 추가

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



        TextView friendRequestHeader = new TextView(this);

        friendRequestHeader.setText("🔔 친구 요청");

        friendRequestHeader.setTextSize(18f);

        friendRequestHeader.setTextColor(Color.BLACK);

        friendRequestHeader.setTypeface(null, android.graphics.Typeface.BOLD);

        friendRequestHeader.setPadding(16, 32, 16, 8);

        layoutFriendRequests.addView(friendRequestHeader);



        invitationHeader = new TextView(this);

        invitationHeader.setText("📩 그룹 초대 (0)");

        invitationHeader.setTextSize(18f);

        invitationHeader.setTextColor(Color.BLACK);

        invitationHeader.setTypeface(null, android.graphics.Typeface.BOLD);

        invitationHeader.setPadding(16, 32, 16, 8);

        layoutInvitations.addView(invitationHeader);



// 데이터 로드

        loadFriendsFromFirestore();

        loadGroupsFromFirestore();

        loadInvitations();

        loadFriendRequests();

    }



    /**

     * Firestore에서 친구 목록을 불러와 UI를 갱신합니다.

     */

    private void loadFriendsFromFirestore() {

        groupRepository.loadFriends(currentUserId, new GroupRepository.FirestoreCallback<List<String>>() {

            @Override

            public void onSuccess(List<String> friends) {

                friendSet.clear();

                friendSet.addAll(friends);

                refreshFriendList();

            }



            @Override

            public void onFailure(Exception e) {

// GroupRepository에서 Toast 메시지를 처리하므로 여기서는 추가 작업이 불필요

            }

        });

    }



    /**

     * 친구 목록 UI를 새로 고칩니다.

     */

    private void refreshFriendList() {

        layoutFriends.removeAllViews();

        layoutFriends.addView(friendHeader); // 헤더 다시 추가



        for (String friendId : friendSet) {

            addFriendView(friendId);

        }

        updateFriendHeader();

    }



    /**

     * 현재 사용자에게 온 친구 요청 목록을 불러와 UI를 갱신합니다.

     */

    private void loadFriendRequests() {

        layoutFriendRequests.removeAllViews(); // 기존 요청 삭제 후 다시 로드

        TextView friendRequestHeader = new TextView(this); // 친구 요청 헤더 다시 추가

        friendRequestHeader.setText("🔔 친구 요청");

        friendRequestHeader.setTextSize(18f);

        friendRequestHeader.setTextColor(Color.BLACK);

        friendRequestHeader.setTypeface(null, android.graphics.Typeface.BOLD);

        friendRequestHeader.setPadding(16, 32, 16, 8);

        layoutFriendRequests.addView(friendRequestHeader);



        groupRepository.loadFriendRequests(currentUserId, new GroupRepository.FirestoreCallback<com.google.firebase.firestore.QuerySnapshot>() {

            @Override

            public void onSuccess(com.google.firebase.firestore.QuerySnapshot querySnapshot) {

                for (DocumentSnapshot doc : querySnapshot.getDocuments()) {

                    String requestId = doc.getId();

                    String fromUserId = doc.getString("fromUserId");

                    showFriendRequest(requestId, fromUserId);

                }

            }



            @Override

            public void onFailure(Exception e) {

// GroupRepository에서 Toast 메시지를 처리하므로 여기서는 추가 작업이 불필요

            }

        });

    }



    /**

     * 친구 추가 다이얼로그를 표시합니다.

     */

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

                        groupRepository.sendFriendRequest(currentUserId, friendId, new GroupRepository.FirestoreCallback<Void>() {

                            @Override

                            public void onSuccess(Void result) {

// 요청 성공 시 Toast는 GroupRepository에서 처리됨

// 친구 요청 목록 및 친구 목록 갱신 (자동 업데이트)

                                loadFriendRequests();

                            }



                            @Override

                            public void onFailure(Exception e) {

// 실패 시 Toast는 GroupRepository에서 처리됨

                            }

                        });

                    } else {

                        Toast.makeText(this, "이미 친구이거나 입력이 잘못되었어요.", Toast.LENGTH_SHORT).show();

                    }

                })

                .setNegativeButton("취소", null)

                .show();

    }



    /**

     * 친구 목록에 친구 뷰를 추가합니다.

     *

     * @param friendId 친구 ID

     */

    private void addFriendView(String friendId) {

        TextView textView = new TextView(this);

        textView.setText(friendId);

        textView.setTextSize(16f);

        textView.setPadding(16, 16, 16, 16);

        textView.setTextColor(Color.BLACK);



// 친구 이름을 길게 눌렀을 때 삭제 다이얼로그 표시

        textView.setOnLongClickListener(v -> {

            showDeleteFriendDialog(friendId);

            return true; // 롱클릭 이벤트를 소비했음을 알림

        });



        View divider = new View(this);

        divider.setLayoutParams(new LinearLayout.LayoutParams(

                LinearLayout.LayoutParams.MATCH_PARENT, 1));

        divider.setBackgroundColor(Color.LTGRAY);



        layoutFriends.addView(textView);

        layoutFriends.addView(divider);

    }



    /**

     * 친구 삭제 확인 다이얼로그를 표시합니다.

     *

     * @param friendIdToDelete 삭제할 친구 ID

     */

    private void showDeleteFriendDialog(String friendIdToDelete) {

        new AlertDialog.Builder(this)

                .setTitle("친구 삭제")

                .setMessage("정말로 " + friendIdToDelete + "님을 친구 목록에서 삭제하시겠어요?")

                .setPositiveButton("삭제", (dialog, which) -> {

                    groupRepository.deleteFriend(currentUserId, friendIdToDelete, new GroupRepository.FirestoreCallback<Void>() {

                        @Override

                        public void onSuccess(Void result) {

                            friendSet.remove(friendIdToDelete); // 로컬 친구 목록에서도 제거

                            refreshFriendList(); // UI 갱신

                        }



                        @Override

                        public void onFailure(Exception e) {

// GroupRepository에서 Toast 메시지를 처리하므로 여기서는 추가 작업이 불필요

                        }

                    });

                })

                .setNegativeButton("취소", null)

                .show();

    }



    /**

     * 그룹 생성 다이얼로그를 표시합니다.

     */

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

                    if (!groupName.isEmpty() && !groupNameExists(groupName)) {

                        Set<String> selectedMembers = new HashSet<>();

                        for (CheckBox cb : checkBoxes) {

                            if (cb.isChecked()) {

                                selectedMembers.add(cb.getText().toString());

                            }

                        }

                        selectedMembers.add(currentUserId); // 본인도 멤버에 포함



                        groupRepository.createGroup(currentUserId, groupName, selectedMembers, new GroupRepository.FirestoreCallback<GroupItem>() {

                            @Override

                            public void onSuccess(GroupItem newGroup) {

// 기존 그룹 목록을 비우고 새 그룹을 추가합니다. (사용자는 단일 그룹에만 속할 수 있으므로)

                                groupList.clear();

                                groupMembers.clear();

                                groupList.add(newGroup);

                                groupMembers.put(newGroup.groupName, selectedMembers); // 모든 멤버 포함

                                refreshGroupList(); // UI 갱신

                                loadInvitations(); // 새 그룹 생성으로 인해 초대 목록이 영향을 받을 수 있으므로 새로고침

                            }



                            @Override

                            public void onFailure(Exception e) {

// GroupRepository에서 Toast 메시지를 처리하므로 여기서는 추가 작업이 불필요

                            }

                        });

                    } else {

                        Toast.makeText(this, "그룹 이름이 비어있거나 중복돼요.", Toast.LENGTH_SHORT).show();

                    }

                })

                .setNegativeButton("취소", null)

                .show();

    }



    /**

     * 그룹 이름이 이미 존재하는지 확인합니다.

     *

     * @param groupName 확인할 그룹 이름

     * @return 존재하면 true, 아니면 false

     */

    private boolean groupNameExists(String groupName) {

        for (GroupItem group : groupList) {

            if (group.groupName.equals(groupName)) return true;

        }

        return false;

    }



    /**

     * Firestore에서 현재 사용자가 속한 그룹을 불러와 UI를 갱신합니다.

     * (현재는 사용자가 하나의 그룹에만 속할 수 있도록 로직이 구현됨)

     */

    private void loadGroupsFromFirestore() {

        layoutGroups.removeAllViews();

        layoutGroups.addView(groupHeader);



        groupRepository.loadCurrentGroup(currentUserId, new GroupRepository.FirestoreCallback<GroupItem>() {

            @Override

            public void onSuccess(GroupItem groupItem) {

                groupList.clear();

                groupMembers.clear(); // 그룹 멤버 맵 초기화

                if (groupItem != null) {

                    groupList.add(groupItem);

// 그룹 멤버 정보도 가져와서 groupMembers 맵에 업데이트

                    groupRepository.loadGroupMembers(groupItem.groupId, new GroupRepository.FirestoreCallback<List<String>>() {

                        @Override

                        public void onSuccess(List<String> members) {

                            groupMembers.put(groupItem.groupName, new HashSet<>(members));

                            addGroupView(groupItem);

                            updateGroupHeader();

                        }



                        @Override

                        public void onFailure(Exception e) {

                            Log.e("GroupActivity", "그룹 멤버 로드 실패 (UI 업데이트 중): " + e.getMessage());

                            updateGroupHeader(); // 멤버 로드 실패해도 헤더는 업데이트

                        }

                    });

                } else {

                    updateGroupHeader();

                }

            }



            @Override

            public void onFailure(Exception e) {

// GroupRepository에서 Toast 메시지를 처리하므로 여기서는 추가 작업이 불필요

                updateGroupHeader();

            }

        });

    }



    /**

     * 그룹 목록에 그룹 뷰를 추가합니다.

     *

     * @param groupItem 추가할 그룹 아이템

     */

    private void addGroupView(GroupItem groupItem) {

        TextView groupView = new TextView(this);

        groupView.setText(groupItem.groupName);

        groupView.setTextSize(16f);

        groupView.setPadding(16, 16, 16, 16);

        groupView.setTextColor(Color.BLACK);



// 그룹 클릭 시 GroupDetailActivity로 이동

        groupView.setOnClickListener(v -> {

            Intent intent = new Intent(GroupActivity.this, GroupDetailActivity.class);

            intent.putExtra("groupId", groupItem.groupId);

            intent.putExtra("groupName", groupItem.groupName);



// groupMembers 맵은 groupName을 키로 사용하므로, groupName으로 멤버 목록을 가져옵니다.

            ArrayList<String> members = new ArrayList<>(groupMembers.getOrDefault(groupItem.groupName, new HashSet<>()));

            intent.putStringArrayListExtra("memberList", members);



            startActivityForResult(intent, 1234); // 결과 받을 수 있도록 시작

        });



        View divider = new View(this);

        divider.setLayoutParams(new LinearLayout.LayoutParams(

                LinearLayout.LayoutParams.MATCH_PARENT, 1));

        divider.setBackgroundColor(Color.LTGRAY);



        layoutGroups.addView(groupView);

        layoutGroups.addView(divider);

    }



    /**

     * 그룹 목록 UI를 새로 고칩니다.

     */

    private void refreshGroupList() {

        layoutGroups.removeAllViews();

        layoutGroups.addView(groupHeader);



        for (GroupItem group : groupList) {

            addGroupView(group);

        }

        updateGroupHeader();

    }



    /**

     * 그룹 헤더 텍스트를 업데이트합니다.

     */

    private void updateGroupHeader() {

        groupHeader.setText("📁 생성된 그룹 (" + groupList.size() + ")");

    }



    /**

     * 친구 헤더 텍스트를 업데이트합니다.

     */

    private void updateFriendHeader() {

        friendHeader.setText("👥 친구 목록 (" + friendSet.size() + ")");

    }



    /**

     * 초대 헤더 텍스트를 업데이트합니다.

     *

     * @param count 현재 초대 개수

     */

    private void updateInvitationHeader(int count) {

        invitationHeader.setText("📩 그룹 초대 (" + count + ")");

    }



    @Override

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1234 && resultCode == RESULT_OK) {

// GroupDetailActivity에서 그룹 정보가 변경되었을 수 있으므로 다시 로드

            loadGroupsFromFirestore();

            loadInvitations(); // 혹시 그룹 탈퇴/삭제로 인해 초대 목록이 영향을 받을 수 있으므로 새로고침

        }

    }



    /**

     * Firestore에서 그룹 초대 목록을 불러와 UI를 갱신합니다.

     */

    private void loadInvitations() {

        layoutInvitations.removeAllViews();

        layoutInvitations.addView(invitationHeader);



        groupRepository.loadInvitations(currentUserId, new GroupRepository.FirestoreCallback<com.google.firebase.firestore.QuerySnapshot>() {

            @Override

            public void onSuccess(com.google.firebase.firestore.QuerySnapshot querySnapshot) {

                int count = querySnapshot.size();

                updateInvitationHeader(count);



                for (DocumentSnapshot doc : querySnapshot.getDocuments()) {

                    String invitationId = doc.getId();

                    String groupId = doc.getString("groupId");

                    String inviterUserId = doc.getString("inviterUserId");



                    addInvitationView(invitationId, groupId, inviterUserId);

                }

            }



            @Override

            public void onFailure(Exception e) {

// GroupRepository에서 Toast 메시지를 처리하므로 여기서는 추가 작업이 불필요

            }

        });

    }



    /**

     * 초대 목록에 초대 뷰를 추가합니다.

     *

     * @param invitationId 초대 문서 ID

     * @param groupId 초대된 그룹 ID

     * @param inviterUserId 초대한 사용자 ID

     */

    private void addInvitationView(String invitationId, String groupId, String inviterUserId) {

        LinearLayout layout = new LinearLayout(this);

        layout.setOrientation(LinearLayout.HORIZONTAL);

        layout.setPadding(16, 16, 16, 16);

        layout.setGravity(Gravity.CENTER_VERTICAL);



        TextView textView = new TextView(this);

// 그룹 이름을 가져오기 위해 groupId로 groups 컬렉션을 조회합니다. (비동기)

        groupRepository.getGroupName(groupId, new GroupRepository.FirestoreCallback<String>() {

            @Override

            public void onSuccess(String groupName) {

                if (groupName != null) {

                    textView.setText("그룹 초대: " + groupName + " (초대한 사람: " + inviterUserId + ")");

                } else {

                    textView.setText("그룹 초대: (알 수 없음) (초대한 사람: " + inviterUserId + ")");

                }

            }



            @Override

            public void onFailure(Exception e) {

                textView.setText("그룹 초대: (로드 실패) (초대한 사람: " + inviterUserId + ")");

                Log.e("GroupActivity", "그룹 이름 로드 실패: " + e.getMessage());

            }

        });



        textView.setTextColor(Color.BLACK);

        textView.setLayoutParams(new LinearLayout.LayoutParams(0,

                LinearLayout.LayoutParams.WRAP_CONTENT, 1));



        Button btnAccept = new Button(this);

        btnAccept.setText("수락");

        btnAccept.setOnClickListener(v -> acceptInvitation(invitationId, groupId));



        Button btnReject = new Button(this);

        btnReject.setText("거절");

        btnReject.setOnClickListener(v -> rejectInvitation(invitationId));



        layout.addView(textView);

        layout.addView(btnAccept);

        layout.addView(btnReject);



        layoutInvitations.addView(layout);

    }



    /**

     * 그룹 초대를 수락합니다.

     *

     * @param invitationId 초대 문서 ID

     * @param groupId 수락할 그룹 ID

     */

    private void acceptInvitation(String invitationId, String groupId) {

        groupRepository.acceptInvitation(invitationId, groupId, currentUserId, new GroupRepository.FirestoreCallback<Void>() {

            @Override

            public void onSuccess(Void result) {

                Toast.makeText(GroupActivity.this, "그룹 초대 수락", Toast.LENGTH_SHORT).show();

                loadGroupsFromFirestore(); // 그룹 목록 갱신

                loadInvitations(); // 초대 목록 갱신

            }



            @Override

            public void onFailure(Exception e) {

// GroupRepository에서 Toast 메시지를 처리하므로 여기서는 추가 작업이 불필요

            }

        });

    }



    /**

     * 그룹 초대를 거절합니다.

     *

     * @param invitationId 거절할 초대 문서 ID

     */

    private void rejectInvitation(String invitationId) {

        groupRepository.rejectInvitation(invitationId, new GroupRepository.FirestoreCallback<Void>() {

            @Override

            public void onSuccess(Void result) {

// GroupRepository에서 Toast 메시지를 처리함

                loadInvitations(); // 초대 목록 갱신

            }



            @Override

            public void onFailure(Exception e) {

// GroupRepository에서 Toast 메시지를 처리함

            }

        });

    }



    /**

     * 친구 요청 뷰를 추가합니다.

     *

     * @param requestId 친구 요청 문서 ID

     * @param fromUserId 요청을 보낸 사용자 ID

     */

    private void showFriendRequest(String requestId, String fromUserId) {

        LinearLayout layout = new LinearLayout(this);

        layout.setOrientation(LinearLayout.HORIZONTAL);

        layout.setPadding(16, 16, 16, 16);

        layout.setGravity(Gravity.CENTER_VERTICAL);



        TextView textView = new TextView(this);

        textView.setText(fromUserId + "님이 친구 요청을 보냈어요.");

        textView.setTextColor(Color.BLACK);

        textView.setTextSize(16f);

        textView.setLayoutParams(new LinearLayout.LayoutParams(0,

                LinearLayout.LayoutParams.WRAP_CONTENT, 1));



        Button btnAccept = new Button(this);

        btnAccept.setText("수락");

        btnAccept.setOnClickListener(v -> acceptFriendRequest(requestId, fromUserId));



        Button btnReject = new Button(this);

        btnReject.setText("거절");

        btnReject.setOnClickListener(v -> rejectFriendRequest(requestId));



        layout.addView(textView);

        layout.addView(btnAccept);

        layout.addView(btnReject);



        layoutFriendRequests.addView(layout);



        View divider = new View(this);

        divider.setLayoutParams(new LinearLayout.LayoutParams(

                LinearLayout.LayoutParams.MATCH_PARENT, 1));

        divider.setBackgroundColor(Color.LTGRAY);

        layoutFriendRequests.addView(divider);

    }



    /**

     * 친구 요청을 수락합니다.

     *

     * @param requestId 친구 요청 문서 ID

     * @param fromUserId 요청을 보낸 사용자 ID

     */

    private void acceptFriendRequest(String requestId, String fromUserId) {

        groupRepository.acceptFriendRequest(requestId, currentUserId, fromUserId, new GroupRepository.FirestoreCallback<Void>() {

            @Override

            public void onSuccess(Void result) {

                loadFriendRequests(); // 친구 요청 목록 갱신

                loadFriendsFromFirestore(); // 친구 목록 갱신 (새로 추가된 친구를 반영)

            }



            @Override

            public void onFailure(Exception e) {

// GroupRepository에서 Toast 메시지를 처리하므로 여기서는 추가 작업이 불필요

            }

        });

    }



    /**

     * 친구 요청을 거절합니다.

     *

     * @param requestId 친구 요청 문서 ID

     */

    private void rejectFriendRequest(String requestId) {

        groupRepository.rejectFriendRequest(requestId, new GroupRepository.FirestoreCallback<Void>() {

            @Override

            public void onSuccess(Void result) {

                loadFriendRequests(); // 친구 요청 목록 갱신

            }



            @Override

            public void onFailure(Exception e) {

// GroupRepository에서 Toast 메시지를 처리하므로 여기서는 추가 작업이 불필요

            }

        });

    }
}