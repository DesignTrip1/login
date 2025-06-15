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
import com.google.firebase.firestore.DocumentReference; // 추가: DocumentReference 임포트

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
    private EditText editPlace, editMemo;
    private Button btnAddSchedule;

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
        editPlace = view.findViewById(R.id.editPlace);
        editMemo = view.findViewById(R.id.editMemo);
        btnAddSchedule = view.findViewById(R.id.btnAddSchedule);

        recyclerView = view.findViewById(R.id.recyclerSchedule);
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

        editPlace.setOnClickListener(v -> {
            if (placeSelectedListener != null) {
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

                    // ⭐ 추가된 로직: 마커의 isSelected 필드를 true로 업데이트 ⭐
                    if (!TextUtils.isEmpty(place) && currentGroupId != null && !currentGroupId.isEmpty()) {
                        db.collection("markers")
                                .whereEqualTo("group", currentGroupId)
                                .whereEqualTo("name", place)
                                .get()
                                .addOnSuccessListener(markerQuerySnapshots -> {
                                    if (!markerQuerySnapshots.isEmpty()) {
                                        // 해당 이름과 그룹에 일치하는 마커가 있다면 (첫 번째 문서)
                                        DocumentReference markerRef = markerQuerySnapshots.getDocuments().get(0).getReference();
                                        markerRef.update("isSelected", true)
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d("DayFragment", "Marker '" + place + "' isSelected updated to true.");
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("DayFragment", "Failed to update isSelected for marker '" + place + "': " + e.getMessage());
                                                    // 토스트 메시지는 너무 빈번할 수 있어 여기서는 주석 처리했습니다. 필요 시 활성화하세요.
                                                    // Toast.makeText(getContext(), "마커 상태 업데이트 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                    } else {
                                        Log.w("DayFragment", "No marker found for place: " + place + " in group: " + currentGroupId + ". Cannot update isSelected.");
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("DayFragment", "Error querying markers for place '" + place + "': " + e.getMessage());
                                    // Toast.makeText(getContext(), "마커 조회 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
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

        // 삭제할 장소 이름 가져오기 (마커 isSelected를 다시 false로 변경하려면 필요)
        String placeToDelete = scheduleList.get(position).getPlace(); // 현재 삭제하려는 아이템의 장소 이름

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

                    // ⭐ 추가된 로직: 마커의 isSelected 필드를 false로 업데이트 ⭐
                    // 이 마커가 다른 일정에도 사용되지 않는다는 보장이 있을 때만 실행해야 합니다.
                    // 복잡성을 줄이려면 이 로직을 DetailScheduleActivity의 최종 저장 시에만 두는 것이 좋습니다.
                    // 현재는 DayFragment에서 추가 시 true로, 삭제 시 false로 변경하도록 예시를 제공하지만,
                    // 이 부분이 앱의 전체적인 'isSelected' 논리와 충돌할 수 있습니다.
                    if (!TextUtils.isEmpty(placeToDelete) && currentGroupId != null && !currentGroupId.isEmpty()) {
                        db.collection("markers")
                                .whereEqualTo("group", currentGroupId)
                                .whereEqualTo("name", placeToDelete)
                                .get()
                                .addOnSuccessListener(markerQuerySnapshots -> {
                                    if (!markerQuerySnapshots.isEmpty()) {
                                        DocumentReference markerRef = markerQuerySnapshots.getDocuments().get(0).getReference();
                                        markerRef.update("isSelected", false)
                                                .addOnSuccessListener(result -> {
                                                    Log.d("DayFragment", "Marker '" + placeToDelete + "' isSelected updated to false on delete.");
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("DayFragment", "Failed to update isSelected for marker '" + placeToDelete + "' on delete: " + e.getMessage());
                                                });
                                    }
                                });
                    }
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

        // 기존 장소 이름
        String oldPlace = scheduleList.get(position).getPlace();

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

                    // ⭐ 추가된 로직: 장소 변경 시 이전 마커 isSelected false, 새 마커 isSelected true로 업데이트 ⭐
                    // 이 역시 복잡성을 고려해야 합니다. 아래 설명을 참고하세요.
                    /*
                    if (!oldPlace.equals(newPlace)) { // 장소 이름이 변경되었을 경우
                        // 이전 마커 isSelected false로 변경
                        if (!TextUtils.isEmpty(oldPlace) && currentGroupId != null && !currentGroupId.isEmpty()) {
                            db.collection("markers")
                                    .whereEqualTo("group", currentGroupId)
                                    .whereEqualTo("name", oldPlace)
                                    .get()
                                    .addOnSuccessListener(oldMarkerSnapshots -> {
                                        if (!oldMarkerSnapshots.isEmpty()) {
                                            DocumentReference oldMarkerRef = oldMarkerSnapshots.getDocuments().get(0).getReference();
                                            oldMarkerRef.update("isSelected", false);
                                        }
                                    });
                        }

                        // 새 마커 isSelected true로 변경
                        if (!TextUtils.isEmpty(newPlace) && currentGroupId != null && !currentGroupId.isEmpty()) {
                            db.collection("markers")
                                    .whereEqualTo("group", currentGroupId)
                                    .whereEqualTo("name", newPlace)
                                    .get()
                                    .addOnSuccessListener(newMarkerSnapshots -> {
                                        if (!newMarkerSnapshots.isEmpty()) {
                                            DocumentReference newMarkerRef = newMarkerSnapshots.getDocuments().get(0).getReference();
                                            newMarkerRef.update("isSelected", true);
                                        }
                                    });
                        }
                    } else if (!TextUtils.isEmpty(newPlace) && currentGroupId != null && !currentGroupId.isEmpty()) {
                        // 장소는 변경되지 않았지만 isSelected가 false일 수 있으므로 다시 true로 설정
                        // (이미 true였다면 영향 없음)
                         db.collection("markers")
                                .whereEqualTo("group", currentGroupId)
                                .whereEqualTo("name", newPlace)
                                .get()
                                .addOnSuccessListener(markerSnapshots -> {
                                    if (!markerSnapshots.isEmpty()) {
                                        DocumentReference markerRef = markerSnapshots.getDocuments().get(0).getReference();
                                        markerRef.update("isSelected", true);
                                    }
                                });
                    }
                    */
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "일정 수정 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("DayFragment", "Error updating schedule item: " + e.getMessage());
                });
    }
}