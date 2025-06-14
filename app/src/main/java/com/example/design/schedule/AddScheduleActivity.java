package com.example.design.schedule;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast; // Toast import 추가

import androidx.appcompat.app.AppCompatActivity;

import com.example.design.R;
import com.google.firebase.firestore.FieldValue; // FieldValue import 추가
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

        if (currentUserId == null || currentUserId.isEmpty()) {
            Log.e("AddScheduleActivity", "currentUserId is null or empty. Cannot add schedule.");
            Toast.makeText(this, "사용자 정보를 불러올 수 없습니다. 다시 로그인해주세요.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        btnStartDate = findViewById(R.id.btnStartDate);
        btnEndDate = findViewById(R.id.btnEndDate);
        btnNext = findViewById(R.id.btnNext);
        editTitle = findViewById(R.id.editTitle);

        btnStartDate.setOnClickListener(v -> showDatePickerDialog(true));
        btnEndDate.setOnClickListener(v -> showDatePickerDialog(false));

        btnNext.setOnClickListener(v -> {
            String title = editTitle.getText().toString().trim();

            if (title.isEmpty() || selectedStartDate.isEmpty() || selectedEndDate.isEmpty()) {
                Toast.makeText(this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 날짜 유효성 검사 (시작일이 종료일보다 늦을 수 없음)
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
                java.util.Date startDateObj = sdf.parse(selectedStartDate);
                java.util.Date endDateObj = sdf.parse(selectedEndDate);

                if (startDateObj != null && endDateObj != null && startDateObj.after(endDateObj)) {
                    Toast.makeText(this, "시작일은 종료일보다 빠르거나 같아야 합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (java.text.ParseException e) {
                Log.e("AddScheduleActivity", "날짜 파싱 오류: " + e.getMessage());
                Toast.makeText(this, "날짜 형식 오류", Toast.LENGTH_SHORT).show();
                return;
            }

            findUserGroupAndSaveSchedule(title, selectedStartDate, selectedEndDate);
        });
    }

    private void showDatePickerDialog(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = String.format(Locale.KOREA, "%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    if (isStartDate) {
                        selectedStartDate = date;
                        btnStartDate.setText(date);
                    } else {
                        selectedEndDate = date;
                        btnEndDate.setText(date);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void findUserGroupAndSaveSchedule(String title, String startDate, String endDate) {
        if (currentUserId == null || currentUserId.isEmpty()) {
            Log.e("AddScheduleActivity", "currentUserId is null or empty. Cannot find user group.");
            Toast.makeText(this, "사용자 정보를 불러올 수 없습니다. 다시 로그인해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

        db.collection("users").document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String group = documentSnapshot.getString("group"); // 'group' 필드 사용
                        if (group != null && !group.isEmpty()) {
                            saveScheduleToFirestore(title, startDate, endDate, group);
                        } else {
                            Log.e("AddScheduleActivity", "Group ID not found or empty in user document for ID: " + currentUserId);
                            Toast.makeText(this, "사용자 그룹 정보를 찾을 수 없습니다.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Log.e("AddScheduleActivity", "User document not found for ID: " + currentUserId + ". Check 'users' collection.");
                        Toast.makeText(this, "사용자 문서를 찾을 수 없습니다.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("AddScheduleActivity", "Failed to get user document: " + e.getMessage(), e);
                    Toast.makeText(this, "사용자 정보 로드 실패: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void saveScheduleToFirestore(String title, String startDate, String endDate, String group) {
        Map<String, Object> schedule = new HashMap<>();
        schedule.put("title", title);
        schedule.put("startDate", startDate);
        schedule.put("endDate", endDate);
        schedule.put("group", group); // 'group' 필드 사용
        schedule.put("timestamp", FieldValue.serverTimestamp()); // ⭐ 시간 필드 추가

        db.collection("schedules")
                .add(schedule)
                .addOnSuccessListener(documentReference -> {
                    String travelScheduleId = documentReference.getId();

                    Log.d("AddScheduleActivity", "Schedule added with ID: " + travelScheduleId);
                    Toast.makeText(this, "일정이 성공적으로 추가되었습니다!", Toast.LENGTH_SHORT).show();

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("title", title);
                    resultIntent.putExtra("startDate", startDate);
                    resultIntent.putExtra("endDate", endDate);
                    resultIntent.putExtra("group", group);
                    resultIntent.putExtra("travelScheduleId", travelScheduleId);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("AddScheduleActivity", "Failed to save schedule: " + e.getMessage(), e);
                    Toast.makeText(this, "일정 저장 실패: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}