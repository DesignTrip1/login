package com.example.design;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.design.login.LoginActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class kakaoapi extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseFirestore firestore;
    private RecyclerView recyclerView;
    private MarkerAdapter adapter;
    private FusedLocationProviderClient fusedLocationClient;
    private final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private final int AUTOCOMPLETE_REQUEST_CODE = 2001;
    private PlacesClient placesClient;
    private List<Marker> markerList = new ArrayList<>();
    private List<MarkerItem> markerItemList = new ArrayList<>();

    private String myUserId;
    private String myGroup; // 사용자가 속한 단 하나의 그룹 ID. 그룹이 없으면 null.

    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_USER_ID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kakaoapi);

        firestore = FirebaseFirestore.getInstance();

        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        myUserId = sharedPreferences.getString(KEY_USER_ID, null);

        if (myUserId == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Places API 초기화
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyDzpOiPwB8sN1zNPMQSEZBsgTGwYy-p80Y"); // 실제 API 키 사용
        }
        placesClient = Places.createClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        requestLocationPermission();

        recyclerView = findViewById(R.id.recyclerViewMarkers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MarkerAdapter(markerItemList, item -> {
            if (mMap != null) {
                LatLng latLng = new LatLng(item.latitude, item.longitude);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        });
        recyclerView.setAdapter(adapter);

        findViewById(R.id.btnSearchPlace).setOnClickListener(v -> openPlaceSearch());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // 위치 권한이 없는 경우 조기 리턴 (onRequestPermissionsResult에서 처리)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        showCurrentLocation();

        mMap.setOnPoiClickListener(poi -> {
            fetchAndShowPlaceInfo(poi.placeId, poi.latLng, true); // true: 저장 버튼 표시
        });

        mMap.setOnMarkerClickListener(marker -> {
            LatLng pos = marker.getPosition();
            MarkerItem clickedItem = null;
            // 마커 클릭 시, 실제 마커의 위치와 목록 아이템의 위치가 일치하는지 확인 (정밀도 고려)
            for (MarkerItem item : markerItemList) {
                // 부동 소수점 비교는 정밀도 문제로 인해 오차가 발생할 수 있습니다.
                // 더 견고한 비교를 위해 LatLng 객체 자체를 비교하거나, 약간의 오차 범위를 허용하는 방법을 고려해야 합니다.
                // 여기서는 간단하게 String으로 변환 후 비교합니다. (권장되는 방식은 아닙니다. 실제 앱에서는 더 견고한 비교 로직 필요)
                // 예: if (Math.abs(item.latitude - pos.latitude) < 0.00001 && Math.abs(item.longitude - pos.longitude) < 0.00001)
                if (Math.abs(item.latitude - pos.latitude) < 0.000001 &&
                        Math.abs(item.longitude - pos.longitude) < 0.000001) {
                    clickedItem = item;
                    break;
                }
            }

            if (clickedItem != null && clickedItem.placeId != null) {
                fetchAndShowPlaceInfo(clickedItem.placeId, pos, false); // false: 삭제 및 공유 버튼 표시 (내가 등록한 경우)
            } else {
                Toast.makeText(this, "저장된 장소가 아닙니다.", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        // ⭐ 지도 준비 완료 후 사용자 그룹 및 마커 로드 시작
        loadUserGroupAndMarkers();
    }

    /**
     * 사용자의 그룹 정보를 로드하고, 이 정보에 따라 Firestore에서 마커를 로드합니다.
     * 이 메서드는 mMap이 준비된 후에 호출되어야 합니다.
     */
    private void loadUserGroupAndMarkers() {
        if (myUserId == null) {
            Log.e("Firestore", "myUserId is null. Cannot load user group.");
            return;
        }

        // 'users' 컬렉션에서 현재 사용자의 문서를 가져와 속한 그룹 ID를 찾습니다.
        // 사용자가 단 하나의 그룹에만 속할 수 있다고 가정합니다.
        firestore.collection("users").document(myUserId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // 'group' 필드를 사용하여 그룹 ID를 가져옵니다. (이전에 'currentGroupId'로 논의했으나, 'group'이 일반적)
                        String userGroupId = documentSnapshot.getString("group");
                        if (userGroupId != null && !userGroupId.isEmpty()) {
                            myGroup = userGroupId; // 단일 그룹 ID를 myGroup으로 설정
                            Log.d("Firestore", "사용자 그룹 로드 성공: " + myGroup);
                        } else {
                            Log.d("Firestore", "사용자가 속한 그룹을 찾을 수 없습니다. 개인 마커 모드로 작동합니다.");
                            Toast.makeText(this, "소속된 그룹이 없어 개인 마커 모드로 작동합니다.", Toast.LENGTH_LONG).show();
                            myGroup = null; // 그룹이 없으면 myGroup을 null로 설정
                        }
                    } else {
                        Log.e("Firestore", "사용자 문서(" + myUserId + ")를 찾을 수 없습니다. 개인 마커 모드로 작동합니다.");
                        Toast.makeText(this, "사용자 정보를 가져오지 못했습니다. 개인 마커 모드로 작동합니다.", Toast.LENGTH_SHORT).show();
                        myGroup = null;
                    }
                    // 그룹 정보 로드 완료 후 마커 로드 시작 (mMap은 이미 준비됨)
                    loadMarkersFromFirestore();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "사용자 그룹 로드 실패: " + e.getMessage());
                    Toast.makeText(this, "그룹 정보를 가져오지 못했습니다. 개인 마커 모드로 작동합니다.", Toast.LENGTH_SHORT).show();
                    myGroup = null;
                    loadMarkersFromFirestore(); // 실패해도 마커 로드 시도 (개인 마커 모드로)
                });
    }


    /**
     * Firestore에서 마커 데이터를 로드하여 지도와 RecyclerView를 갱신합니다.
     * 사용자의 그룹 소속 여부에 따라 개인 마커 또는 그룹 마커를 로드합니다.
     */
    private void loadMarkersFromFirestore() {
        if (myUserId == null) {
            Log.e("Firestore", "myUserId is null. Cannot load markers.");
            return;
        }

        Query query;
        if (myGroup != null) {
            // 그룹에 속해 있다면 해당 그룹의 마커만 로드
            query = firestore.collection("markers").whereEqualTo("group", myGroup);
        } else {
            // 그룹에 속해 있지 않다면 개인 마커만 로드
            // 개인 마커는 group 필드가 null이거나 없는 마커로 정의
            // Firestore에서 'null' 값으로 쿼리하는 것은 해당 필드가 실제로 null인 경우만 포함합니다.
            // 필드가 아예 없는 문서를 포함하려면 isNull() 메서드를 사용해야 합니다.
            // 여기서는 myUserId에 해당하는 마커 중 group 필드가 없는(null) 마커를 가져오는 것이 목적입니다.
            query = firestore.collection("markers")
                    .whereEqualTo("userId", myUserId)
                    .whereEqualTo("group", null); // group 필드가 null인 경우
        }

        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    markerItemList.clear();
                    markerList.clear();
                    if (mMap != null) {
                        mMap.clear(); // 기존 마커 모두 지우기
                    }

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        MarkerItem item = doc.toObject(MarkerItem.class);
                        // group 필드가 없는 개인 마커도 로드하려면 추가적인 확인 필요
                        // (예: doc.contains("group") && item.group == null)
                        if (item.group == null && !item.userId.equals(myUserId)) {
                            // 개인 마커인데 내 것이 아닌 경우 스킵 (그룹 모드 아님)
                            continue;
                        }
                        if (item.group != null && !item.group.equals(myGroup)) {
                            // 그룹 마커인데 내 그룹이 아닌 경우 스킵
                            continue;
                        }


                        markerItemList.add(item);

                        float markerColor;
                        if (item.userId != null && item.userId.equals(myUserId)) {
                            markerColor = BitmapDescriptorFactory.HUE_AZURE; // 내가 등록한 마커
                        } else {
                            markerColor = BitmapDescriptorFactory.HUE_GREEN; // 다른 그룹 멤버가 등록한 마커
                        }

                        Marker m = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(item.latitude, item.longitude))
                                .title(item.name)
                                .icon(BitmapDescriptorFactory.defaultMarker(markerColor)));
                        if (m != null) {
                            markerList.add(m);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "마커 로드 실패", e);
                    Toast.makeText(this, "마커 로드에 실패했습니다.", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * 새 마커를 Firestore에 저장합니다.
     * @param lat 위도
     * @param lng 경도
     * @param name 장소 이름
     * @param placeId 장소 ID
     */
    private void saveMarkerToFirestore(double lat, double lng, String name, String placeId) {
        if (myUserId == null) {
            Toast.makeText(this, "사용자 정보가 없어 마커를 저장할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        MarkerItem markerItem = new MarkerItem(lat, lng, name, placeId);
        markerItem.userId = myUserId;

        // 그룹에 속해 있다면 그룹 마커로 저장, 아니면 개인 마커로 저장 (group 필드를 null로 설정)
        markerItem.group = myGroup;

        firestore.collection("markers")
                .add(markerItem)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "장소가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    loadMarkersFromFirestore(); // 마커 저장 후 목록 갱신
                })
                .addOnFailureListener(e -> Toast.makeText(this, "마커 저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    /**
     * Firestore에서 마커를 삭제합니다.
     * @param lat 위도
     * @param lng 경도
     * @param name 장소 이름
     * @param placeId 장소 ID
     */
    private void deleteMarkerFromFirestore(double lat, double lng, String name, String placeId) {
        if (myUserId == null) {
            Toast.makeText(this, "사용자 정보가 없어 마커를 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Query queryToDelete;
        if (myGroup != null) {
            // 그룹에 속해 있다면 해당 그룹의 마커 중 내가 등록한 마커만 삭제
            queryToDelete = firestore.collection("markers")
                    .whereEqualTo("latitude", lat)
                    .whereEqualTo("longitude", lng)
                    .whereEqualTo("name", name)
                    .whereEqualTo("placeId", placeId)
                    .whereEqualTo("group", myGroup)
                    .whereEqualTo("userId", myUserId); // 내가 등록한 마커만 삭제
        } else {
            // 그룹에 속해 있지 않다면 개인 마커 (group 필드가 null) 중 내가 등록한 마커만 삭제
            queryToDelete = firestore.collection("markers")
                    .whereEqualTo("latitude", lat)
                    .whereEqualTo("longitude", lng)
                    .whereEqualTo("name", name)
                    .whereEqualTo("placeId", placeId)
                    .whereEqualTo("group", null) // 개인 마커는 group 필드가 null
                    .whereEqualTo("userId", myUserId); // 내가 등록한 마커만 삭제
        }

        queryToDelete.get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        Toast.makeText(this, "삭제할 마커를 찾을 수 없거나 삭제 권한이 없습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // 쿼리 결과로 나온 모든 문서를 삭제 (중복 마커 가능성 대비)
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        doc.getReference().delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "마커가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                    loadMarkersFromFirestore(); // 마커 삭제 후 목록 갱신
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "마커 삭제 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "마커 삭제 쿼리 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    /**
     * 장소 정보를 가져와 다이얼로그로 표시합니다.
     * @param placeId 장소 ID
     * @param latLng 장소의 위도/경도
     * @param showSaveButton 저장 버튼 표시 여부
     */
    private void fetchAndShowPlaceInfo(String placeId, LatLng latLng, boolean showSaveButton) {
        if (placeId == null || placeId.isEmpty()) {
            Toast.makeText(this, "Place ID가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Place.Field> fields = Arrays.asList(
                Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS,
                Place.Field.PHONE_NUMBER, Place.Field.RATING,
                Place.Field.OPENING_HOURS, Place.Field.PHOTO_METADATAS
        );

        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, fields);
        placesClient.fetchPlace(request).addOnSuccessListener(response -> {
            Place place = response.getPlace();
            View dialogView = LayoutInflater.from(this).inflate(R.layout.map_place_info, null);
            ImageView placeImageView = dialogView.findViewById(R.id.placeImageView);
            TextView placeInfoTextView = dialogView.findViewById(R.id.placeInfoTextView);

            StringBuilder info = new StringBuilder();
            info.append("이름: ").append(place.getName() != null ? place.getName() : "없음").append("\n")
                    .append("주소: ").append(place.getAddress() != null ? place.getAddress() : "없음").append("\n")
                    .append("전화번호: ").append(place.getPhoneNumber() != null ? place.getPhoneNumber() : "없음").append("\n")
                    .append("평점: ").append(place.getRating() != null ? place.getRating() : "없음").append("\n");

            if (place.getOpeningHours() != null) {
                info.append("영업시간:\n");
                for (String day : place.getOpeningHours().getWeekdayText()) {
                    info.append("  ").append(day).append("\n");
                }
            }

            placeInfoTextView.setText(info.toString());

            List<PhotoMetadata> photoMetadataList = place.getPhotoMetadatas();
            if (photoMetadataList != null && !photoMetadataList.isEmpty()) {
                FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadataList.get(0))
                        .setMaxWidth(600).setMaxHeight(400).build();
                placesClient.fetchPhoto(photoRequest).addOnSuccessListener(photoResponse -> {
                    placeImageView.setImageBitmap(photoResponse.getBitmap());
                    placeImageView.setVisibility(View.VISIBLE);
                });
            } else {
                placeImageView.setVisibility(View.GONE);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("장소 정보")
                    .setView(dialogView);

            if (showSaveButton) {
                // 새로운 장소 클릭 시: 현재 내가 속한 그룹이 있으면 그룹 마커로 저장, 없으면 개인 마커로 저장
                builder.setPositiveButton("장소 저장", (dialog, which) -> {
                    saveMarkerToFirestore(latLng.latitude, latLng.longitude, place.getName(), place.getId());
                }).setNegativeButton("취소", null);
            } else {
                // 기존 마커 클릭 시: 삭제 및 공유 기능 (내가 등록한 마커인 경우)
                MarkerItem clickedMarkerItem = null;
                for (MarkerItem item : markerItemList) {
                    // LatLng 비교 시 정밀도를 높여 비교
                    if (Math.abs(item.latitude - latLng.latitude) < 0.000001 &&
                            Math.abs(item.longitude - latLng.longitude) < 0.000001 &&
                            item.placeId.equals(placeId) &&
                            item.userId != null && item.userId.equals(myUserId)) {
                        clickedMarkerItem = item;
                        break;
                    }
                }

                if (clickedMarkerItem != null) {
                    // 내가 등록한 마커인 경우
                    builder.setPositiveButton("삭제", (dialog, which) -> {
                        deleteMarkerFromFirestore(latLng.latitude, latLng.longitude, place.getName(), place.getId());
                    });

                    // 마커가 개인 마커이고, 내가 그룹에 속해 있다면 공유 버튼 추가
                    if (clickedMarkerItem.group == null && myGroup != null) {
                        builder.setNeutralButton("그룹에 공유", (dialog, which) -> {
                            shareMarkerToGroup(latLng, placeId, place.getName());
                        });
                    }
                } else {
                    // 다른 사용자가 등록한 그룹 마커이거나, 저장되지 않은 마커인 경우 (삭제/공유 불가)
                    builder.setPositiveButton("닫기", null);
                }
            }
            builder.show();

        }).addOnFailureListener(e -> {
            Log.e("PlaceFetch", "장소 정보 실패", e);
            Toast.makeText(this, "장소 정보를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * 개인 마커를 현재 소속된 그룹에 공유하는 기능
     * @param latLng 마커의 위도/경도
     * @param placeId 장소 ID
     * @param placeName 장소 이름
     */
    private void shareMarkerToGroup(LatLng latLng, String placeId, String placeName) {
        if (myGroup == null) {
            Toast.makeText(this, "소속된 그룹이 없어 공유할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 새 마커 아이템을 생성하여 그룹 ID만 변경
        MarkerItem newGroupMarkerItem = new MarkerItem(latLng.latitude, latLng.longitude, placeName, placeId);
        newGroupMarkerItem.userId = myUserId;
        newGroupMarkerItem.group = myGroup; // 현재 소속된 그룹으로 설정

        firestore.collection("markers")
                .add(newGroupMarkerItem)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "마커가 그룹에 공유되었습니다.", Toast.LENGTH_SHORT).show();
                    // 공유 후 현재 마커 목록을 다시 로드하여 변경 사항 반영
                    loadMarkersFromFirestore();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "마커 공유 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    /**
     * 장소 검색 (Autocomplete) 다이얼로그를 엽니다.
     */
    private void openPlaceSearch() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            LatLng latLng = place.getLatLng();
            if (latLng != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                fetchAndShowPlaceInfo(place.getId(), latLng, true);
            }
        }
    }

    /**
     * 위치 권한을 요청합니다.
     */
    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    showCurrentLocation();
                    // 권한 부여 후에도 그룹 및 마커 로드 로직 재확인
                    if (mMap != null && myUserId != null && myGroup == null) { // myGroup이 아직 로드 안된 경우 대비
                        loadUserGroupAndMarkers();
                    }
                }
            } else {
                Toast.makeText(this, "위치 권한이 거부되어 현재 위치를 표시할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 현재 사용자의 위치를 지도에 표시합니다.
     */
    private void showCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null && mMap != null) {
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
            }
        });
    }

    /**
     * Firestore에서 마커 데이터를 나타내는 데이터 클래스입니다.
     */
    public static class MarkerItem {
        public double latitude;
        public double longitude;
        public String name;
        public String placeId;
        public String userId; // 마커를 생성한 사용자 ID
        public String group; // 마커가 속한 그룹 ID (null이면 개인 마커)
        public boolean isSelected; // <--- Uncomment this line!


        public MarkerItem() {}

        public MarkerItem(double latitude, double longitude, String name, String placeId) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.name = name;
            this.placeId = placeId;
        }
    }
}