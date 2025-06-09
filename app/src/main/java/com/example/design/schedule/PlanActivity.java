package com.example.design.schedule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
// import android.widget.Toast; // ⭐ Toast import 제거

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.design.detail.DetailScheduleActivity;
import com.example.design.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
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

        Log.d("PlanActivity", "Loaded currentUserId: " + (currentUserId != null ? currentUserId : "NULL"));

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        ImageButton btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddScheduleActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_PLAN);
        });

        emptyMessage = findViewById(R.id.emptyMessage);
        emptyMessage.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddScheduleActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_PLAN);
        });

        recyclerView = findViewById(R.id.recyclerPlan);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        planList = new ArrayList<>();
        adapter = new PlanAdapter(planList);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(position -> {
            PlanItem selectedPlan = planList.get(position);
            Intent intent = new Intent(PlanActivity.this, DetailScheduleActivity.class);
            intent.putExtra("travelScheduleId", selectedPlan.getId());
            intent.putExtra("title", selectedPlan.getTitle());
            intent.putExtra("startDate", selectedPlan.getStartDate());
            intent.putExtra("endDate", selectedPlan.getEndDate());
            intent.putExtra("group", selectedPlan.getGroup()); // 'groupId' 대신 'group' 인텐트 키 사용
            startActivity(intent);
        });

        adapter.setOnDeleteConfirmedListener(position -> {
            PlanItem itemToDelete = planList.get(position);
            deletePlanFromFirestore(itemToDelete);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (firestoreListener != null) {
            firestoreListener.remove();
            firestoreListener = null;
        }
        findUserGroupAndLoadPlans();
        Log.d("PlanActivity", "onResume: Reloading plans.");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (firestoreListener != null) {
            firestoreListener.remove();
            firestoreListener = null;
        }
        Log.d("PlanActivity", "onPause: Removing Firestore listener.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (firestoreListener != null) {
            firestoreListener.remove();
        }
        Log.d("PlanActivity", "onDestroy: Removing Firestore listener.");
    }

    private void findUserGroupAndLoadPlans() {
        if (currentUserId == null || currentUserId.isEmpty()) {
            Log.e("PlanActivity", "currentUserId is null or empty. Cannot load schedules.");
            updateEmptyMessageVisibility();
            return;
        }

        db.collection("users").document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String group = documentSnapshot.getString("group");
                        if (group != null && !group.isEmpty()) {
                            loadPlansFilteredByGroup(group);
                        } else {
                            Log.e("PlanActivity", "Group ID not found or empty in user document: " + currentUserId);
                            planList.clear();
                            adapter.setPlanList(planList);
                            updateEmptyMessageVisibility();
                        }
                    } else {
                        Log.e("PlanActivity", "User document not found for ID: " + currentUserId + ". Check 'users' collection.");
                        planList.clear();
                        adapter.setPlanList(planList);
                        updateEmptyMessageVisibility();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("PlanActivity", "Failed to get user document: " + e.getMessage(), e);
                    planList.clear();
                    adapter.setPlanList(planList);
                    updateEmptyMessageVisibility();
                });
    }

    private void loadPlansFilteredByGroup(String group) {
        if (firestoreListener != null) {
            firestoreListener.remove();
        }

        firestoreListener = db.collection("schedules")
                .whereEqualTo("group", group)
                .orderBy("startDate")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("PlanActivity", "Failed to load schedules: " + error.getMessage(), error);
                        return;
                    }

                    if (value != null) {
                        List<PlanItem> newPlanList = new ArrayList<>();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            PlanItem item = doc.toObject(PlanItem.class);
                            if (item != null) {
                                item.setId(doc.getId());
                                newPlanList.add(item);
                            }
                        }
                        planList.clear();
                        planList.addAll(newPlanList);
                        adapter.setPlanList(planList);
                        updateEmptyMessageVisibility();
                    }
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
                    Log.d("PlanActivity", "Schedule deleted: " + item.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e("PlanActivity", "Failed to delete schedule: " + e.getMessage(), e);
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_PLAN && resultCode == RESULT_OK && data != null) {
            Log.d("PlanActivity", "New schedule added. Refreshing list.");
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