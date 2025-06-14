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
import com.example.design.schedule.DayPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

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

public class DetailScheduleActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private DayPagerAdapter dayPagerAdapter;
    private List<String> dayTitles; // "Day 1", "Day 2" 등의 리스트
    private Button btnSelectPlaces; // ⭐ 추가: 장소 선택 버튼

    // Firestore 인스턴스
    private FirebaseFirestore db;
    private GroupRepository groupRepository; // ⭐ 추가: GroupRepository 인스턴스

    // ⭐ 추가: 날짜별 선택된 장소를 저장할 맵
    // Key: "Day 1", "Day 2" 등, Value: 해당 날짜에 선택된 장소 이름 리스트
    private HashMap<String, List<String>> selectedPlacesPerDay;

    // SharedPreferences에서 현재 사용자 ID를 가져오기 위함
    private String currentUserId;
    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_USER_ID = "userId";

    // 현재 사용자의 그룹 ID
    private String currentGroupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_schedule);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        btnSelectPlaces = findViewById(R.id.btnSelectPlaces); // ⭐ 초기화

        db = FirebaseFirestore.getInstance();
        groupRepository = new GroupRepository(this); // ⭐ GroupRepository 초기화

        selectedPlacesPerDay = new HashMap<>(); // ⭐ 맵 초기화

        // SharedPreferences에서 currentUserId 가져오기
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        currentUserId = prefs.getString(KEY_USER_ID, null);

        if (currentUserId == null || currentUserId.isEmpty()) {
            Toast.makeText(this, "사용자 ID를 불러올 수 없습니다. 로그인이 필요합니다.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 현재 사용자의 그룹 ID를 불러옵니다.
        // groupRepository.loadCurrentGroup 메서드에 currentUserId 파라미터가 있다면 전달합니다.
        // 만약 GroupRepository.loadCurrentGroup 메서드가 currentUserId를 받지 않도록 수정되었다면,
        // 아래 호출에서 currentUserId 파라미터를 제거해야 합니다.
        // (최근 GroupRepository 수정 시 loadCurrentGroup에서 currentUserId 파라미터를 제거했으니,
        // 해당 부분도 확인이 필요합니다. 여기서는 현재 코드를 기반으로 설명합니다.)
        groupRepository.loadCurrentGroup(currentUserId, new GroupRepository.FirestoreCallback<GroupItem>() { // ⭐ 이 부분 파라미터 확인 필요
            @Override
            public void onSuccess(GroupItem groupItem) {
                if (groupItem != null) {
                    // ⭐ 수정: getGroupId() 대신 필드에 직접 접근
                    currentGroupId = groupItem.groupId; // <-- 이 부분을 수정합니다!
                    Log.d("DetailScheduleActivity", "Current user is in group: " + currentGroupId);
                } else {
                    currentGroupId = null;
                    Toast.makeText(DetailScheduleActivity.this, "사용자가 속한 그룹이 없습니다. 그룹을 생성하거나 가입해주세요.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Exception e) {
                currentGroupId = null;
                Toast.makeText(DetailScheduleActivity.this, "그룹 정보 로드 실패: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("DetailScheduleActivity", "Failed to load current group: " + e.getMessage());
            }
        });


        // Intent로 데이터 받기
        Intent intent = getIntent();
        String startDate = intent.getStringExtra("startDate");
        String endDate = intent.getStringExtra("endDate");
        String scheduleName = intent.getStringExtra("scheduleName"); // 여행 일정 이름

        dayTitles = generateDayList(startDate, endDate); // "Day 1", "Day 2" 등 생성
        dayPagerAdapter = new DayPagerAdapter(this, dayTitles);
        viewPager.setAdapter(dayPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(dayTitles.get(position));
        }).attach();

        // ⭐ 장소 선택 버튼 클릭 리스너
        btnSelectPlaces.setOnClickListener(v -> {
            if (currentGroupId == null || currentGroupId.isEmpty()) {
                Toast.makeText(this, "그룹이 없으므로 장소를 선택할 수 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            showPlaceSelectionDialog();
        });

        // TODO: 실제 앱에서는 '일정 저장' 버튼을 따로 만들고, 그 버튼 클릭 시 saveScheduleToFirestore 호출
        // 현재는 편의상 showAssignPlacesToDaysDialog 완료 후 즉시 호출하도록 되어있습니다.
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

    // ⭐ 추가: 장소 선택 다이얼로그 표시 메서드
    private void showPlaceSelectionDialog() {
        if (currentGroupId == null || currentGroupId.isEmpty()) {
            Toast.makeText(this, "현재 그룹 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firestore에서 현재 그룹에 속한 마커들을 불러옵니다.
        db.collection("markers") // 마커 컬렉션 이름은 실제 Firestore 구조에 맞게 수정
                .whereEqualTo("groupId", currentGroupId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> markerNames = new ArrayList<>();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            String markerName = document.getString("name"); // 마커 이름 필드명
                            if (markerName != null) {
                                markerNames.add(markerName);
                            }
                        }
                    }

                    if (markerNames.isEmpty()) {
                        Toast.makeText(DetailScheduleActivity.this, "그룹에 등록된 마커가 없습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // 마커 이름들을 정렬
                    Collections.sort(markerNames);

                    // 다이얼로그에 표시할 장소 목록 (친구 목록처럼 다중 선택 가능하도록)
                    boolean[] checkedItems = new boolean[markerNames.size()];
                    ArrayList<Integer> selectedItemsIndexes = new ArrayList<>(); // 선택된 아이템들의 인덱스

                    // 이전에 선택된 장소가 있다면 미리 체크 표시
                    for (int i = 0; i < markerNames.size(); i++) {
                        String marker = markerNames.get(i);
                        for (Map.Entry<String, List<String>> entry : selectedPlacesPerDay.entrySet()) {
                            if (entry.getValue().contains(marker)) {
                                checkedItems[i] = true;
                                if (!selectedItemsIndexes.contains(i)) {
                                    selectedItemsIndexes.add(i);
                                }
                                break; // 이미 이 장소는 어느 Day에 할당됨
                            }
                        }
                    }


                    AlertDialog.Builder builder = new AlertDialog.Builder(DetailScheduleActivity.this);
                    builder.setTitle("장소 선택");
                    builder.setMultiChoiceItems(markerNames.toArray(new String[0]), checkedItems, (dialog, which, isChecked) -> {
                        if (isChecked) {
                            if (!selectedItemsIndexes.contains(which)) { // 중복 추가 방지
                                selectedItemsIndexes.add(which);
                            }
                        } else {
                            selectedItemsIndexes.remove(Integer.valueOf(which)); // Integer 객체로 제거
                        }
                    });

                    builder.setPositiveButton("확인", (dialog, which) -> {
                        List<String> tempSelectedPlaces = new ArrayList<>();
                        for (Integer index : selectedItemsIndexes) {
                            tempSelectedPlaces.add(markerNames.get(index));
                        }
                        // 이제 선택된 장소들을 날짜별로 할당하는 다이얼로그를 띄웁니다.
                        showAssignPlacesToDaysDialog(tempSelectedPlaces);
                    });
                    builder.setNegativeButton("취소", null);
                    builder.show();

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(DetailScheduleActivity.this, "마커 로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("DetailScheduleActivity", "Error loading markers: " + e.getMessage());
                });
    }

    // ⭐ 추가: 선택된 장소들을 날짜별로 할당하는 다이얼로그
    private void showAssignPlacesToDaysDialog(List<String> placesToAssign) {
        if (placesToAssign.isEmpty()) {
            Toast.makeText(this, "선택된 장소가 없습니다. 장소를 선택해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("장소 할당 (날짜 선택)");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_assign_places_to_days, null); // 이 레이아웃 파일 필요
        android.widget.LinearLayout layoutDaysContainer = dialogView.findViewById(R.id.layoutDaysContainer);


        // 이전에 할당된 장소들을 초기 상태로 설정
        // 이 맵에 할당된 장소는 `selectedPlacesPerDay`에 저장됩니다.
        // 이 다이얼로그는 각 Day에 할당된 장소를 보여주고, '장소 수정' 버튼을 통해 Day별 할당을 다시 할 수 있게 합니다.

        for (String dayTitle : dayTitles) {
            TextView dayTextView = new TextView(this);
            dayTextView.setText(dayTitle);
            dayTextView.setTextSize(18f);
            dayTextView.setTextColor(Color.BLACK);
            dayTextView.setPadding(0, 10, 0, 5);
            layoutDaysContainer.addView(dayTextView);

            // 해당 Day에 현재 할당된 장소 목록 표시
            List<String> currentDayPlaces = selectedPlacesPerDay.getOrDefault(dayTitle, new ArrayList<>());
            StringBuilder currentPlacesText = new StringBuilder();
            if (!currentDayPlaces.isEmpty()) {
                currentPlacesText.append("현재 장소: ");
                for (int i = 0; i < currentDayPlaces.size(); i++) {
                    currentPlacesText.append(currentDayPlaces.get(i));
                    if (i < currentDayPlaces.size() - 1) {
                        currentPlacesText.append(", ");
                    }
                }
            } else {
                currentPlacesText.append("현재 할당된 장소 없음");
            }
            TextView currentPlacesTextView = new TextView(this);
            currentPlacesTextView.setText(currentPlacesText.toString());
            currentPlacesTextView.setTextColor(Color.GRAY);
            currentPlacesTextView.setPadding(0, 0, 0, 10);
            layoutDaysContainer.addView(currentPlacesTextView);


            // 해당 Day에 장소를 추가/제거할 수 있는 버튼 (혹은 스피너)
            Button editDayPlacesButton = new Button(this);
            editDayPlacesButton.setText(dayTitle + " 장소 수정");
            editDayPlacesButton.setOnClickListener(v -> {
                showMultiChoicePlaceDialogForDay(dayTitle, placesToAssign, currentDayPlaces); // 선택된 장소 전체를 넘겨줍니다.
            });
            layoutDaysContainer.addView(editDayPlacesButton);

            View divider = new View(this);
            divider.setLayoutParams(new android.widget.LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, 1));
            divider.setBackgroundColor(Color.LTGRAY);
            layoutDaysContainer.addView(divider);
        }

        builder.setView(dialogView);
        builder.setPositiveButton("할당 완료 및 저장", (dialog, which) -> {
            // 모든 할당이 완료되었음을 알리고 최종 저장 로직을 호출
            // Intent에서 받은 scheduleName, startDate, endDate 값을 다시 가져옵니다.
            String scheduleName = getIntent().getStringExtra("scheduleName");
            String startDate = getIntent().getStringExtra("startDate");
            String endDate = getIntent().getStringExtra("endDate");
            saveScheduleToFirestore(scheduleName, startDate, endDate); // ⭐ 장소 할당 후 즉시 저장 호출
        });
        builder.setNegativeButton("취소", null);
        builder.show();
    }

    // ⭐ 추가: 특정 Day에 장소를 할당하는 다중 선택 다이얼로그
    private void showMultiChoicePlaceDialogForDay(String dayTitle, List<String> allAvailablePlaces, List<String> currentDayPlaces) {
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
            // 선택된 인덱스들을 정렬하여 순서 보장 (선택 순서가 아닌 목록 순서)
            Collections.sort(selectedItemsIndexes);
            for (Integer index : selectedItemsIndexes) {
                newDayPlaces.add(allAvailablePlaces.get(index));
            }
            selectedPlacesPerDay.put(dayTitle, newDayPlaces); // 맵에 할당된 장소 업데이트
            Toast.makeText(this, dayTitle + " 장소 할당이 업데이트되었습니다.", Toast.LENGTH_SHORT).show();

            // 장소 할당 다이얼로그를 닫고 다시 띄워서 변경사항을 반영
            // (사용자 경험을 위해 할당 다이얼로그를 새로고침하는 방법)
            // 주의: 이전에 showAssignPlacesToDaysDialog를 호출했던 placesToAssign와 동일한 리스트를 넘겨야 합니다.
            // 여기서는 `getIntent().getStringExtra("startDate/endDate")`를 사용하여 `dayTitles`를 다시 생성하는 대신,
            // `showPlaceSelectionDialog`에서 얻은 `placesToAssign` (전체 선택 장소)를 재사용해야 합니다.
            // 복잡도를 줄이기 위해, 할당 완료 시 토스트만 띄우고 사용자가 '할당 완료 및 저장' 버튼을 누르면 최종 반영되도록 유지합니다.
            // 만약 즉각적인 UI 반영이 필요하다면 `showAssignPlacesToDaysDialog(placesToAssign);`를 다시 호출해야 합니다.
        });
        builder.setNegativeButton("취소", null);
        builder.show();
    }


    // ⭐ 수정: Firestore에 일정 데이터를 저장하는 메서드 (장소 데이터 포함)
    private void saveScheduleToFirestore(String scheduleName, String startDate, String endDate) {
        if (scheduleName == null || startDate == null || endDate == null) {
            Toast.makeText(this, "일정 이름 또는 날짜 정보가 부족합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // currentUserId가 없으면 저장 불가
        if (currentUserId == null || currentUserId.isEmpty()) {
            Toast.makeText(this, "사용자 ID를 알 수 없어 일정을 저장할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> scheduleData = new HashMap<>();
        scheduleData.put("scheduleName", scheduleName);
        scheduleData.put("startDate", startDate);
        scheduleData.put("endDate", endDate);
        scheduleData.put("timestamp", new Date()); // 저장 시간 추가
        scheduleData.put("userId", currentUserId); // ⭐ 사용자 ID 추가

        // ⭐ 날짜별 장소 데이터를 맵으로 저장 (예: {"Day 1": ["장소A", "장소B"], "Day 2": ["장소C"]})
        // selectedPlacesPerDay 맵은 이미 HashMap<String, List<String>>이므로 바로 저장 가능
        scheduleData.put("placesPerDay", selectedPlacesPerDay);

        // Firestore에 "users" 컬렉션 아래에 사용자 ID별로 서브컬렉션을 만들고, 그 안에 일정을 저장
        // 예: users/{userId}/schedules/{scheduleId}
        String scheduleId = UUID.randomUUID().toString(); // 고유한 일정 ID 생성

        db.collection("users").document(currentUserId)
                .collection("schedules").document(scheduleId) // 사용자별 서브컬렉션
                .set(scheduleData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "일정이 성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    // 일정 저장 후, 필요하다면 메인 화면으로 돌아가거나 다음 액티비티로 이동
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "일정 저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("DetailScheduleActivity", "Error saving schedule: " + e.getMessage());
                });
    }
}