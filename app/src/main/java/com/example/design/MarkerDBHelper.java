package com.example.design;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarkerDBHelper {
    private static final String COLLECTION_NAME = "markers";
    private FirebaseFirestore db;

    public MarkerDBHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public void insertMarker(String userId, double lat, double lng, String name, String placeId) {
        Map<String, Object> marker = new HashMap<>();
        marker.put("userId", userId);
        marker.put("latitude", lat);
        marker.put("longitude", lng);
        marker.put("name", name);
        marker.put("placeId", placeId);

        db.collection(COLLECTION_NAME)
                .add(marker)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "Marker added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding marker", e));
    }

    public void getAllMarkers(OnMarkersLoadedListener listener) {
        db.collection(COLLECTION_NAME)
                .get()
                .addOnCompleteListener(task -> {
                    List<kakaoapi.MarkerItem> markers = new ArrayList<>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            double latitude = document.getDouble("latitude");
                            double longitude = document.getDouble("longitude");
                            String placeId = document.getString("placeId");
                            markers.add(new kakaoapi.MarkerItem(latitude, longitude, name, placeId));
                        }
                        listener.onMarkersLoaded(markers);
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                });
    }

    public interface OnMarkersLoadedListener {
        void onMarkersLoaded(List<kakaoapi.MarkerItem> markers);
    }

    public void deleteMarker(double latitude, double longitude) {
        db.collection(COLLECTION_NAME)
                .whereEqualTo("latitude", latitude)
                .whereEqualTo("longitude", longitude)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().delete();
                    }
                });
    }
}


class MarkerAdapter extends RecyclerView.Adapter<MarkerAdapter.MarkerViewHolder> {
    private List<kakaoapi.MarkerItem> markerList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(kakaoapi.MarkerItem item);
    }

    public MarkerAdapter(List<kakaoapi.MarkerItem> markerList, OnItemClickListener listener) {
        this.markerList = markerList;
        this.listener = listener;
    }

    public void updateMarkers(List<kakaoapi.MarkerItem> newMarkers) {
        markerList = newMarkers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MarkerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new MarkerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MarkerViewHolder holder, int position) {
        kakaoapi.MarkerItem item = markerList.get(position);
        holder.textView.setText(item.name != null ? item.name : "이름 없음");
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return markerList.size();
    }

    static class MarkerViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public MarkerViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
    private void displayMarkers(GoogleMap mMap, List<kakaoapi.MarkerItem> markers, String myUserId) {
        mMap.clear();  // 마커 초기화
        for (kakaoapi.MarkerItem item : markers) {
            float color;
            if (item.isSelected) {
                color = BitmapDescriptorFactory.HUE_YELLOW; // 여행지
            } else if (item.userId.equals(myUserId)) {
                color = BitmapDescriptorFactory.HUE_AZURE;  // 내 마커
            } else {
                color = BitmapDescriptorFactory.HUE_GREEN;  // 그룹원 마커
            }

            MarkerOptions options = new MarkerOptions()
                    .position(new LatLng(item.latitude, item.longitude))
                    .title(item.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(color));
            mMap.addMarker(options);
        }
    }

}
