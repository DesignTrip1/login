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

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
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

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "YOUR_API_KEY");
        }

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

        List<MarkerItem> savedMarkers = dbHelper.getAllMarkers();
        for (MarkerItem marker : savedMarkers) {
            LatLng pos = new LatLng(marker.latitude, marker.longitude);
            mMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .title(marker.name != null ? marker.name : "이름 없음")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }

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
                    .setNegativeButton("아니오", (dialog, which) -> dialog.dismiss())
                    .show();
            return true;
        });

        mMap.setOnPoiClickListener(poi -> {
            String placeId = poi.placeId;
            List<Place.Field> fields = Arrays.asList(
                    Place.Field.NAME,
                    Place.Field.ADDRESS,
                    Place.Field.PHONE_NUMBER,
                    Place.Field.OPENING_HOURS,
                    Place.Field.RATING
            );

            FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, fields);
            PlacesClient placesClient = Places.createClient(this);

            placesClient.fetchPlace(request)
                    .addOnSuccessListener(response -> {
                        Place place = response.getPlace();

                        StringBuilder info = new StringBuilder();
                        info.append("이름: ").append(place.getName()).append("\n");
                        info.append("주소: ").append(place.getAddress()).append("\n");
                        info.append("전화번호: ").append(place.getPhoneNumber()).append("\n");
                        info.append("평점: ").append(place.getRating()).append("\n");

                        if (place.getOpeningHours() != null) {
                            info.append("영업시간:\n");
                            for (String day : place.getOpeningHours().getWeekdayText()) {
                                info.append(day).append("\n");
                            }
                        }

                        new android.app.AlertDialog.Builder(this)
                                .setTitle("장소 상세 정보")
                                .setMessage(info.toString())
                                .setPositiveButton("확인", null)
                                .show();

                        mMap.addMarker(new MarkerOptions()
                                        .position(poi.latLng)
                                        .title(place.getName()))
                                .showInfoWindow();
                    })
                    .addOnFailureListener(e -> {
                        String errorMsg;
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            errorMsg = "API 오류 (코드 " + apiException.getStatusCode() + "): " +
                                    (apiException.getStatusMessage() != null ? apiException.getStatusMessage() : "알 수 없음");
                        } else {
                            errorMsg = e.getLocalizedMessage();
                        }
                        Toast.makeText(this, "장소 정보를 가져오지 못했습니다:\n" + errorMsg, Toast.LENGTH_LONG).show();
                    });
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

    private void openPlaceSearch() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
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
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    dbHelper.insertMarker(latLng.latitude, latLng.longitude, place.getName());
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

    public static class MarkerItem {
        double latitude, longitude;
        String name;

        public MarkerItem(double lat, double lng, String name) {
            this.latitude = lat;
            this.longitude = lng;
            this.name = name;
        }
    }

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
            holder.title.setText(item.name != null ? item.name : "이름 없음");
            holder.subtitle.setText("위도: " + item.latitude + ", 경도: " + item.longitude);

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

    public static class MarkerDBHelper extends SQLiteOpenHelper {
        private static final String DB_NAME = "markers.db";
        private static final int DB_VERSION = 1;

        public MarkerDBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE marker_table (id INTEGER PRIMARY KEY AUTOINCREMENT, latitude REAL, longitude REAL, name TEXT)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS marker_table");
            onCreate(db);
        }

        public void insertMarker(double lat, double lng, String name) {
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL("INSERT INTO marker_table (latitude, longitude, name) VALUES (?, ?, ?)", new Object[]{lat, lng, name});
        }

        public void deleteMarker(double lat, double lng) {
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL("DELETE FROM marker_table WHERE latitude = ? AND longitude = ?", new Object[]{lat, lng});
        }

        public List<MarkerItem> getAllMarkers() {
            List<MarkerItem> list = new ArrayList<>();
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT latitude, longitude, name FROM marker_table", null);
            while (cursor.moveToNext()) {
                double lat = cursor.getDouble(0);
                double lng = cursor.getDouble(1);
                String name = cursor.getString(2);
                list.add(new MarkerItem(lat, lng, name));
            }
            cursor.close();
            return list;
        }
    }
}
