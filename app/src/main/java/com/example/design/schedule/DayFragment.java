// DayFragment.java (이전에 수정했던 내용에서 btnSelectPlace 관련 부분만 변경)
package com.example.design.schedule;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.design.R;
import com.example.design.detail.OnPlaceSelectedListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DayFragment extends Fragment {

    private static final String ARG_DAY_TITLE = "dayTitle";
    private static final String ARG_DAY_POSITION = "dayPosition";
    private static final String ARG_TRAVEL_SCHEDULE_ID = "travelScheduleId";
    private static final String ARG_GROUP_ID = "groupId";

    private String dayTitle;
    private int dayPosition;
    private String travelScheduleId;
    private String currentGroupId;

    private TextView textDayTitle;
    private TextView editStartTime, editEndTime;
    private EditText editPlace, editMemo; // editPlace는 EditText입니다.
    private Button btnAddSchedule; // btnSelectPlace는 더 이상 필요 없음

    private RecyclerView recyclerView;
    private DayScheduleAdapter adapter;
    private List<ScheduleItem> scheduleList;

    private OnPlaceSelectedListener placeSelectedListener;

    private FirebaseFirestore db;

    public DayFragment() {
        // Required empty public constructor
    }

    public static DayFragment newInstance(String dayTitle, int dayPosition, String travelScheduleId, String groupId) {
        DayFragment fragment = new DayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DAY_TITLE, dayTitle);
        args.putInt(ARG_DAY_POSITION, dayPosition);
        args.putString(ARG_TRAVEL_SCHEDULE_ID, travelScheduleId);
        args.putString(ARG_GROUP_ID, groupId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnPlaceSelectedListener) {
            placeSelectedListener = (OnPlaceSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPlaceSelectedListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dayTitle = getArguments().getString(ARG_DAY_TITLE);
            dayPosition = getArguments().getInt(ARG_DAY_POSITION);
            travelScheduleId = getArguments().getString(ARG_TRAVEL_SCHEDULE_ID);
            currentGroupId = getArguments().getString(ARG_GROUP_ID);
        }
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day, container, false);

        textDayTitle = view.findViewById(R.id.textDayTitle);
        editStartTime = view.findViewById(R.id.editStartTime);
        editEndTime = view.findViewById(R.id.editEndTime);
        editPlace = view.findViewById(R.id.editPlace); // EditText로 선언되어 있음
        editMemo = view.findViewById(R.id.editMemo);
        btnAddSchedule = view.findViewById(R.id.btnAddSchedule);
        // btnSelectPlace는 제거합니다.

        recyclerView = view.findViewById(R.id.recyclerSchedule); // ID 변경 확인: recyclerViewDaySchedule -> recyclerSchedule
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        scheduleList = new ArrayList<>();
        adapter = new DayScheduleAdapter(scheduleList);
        recyclerView.setAdapter(adapter);

        textDayTitle.setText(dayTitle);

        adapter.setOnItemDeleteListener(position -> {
            ScheduleItem itemToDelete = scheduleList.get(position);
            deleteScheduleItemFromFirestore(itemToDelete.getId(), position);
        });

        adapter.setOnItemEditListener((position, newStartTime, newEndTime, newPlace, newMemo) -> {
            ScheduleItem itemToEdit = scheduleList.get(position);
            updateScheduleItemInFirestore(itemToEdit.getId(), newStartTime, newEndTime, newPlace, newMemo, position);
        });

        editStartTime.setOnClickListener(v -> showTimePicker(editStartTime));
        editEndTime.setOnClickListener(v -> showTimePicker(editEndTime));

        // ⭐ editPlace 클릭 리스너 설정: 이전에 btnSelectPlace에 연결했던 로직을 여기에 연결합니다.
        editPlace.setOnClickListener(v -> {
            if (placeSelectedListener != null) {
                // Activity에 마커 선택 다이얼로그 요청
                // 현재 editPlace의 텍스트와 현재 DayFragment의 position을 전달합니다.
                placeSelectedListener.onPlaceSelected(editPlace.getText().toString(), dayPosition);
            }
        });

        btnAddSchedule.setOnClickListener(v -> {
            String startTime = editStartTime.getText().toString().replace("시작 시간 선택", "").trim();
            String endTime = editEndTime.getText().toString().replace("종료 시간 선택", "").trim();
            String place = editPlace.getText().toString().trim();
            String memo = editMemo.getText().toString().trim();

            if (TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime) || TextUtils.isEmpty(place)) {
                Toast.makeText(getContext(), "시간과 장소는 필수로 입력해야 합니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (compareTimes(startTime, endTime) > 0) {
                Toast.makeText(getContext(), "시작 시간은 종료 시간보다 빨라야 합니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            saveScheduleItemToFirestore(startTime, endTime, place, memo);

            editStartTime.setText("시작 시간 선택");
            editEndTime.setText("종료 시간 선택");
            editPlace.setText("");
            editMemo.setText("");
        });

        loadScheduleItemsFromFirestore();

        return view;
    }

    /**
     * Activity로부터 선택된 마커 이름을 받아와 EditText에 설정하는 메서드입니다.
     * @param placeName 선택된 마커 이름
     */
    public void setPlaceText(String placeName) {
        if (editPlace != null) {
            editPlace.setText(placeName);
            Log.d("DayFragment", "Place text set to: " + placeName + " for Day: " + dayPosition);
        }
    }

    public String getPlaceText() {
        if (editPlace != null) {
            return editPlace.getText().toString();
        }
        return "";
    }

    private void showTimePicker(TextView targetView) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
            String formattedTime = String.format(Locale.KOREA, "%02d:%02d", hourOfDay, minute);
            targetView.setText(formattedTime);
        }, 12, 0, false);
        timePickerDialog.show();
    }

    private int compareTimes(String time1, String time2) {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm", Locale.KOREA);
            java.util.Date date1 = sdf.parse(time1);
            java.util.Date date2 = sdf.parse(time2);
            return date1.compareTo(date2);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void saveScheduleItemToFirestore(String startTime, String endTime, String place, String memo) {
        if (travelScheduleId == null || travelScheduleId.isEmpty()) {
            Toast.makeText(getContext(), "메인 일정 정보가 없어 상세 일정을 저장할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentGroupId == null || currentGroupId.isEmpty()) {
            Toast.makeText(getContext(), "그룹 정보가 없어 상세 일정을 저장할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        CollectionReference daySchedulesRef = db.collection("schedules")
                .document(travelScheduleId)
                .collection("dayDetails")
                .document(dayTitle)
                .collection("schedules");

        Map<String, Object> scheduleData = new HashMap<>();
        scheduleData.put("startTime", startTime);
        scheduleData.put("endTime", endTime);
        scheduleData.put("place", place);
        scheduleData.put("memo", memo);
        scheduleData.put("timestamp", com.google.firebase.Timestamp.now());

        daySchedulesRef.add(scheduleData)
                .addOnSuccessListener(documentReference -> {
                    String newScheduleId = documentReference.getId();
                    ScheduleItem newItem = new ScheduleItem(newScheduleId, startTime, endTime, place, memo);
                    scheduleList.add(newItem);
                    Collections.sort(scheduleList, Comparator.comparing(ScheduleItem::getStartTime));
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "일정 추가 및 저장 완료!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "일정 저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("DayFragment", "Error saving schedule item: " + e.getMessage());
                });
    }

    private void loadScheduleItemsFromFirestore() {
        if (travelScheduleId == null || travelScheduleId.isEmpty()) {
            Log.w("DayFragment", "travelScheduleId is null or empty. Cannot load schedule items.");
            return;
        }

        CollectionReference daySchedulesRef = db.collection("schedules")
                .document(travelScheduleId)
                .collection("dayDetails")
                .document(dayTitle)
                .collection("schedules");

        daySchedulesRef.orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    scheduleList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String id = document.getId();
                        String startTime = document.getString("startTime");
                        String endTime = document.getString("endTime");
                        String place = document.getString("place");
                        String memo = document.getString("memo");
                        scheduleList.add(new ScheduleItem(id, startTime, endTime, place, memo));
                    }
                    Collections.sort(scheduleList, Comparator.comparing(ScheduleItem::getStartTime));
                    adapter.notifyDataSetChanged();
                    Log.d("DayFragment", "Loaded " + scheduleList.size() + " schedule items for " + dayTitle);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "일정 로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("DayFragment", "Error loading schedule items: " + e.getMessage());
                });
    }

    private void deleteScheduleItemFromFirestore(String scheduleItemId, int position) {
        if (travelScheduleId == null || travelScheduleId.isEmpty() || scheduleItemId == null || scheduleItemId.isEmpty()) {
            Toast.makeText(getContext(), "삭제할 일정 정보가 부족합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("schedules")
                .document(travelScheduleId)
                .collection("dayDetails")
                .document(dayTitle)
                .collection("schedules")
                .document(scheduleItemId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    scheduleList.remove(position);
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(getContext(), "일정이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "일정 삭제 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("DayFragment", "Error deleting schedule item: " + e.getMessage());
                });
    }

    private void updateScheduleItemInFirestore(String scheduleItemId, String newStartTime, String newEndTime, String newPlace, String newMemo, int position) {
        if (travelScheduleId == null || travelScheduleId.isEmpty() || scheduleItemId == null || scheduleItemId.isEmpty()) {
            Toast.makeText(getContext(), "수정할 일정 정보가 부족합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("startTime", newStartTime);
        updates.put("endTime", newEndTime);
        updates.put("place", newPlace);
        updates.put("memo", newMemo);

        db.collection("schedules")
                .document(travelScheduleId)
                .collection("dayDetails")
                .document(dayTitle)
                .collection("schedules")
                .document(scheduleItemId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    ScheduleItem updatedItem = new ScheduleItem(scheduleItemId, newStartTime, newEndTime, newPlace, newMemo);
                    scheduleList.set(position, updatedItem);
                    Collections.sort(scheduleList, Comparator.comparing(ScheduleItem::getStartTime));
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "일정이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "일정 수정 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("DayFragment", "Error updating schedule item: " + e.getMessage());
                });
    }
}