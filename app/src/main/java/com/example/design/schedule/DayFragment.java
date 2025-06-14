// DayFragment.java
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
import android.widget.EditText; // EditText 사용
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.design.R;
import com.example.design.detail.OnPlaceSelectedListener; // ⭐ import 경로 확인

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class DayFragment extends Fragment {

    private static final String ARG_DAY_TITLE = "dayTitle";
    private static final String ARG_DAY_POSITION = "dayPosition";

    private String dayTitle;
    private int dayPosition; // 현재 DayFragment의 인덱스

    private TextView textDayTitle;
    private TextView editStartTime, editEndTime;
    private EditText editPlace, editMemo; // editPlace는 EditText입니다.
    private Button btnAddSchedule;
    private RecyclerView recyclerSchedule;
    private DayScheduleAdapter adapter;
    private List<ScheduleItem> scheduleList;

    // Activity와의 통신을 위한 리스너
    private OnPlaceSelectedListener placeSelectedListener;

    public static DayFragment newInstance(String dayTitle, int dayPosition) {
        DayFragment fragment = new DayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DAY_TITLE, dayTitle);
        args.putInt(ARG_DAY_POSITION, dayPosition);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dayTitle = getArguments().getString(ARG_DAY_TITLE);
            dayPosition = getArguments().getInt(ARG_DAY_POSITION);
        }
        scheduleList = new ArrayList<>();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Activity가 OnPlaceSelectedListener 인터페이스를 구현했는지 확인
        if (context instanceof OnPlaceSelectedListener) {
            placeSelectedListener = (OnPlaceSelectedListener) context;
        } else {
            // 구현하지 않았다면 런타임 오류 발생 (개발 시점에 인지하도록)
            throw new RuntimeException(context.toString() + " must implement OnPlaceSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day, container, false);

        textDayTitle = view.findViewById(R.id.textDayTitle);
        editStartTime = view.findViewById(R.id.editStartTime);
        editEndTime = view.findViewById(R.id.editEndTime);
        editPlace = view.findViewById(R.id.editPlace); // EditText로 캐스팅
        editMemo = view.findViewById(R.id.editMemo);
        btnAddSchedule = view.findViewById(R.id.btnAddSchedule);
        recyclerSchedule = view.findViewById(R.id.recyclerSchedule);
        recyclerSchedule.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new DayScheduleAdapter(scheduleList);
        recyclerSchedule.setAdapter(adapter);

        textDayTitle.setText(dayTitle);

        editStartTime.setOnClickListener(v -> showTimePicker(editStartTime));
        editEndTime.setOnClickListener(v -> showTimePicker(editEndTime));

        // editPlace 클릭 리스너: Activity에 마커 선택 다이얼로그를 요청합니다.
        editPlace.setOnClickListener(v -> {
            Log.d("DayFragment", "editPlace clicked for Day: " + dayPosition);
            if (placeSelectedListener != null) {
                // Activity에 마커 선택 다이얼로그를 띄우도록 요청합니다.
                // 이때 현재 DayFragment의 position을 함께 전달하여 어떤 Fragment가 요청했는지 알 수 있게 합니다.
                // 첫 번째 매개변수(placeName)는 다이얼로그를 띄우는 요청이므로 null을 전달합니다.
                placeSelectedListener.onPlaceSelected(null, dayPosition);
            } else {
                Toast.makeText(getContext(), "Activity 리스너가 설정되지 않았습니다.", Toast.LENGTH_SHORT).show();
                Log.e("DayFragment", "placeSelectedListener is null!");
            }
        });

        btnAddSchedule.setOnClickListener(v -> {
            String startTime = editStartTime.getText().toString().trim();
            String endTime = editEndTime.getText().toString().trim();
            String place = editPlace.getText().toString().trim();
            String memo = editMemo.getText().toString().trim();

            if (TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime) || TextUtils.isEmpty(place)) {
                Toast.makeText(getContext(), "시작/종료 시간과 장소를 입력해주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            if (startTime.compareTo(endTime) >= 0) {
                Toast.makeText(getContext(), "시작 시간은 종료 시간보다 빨라야 합니다", Toast.LENGTH_SHORT).show();
                return;
            }

            ScheduleItem item = new ScheduleItem(startTime, endTime, place, memo);
            scheduleList.add(item);

            Collections.sort(scheduleList, Comparator.comparing(ScheduleItem::getStartTime));
            adapter.notifyDataSetChanged();

            editStartTime.setText("시작 시간 선택");
            editEndTime.setText("종료 시간 선택");
            editPlace.setText(""); // 장소 필드 초기화
            editMemo.setText("");
        });

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
        }, 9, 0, true);

        timePickerDialog.show();
    }
}