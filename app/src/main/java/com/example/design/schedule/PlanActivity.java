package com.example.design.schedule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.design.detail.DetailScheduleActivity;
import com.example.design.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentChange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PlanActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PlanAdapter adapter;
    private List<PlanItem> planList;
    private static final int REQUEST_CODE_ADD_PLAN = 1001;

    private TextView emptyMessage;

    private FirebaseFirestore db;
    private ListenerRegistration firestoreListener;
    private String currentUserId;

    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_USER_ID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        db = FirebaseFirestore.getInstance();

        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        currentUserId = prefs.getString(KEY_USER_ID, null);

        if (currentUserId == null || currentUserId.isEmpty()) {
            Log.e("PlanActivity", "User ID is null. Cannot load plans. Please ensure user is logged in.");
            updateEmptyMessageVisibility();
            // 필요한 경우 로그인 화면으로 이동
            // finish();
            return;
        }

        recyclerView = findViewById(R.id.recyclerPlan);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        planList = new ArrayList<>();
        adapter = new PlanAdapter(planList);
        recyclerView.setAdapter(adapter);

        emptyMessage = findViewById(R.id.emptyMessage);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        ImageButton btnAddPlan = findViewById(R.id.btnAdd);
        btnAddPlan.setOnClickListener(v -> {
            Intent intent = new Intent(PlanActivity.this, AddScheduleActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_PLAN);
        });

        adapter.setOnItemClickListener(position -> {
            PlanItem clickedItem = planList.get(position);
            Intent intent = new Intent(PlanActivity.this, DetailScheduleActivity.class);
            intent.putExtra("travelScheduleId", clickedItem.getId());
            intent.putExtra("title", clickedItem.getTitle());
            intent.putExtra("startDate", clickedItem.getStartDate());
            intent.putExtra("endDate", clickedItem.getEndDate());
            intent.putExtra("group", clickedItem.getGroup());
            startActivity(intent);
        });

        adapter.setOnDeleteConfirmedListener(position -> {
            PlanItem itemToDelete = planList.get(position);
            deletePlanFromFirestore(itemToDelete);
            // Firestore 리스너가 삭제 후 UI 업데이트를 자동으로 처리합니다.
        });

        // Firestore 리스너 설정
        setupFirestoreListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // onStop에서 리스너가 제거되었다면, onStart에서 다시 설정합니다.
        if (firestoreListener == null) {
            setupFirestoreListener();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firestoreListener != null) {
            firestoreListener.remove();
            firestoreListener = null;
            Log.d("PlanActivity", "Firestore listener removed in onStop.");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // onStop에서 리스너가 제거되지 않았을 경우를 대비하여 한 번 더 확인합니다.
        if (firestoreListener != null) {
            firestoreListener.remove();
            Log.d("PlanActivity", "Firestore listener removed in onDestroy.");
        }
    }

    private void setupFirestoreListener() {
        if (currentUserId == null || currentUserId.isEmpty()) {
            Log.e("PlanActivity", "Cannot set up Firestore listener: User ID is null or empty.");
            updateEmptyMessageVisibility();
            return;
        }

        db.collection("users").document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String userGroup = documentSnapshot.getString("group");
                        if (userGroup != null && !userGroup.isEmpty()) {
                            Log.d("PlanActivity", "User is in group: " + userGroup);

                            // 중요: 기존 리스너가 있다면 제거하여 중복 부착 방지
                            if (firestoreListener != null) {
                                firestoreListener.remove();
                            }

                            // Firestore 실시간 리스너 설정
                            firestoreListener = db.collection("schedules")
                                    .whereEqualTo("group", userGroup)
                                    // 'startDate' 또는 'timestamp' (AddScheduleActivity에서 추가했다면)를 기준으로 정렬
                                    .orderBy("startDate", Query.Direction.ASCENDING)
                                    .addSnapshotListener((snapshots, e) -> {
                                        if (e != null) {
                                            Log.e("PlanActivity", "Failed to load schedules: " + e.getMessage(), e);
                                            return;
                                        }

                                        if (snapshots != null) {
                                            // ⭐ 중요: planList를 완전히 초기화하고 현재 스냅샷으로 다시 채웁니다.
                                            // 이렇게 하면 중복 추가 문제를 가장 확실하게 방지할 수 있습니다.
                                            List<PlanItem> newPlanList = new ArrayList<>();
                                            for (DocumentSnapshot doc : snapshots.getDocuments()) {
                                                PlanItem item = doc.toObject(PlanItem.class);
                                                if (item != null) {
                                                    item.setId(doc.getId()); // 문서 ID 설정
                                                    newPlanList.add(item);
                                                }
                                            }

                                            planList.clear(); // 기존 목록을 비웁니다.
                                            planList.addAll(newPlanList); // 새 데이터를 추가합니다.

                                            // startDate를 기준으로 정렬 (Firestore 쿼리에서 이미 정렬되었더라도 로컬에서 다시 확인)
                                            Collections.sort(planList, Comparator.comparing(PlanItem::getStartDate));

                                            adapter.notifyDataSetChanged(); // 어댑터에 데이터 변경 알림
                                            updateEmptyMessageVisibility(); // 빈 메시지 가시성 업데이트
                                        }
                                    });
                        } else {
                            Log.d("PlanActivity", "User is not part of any group or group ID is empty. Clearing plans.");
                            planList.clear();
                            adapter.notifyDataSetChanged();
                            updateEmptyMessageVisibility();
                        }
                    } else {
                        Log.d("PlanActivity", "User document does not exist for ID: " + currentUserId + ". Clearing plans.");
                        planList.clear();
                        adapter.notifyDataSetChanged();
                        updateEmptyMessageVisibility();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("PlanActivity", "Failed to get user document for group ID: " + e.getMessage(), e);
                    planList.clear();
                    adapter.notifyDataSetChanged();
                    updateEmptyMessageVisibility();
                });
    }

    private void deletePlanFromFirestore(PlanItem item) {
        if (item.getId() == null) {
            Log.w("PlanActivity", "Cannot delete plan: ID is null.");
            return;
        }

        db.collection("schedules").document(item.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("PlanActivity", "Schedule deleted from Firestore: " + item.getId());
                    // 삭제 후 Firestore 리스너가 자동으로 UI를 업데이트할 것입니다.
                })
                .addOnFailureListener(e -> {
                    Log.e("PlanActivity", "Failed to delete schedule from Firestore: " + e.getMessage(), e);
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_PLAN && resultCode == RESULT_OK) {
            // 새 일정이 추가되거나 기존 일정이 저장되었습니다.
            // Firestore 리스너가 자동으로 목록을 새로 고치므로, 여기서 추가 작업은 필요하지 않습니다.
            Log.d("PlanActivity", "New schedule added or existing schedule saved. Firestore listener will refresh.");
        }
    }

    private void updateEmptyMessageVisibility() {
        if (planList.isEmpty()) {
            emptyMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}