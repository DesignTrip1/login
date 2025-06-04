package com.example.design;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.*;

public class DetailScheduleActivity extends AppCompatActivity {

    private Spinner daySpinner;
    private EditText editTime, editPlace, editMemo;
    private Button btnAddDetail;
    private RecyclerView recyclerDetail;
    private DetailAdapter adapter;
    private List<DetailItem> detailList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_schedule);

        daySpinner = findViewById(R.id.spinnerDay);
        editTime = findViewById(R.id.editTime);
        editPlace = findViewById(R.id.editPlace);
        editMemo = findViewById(R.id.editMemo);
        btnAddDetail = findViewById(R.id.btnAddDetail);
        recyclerDetail = findViewById(R.id.recyclerDetail);

        detailList = new ArrayList<>();
        adapter = new DetailAdapter(detailList);
        recyclerDetail.setLayoutManager(new LinearLayoutManager(this));
        recyclerDetail.setAdapter(adapter);

        // 요일 설정
        String[] days = {"1일차", "2일차", "3일차", "4일차", "5일차"};
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, days);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(dayAdapter);

        // 시간 선택
        editTime.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new TimePickerDialog(this, (view, hour, minute) -> {
                String timeStr = String.format("%02d:%02d", hour, minute);
                editTime.setText(timeStr);
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        });

        // 추가 버튼
        btnAddDetail.setOnClickListener(v -> {
            String day = daySpinner.getSelectedItem().toString();
            String time = editTime.getText().toString();
            String place = editPlace.getText().toString().trim();
            String memo = editMemo.getText().toString().trim();

            if (time.isEmpty() || place.isEmpty()) {
                Toast.makeText(this, "시간과 장소는 필수 입력입니다", Toast.LENGTH_SHORT).show();
                return;
            }

            detailList.add(new DetailItem(day, time, place, memo));
            adapter.notifyItemInserted(detailList.size() - 1);

            editTime.setText("");
            editPlace.setText("");
            editMemo.setText("");
        });
    }
}
