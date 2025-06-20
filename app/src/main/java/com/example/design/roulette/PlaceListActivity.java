package com.example.design.roulette;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.design.MainActivity;
import com.example.design.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot; // QuerySnapshot 임포트 추가

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class PlaceListActivity extends AppCompatActivity implements PlaceListAdapter.OnPlaceCheckedChangeListener {

    private static final String TAG = "PlaceListActivity";

    private TextView titleTextView;
    private RecyclerView placesRecyclerView;
    private Button confirmButton;
    private PlaceListAdapter adapter;
    private List<RouletteData.Place> currentPlaces;
    private String selectedDestination;

    // Firebase Firestore 관련 필드
    private FirebaseFirestore firestore;
    private String myUserId;
    private String myGroup; // 현재 사용자가 속한 그룹 ID

    // SharedPreferences 관련 필드
    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_USER_ID = "userId";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);

        // Firebase Firestore 초기화
        firestore = FirebaseFirestore.getInstance();

        // SharedPreferences 초기화 및 userId 가져오기
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        myUserId = sharedPreferences.getString(KEY_USER_ID, null);

        if (myUserId == null) {
            Log.e(TAG, "사용자 ID를 SharedPreferences에서 찾을 수 없습니다.");
            Toast.makeText(this, "로그인이 필요합니다. 개인 마커로 저장됩니다.", Toast.LENGTH_LONG).show();
            myUserId = "anonymous";
            // TODO: 실제 앱에서는 로그인 화면으로 이동하는 로직을 추가해야 함.
        } else {
            Log.d(TAG, "SharedPreferences에서 사용자 ID 로드 성공: " + myUserId);
        }

        titleTextView = findViewById(R.id.titleTextView);
        placesRecyclerView = findViewById(R.id.placesRecyclerView);
        confirmButton = findViewById(R.id.confirmButton);

        selectedDestination = getIntent().getStringExtra("destinationName");
        if (selectedDestination == null || selectedDestination.isEmpty()) {
            Toast.makeText(this, "선택된 여행지 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        titleTextView.setText(selectedDestination + " 장소 선택");

        placesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        currentPlaces = RouletteData.getPlacesForDestination(selectedDestination);
        adapter = new PlaceListAdapter(this, currentPlaces, this);
        placesRecyclerView.setAdapter(adapter);

        confirmButton.setOnClickListener(v -> {
            List<RouletteData.Place> selectedPlaces = new ArrayList<>();
            for (RouletteData.Place place : currentPlaces) {
                if (place.isChecked()) {
                    selectedPlaces.add(place);
                }
            }

            if (!selectedPlaces.isEmpty()) {
                saveMarkersToFirestore(selectedPlaces);
            } else {
                Toast.makeText(PlaceListActivity.this, "선택된 장소가 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        updateConfirmButtonState();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(selectedDestination + " 장소");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        loadUserGroup();
    }

    private void loadUserGroup() {
        if (myUserId == null || myUserId.equals("anonymous")) {
            Log.w(TAG, "User ID is anonymous or null, proceeding as individual. Group will be null.");
            myGroup = null;
            return;
        }

        firestore.collection("users").document(myUserId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String userGroupId = documentSnapshot.getString("group");
                        if (userGroupId != null && !userGroupId.isEmpty()) {
                            myGroup = userGroupId;
                            Log.d(TAG, "사용자 그룹 로드 성공: " + myGroup);
                        } else {
                            Log.d(TAG, "사용자 문서에 그룹 필드를 찾을 수 없거나 비어있습니다. 개인 마커 모드로 작동합니다.");
                            Toast.makeText(this, "소속된 그룹이 없어 개인 마커 모드로 작동합니다.", Toast.LENGTH_LONG).show();
                            myGroup = null;
                        }
                    } else {
                        Log.e(TAG, "사용자 문서(" + myUserId + ")를 찾을 수 없습니다. 개인 마커 모드로 작동합니다.");
                        Toast.makeText(this, "사용자 정보를 가져오지 못했습니다. 개인 마커 모드로 작동합니다.", Toast.LENGTH_SHORT).show();
                        myGroup = null;
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "사용자 그룹 로드 실패: " + e.getMessage());
                    Toast.makeText(this, "그룹 정보를 가져오지 못했습니다. 개인 마커 모드로 작동합니다.", Toast.LENGTH_SHORT).show();
                    myGroup = null;
                });
    }

    private void saveMarkersToFirestore(List<RouletteData.Place> placesToSave) {
        if (placesToSave.isEmpty()) {
            return;
        }

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        AtomicInteger skippedCount = new AtomicInteger(0); // 새로 추가: 중복으로 인해 건너뛴 개수
        int totalPlaces = placesToSave.size();

        for (RouletteData.Place place : placesToSave) {
            // Firestore 쿼리를 사용하여 중복 확인
            firestore.collection("markers")
                    .whereEqualTo("userId", myUserId) // 현재 사용자 ID
                    .whereEqualTo("placeId", place.placeId) // 선택된 장소의 Place ID
                    // .whereEqualTo("group", myGroup) // 그룹까지 포함하여 중복을 확인할 경우 주석 해제 (단, myGroup이 null일 때 처리 필요)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                // 이미 동일한 userId와 placeId를 가진 문서가 존재함 (중복)
                                Log.d(TAG, "마커 중복! 저장 건너뛰기: " + place.name);
                                skippedCount.incrementAndGet();
                                checkAllSavesCompleted(successCount.get(), failureCount.get(), skippedCount.get(), totalPlaces);
                            } else {
                                // 중복이 아니므로 새로운 마커 저장
                                Map<String, Object> markerData = new HashMap<>();
                                markerData.put("name", place.name);
                                markerData.put("latitude", place.latitude);
                                markerData.put("longitude", place.longitude);
                                markerData.put("placeId", place.placeId);
                                markerData.put("userId", myUserId);
                                markerData.put("group", myGroup);
                                markerData.put("isSelected", place.isChecked());

                                firestore.collection("markers")
                                        .add(markerData)
                                        .addOnSuccessListener(documentReference -> {
                                            Log.d(TAG, "마커 저장 성공: " + documentReference.getId() + " - " + place.name);
                                            successCount.incrementAndGet();
                                            checkAllSavesCompleted(successCount.get(), failureCount.get(), skippedCount.get(), totalPlaces);
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e(TAG, "마커 저장 실패: " + place.name + ", 오류: " + e.getMessage());
                                            failureCount.incrementAndGet();
                                            checkAllSavesCompleted(successCount.get(), failureCount.get(), skippedCount.get(), totalPlaces);
                                        });
                            }
                        } else {
                            // 쿼리 실패 시에도 에러 처리 및 실패 카운트 증가
                            Log.e(TAG, "마커 중복 확인 쿼리 실패: " + place.name + ", 오류: " + task.getException());
                            failureCount.incrementAndGet(); // 쿼리 실패도 저장 실패로 간주
                            checkAllSavesCompleted(successCount.get(), failureCount.get(), skippedCount.get(), totalPlaces);
                        }
                    });
        }
    }

    // 모든 저장 작업이 완료되었는지 확인하고 액티비티를 종료하는 헬퍼 메서드
    private void checkAllSavesCompleted(int success, int failure, int skipped, int total) {
        if (success + failure + skipped == total) { // 모든 장소에 대한 처리(성공/실패/건너뜀) 완료
            String message = "";
            if (success > 0) {
                message += "선택된 장소 " + success + "개 저장 완료!";
            }
            if (skipped > 0) {
                if (!message.isEmpty()) message += "\n";
                message += "장소 " + skipped + "개는 이미 저장되어 건너뛰었습니다.";
            }
            if (failure > 0) {
                if (!message.isEmpty()) message += "\n";
                message += "일부 장소 저장 실패: " + failure + "개";
            }
            if (message.isEmpty()) { // 아무것도 처리되지 않은 경우 (이런 경우는 드물겠지만)
                message = "선택된 장소 처리 완료 (변경 없음).";
            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();


            // 홈 화면으로 돌아가기 (MainActivity로 가정)
            Intent intent = new Intent(PlaceListActivity.this, MainActivity.class);
            // FLAG_ACTIVITY_CLEAR_TOP: 현재 액티비티를 포함하여 그 위에 있는 모든 액티비티를 스택에서 제거합니다.
            // FLAG_ACTIVITY_NEW_TASK: 새 태스크에서 액티비티를 시작하거나, 이미 실행 중인 태스크가 있다면 그 태스크로 이동합니다.
            // 이 두 플래그는 스택을 깨끗하게 정리하고 MainActivity로 돌아가는 데 유용합니다.
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // 현재 액티비티 종료
        }
    }

    @Override
    public void onPlaceCheckedChange(String placeName, boolean isChecked) {
        updateConfirmButtonState();
    }

    private void updateConfirmButtonState() {
        boolean anyChecked = false;
        for (RouletteData.Place place : currentPlaces) {
            if (place.isChecked()) {
                anyChecked = true;
                break;
            }
        }
        confirmButton.setEnabled(anyChecked);
    }
}