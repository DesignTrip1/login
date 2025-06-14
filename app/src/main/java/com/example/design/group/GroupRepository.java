package com.example.design.group;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GroupRepository {
    private FirebaseFirestore db;
    private Context context;

    public GroupRepository(Context context) {
        this.db = FirebaseFirestore.getInstance();
        this.context = context;
    }

    public interface FirestoreCallback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }

    //Firestore에서 현재 사용자의 친구 목록을 불러옵니다.

    public void loadFriends(String userId, FirestoreCallback<List<String>> callback) {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> friends = (List<String>) documentSnapshot.get("friends");
                        callback.onSuccess(friends != null ? friends : new ArrayList<>());
                    } else {
                        callback.onSuccess(new ArrayList<>());
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "친구 목록 로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    callback.onFailure(e);
                });
    }


    //Firestore에서 친구를 삭제합니다 (양방향 삭제).

    public void deleteFriend(String currentUserId, String friendIdToDelete, FirestoreCallback<Void> callback) {
        Task<Void> user1Update = db.collection("users").document(currentUserId)
                .update("friends", FieldValue.arrayRemove(friendIdToDelete));

        Task<Void> user2Update = db.collection("users").document(friendIdToDelete)
                .update("friends", FieldValue.arrayRemove(currentUserId));

        Tasks.whenAllSuccess(user1Update, user2Update)
                .addOnSuccessListener(results -> {
                    Toast.makeText(context, friendIdToDelete + "님을 친구 목록에서 삭제했어요.", Toast.LENGTH_SHORT).show();
                    callback.onSuccess(null);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "친구 삭제 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    callback.onFailure(e);
                });
    }
    //친구 요청을 보냅니다.
    public void sendFriendRequest(String fromUserId, String toUserId, FirestoreCallback<Void> callback) {
        Map<String, Object> request = new HashMap<>();
        request.put("fromUserId", fromUserId);
        request.put("toUserId", toUserId);
        request.put("sentAt", FieldValue.serverTimestamp());

        db.collection("friend_requests")
                .add(request)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(context, "친구 요청을 보냈어요.", Toast.LENGTH_SHORT).show();
                    callback.onSuccess(null);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "친구 요청 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    callback.onFailure(e);
                });
    }

    //현재 사용자에게 온 친구 요청 목록을 불러옵니다.

    public void loadFriendRequests(String toUserId, FirestoreCallback<QuerySnapshot> callback) {
        db.collection("friend_requests")
                .whereEqualTo("toUserId", toUserId)
                .get()
                .addOnSuccessListener(callback::onSuccess)
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "친구 요청 로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    callback.onFailure(e);
                });
    }
    // 친구 요청을 수락합니다.

    public void acceptFriendRequest(String requestId, String currentUserId, String fromUserId, FirestoreCallback<Void> callback) {
        Task<Void> user1Update = db.collection("users").document(currentUserId)
                .update("friends", FieldValue.arrayUnion(fromUserId));

        Task<Void> user2Update = db.collection("users").document(fromUserId)
                .update("friends", FieldValue.arrayUnion(currentUserId));

        Tasks.whenAllSuccess(user1Update, user2Update)
                .addOnSuccessListener(results -> {
                    db.collection("friend_requests").document(requestId)
                            .delete()
                            .addOnSuccessListener(aVoid3 -> {
                                callback.onSuccess(null);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "친구 요청 문서 삭제 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                callback.onFailure(e);
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "친구 요청 수락 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    callback.onFailure(e);
                });
    }

    //친구 요청을 거절합니다.

    public void rejectFriendRequest(String requestId, FirestoreCallback<Void> callback) {
        db.collection("friend_requests").document(requestId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess(null);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "친구 요청 거절 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    callback.onFailure(e);
                });
    }

    //사용자가 이미 그룹에 속해 있다면 그룹 생성을 허용하지 않습니다.**

    public void createGroup(String currentUserId, String groupName, Set<String> members, FirestoreCallback<GroupItem> callback) {
        db.collection("users").document(currentUserId).get()
                .addOnSuccessListener(userDoc -> {
                    String existingGroupId = null;
                    if (userDoc.exists() && userDoc.contains("group") && userDoc.get("group") instanceof String) {
                        existingGroupId = userDoc.getString("group");
                    }

                    if (existingGroupId != null && !existingGroupId.isEmpty()) {
                        // 사용자가 이미 그룹에 속해 있다면 그룹 생성을 막고 실패 콜백 호출
                        Toast.makeText(context, "이미 그룹에 속해 있어서 새로운 그룹을 생성할 수 없어요.", Toast.LENGTH_LONG).show();
                        callback.onFailure(new IllegalStateException("User already belongs to a group."));
                    } else {
                        // 그룹에 속해 있지 않다면 그룹 생성 진행
                        proceedGroupCreation(currentUserId, groupName, members, callback);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("GroupRepository", "사용자 정보 로드 실패: " + e.getMessage());
                    Toast.makeText(context, "그룹 생성 전 사용자 정보 로드 실패", Toast.LENGTH_SHORT).show();
                    callback.onFailure(e);
                });
    }

    // 실제 그룹 생성 및 초대장 발송 로직 (내부 헬퍼 함수)
    private void proceedGroupCreation(String currentUserId, String groupName, Set<String> members, FirestoreCallback<GroupItem> callback) {
        Map<String, Object> groupData = new HashMap<>();
        groupData.put("groupName", groupName);
        groupData.put("members", new ArrayList<>(members));
        groupData.put("createdAt", FieldValue.serverTimestamp());

        db.collection("groups")
                .add(groupData)
                .addOnSuccessListener(documentReference -> {
                    String groupId = documentReference.getId();
                    GroupItem newGroup = new GroupItem(groupId, groupName, currentUserId, new ArrayList<>(members));
                    db.collection("users").document(currentUserId)
                            .update("group", groupId)
                            .addOnSuccessListener(aVoid -> Log.d("GroupRepository", "사용자 group 필드 업데이트 완료: " + groupId))
                            .addOnFailureListener(e -> Log.e("GroupRepository", "사용자 group 필드 업데이트 실패", e));

                    List<Task<Void>> invitationTasks = new ArrayList<>();
                    for (String memberId : members) {
                        if (!memberId.equals(currentUserId)) {
                            invitationTasks.add(sendInvitationTask(groupId, currentUserId, memberId));
                        }
                    }

                    Tasks.whenAllComplete(invitationTasks)
                            .addOnSuccessListener(results -> {
                                Log.d("GroupRepository", "모든 초대장 발송 처리 완료.");
                                callback.onSuccess(newGroup);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "그룹 생성은 되었으나 일부 초대장 발송 실패: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                Log.e("GroupRepository", "일부 초대장 발송 실패", e);
                                callback.onSuccess(newGroup);
                            });

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "그룹 생성 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    callback.onFailure(e);
                });
    }


    //현재 사용자가 속한 단일 그룹을 Firestore에서 불러옵니다.

    public void loadCurrentGroup(String currentUserId, FirestoreCallback<GroupItem> callback) {
        db.collection("users").document(currentUserId).get()
                .addOnSuccessListener(userDoc -> {
                    String tempGroupId = null;
                    if (userDoc.exists() && userDoc.contains("group") && userDoc.get("group") instanceof String) {
                        tempGroupId = userDoc.getString("group");
                    }

                    final String groupId = tempGroupId; // final 변수에 값 복사

                    if (groupId != null && !groupId.isEmpty()) {
                        db.collection("groups").document(groupId).get()
                                .addOnSuccessListener(groupDoc -> {
                                    if (groupDoc.exists()) {
                                        String retrievedGroupName = groupDoc.getString("groupName");
                                        // if (retrievedGroupName != null) { 이 부분 대신 아래 코드 사용
                                        GroupItem groupItem = groupDoc.toObject(GroupItem.class);
                                        if (groupItem != null) {
                                            groupItem.groupId = groupDoc.getId(); // 문서 ID를 GroupItem의 groupId 필드에 설정
                                            callback.onSuccess(groupItem);
                                        } else {
                                            Log.w("GroupRepository", "그룹 문서 데이터가 GroupItem으로 변환되지 않음: " + groupId);
                                            callback.onSuccess(null); // 변환 실패 시 null 반환
                                        }
                                    } else {
                                        Log.w("GroupRepository", "사용자의 group ID에 해당하는 그룹 문서가 존재하지 않음: " + groupId);
                                        // ⭐ 변경됨: FieldValue.delete() 대신 null로 업데이트
                                        db.collection("users").document(currentUserId)
                                                .update("group", null)
                                                .addOnSuccessListener(aVoid -> Log.d("GroupRepository", "유저의 잘못된 group 필드 null로 업데이트 완료"))
                                                .addOnFailureListener(e -> Log.e("GroupRepository", "유저의 잘못된 group 필드 null로 업데이트 실패", e));
                                        callback.onSuccess(null);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "그룹 로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.e("GroupRepository", "단일 그룹 로드 실패: " + e.getMessage());
                                    callback.onFailure(e);
                                });
                    } else {
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "사용자 그룹 정보 로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("GroupRepository", "사용자 문서 로드 실패: " + e.getMessage());
                    callback.onFailure(e);
                });
    }

    //특정 그룹의 멤버 목록을 불러옵니다.

    public void loadGroupMembers(String groupId, FirestoreCallback<List<String>> callback) {
        db.collection("groups").document(groupId).get()
                .addOnSuccessListener(groupDoc -> {
                    if (groupDoc.exists()) {
                        List<String> members = (List<String>) groupDoc.get("members");
                        callback.onSuccess(members != null ? members : new ArrayList<>());
                    } else {
                        callback.onSuccess(new ArrayList<>());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("GroupRepository", "그룹 멤버 로드 실패: " + e.getMessage());
                    callback.onFailure(e);
                });
    }


    //현재 사용자에게 온 그룹 초대 목록을 불러옵니다.

    public void loadInvitations(String inviteeUserId, FirestoreCallback<QuerySnapshot> callback) {
        db.collection("invitations")
                .whereEqualTo("inviteeUserId", inviteeUserId)
                .get()
                .addOnSuccessListener(callback::onSuccess)
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "초대장 로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    callback.onFailure(e);
                });
    }


    //그룹 초대를 보냅니다. (Task 반환 버전)
    private Task<Void> sendInvitationTask(String groupId, String inviterUserId, String inviteeUserId) {
        Map<String, Object> invitation = new HashMap<>();
        invitation.put("groupId", groupId);
        invitation.put("inviteeUserId", inviteeUserId);
        invitation.put("inviterUserId", inviterUserId);
        invitation.put("sentAt", FieldValue.serverTimestamp());

        return db.collection("invitations")
                .add(invitation)
                .continueWith(task -> null);
    }
    //그룹 초대를 수락합니다.
    public void acceptInvitation(String invitationId, String newGroupId, String currentUserId, FirestoreCallback<Void> callback) {
        db.collection("users").document(currentUserId).get()
                .addOnSuccessListener(userDoc -> {
                    String tempExistingGroupId = null; // 임시 변수 사용
                    if (userDoc.exists() && userDoc.contains("group") && userDoc.get("group") instanceof String) {
                        tempExistingGroupId = userDoc.getString("group");
                    }

                    final String existingGroupId = tempExistingGroupId; // final 변수에 값 복사

                    if (existingGroupId != null && !existingGroupId.isEmpty() && !existingGroupId.equals(newGroupId)) {
                        db.collection("groups").document(existingGroupId)
                                .update("members", FieldValue.arrayRemove(currentUserId))
                                .addOnSuccessListener(aVoid -> {
                                    // 이제 existingGroupId는 final이므로 여기서 사용 가능
                                    Log.d("GroupRepository", "초대 수락 전 기존 그룹(" + existingGroupId + ")에서 사용자 제거 완료");
                                    joinNewGroup(invitationId, newGroupId, currentUserId, callback);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("GroupRepository", "초대 수락 전 기존 그룹에서 사용자 제거 실패", e);
                                    Toast.makeText(context, "기존 그룹 탈퇴 처리 중 오류 발생", Toast.LENGTH_SHORT).show();
                                    deleteInvitation(invitationId, new FirestoreCallback<Void>() {
                                        @Override public void onSuccess(Void result) {}
                                        @Override public void onFailure(Exception e) {Log.e("GroupRepository", "초대장 삭제 실패", e);}
                                    });
                                    callback.onFailure(e);
                                });
                    } else {
                        joinNewGroup(invitationId, newGroupId, currentUserId, callback);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("GroupRepository", "사용자 정보 로드 실패", e);
                    Toast.makeText(context, "초대 수락 전 사용자 정보 로드 실패", Toast.LENGTH_SHORT).show();
                    deleteInvitation(invitationId, new FirestoreCallback<Void>() {
                        @Override public void onSuccess(Void result) {}
                        @Override public void onFailure(Exception e) {Log.e("GroupRepository", "초대장 삭제 실패", e);}
                    });
                    callback.onFailure(e);
                });
    }

    // 새로운 그룹 가입 처리 (내부 헬퍼 함수)
    private void joinNewGroup(String invitationId, String newGroupId, String currentUserId, FirestoreCallback<Void> callback) {
        DocumentReference groupRef = db.collection("groups").document(newGroupId);
        groupRef.update("members", FieldValue.arrayUnion(currentUserId))
                .addOnSuccessListener(aVoid -> {
                    db.collection("users").document(currentUserId)
                            .update("group", newGroupId)
                            .addOnSuccessListener(v -> {
                                Log.d("GroupRepository", "사용자 group 필드 업데이트 완료: " + newGroupId);
                                deleteInvitation(invitationId, new FirestoreCallback<Void>() {
                                    @Override
                                    public void onSuccess(Void result) {
                                        callback.onSuccess(null);
                                    }
                                    @Override
                                    public void onFailure(Exception e) {
                                        Log.e("GroupRepository", "초대장 삭제 실패 (초대 수락 과정에서)", e);
                                        callback.onSuccess(null);
                                    }
                                });
                            })
                            .addOnFailureListener(e -> {
                                Log.e("GroupRepository", "유저의 group 필드 업데이트 실패", e);
                                Toast.makeText(context, "그룹 가입 처리 중 오류 발생", Toast.LENGTH_SHORT).show();
                                deleteInvitation(invitationId, new FirestoreCallback<Void>() {
                                    @Override public void onSuccess(Void result) {}
                                    @Override public void onFailure(Exception e) {Log.e("GroupRepository", "초대장 삭제 실패", e);}
                                });
                                callback.onFailure(e);
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "초대 수락 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    deleteInvitation(invitationId, new FirestoreCallback<Void>() {
                        @Override public void onSuccess(Void result) {}
                        @Override public void onFailure(Exception e) {Log.e("GroupRepository", "초대장 삭제 실패", e);}
                    });
                    callback.onFailure(e);
                });
    }
    public void updateGroupMembers(String groupId, List<String> newMembers, FirestoreCallback<Void> callback) {
        if (groupId == null || groupId.isEmpty()) {
            Toast.makeText(context, "그룹 ID가 유효하지 않습니다.", Toast.LENGTH_SHORT).show();
            callback.onFailure(new IllegalArgumentException("Group ID cannot be null or empty."));
            return;
        }
        db.collection("groups").document(groupId)
                .update("members", newMembers) // 'members' 필드를 새 리스트로 덮어씌웁니다.
                .addOnSuccessListener(aVoid -> {
                    Log.d("GroupRepository", "그룹 멤버 업데이트 성공: " + groupId);
                    Toast.makeText(context, "그룹 구성원이 업데이트되었습니다.", Toast.LENGTH_SHORT).show();
                    callback.onSuccess(null);
                })
                .addOnFailureListener(e -> {
                    Log.e("GroupRepository", "그룹 멤버 업데이트 실패: " + e.getMessage(), e);
                    Toast.makeText(context, "그룹 구성원 업데이트 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    callback.onFailure(e);
                });
    }
    // 이 메서드는 'users' 컬렉션의 특정 필드에서 친구 ID를 가져온다고 가정합니다.

    public void getAllFriends(String currentUserId, FirestoreCallback<Set<String>> callback) {
        if (currentUserId == null || currentUserId.isEmpty()) {
            callback.onFailure(new IllegalArgumentException("Current User ID is null or empty."));
            return;
        }

        db.collection("users").document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("friends")) {

                        List<String> friendsList = (List<String>) documentSnapshot.get("friends");
                        if (friendsList != null) {
                            callback.onSuccess(new java.util.HashSet<>(friendsList));
                        } else {
                            callback.onSuccess(new java.util.HashSet<>()); // 친구가 없으면 빈 Set 반환
                        }
                    } else {
                        callback.onSuccess(new java.util.HashSet<>()); // 문서가 없거나 friends 필드가 없으면 빈 Set 반환
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("GroupRepository", "친구 목록 불러오기 실패: " + e.getMessage(), e);
                    Toast.makeText(context, "친구 목록 불러오기 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    callback.onFailure(e);
                });
    }

    //그룹을 삭제합니다. 그룹의 모든 멤버를 탈퇴 처리한 후, 해당 그룹에 속한 마커들을 삭제하고 마지막으로 그룹 문서를 삭제합니다.

    public void leaveGroup(String groupId, String userIdToLeave, FirestoreCallback<Void> callback) {
        // 1. groups 컬렉션에서 해당 그룹의 members 배열에서 사용자 ID 제거
        db.collection("groups").document(groupId)
                .update("members", FieldValue.arrayRemove(userIdToLeave))
                .addOnSuccessListener(aVoid -> {
                    Log.d("GroupRepository", "그룹(" + groupId + ") 멤버에서 " + userIdToLeave + " 제거 성공.");

                    // 2. users 컬렉션에서 해당 사용자의 'group' 필드 null로 업데이트
                    // ⭐ 변경됨: FieldValue.delete() 대신 null로 업데이트
                    db.collection("users").document(userIdToLeave)
                            .update("group", null)
                            .addOnSuccessListener(aVoid1 -> {
                                Log.d("GroupRepository", userIdToLeave + "의 'group' 필드를 null로 업데이트 성공.");

                                // 3. 그룹에 남은 멤버가 없으면 그룹 문서 자체를 삭제 (선택 사항)
                                db.collection("groups").document(groupId).get()
                                        .addOnSuccessListener(groupDoc -> {
                                            if (groupDoc.exists()) {
                                                List<String> currentMembers = (List<String>) groupDoc.get("members");
                                                if (currentMembers == null || currentMembers.isEmpty()) {
                                                    // 남은 멤버가 없으므로 그룹 삭제
                                                    db.collection("groups").document(groupId).delete()
                                                            .addOnSuccessListener(aVoid2 -> Log.d("GroupRepository", "빈 그룹(" + groupId + ") 자동 삭제 완료."))
                                                            .addOnFailureListener(e -> Log.e("GroupRepository", "빈 그룹(" + groupId + ") 자동 삭제 실패: " + e.getMessage()));
                                                }
                                            }
                                            Toast.makeText(context, "그룹을 성공적으로 탈퇴했습니다.", Toast.LENGTH_SHORT).show();
                                            callback.onSuccess(null);
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("GroupRepository", "탈퇴 후 그룹 멤버 확인 실패: " + e.getMessage());
                                            Toast.makeText(context, "그룹 탈퇴는 완료되었으나, 그룹 상태 확인 중 오류 발생: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            callback.onFailure(e); // 그룹 탈퇴는 성공했지만, 후처리 실패로 볼 수도 있음
                                        });
                            })
                            .addOnFailureListener(e -> {
                                Log.e("GroupRepository", userIdToLeave + "의 'group' 필드 null 업데이트 실패: " + e.getMessage());
                                Toast.makeText(context, "그룹 탈퇴 중 사용자 정보 업데이트 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                callback.onFailure(e);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("GroupRepository", "그룹(" + groupId + ") 멤버에서 " + userIdToLeave + " 제거 실패: " + e.getMessage());
                    Toast.makeText(context, "그룹 탈퇴 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    callback.onFailure(e);
                });
    }
    public void deleteGroup(String groupId, FirestoreCallback<Void> callback) {
        db.collection("groups").document(groupId).get()
                .addOnSuccessListener(groupDoc -> {
                    if (groupDoc.exists()) {
                        List<String> members = (List<String>) groupDoc.get("members");
                        List<Task<Void>> tasks = new ArrayList<>();

                        if (members != null && !members.isEmpty()) {
                            for (String memberId : members) {
                                // ⭐ 변경됨: FieldValue.delete() 대신 null로 업데이트
                                tasks.add(db.collection("users").document(memberId)
                                        .update("group", null)
                                        .addOnFailureListener(e -> Log.e("GroupRepository", "멤버(" + memberId + ")의 group 필드 null 업데이트 실패", e)));
                            }
                        }

                        Tasks.whenAllComplete(tasks)
                                .addOnSuccessListener(aVoid -> {
                                    db.collection("markers")
                                            .whereEqualTo("groupId", groupId)
                                            .get()
                                            .addOnSuccessListener(querySnapshot -> {
                                                List<Task<Void>> markerDeletionTasks = new ArrayList<>();
                                                for (DocumentSnapshot docSnapshot : querySnapshot.getDocuments()) {
                                                    markerDeletionTasks.add(docSnapshot.getReference().delete()); // Get DocumentReference for deletion
                                                }

                                                Tasks.whenAllComplete(markerDeletionTasks)
                                                        .addOnSuccessListener(markerResults -> {
                                                            Log.d("GroupRepository", "그룹(" + groupId + ")에 속한 모든 마커 삭제 완료.");
                                                            // 3. 그룹 문서 삭제
                                                            db.collection("groups").document(groupId)
                                                                    .delete()
                                                                    .addOnSuccessListener(aVoid2 -> {
                                                                        Toast.makeText(context, "그룹이 삭제되었어요.", Toast.LENGTH_SHORT).show();
                                                                        callback.onSuccess(null);
                                                                    })
                                                                    .addOnFailureListener(e -> {
                                                                        Toast.makeText(context, "그룹 문서 삭제 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                        callback.onFailure(e);
                                                                    });
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Log.e("GroupRepository", "그룹(" + groupId + ") 마커 삭제 실패: " + e.getMessage());
                                                            Toast.makeText(context, "그룹 마커 삭제 중 오류 발생: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            // 마커 삭제는 실패했지만, 그룹 자체는 삭제 시도 (선택 사항: 그룹 삭제도 취소할지 결정)
                                                            // 여기서는 그룹 삭제를 계속 진행하는 것으로 가정합니다.
                                                            db.collection("groups").document(groupId)
                                                                    .delete()
                                                                    .addOnSuccessListener(aVoid2 -> {
                                                                        Toast.makeText(context, "그룹은 삭제되었으나, 일부 마커 삭제 실패.", Toast.LENGTH_LONG).show();
                                                                        callback.onSuccess(null); // 그룹 자체는 삭제 성공으로 간주
                                                                    })
                                                                    .addOnFailureListener(e3 -> {
                                                                        Toast.makeText(context, "그룹 문서 삭제 실패: " + e3.getMessage(), Toast.LENGTH_SHORT).show();
                                                                        callback.onFailure(e3);
                                                                    });
                                                        });
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e("GroupRepository", "그룹 마커 쿼리 실패: " + e.getMessage());
                                                Toast.makeText(context, "그룹 마커 정보 로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                // 마커 로드 실패 시에도 그룹 삭제는 시도
                                                db.collection("groups").document(groupId)
                                                        .delete()
                                                        .addOnSuccessListener(aVoid2 -> {
                                                            Toast.makeText(context, "그룹이 삭제되었으나, 마커 처리 중 오류 발생.", Toast.LENGTH_LONG).show();
                                                            callback.onSuccess(null);
                                                        })
                                                        .addOnFailureListener(e3 -> {
                                                            Toast.makeText(context, "그룹 문서 삭제 실패: " + e3.getMessage(), Toast.LENGTH_SHORT).show();
                                                            callback.onFailure(e3);
                                                        });
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "그룹 멤버의 group 필드 업데이트 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    callback.onFailure(e);
                                });
                    } else {
                        Toast.makeText(context, "삭제할 그룹이 존재하지 않아요.", Toast.LENGTH_SHORT).show();
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "그룹 정보 로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    callback.onFailure(e);
                });
    }
    //그룹 초대를 거절합니다.
    public void rejectInvitation(String invitationId, FirestoreCallback<Void> callback) {
        deleteInvitation(invitationId, new FirestoreCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(context, "그룹 초대를 거절했어요.", Toast.LENGTH_SHORT).show();
                callback.onSuccess(null);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }
    //초대장을 Firestore에서 삭제합니다.

    public void deleteInvitation(String invitationId, FirestoreCallback<Void> callback) {
        db.collection("invitations").document(invitationId)
                .delete()
                .addOnSuccessListener(callback::onSuccess)
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "초대장 삭제 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    callback.onFailure(e);
                });
    }
    //그룹 ID를 이용해 그룹 이름을 가져옵니다.
    public void getGroupName(String groupId, FirestoreCallback<String> callback) {
        db.collection("groups").document(groupId).get()
                .addOnSuccessListener(groupDoc -> {
                    if (groupDoc.exists()) {
                        callback.onSuccess(groupDoc.getString("groupName"));
                    } else {
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("GroupRepository", "그룹 이름 로드 실패: " + e.getMessage());
                    callback.onFailure(e);
                });
    }

    /**
     * 그룹 이름을 업데이트합니다.
     * @param groupId 업데이트할 그룹의 ID.
     * @param newGroupName 새로 설정할 그룹 이름.
     * @param callback Firestore 작업의 성공 또는 실패를 처리할 콜백.
     */
    public void updateGroupName(String groupId, String newGroupName, FirestoreCallback<Void> callback) {
        DocumentReference groupRef = db.collection("groups").document(groupId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("groupName", newGroupName); // 'groupName' 필드를 업데이트합니다.

        groupRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d("GroupRepository", "Group name successfully updated for groupId: " + groupId);
                    callback.onSuccess(null); // 성공 시 콜백 호출
                })
                .addOnFailureListener(e -> {
                    Log.e("GroupRepository", "Error updating group name for groupId: " + groupId, e);
                    callback.onFailure(e); // 실패 시 콜백 호출
                });
    }
}