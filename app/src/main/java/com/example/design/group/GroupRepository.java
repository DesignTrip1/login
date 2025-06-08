// com/example/design/group/GroupRepository.java

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

    /**
     * Firestore에서 현재 사용자의 친구 목록을 불러옵니다.
     *
     * @param userId 현재 사용자 ID
     * @param callback 친구 목록을 받을 콜백
     */
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

    /**
     * Firestore에서 친구를 삭제합니다 (양방향 삭제).
     *
     * @param currentUserId 현재 사용자 ID
     * @param friendIdToDelete 삭제할 친구 ID
     * @param callback 삭제 결과를 받을 콜백
     */
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

    /**
     * 친구 요청을 보냅니다.
     *
     * @param fromUserId 요청을 보내는 사용자 ID
     * @param toUserId 요청을 받을 사용자 ID
     * @param callback 요청 결과를 받을 콜백
     */
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

    /**
     * 현재 사용자에게 온 친구 요청 목록을 불러옵니다.
     *
     * @param toUserId 현재 사용자 ID
     * @param callback 친구 요청 목록(QuerySnapshot)을 받을 콜백
     */
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

    /**
     * 친구 요청을 수락합니다.
     *
     * @param requestId 친구 요청 문서 ID
     * @param currentUserId 현재 사용자 ID (요청을 받는 사람)
     * @param fromUserId 요청을 보낸 사용자 ID
     * @param callback 수락 결과를 받을 콜백
     */
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

    /**
     * 친구 요청을 거절합니다.
     *
     * @param requestId 친구 요청 문서 ID
     * @param callback 거절 결과를 받을 콜백
     */
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

    /**
     * 새로운 그룹을 생성합니다.
     * **사용자가 이미 그룹에 속해 있다면 그룹 생성을 허용하지 않습니다.**
     *
     * @param currentUserId 현재 사용자 ID
     * @param groupName 생성할 그룹 이름
     * @param members 그룹에 포함될 멤버 ID 목록
     * @param callback 그룹 생성 결과를 받을 콜백
     */
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
                    GroupItem newGroup = new GroupItem(groupId, groupName);

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

    /**
     * 현재 사용자가 속한 단일 그룹을 Firestore에서 불러옵니다.
     *
     * @param currentUserId 현재 사용자 ID
     * @param callback 그룹 정보를 받을 콜백 (속한 그룹이 없으면 null 반환)
     */
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
                                        if (retrievedGroupName != null) {
                                            callback.onSuccess(new GroupItem(groupId, retrievedGroupName));
                                        } else {
                                            Log.w("GroupRepository", "그룹 문서에 groupName 필드가 없거나 유효하지 않음: " + groupId);
                                            callback.onSuccess(null);
                                        }
                                    } else {
                                        Log.w("GroupRepository", "사용자의 group ID에 해당하는 그룹 문서가 존재하지 않음: " + groupId);
                                        db.collection("users").document(currentUserId)
                                                .update("group", FieldValue.delete())
                                                .addOnSuccessListener(aVoid -> Log.d("GroupRepository", "유저의 잘못된 group 필드 삭제 완료"))
                                                .addOnFailureListener(e -> Log.e("GroupRepository", "유저의 잘못된 group 필드 삭제 실패", e));
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

    /**
     * 특정 그룹의 멤버 목록을 불러옵니다.
     * @param groupId 그룹 ID
     * @param callback 멤버 목록을 받을 콜백
     */
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

    /**
     * 현재 사용자에게 온 그룹 초대 목록을 불러옵니다.
     *
     * @param inviteeUserId 초대받은 사용자 ID
     * @param callback 초대 목록(QuerySnapshot)을 받을 콜백
     */
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

    /**
     * 그룹 초대를 보냅니다. (Task 반환 버전)
     *
     * @param groupId 초대할 그룹 ID
     * @param inviterUserId 초대한 사용자 ID
     * @param inviteeUserId 초대받을 사용자 ID
     * @return 작업 Task
     */
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

    /**
     * 그룹 초대를 수락합니다.
     * 기존 그룹에서 탈퇴 처리 후 새로운 그룹에 가입시킵니다.
     *
     * @param invitationId 초대 문서 ID
     * @param newGroupId 수락할 그룹 ID
     * @param currentUserId 현재 사용자 ID
     * @param callback 수락 결과를 받을 콜백
     */
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

    /**
     * 그룹을 삭제합니다. 그룹의 모든 멤버를 탈퇴 처리한 후, 해당 그룹에 속한 마커들을 삭제하고 마지막으로 그룹 문서를 삭제합니다.
     * @param groupId 삭제할 그룹 ID
     * @param callback 삭제 결과를 받을 콜백
     */
    public void deleteGroup(String groupId, FirestoreCallback<Void> callback) {
        db.collection("groups").document(groupId).get()
                .addOnSuccessListener(groupDoc -> {
                    if (groupDoc.exists()) {
                        List<String> members = (List<String>) groupDoc.get("members");
                        List<Task<Void>> tasks = new ArrayList<>();

                        // 1. 모든 멤버의 'group' 필드 초기화 (필드 자체를 제거)
                        if (members != null && !members.isEmpty()) {
                            for (String memberId : members) {
                                tasks.add(db.collection("users").document(memberId)
                                        .update("group", FieldValue.delete())
                                        .addOnFailureListener(e -> Log.e("GroupRepository", "멤버(" + memberId + ")의 group 필드 삭제 실패", e)));
                            }
                        }

                        Tasks.whenAllComplete(tasks)
                                .addOnSuccessListener(aVoid -> {
                                    // 2. 해당 그룹에 속한 마커들을 삭제
                                    db.collection("markers") // 마커 컬렉션 이름은 실제 프로젝트에 맞게 수정해주세요.
                                            .whereEqualTo("groupId", groupId)
                                            .get()
                                            .addOnSuccessListener(querySnapshot -> {
                                                List<Task<Void>> markerDeletionTasks = new ArrayList<>();
                                                // Corrected line: Use DocumentSnapshot
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
    /**
     * 그룹 초대를 거절합니다.
     *
     * @param invitationId 초대 요청 문서 ID
     * @param callback 거절 결과를 받을 콜백
     */
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

    /**
     * 초대장을 Firestore에서 삭제합니다.
     *
     * @param invitationId 삭제할 초대 문서 ID
     * @param callback 삭제 결과를 받을 콜백
     */
    public void deleteInvitation(String invitationId, FirestoreCallback<Void> callback) {
        db.collection("invitations").document(invitationId)
                .delete()
                .addOnSuccessListener(callback::onSuccess)
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "초대장 삭제 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    callback.onFailure(e);
                });
    }

    /**
     * 그룹 ID를 이용해 그룹 이름을 가져옵니다.
     *
     * @param groupId 그룹 ID
     * @param callback 그룹 이름을 받을 콜백
     */
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
}