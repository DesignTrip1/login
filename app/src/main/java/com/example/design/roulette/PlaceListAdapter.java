package com.example.design.roulette;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.design.R;

import java.util.List;

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.PlaceViewHolder> {

    private List<RouletteData.Place> places;
    private OnPlaceCheckedChangeListener listener;

    public PlaceListAdapter(List<RouletteData.Place> places, OnPlaceCheckedChangeListener listener) {
        this.places = places;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place_checkbox, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        RouletteData.Place place = places.get(position);
        holder.placeNameTextView.setText(place.name);
        holder.placeCheckBox.setChecked(place.isChecked);

        // 체크박스 상태 변경 리스너
        holder.placeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            place.isChecked = isChecked; // 데이터 모델 업데이트
            if (listener != null) {
                listener.onPlaceCheckedChange(place.name, isChecked);
            }
        });

        // 텍스트 클릭 시 체크박스 상태 토글
        holder.itemView.setOnClickListener(v -> {
            holder.placeCheckBox.toggle();
        });
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    // 체크박스 상태 변경을 알리는 인터페이스
    public interface OnPlaceCheckedChangeListener {
        void onPlaceCheckedChange(String placeName, boolean isChecked);
    }

    public static class PlaceViewHolder extends RecyclerView.ViewHolder {
        TextView placeNameTextView;
        CheckBox placeCheckBox;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            placeNameTextView = itemView.findViewById(R.id.placeNameTextView);
            placeCheckBox = itemView.findViewById(R.id.placeCheckBox);
        }
    }
}