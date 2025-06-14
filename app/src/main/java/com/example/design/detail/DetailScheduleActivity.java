package com.example.design.detail;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.design.R;
import com.example.design.group.GroupItem;
import com.example.design.group.GroupRepository;
import com.example.design.schedule.DayFragment;
import com.example.design.schedule.DayPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

// OnPlaceSelectedListener 인터페이스를 구현합니다.
public class DetailScheduleActivity extends AppCompatActivity implements OnPlaceSelectedListener {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private DayPagerAdapter dayPagerAdapter;
    private List<String> dayTitles;

    // Firestore 인스턴스
    private FirebaseFirestore db;
    private GroupRepository groupRepository;

    private HashMap<String, List<String>> selectedPlacesPerDay;

    // SharedPreferences에서 현재 사용자 ID를 가져오기 위함
    private String currentUserId;
    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_USER_ID = "userId";

    // 현재 사용자의 그룹 ID
    private String currentGroupId;

    // Fragment에서 마커 선택 다이얼로그 요청 시 어떤 DayFragment에서 요청했는지 추적하기 위한 변수
    private int requestingDayPosition = -1; // -1은 Activity에서 요청했음을 의미

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_schedule);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        db = FirebaseFirestore.getInstance();
        groupRepository = new GroupRepository(this);

        selectedPlacesPerDay = new HashMap<>();

        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        currentUserId = prefs.getString(KEY_USER_ID, null);

        if (currentUserId == null || currentUserId.isEmpty()) {
            Toast.makeText(this, "사용자 ID를 불러올 수 없습니다. 로그인이 필요합니다.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        groupRepository.loadCurrentGroup(currentUserId, new GroupRepository.FirestoreCallback<GroupItem>() {
            @Override
            public void onSuccess(GroupItem groupItem) {
                if (groupItem != null) {
                    currentGroupId = groupItem.groupId;
                    Log.d("DetailScheduleActivity", "Current user is in group: " + currentGroupId);
                } else {
                    currentGroupId = null;
                    Toast.makeText(DetailScheduleActivity.this, "사용자가 속한 그룹이 없습니다. 그룹을 생성하거나 가입해주세요.", Toast.LENGTH_LONG).show();
                    Log.d("DetailScheduleActivity", "사용자가 속한 그룹이 없습니다.");
                }
            }

            @Override
            public void onFailure(Exception e) {
                currentGroupId = null;
                Toast.makeText(DetailScheduleActivity.this, "그룹 정보 로드 실패: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("DetailScheduleActivity", "Failed to load current group: " + e.getMessage());
            }
        });

        Intent intent = getIntent();
        String startDate = intent.getStringExtra("startDate");
        String endDate = intent.getStringExtra("endDate");
        String scheduleName = intent.getStringExtra("scheduleName");

        dayTitles = generateDayList(startDate, endDate);
        dayPagerAdapter = new DayPagerAdapter(this, dayTitles, this);
        viewPager.setAdapter(dayPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(dayTitles.get(position));
        }).attach();
    }

    // OnPlaceSelectedListener 인터페이스 구현 메서드
    @Override
    public void onPlaceSelected(String placeName, int dayPosition) {
        this.requestingDayPosition = dayPosition;
        showPlaceSelectionDialogForFragment();
    }

    private List<String> generateDayList(String start, String end) {
        List<String> result = new ArrayList<>();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            Date startDate = sdf.parse(start);
            Date endDate = sdf.parse(end);

            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);

            int dayIndex = 1;
            while (!cal.getTime().after(endDate)) {
                result.add("Day " + dayIndex);
                cal.add(Calendar.DATE, 1);
                dayIndex++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "날짜 생성 오류: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    private void showPlaceSelectionDialogForFragment() {
        if (currentGroupId == null || currentGroupId.isEmpty()) {
            Toast.makeText(this, "현재 그룹 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("markers")
                .whereEqualTo("group", currentGroupId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> markerNames = new ArrayList<>();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            String markerName = document.getString("name");
                            if (markerName != null) {
                                markerNames.add(markerName);
                            }
                        }
                    }

                    if (markerNames.isEmpty()) {
                        Toast.makeText(DetailScheduleActivity.this, "그룹에 등록된 마커가 없습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Collections.sort(markerNames);

                    String currentPlaceInFragment = null;
                    DayFragment targetFragment = (DayFragment) dayPagerAdapter.getFragment(requestingDayPosition);
                    if (targetFragment != null) {
                        currentPlaceInFragment = targetFragment.getPlaceText();
                    }

                    int checkedItem = -1;
                    if (currentPlaceInFragment != null && !currentPlaceInFragment.isEmpty()) {
                        checkedItem = markerNames.indexOf(currentPlaceInFragment);
                    }

                    final int[] selectedItemFinalIndex = {checkedItem};

                    AlertDialog.Builder builder = new AlertDialog.Builder(DetailScheduleActivity.this);
                    builder.setTitle("장소 선택");
                    builder.setSingleChoiceItems(markerNames.toArray(new String[0]), checkedItem, (dialog, which) -> {
                        selectedItemFinalIndex[0] = which;
                    });

                    builder.setPositiveButton("확인", (dialog, which) -> {
                        if (selectedItemFinalIndex[0] != -1) {
                            String selectedPlace = markerNames.get(selectedItemFinalIndex[0]);
                            DayFragment fragment = (DayFragment) dayPagerAdapter.getFragment(requestingDayPosition);
                            if (fragment != null) {
                                fragment.setPlaceText(selectedPlace);
                            }
                        } else {
                            Toast.makeText(DetailScheduleActivity.this, "장소를 선택해주세요.", Toast.LENGTH_SHORT).show();
                        }
                        requestingDayPosition = -1;
                    });
                    builder.setNegativeButton("취소", (dialog, which) -> {
                        requestingDayPosition = -1;
                        dialog.dismiss();
                    });
                    builder.show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(DetailScheduleActivity.this, "마커 로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("DetailScheduleActivity", "Error loading markers: " + e.getMessage());
                });
    }

    private void showAssignPlacesToDaysDialog(List<String> placesToAssign) {
        if (placesToAssign.isEmpty()) {
            Toast.makeText(this, "선택된 장소가 없습니다. 장소를 선택해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("장소 할당 (날짜 선택)");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_assign_places_to_days, null);
        android.widget.LinearLayout layoutDaysContainer = dialogView.findViewById(R.id.layoutDaysContainer);

        final HashMap<String, List<String>> tempAssignedPlaces = new HashMap<>(selectedPlacesPerDay);

        for (String dayTitle : dayTitles) {
            TextView dayTextView = new TextView(this);
            dayTextView.setText(dayTitle);
            dayTextView.setTextSize(18f);
            dayTextView.setTextColor(Color.BLACK);
            dayTextView.setPadding(0, 10, 0, 5);
            layoutDaysContainer.addView(dayTextView);

            List<String> currentDayPlaces = tempAssignedPlaces.getOrDefault(dayTitle, new ArrayList<>());
            TextView currentPlacesTextView = new TextView(this);
            updateCurrentPlacesTextView(currentPlacesTextView, currentDayPlaces);
            currentPlacesTextView.setTextColor(Color.GRAY);
            currentPlacesTextView.setPadding(0, 0, 0, 10);
            layoutDaysContainer.addView(currentPlacesTextView);

            Button editDayPlacesButton = new Button(this);
            editDayPlacesButton.setText(dayTitle + " 장소 수정");
            editDayPlacesButton.setOnClickListener(v -> {
                showMultiChoicePlaceDialogForDay(dayTitle, placesToAssign, tempAssignedPlaces.getOrDefault(dayTitle, new ArrayList<>()),
                        (newPlacesList) -> {
                            tempAssignedPlaces.put(dayTitle, newPlacesList);
                            updateCurrentPlacesTextView(currentPlacesTextView, newPlacesList);
                        });
            });
            layoutDaysContainer.addView(editDayPlacesButton);

            View divider = new View(this);
            divider.setLayoutParams(new android.widget.LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, 1));
            divider.setBackgroundColor(Color.LTGRAY);
            layoutDaysContainer.addView(divider);
        }

        builder.setView(dialogView);
        builder.setPositiveButton("할당 완료 및 저장", (dialog, which) -> {
            selectedPlacesPerDay.clear();
            selectedPlacesPerDay.putAll(tempAssignedPlaces);

            String scheduleName = getIntent().getStringExtra("scheduleName");
            String startDate = getIntent().getStringExtra("startDate");
            String endDate = getIntent().getStringExtra("endDate");
            saveScheduleToFirestore(scheduleName, startDate, endDate);
        });
        builder.setNegativeButton("취소", null);
        builder.show();
    }

    public interface OnPlacesAssignedListener {
        void onPlacesAssigned(List<String> newPlacesList);
    }

    private void showMultiChoicePlaceDialogForDay(String dayTitle, List<String> allAvailablePlaces, List<String> currentDayPlaces, OnPlacesAssignedListener listener) {
        boolean[] checkedItems = new boolean[allAvailablePlaces.size()];
        ArrayList<Integer> selectedItemsIndexes = new ArrayList<>();

        for (int i = 0; i < allAvailablePlaces.size(); i++) {
            if (currentDayPlaces.contains(allAvailablePlaces.get(i))) {
                checkedItems[i] = true;
                selectedItemsIndexes.add(i);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(dayTitle + " 장소 선택");
        builder.setMultiChoiceItems(allAvailablePlaces.toArray(new String[0]), checkedItems, (dialog, which, isChecked) -> {
            if (isChecked) {
                if (!selectedItemsIndexes.contains(which)) {
                    selectedItemsIndexes.add(which);
                }
            } else {
                selectedItemsIndexes.remove(Integer.valueOf(which));
            }
        });
        builder.setPositiveButton("확인", (dialog, which) -> {
            List<String> newDayPlaces = new ArrayList<>();
            Collections.sort(selectedItemsIndexes);
            for (Integer index : selectedItemsIndexes) {
                newDayPlaces.add(allAvailablePlaces.get(index));
            }
            if (listener != null) {
                listener.onPlacesAssigned(newDayPlaces);
            }
            Toast.makeText(this, dayTitle + " 장소 할당이 업데이트되었습니다.", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("취소", null);
        builder.show();
    }

    private void updateCurrentPlacesTextView(TextView textView, List<String> places) {
        StringBuilder currentPlacesText = new StringBuilder();
        if (!places.isEmpty()) {
            currentPlacesText.append("현재 장소: ");
            for (int i = 0; i < places.size(); i++) {
                currentPlacesText.append(places.get(i));
                if (i < places.size() - 1) {
                    currentPlacesText.append(", ");
                }
            }
        } else {
            currentPlacesText.append("현재 할당된 장소 없음");
        }
        textView.setText(currentPlacesText.toString());
    }

    /**
     * Firestore에 일정 데이터를 저장하는 메서드 (장소 데이터 포함)
     * 사용자님이 보여주신 구조(최상위 "schedules" 컬렉션 아래에 일정 문서 저장 및 'group' 필드 포함)에 맞춰 수정되었습니다.
     * 이 일정 문서 아래에 각 Day별 상세 일정을 subcollection으로 저장합니다.
     */
    private void saveScheduleToFirestore(String scheduleName, String startDate, String endDate) {
        if (scheduleName == null || startDate == null || endDate == null) {
            Toast.makeText(this, "일정 이름 또는 날짜 정보가 부족합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentGroupId == null || currentGroupId.isEmpty()) {
            Toast.makeText(this, "그룹 ID를 알 수 없어 일정을 저장할 수 없습니다. 그룹에 먼저 가입해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        String scheduleId = UUID.randomUUID().toString();
        // ★★★ 핵심 변경 사항: 최상위 "schedules" 컬렉션 아래에 일정을 저장
        DocumentReference scheduleRef = db.collection("schedules").document(scheduleId);

        // 메인 일정 데이터
        Map<String, Object> scheduleData = new HashMap<>();
        scheduleData.put("scheduleName", scheduleName);
        scheduleData.put("startDate", startDate);
        scheduleData.put("endDate", endDate);
        scheduleData.put("timestamp", new Date());
        // ★★★ 이 필드가 이 일정이 속한 그룹을 나타냅니다. 그룹 멤버들이 이 필드를 기준으로 일정을 조회할 수 있습니다.
        scheduleData.put("group", currentGroupId);

        // Firestore 배치(batch) 초기화 (모든 쓰기 작업을 한 번에 처리하여 데이터 일관성 유지)
        WriteBatch batch = db.batch();

        // 1. 메인 일정 문서를 저장합니다.
        batch.set(scheduleRef, scheduleData, SetOptions.merge());

        // 2. 각 날짜별 상세 일정을 메인 일정 문서의 "dayDetails" 하위 컬렉션의 서브 문서로 저장합니다.
        for (Map.Entry<String, List<String>> entry : selectedPlacesPerDay.entrySet()) {
            String dayTitle = entry.getKey(); // 예: "Day 1"
            List<String> places = entry.getValue(); // 해당 날짜에 선택된 장소 목록

            Map<String, Object> dayDetailData = new HashMap<>();
            dayDetailData.put("dayTitle", dayTitle);
            dayDetailData.put("places", places); // 장소 이름 목록을 저장합니다.

            // "dayDetails" 하위 컬렉션 내에 각 날짜별 문서를 생성합니다.
            DocumentReference dayDetailRef = scheduleRef.collection("dayDetails").document(dayTitle);
            batch.set(dayDetailRef, dayDetailData, SetOptions.merge());
        }

        // 모든 배치 작업을 커밋(실행)합니다.
        batch.commit()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "일정이 성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    finish(); // 저장 성공 시 액티비티 종료
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "일정 저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("DetailScheduleActivity", "Error saving schedule: " + e.getMessage());
                });
    }
}