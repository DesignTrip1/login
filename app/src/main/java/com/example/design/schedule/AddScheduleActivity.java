package com.example.design.schedule;

import android.app.DatePickerDialog;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.design.R;

import java.util.Calendar;

public class AddScheduleActivity extends AppCompatActivity {

    private Button btnStartDate, btnEndDate, btnNext;
    private EditText editTitle;

    private String selectedStartDate = "";
    private String selectedEndDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        btnStartDate = findViewById(R.id.btnStartDate);
        btnEndDate = findViewById(R.id.btnEndDate);
        btnNext = findViewById(R.id.btnNext);
        editTitle = findViewById(R.id.editTitle);

        btnStartDate.setOnClickListener(v -> showDatePicker(true));
        btnEndDate.setOnClickListener(v -> showDatePicker(false));

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
