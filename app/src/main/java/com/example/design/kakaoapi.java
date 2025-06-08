package com.example.design;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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

    // 유저 ID, 그룹명 (실제 프로젝트에선 로그인 기능 통해 받아야 함)
    private String myUserId = "user123";  // 예시 userId
    private String myGroup = "groupA";    // 예시 그룹명

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kakaoapi);

        firestore = FirebaseFirestore.getInstance();

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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        showCurrentLocation();

        loadMarkersFromFirestore();

        mMap.setOnPoiClickListener(poi -> {
            fetchAndShowPlaceInfo(poi.placeId, poi.latLng, true);
        });

        mMap.setOnMarkerClickListener(marker -> {
            LatLng pos = marker.getPosition();
            MarkerItem clickedItem = null;
            for (MarkerItem item : markerItemList) {
                if (item.latitude == pos.latitude && item.longitude == pos.longitude) {
                    clickedItem = item;
                    break;
                }
            }

            if (clickedItem != null && clickedItem.placeId != null) {
                fetchAndShowPlaceInfo(clickedItem.placeId, pos, false);
            } else {
                Toast.makeText(this, "저장된 장소가 아닙니다.", Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    private void loadMarkersFromFirestore() {
        // 내 그룹과 같은 그룹에 속한 마커들만 가져오기
        firestore.collection("markers")
                .whereEqualTo("group", myGroup)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    markerItemList.clear();
                    markerList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        MarkerItem item = doc.toObject(MarkerItem.class);
                        markerItemList.add(item);

                        float markerColor;
                        if (item.userId != null && item.userId.equals(myUserId)) {
                            markerColor = BitmapDescriptorFactory.HUE_AZURE; // 내 마커
                        } else {
                            markerColor = BitmapDescriptorFactory.HUE_GREEN; // 그룹원 마커
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

    private void saveMarkerToFirestore(double lat, double lng, String name, String placeId) {
        MarkerItem markerItem = new MarkerItem(lat, lng, name, placeId);
        markerItem.userId = myUserId;  // 내 userId 저장
        markerItem.group = myGroup;    // 내 그룹 저장

        firestore.collection("markers")
                .add(markerItem)
                .addOnSuccessListener(documentReference -> loadMarkersFromFirestore())
                .addOnFailureListener(e -> Toast.makeText(this, "마커 저장 실패", Toast.LENGTH_SHORT).show());
    }

    private void deleteMarkerFromFirestore(double lat, double lng) {
        firestore.collection("markers")
                .whereEqualTo("latitude", lat)
                .whereEqualTo("longitude", lng)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        doc.getReference().delete();
                    }
                    loadMarkersFromFirestore();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "마커 삭제 실패", Toast.LENGTH_SHORT).show());
    }

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
                builder.setPositiveButton("장소 저장", (dialog, which) -> {
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(place.getName())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                    if (marker != null) {
                        markerList.add(marker);
                    }
                    saveMarkerToFirestore(latLng.latitude, latLng.longitude, place.getName(), place.getId());
                    Toast.makeText(this, "장소가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                }).setNegativeButton("취소", null);
            } else {
                builder.setPositiveButton("삭제", (dialog, which) -> {
                    deleteMarkerFromFirestore(latLng.latitude, latLng.longitude);
                    for (Iterator<Marker> it = markerList.iterator(); it.hasNext(); ) {
                        Marker m = it.next();
                        if (m.getPosition().equals(latLng)) {
                            m.remove();
                            it.remove();
                            break;
                        }
                    }
                    Toast.makeText(this, "마커가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                }).setNegativeButton("닫기", null);
            }
            builder.show();

        }).addOnFailureListener(e -> {
            Log.e("PlaceFetch", "장소 정보 실패", e);
            Toast.makeText(this, "장소 정보를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
        });
    }

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

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

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

    public static class MarkerItem {
        public double latitude;
        public double longitude;
        public String name;
        public String placeId;
        public String userId;
        public String group;
        public boolean isSelected;

        public MarkerItem() {}

        public MarkerItem(double latitude, double longitude, String name, String placeId) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.name = name;
            this.placeId = placeId;
        }
    }

    // 마커 표시 함수 (사용 안함. 필요시 참고하세요)
    private void displayMarkers(GoogleMap nMap, List<MarkerItem> markers, String currentUserId) {
        for (MarkerItem item : markers) {
            float color;
            if (item.userId.equals(currentUserId)) {
                color = BitmapDescriptorFactory.HUE_AZURE;  // 파란색 - 내 마커
            } else {
                color = BitmapDescriptorFactory.HUE_GREEN;  // 초록색 - 그룹 마커
            }
            nMap.addMarker(new MarkerOptions()
                    .position(new LatLng(item.latitude, item.longitude))
                    .title(item.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(color)));
        }
    }
}
