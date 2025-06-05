package com.example.design;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;

public class kakaoapi extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private MarkerDBHelper dbHelper;
    private RecyclerView recyclerView;
    private MarkerAdapter adapter;
    private FusedLocationProviderClient fusedLocationClient;
    private final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private final int AUTOCOMPLETE_REQUEST_CODE = 2001;
    private PlacesClient placesClient;
    private List<Marker> markerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kakaoapi);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyDzpOiPwB8sN1zNPMQSEZBsgTGwYy-p80Y");
        }
        placesClient = Places.createClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        requestLocationPermission();

        dbHelper = new MarkerDBHelper(this);

        recyclerView = findViewById(R.id.recyclerViewMarkers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MarkerAdapter(dbHelper.getAllMarkers(), item -> {
            if (mMap != null) {
                LatLng latLng = new LatLng(item.latitude, item.longitude);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        });
        recyclerView.setAdapter(adapter);

        findViewById(R.id.btnSearchPlace).setOnClickListener(v -> openPlaceSearch());
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mMap != null) showCurrentLocation();
            } else {
                Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        showCurrentLocation();

        // 저장된 마커 로드 및 표시
        List<MarkerItem> savedMarkers = dbHelper.getAllMarkers();
        for (MarkerItem marker : savedMarkers) {
            LatLng pos = new LatLng(marker.latitude, marker.longitude);
            Marker m = mMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .title(marker.name != null ? marker.name : "이름 없음")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            if (m != null) {
                markerList.add(m);  // 🔥 이 줄이 중요!
            }
        }


        // 장소 검색 시 POI 클릭 처리 (기존)
        mMap.setOnPoiClickListener(poi -> {
            fetchAndShowPlaceInfo(poi.placeId, poi.latLng, true);
        });
        mMap.setOnMarkerClickListener(marker -> {
            LatLng pos = marker.getPosition();

            // DB에서 마커 정보 조회
            MarkerItem clickedItem = dbHelper.getMarkerByLatLng(pos.latitude, pos.longitude);

            if (clickedItem != null && clickedItem.placeId != null) {
                // 사진 포함된 상세 정보 다이얼로그 표시 (저장된 마커이므로 showSaveButton = false)
                fetchAndShowPlaceInfo(clickedItem.placeId, pos, false);
            } else {
                if (clickedItem != null && clickedItem.placeId == null) {
                    Toast.makeText(this, "이 마커는 Place ID가 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "저장된 장소가 아닙니다.", Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        });
    }
    private void showDeleteDialog(MarkerItem item, Marker marker) {
        String info = "이름: " + (item.name != null ? item.name : "이름 없음") +
                "\n위도: " + item.latitude +
                "\n경도: " + item.longitude;

        new AlertDialog.Builder(this)
                .setTitle("저장된 장소 정보")
                .setMessage(info + "\n\n이 마커를 삭제하시겠습니까?")
                .setPositiveButton("예", (dialog, which) -> {
                    dbHelper.deleteMarker(item.latitude, item.longitude);
                    marker.remove();
                    adapter.updateMarkers(dbHelper.getAllMarkers());
                    Toast.makeText(this, "마커가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("아니오", null)
                .show();
    }
            // 상세 정보 + 사진 요청 및 다이얼로그 표시
            private void fetchAndShowPlaceInfo(String placeId, LatLng latLng, boolean showSaveButton) {
                if (placeId == null || placeId.isEmpty()) {
                    Toast.makeText(this, "Place ID가 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Place.Field> fields = Arrays.asList(
                        Place.Field.ID,
                        Place.Field.NAME,
                        Place.Field.ADDRESS,
                        Place.Field.PHONE_NUMBER,
                        Place.Field.RATING,
                        Place.Field.OPENING_HOURS,
                        Place.Field.PHOTO_METADATAS
                );

                FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, fields);

                placesClient.fetchPlace(request).addOnSuccessListener(response -> {
                    Place place = response.getPlace();

                    // 다이얼로그 뷰 설정
                    View dialogView = LayoutInflater.from(this).inflate(R.layout.map_place_info, null);
                    ImageView placeImageView = dialogView.findViewById(R.id.placeImageView);
                    TextView placeInfoTextView = dialogView.findViewById(R.id.placeInfoTextView);

                    // 텍스트 정보 구성
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

                    // 사진 처리
                    List<PhotoMetadata> photoMetadataList = place.getPhotoMetadatas();
                    if (photoMetadataList != null && !photoMetadataList.isEmpty()) {
                        PhotoMetadata photoMetadata = photoMetadataList.get(0);
                        FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                                .setMaxWidth(600)
                                .setMaxHeight(400)
                                .build();

                        placesClient.fetchPhoto(photoRequest)
                                .addOnSuccessListener(photoResponse -> {
                                    Bitmap bitmap = photoResponse.getBitmap();
                                    placeImageView.setImageBitmap(bitmap);
                                    placeImageView.setVisibility(View.VISIBLE);
                                })
                                .addOnFailureListener(e -> Log.e("PlacePhoto", "사진 로딩 실패", e));
                    } else {
                        placeImageView.setVisibility(View.GONE);
                    }
// 다이얼로그 빌더 생성
                    AlertDialog.Builder builder = new AlertDialog.Builder(this)
                            .setTitle("장소 정보")
                            .setView(dialogView);

                    if (showSaveButton) {
                        builder.setPositiveButton("장소 저장", (dialog, which) -> {
                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(place.getName())
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                            if (marker != null) {
                                markerList.add(marker); // 마커 리스트에 추가
                            }

                            dbHelper.insertMarker(latLng.latitude, latLng.longitude, place.getName(), place.getId());
                            adapter.updateMarkers(dbHelper.getAllMarkers());
                            Toast.makeText(this, "장소가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                        });
                        builder.setNegativeButton("취소", null);
                    } else {
                        builder.setPositiveButton("삭제", (dialog, which) -> {
                            // DB에서 삭제
                            dbHelper.deleteMarker(latLng.latitude, latLng.longitude);

                            // 마커 리스트에서 제거
                            Iterator<Marker> iterator = markerList.iterator();
                            while (iterator.hasNext()) {
                                Marker m = iterator.next();
                                if (m.getPosition().equals(latLng)) {
                                    m.remove();
                                    iterator.remove();
                                    break;
                                }
                            }

                            adapter.updateMarkers(dbHelper.getAllMarkers());
                            Toast.makeText(this, "마커가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        });
                        builder.setNegativeButton("닫기", null);
                    }
                builder.show();

                }).addOnFailureListener(e -> {
                    Log.e("PlaceFetch", "장소 정보 가져오기 실패", e);
                    Toast.makeText(this, "장소 정보를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
                });
            }

    private void showCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null && mMap != null) {
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
            }
        });
    }
    private void openPlaceSearch() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);

                LatLng latLng = place.getLatLng();
                if (latLng != null) {
                    mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(place.getName())
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
                            .showInfoWindow();

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                    // 여기서 place.getId() 대신 place.getId() 혹은 place.getPlaceId() 호출 (후자 권장)
                    String placeId = null;
                    try {
                        // place.getPlaceId()가 있을 경우 사용
                        placeId = (String) Place.class.getMethod("getPlaceId").invoke(place);
                    } catch (Exception e) {
                        // fallback
                        placeId = place.getId();
                    }

                    fetchAndShowPlaceInfo(placeId, latLng, true);
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Toast.makeText(this, "장소 검색 에러", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // DB Helper 클래스 (Place ID 컬럼 추가됨)
    private static class MarkerDBHelper extends SQLiteOpenHelper {

        private static final String DB_NAME = "markers.db";
        private static final int DB_VERSION = 1;
        private static final String TABLE_NAME = "markers";

        public MarkerDBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "CREATE TABLE " + TABLE_NAME + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "latitude REAL, " +
                    "longitude REAL, " +
                    "name TEXT, " +
                    "place_id TEXT)";
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }

        public void insertMarker(double lat, double lng, String name, String placeId) {
            SQLiteDatabase db = getWritableDatabase();
            Log.d("MarkerQuery", "Searching for marker near: " + lat + ", " + lng);
            Log.d("InsertCheck", "Inserted marker: " + lat + ", " + lng + ", " + name + ", " + placeId);

            db.execSQL("INSERT INTO " + TABLE_NAME + " (latitude, longitude, name, place_id) VALUES (?, ?, ?, ?)",
                    new Object[]{lat, lng, name, placeId});
        }

        public List<MarkerItem> getAllMarkers() {
            List<MarkerItem> markers = new ArrayList<>();
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT latitude, longitude, name, place_id FROM " + TABLE_NAME, null);

            while (cursor.moveToNext()) {
                double lat = cursor.getDouble(0);
                double lng = cursor.getDouble(1);
                String name = cursor.getString(2);
                String placeId = cursor.getString(3);
                markers.add(new MarkerItem(lat, lng, name, placeId));
            }
            cursor.close();
            return markers;
        }
        public MarkerItem getMarkerByLatLng(double lat, double lng) {
            double delta = 0.0001;
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT latitude, longitude, name, place_id FROM " + TABLE_NAME +
                            " WHERE latitude BETWEEN ? AND ? AND longitude BETWEEN ? AND ?",
                    new String[]{
                            String.valueOf(lat - delta),
                            String.valueOf(lat + delta),
                            String.valueOf(lng - delta),
                            String.valueOf(lng + delta)
                    });
            MarkerItem item = null;
            if (cursor.moveToFirst()) {
                double latitude = cursor.getDouble(0);
                double longitude = cursor.getDouble(1);
                String name = cursor.getString(2);
                String placeId = cursor.getString(3);
                item = new MarkerItem(latitude, longitude, name, placeId);
            }
            cursor.close();
            return item;
        }

        public void deleteMarker(double lat, double lng) {
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE latitude=? AND longitude=?", new Object[]{lat, lng});
        }
    }

    // 마커 아이템 데이터 클래스
    private static class MarkerItem {
        double latitude, longitude;
        String name;
        String placeId;

        public MarkerItem(double latitude, double longitude, String name, String placeId) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.name = name;
            this.placeId = placeId;
        }
    }

    // RecyclerView 어댑터 (간단하게 구현)
    private static class MarkerAdapter extends RecyclerView.Adapter<MarkerAdapter.ViewHolder> {

        interface OnItemClickListener {
            void onItemClick(MarkerItem item);
        }

        private List<MarkerItem> markerList;
        private final OnItemClickListener listener;

        public MarkerAdapter(List<MarkerItem> markerList, OnItemClickListener listener) {
            this.markerList = markerList;
            this.listener = listener;

        }

        public void updateMarkers(List<MarkerItem> newMarkers) {
            this.markerList = newMarkers;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            MarkerItem item = markerList.get(position);
            holder.textView.setText(item.name != null ? item.name : "이름 없음");
            holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        }

        @Override
        public int getItemCount() {
            return markerList.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}