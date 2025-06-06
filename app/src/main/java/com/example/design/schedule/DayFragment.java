package com.example.design.schedule;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class DayFragment extends Fragment {

    private static final String ARG_DAY_TITLE = "dayTitle";
    private String dayTitle;

    private TextView editStartTime, editEndTime;
    private EditText editPlace, editMemo;
    private Button btnAddSchedule;
    private RecyclerView recyclerSchedule;
    private DayScheduleAdapter adapter;
    private List<ScheduleItem> scheduleList;

    public static DayFragment newInstance(String dayTitle) {
        DayFragment fragment = new DayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DAY_TITLE, dayTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dayTitle = getArguments().getString(ARG_DAY_TITLE);
        }
        scheduleList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day, container, false);

        TextView textDayTitle = view.findViewById(R.id.textDayTitle);
        editStartTime = view.findViewById(R.id.editStartTime);
        editEndTime = view.findViewById(R.id.editEndTime);
        editPlace = view.findViewById(R.id.editPlace);
        editMemo = view.findViewById(R.id.editMemo);
        btnAddSchedule = view.findViewById(R.id.btnAddSchedule);
        recyclerSchedule = view.findViewById(R.id.recyclerSchedule);
        recyclerSchedule.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new DayScheduleAdapter(scheduleList);
        recyclerSchedule.setAdapter(adapter);

        textDayTitle.setText(dayTitle);

        editStartTime.setOnClickListener(v -> showTimePicker(editStartTime));
        editEndTime.setOnClickListener(v -> showTimePicker(editEndTime));

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
            editPlace.setText("");
            editMemo.setText("");
        });

        return view;
    }

    private void showTimePicker(TextView targetView) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
            String formattedTime = String.format(Locale.KOREA, "%02d:%02d", hourOfDay, minute);
            targetView.setText(formattedTime);
        }, 9, 0, true);

        timePickerDialog.show();
    }
}
