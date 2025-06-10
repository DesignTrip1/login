package com.example.design.roulette;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.design.R;

import java.util.ArrayList;
import java.util.List;

public class PlaceListActivity extends AppCompatActivity implements PlaceListAdapter.OnPlaceCheckedChangeListener {

    private TextView titleTextView;
    private RecyclerView placesRecyclerView;
    private Button confirmButton;
    private PlaceListAdapter adapter;
    private List<RouletteData.Place> currentPlaces; // 현재 표시되는 장소 목록
    private String selectedDestination; // 이전 액티비티에서 넘어온 여행지 이름

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);

        titleTextView = findViewById(R.id.titleTextView);
        placesRecyclerView = findViewById(R.id.placesRecyclerView);
        confirmButton = findViewById(R.id.confirmButton);

        // Intent로부터 여행지 이름 받기
        selectedDestination = getIntent().getStringExtra("destinationName");
        if (selectedDestination == null) {
            selectedDestination = "선택된 여행지 없음"; // 오류 처리 또는 기본값
        }
        titleTextView.setText(selectedDestination + " 장소 선택");

        // 해당 여행지의 장소 목록 가져오기
        currentPlaces = RouletteData.getPlacesForDestination(selectedDestination);

        // RecyclerView 설정
        placesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlaceListAdapter(currentPlaces, this); // 'this'는 OnPlaceCheckedChangeListener 구현
        placesRecyclerView.setAdapter(adapter);

        // 확인 버튼 클릭 리스너
        confirmButton.setOnClickListener(v -> {
            List<String> selectedPlaceNames = new ArrayList<>();
            for (RouletteData.Place place : currentPlaces) {
                if (place.isChecked) {
                    selectedPlaceNames.add(place.name);
                }
            }
            if (!selectedPlaceNames.isEmpty()) {
                // 선택된 장소들을 Toast 메시지로 보여줍니다.
                // 실제 앱에서는 이 데이터를 다음 화면으로 전달하거나 다른 로직을 수행할 수 있습니다.
                Toast.makeText(PlaceListActivity.this,
                        "선택된 장소: " + String.join(", ", selectedPlaceNames),
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(PlaceListActivity.this, "선택된 장소가 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 초기 확인 버튼 상태 업데이트
        updateConfirmButtonState();

        // 액션바 설정 (선택 사항)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(selectedDestination + " 장소");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onPlaceCheckedChange(String placeName, boolean isChecked) {
        // 체크박스 상태 변경 시 호출됩니다.
        updateConfirmButtonState(); // 확인 버튼 상태를 업데이트합니다.
    }

    // 확인 버튼의 활성화 상태를 업데이트하는 메서드
    private void updateConfirmButtonState() {
        boolean anyChecked = false;
        for (RouletteData.Place place : currentPlaces) {
            if (place.isChecked) {
                anyChecked = true;
                break;
            }
        }
        confirmButton.setEnabled(anyChecked); // 하나라도 체크되면 활성화
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // 뒤로가기 버튼 처리
        return true;
    }
}