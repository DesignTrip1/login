package com.example.design;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
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

    private TextView emptyMessage;

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

        emptyMessage = findViewById(R.id.emptyMessage);
        emptyMessage.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddScheduleActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_PLAN);
        });

        recyclerView = findViewById(R.id.recyclerPlan);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        planList = new ArrayList<>();
        adapter = new PlanAdapter(planList);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(position -> {
            PlanItem selectedPlan = planList.get(position);
            String period = selectedPlan.getPeriod(); // 예: "2025-06-10 ~ 2025-06-12"

            // 시작일과 종료일 분리
            String[] dates = period.split(" ~ ");
            String startDate = dates[0];
            String endDate = dates[1];

            Intent intent = new Intent(PlanActivity.this, DetailScheduleActivity.class);
            intent.putExtra("startDate", startDate);
            intent.putExtra("endDate", endDate);
            startActivity(intent);
        });

        adapter.setOnDeleteConfirmedListener(position -> {
            planList.remove(position);
            adapter.notifyItemRemoved(position);
            updateEmptyMessageVisibility();
        });

        updateEmptyMessageVisibility();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_PLAN && resultCode == RESULT_OK && data != null) {
            String title = data.getStringExtra("title");
            String startDate = data.getStringExtra("startDate");
            String endDate = data.getStringExtra("endDate");

            String date = startDate + " ~ " + endDate;
            planList.add(new PlanItem(title, startDate, endDate));
            adapter.notifyItemInserted(planList.size() - 1);
            updateEmptyMessageVisibility();
        }
    }

    private void updateEmptyMessageVisibility() {
        if (planList.isEmpty()) {
            emptyMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);  // 👈 추가!
        } else {
            emptyMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);  // 👈 추가!
        }
    }
}
