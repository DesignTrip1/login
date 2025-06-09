package com.example.design.schedule;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
// import android.widget.Toast; // ⭐ Toast import 제거

import androidx.appcompat.app.AppCompatActivity;

import com.example.design.R;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddScheduleActivity extends AppCompatActivity {

    private Button btnStartDate, btnEndDate, btnNext;
    private EditText editTitle;

    private String selectedStartDate = "";
    private String selectedEndDate = "";

    private FirebaseFirestore db;
    private String currentUserId;

    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_USER_ID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        db = FirebaseFirestore.getInstance();

        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        currentUserId = prefs.getString(KEY_USER_ID, null);

        Log.d("AddScheduleActivity", "Loaded currentUserId: " + (currentUserId != null ? currentUserId : "NULL"));

        btnStartDate = findViewById(R.id.btnStartDate);
        btnEndDate = findViewById(R.id.btnEndDate);
        btnNext = findViewById(R.id.btnNext);
        editTitle = findViewById(R.id.editTitle);

        btnStartDate.setOnClickListener(v -> showDatePicker(true));
        btnEndDate.setOnClickListener(v -> showDatePicker(false));

        btnNext.setOnClickListener(v -> {
            String title = editTitle.getText().toString().trim();

            if (title.isEmpty() || selectedStartDate.isEmpty() || selectedEndDate.isEmpty()) {
                // Toast 대신 로그로 대체
                Log.w("AddScheduleActivity", "Validation failed: Title, start date, or end date is empty.");
                return;
            }

            findUserGroupAndSaveSchedule(title, selectedStartDate, selectedEndDate);
        });
    }

    private void showDatePicker(boolean isStart) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String dateStr = String.format(Locale.getDefault(), "%d-%02d-%02d", year, (month + 1), dayOfMonth);
                    if (isStart) {
                        selectedStartDate = dateStr;
                        btnStartDate.setText("시작일: " + dateStr);
                    } else {
                        selectedEndDate = dateStr;
                        btnEndDate.setText("종료일: " + dateStr);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void findUserGroupAndSaveSchedule(String title, String startDate, String endDate) {
        if (currentUserId == null || currentUserId.isEmpty()) {
            Log.e("AddScheduleActivity", "currentUserId is null or empty. Cannot save schedule.");
            return;
        }

        db.collection("users").document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // 'groupId' 대신 'group' 필드를 읽음
                        String group = documentSnapshot.getString("group");
                        if (group != null && !group.isEmpty()) {
                            saveScheduleToFirestore(title, startDate, endDate, group);
                        } else {
                            // 에러 메시지 변경: 'group' 필드 언급
                            Log.e("AddScheduleActivity", "Group ID not found or empty in user document: " + currentUserId);
                        }
                    } else {
                        Log.e("AddScheduleActivity", "User document not found for ID: " + currentUserId + ". Check 'users' collection.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("AddScheduleActivity", "Failed to get user document: " + e.getMessage(), e);
                });
    }

    private void saveScheduleToFirestore(String title, String startDate, String endDate, String group) {
        Map<String, Object> schedule = new HashMap<>();
        schedule.put("title", title);
        schedule.put("startDate", startDate);
        schedule.put("endDate", endDate);
        // 'groupId' 대신 'group' 필드 사용
        schedule.put("group", group);

        db.collection("schedules")
                .add(schedule)
                .addOnSuccessListener(documentReference -> {
                    String travelScheduleId = documentReference.getId();

                    Log.d("AddScheduleActivity", "Schedule added with ID: " + travelScheduleId);

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("title", title);
                    resultIntent.putExtra("startDate", startDate);
                    resultIntent.putExtra("endDate", endDate);
                    // 'groupId' 대신 'group' 인텐트 키 사용
                    resultIntent.putExtra("group", group);
                    resultIntent.putExtra("travelScheduleId", travelScheduleId);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("AddScheduleActivity", "Failed to save schedule: " + e.getMessage(), e);
                });
    }
}