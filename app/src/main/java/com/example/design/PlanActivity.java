package com.example.design;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class PlanActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PlanAdapter adapter;
    private List<PlanItem> planList;
    private static final int REQUEST_CODE_ADD_PLAN = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        ImageButton btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddScheduleActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_PLAN);
        });

        recyclerView = findViewById(R.id.recyclerPlan);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        planList = new ArrayList<>();
        adapter = new PlanAdapter(planList);
        recyclerView.setAdapter(adapter);

        // ✅ 일정 항목 클릭 시 DetailScheduleActivity 로 이동
        adapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(PlanActivity.this, DetailScheduleActivity.class);
            // 나중에 plan ID 등 넘길 수 있음
            startActivity(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_PLAN && resultCode == RESULT_OK && data != null) {
            String title = data.getStringExtra("title");
            String startDate = data.getStringExtra("startDate");
            String endDate = data.getStringExtra("endDate");

            String date = startDate + " ~ " + endDate;
            planList.add(new PlanItem(title, date));
            adapter.notifyItemInserted(planList.size() - 1);
        }
    }
}
