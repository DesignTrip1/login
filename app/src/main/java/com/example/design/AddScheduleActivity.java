package com.example.design;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class AddScheduleActivity extends AppCompatActivity {

    private Spinner spinnerProvince, spinnerCity;
    private Button btnStartDate, btnEndDate, btnNext, btnSelectGroup;
    private EditText editTitle;

    private String selectedStartDate = "";
    private String selectedEndDate = "";

    private HashMap<String, String[]> regionData = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        regionData.put("서울특별시", new String[]{"종로구", "중구", "강남구", "서초구"});
        regionData.put("부산광역시", new String[]{"해운대구", "수영구", "동래구"});
        regionData.put("경기도", new String[]{"수원시", "성남시", "고양시"});
        regionData.put("강원도", new String[]{"춘천시", "강릉시", "원주시"});

        spinnerProvince = findViewById(R.id.spinnerProvince);
        spinnerCity = findViewById(R.id.spinnerCity);
        btnStartDate = findViewById(R.id.btnStartDate);
        btnEndDate = findViewById(R.id.btnEndDate);
        btnNext = findViewById(R.id.btnNext);
        btnSelectGroup = findViewById(R.id.btnSelectGroup);
        editTitle = findViewById(R.id.editTitle);

        ArrayList<String> provinceList = new ArrayList<>(regionData.keySet());
        ArrayAdapter<String> provinceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, provinceList);
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProvince.setAdapter(provinceAdapter);

        spinnerProvince.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedProvince = provinceList.get(position);
                String[] cities = regionData.get(selectedProvince);
                ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(AddScheduleActivity.this, android.R.layout.simple_spinner_item, cities);
                cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCity.setAdapter(cityAdapter);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        btnStartDate.setOnClickListener(v -> showDatePicker(true));
        btnEndDate.setOnClickListener(v -> showDatePicker(false));

        btnSelectGroup.setOnClickListener(v ->
                Toast.makeText(this, "그룹 선택 팝업 예정", Toast.LENGTH_SHORT).show()
        );

        btnNext.setOnClickListener(v -> {
            String title = editTitle.getText().toString().trim();
            if (title.isEmpty() || selectedStartDate.isEmpty() || selectedEndDate.isEmpty()) {
                Toast.makeText(this, "모든 정보를 입력해 주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra("title", title);
            resultIntent.putExtra("startDate", selectedStartDate);
            resultIntent.putExtra("endDate", selectedEndDate);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private void showDatePicker(boolean isStart) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String dateStr = year + "-" + (month + 1) + "-" + dayOfMonth;
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
}
