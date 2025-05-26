package com.example.design;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class kakaoapi extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MarkerDBHelper dbHelper;
    private RecyclerView recyclerView;
    private MarkerAdapter adapter;
    private FusedLocationProviderClient fusedLocationClient;
    private final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private final int AUTOCOMPLETE_REQUEST_CODE = 2001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kakaoapi);

        // Places API 초기화 (API 키를 AndroidManifest.xml에 등록했는지 확인)
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyDzpOiPwB8sN1zNPMQSEZBsgTGwYy-p80Y");
        }

        // 지도 프래그먼트 초기화
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        requestLocationPermission();

        // DB 초기화
        dbHelper = new MarkerDBHelper(this);

        // 리사이클러뷰 초기화 및 어댑터에 클릭 리스너 연결
        recyclerView = findViewById(R.id.recyclerViewMarkers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MarkerAdapter(dbHelper.getAllMarkers(), new MarkerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MarkerItem item) {
                if (mMap != null) {
                    LatLng latLng = new LatLng(item.latitude, item.longitude);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                }
            }
        });
        recyclerView.setAdapter(adapter);

        // 검색 버튼 초기화 및 클릭 리스너 연결
        Button btnSearch = findViewById(R.id.btnSearchPlace);
        btnSearch.setOnClickListener(v -> openPlaceSearch());
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

        // DB에서 저장된 핀 불러오기
        List<MarkerItem> savedMarkers = dbHelper.getAllMarkers();
        for (MarkerItem marker : savedMarkers) {
            LatLng pos = new LatLng(marker.latitude, marker.longitude);
            mMap.addMarker(new MarkerOptions().position(pos).title("저장된 핀"));
        }

        // 마커 클릭 시 DB에서 삭제 및 지도/리스트 갱신
        mMap.setOnMarkerClickListener(marker -> {
            LatLng pos = marker.getPosition();

            new android.app.AlertDialog.Builder(this)
                    .setTitle("핀 삭제")
                    .setMessage("이 위치의 핀을 삭제하시겠습니까?")
                    .setPositiveButton("예", (dialog, which) -> {
                        dbHelper.deleteMarker(pos.latitude, pos.longitude);
                        marker.remove();
                        adapter.updateMarkers(dbHelper.getAllMarkers());
                        Toast.makeText(this, "핀 삭제됨", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("아니오", (dialog, which) -> {
                        dialog.dismiss(); // 아무 일도 하지 않음
                    })
                    .show();

            return true; // 클릭 이벤트 소비됨
        });
        // ★ POI 클릭 이벤트 추가 ★
        mMap.setOnPoiClickListener(poi -> {
            // POI 클릭 시 장소명과 ID를 다이얼로그로 보여주기
            new android.app.AlertDialog.Builder(this)
                    .setTitle(poi.name)
                    .setMessage("Place ID: " + poi.placeId + "\n위도: " + poi.latLng.latitude + "\n경도: " + poi.latLng.longitude)
                    .setPositiveButton("확인", null)
                    .show();

            // POI 위치에 마커 표시 및 인포 윈도우 바로 열기
            mMap.addMarker(new MarkerOptions().position(poi.latLng).title(poi.name)).showInfoWindow();
        });
    }

    private void showCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 15));
                    } else {
                        Toast.makeText(this, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // 장소 검색 화면 열기
    private void openPlaceSearch() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    // 검색 결과 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                LatLng latLng = place.getLatLng();

                if (latLng != null) {
                    mMap.addMarker(new MarkerOptions().position(latLng).title(place.getName()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    dbHelper.insertMarker(latLng.latitude, latLng.longitude);
                    adapter.updateMarkers(dbHelper.getAllMarkers());
                    Toast.makeText(this, place.getName() + " 위치에 마커를 추가했습니다.", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Toast.makeText(this, "검색 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 데이터 클래스
    public static class MarkerItem {
        double latitude, longitude;

        public MarkerItem(double lat, double lng) {
            latitude = lat;
            longitude = lng;
        }
    }

    // RecyclerView 어댑터
    public static class MarkerAdapter extends RecyclerView.Adapter<MarkerAdapter.MarkerViewHolder> {
        private List<MarkerItem> markerList;
        private OnItemClickListener listener;

        public interface OnItemClickListener {
            void onItemClick(MarkerItem item);
        }

        public MarkerAdapter(List<MarkerItem> list, OnItemClickListener listener) {
            this.markerList = list;
            this.listener = listener;
        }

        public void updateMarkers(List<MarkerItem> newList) {
            markerList = newList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public MarkerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            return new MarkerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MarkerViewHolder holder, int position) {
            MarkerItem item = markerList.get(position);
            holder.title.setText("위도: " + item.latitude);
            holder.subtitle.setText("경도: " + item.longitude);

            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        }

        @Override
        public int getItemCount() {
            return markerList.size();
        }

        static class MarkerViewHolder extends RecyclerView.ViewHolder {
            TextView title, subtitle;

            public MarkerViewHolder(View itemView) {
                super(itemView);
                title = itemView.findViewById(android.R.id.text1);
                subtitle = itemView.findViewById(android.R.id.text2);
            }
        }
    }

    // SQLite 헬퍼 클래스
    public static class MarkerDBHelper extends SQLiteOpenHelper {
        private static final String DB_NAME = "markers.db";
        private static final int DB_VERSION = 1;

        public MarkerDBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE marker_table (id INTEGER PRIMARY KEY AUTOINCREMENT, latitude REAL, longitude REAL)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS marker_table");
            onCreate(db);
        }

        public void insertMarker(double lat, double lng) {
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL("INSERT INTO marker_table (latitude, longitude) VALUES (?, ?)", new Object[]{lat, lng});
        }

        public void deleteMarker(double lat, double lng) {
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL("DELETE FROM marker_table WHERE latitude = ? AND longitude = ?",
                    new Object[]{lat, lng});
        }

        public List<MarkerItem> getAllMarkers() {
            List<MarkerItem> list = new ArrayList<>();
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT latitude, longitude FROM marker_table", null);
            while (cursor.moveToNext()) {
                double lat = cursor.getDouble(0);
                double lng = cursor.getDouble(1);
                list.add(new MarkerItem(lat, lng));
            }
            cursor.close();
            return list;
        }
    }
}
