package com.example.design.roulette;

import android.content.Context; // Context 임포트 (이미지 로딩에 필요)
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView; // ImageView 임포트
import android.widget.TextView;
import android.widget.Toast; // 테스트용 (선택 사항)

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.design.R;
// import com.bumptech.glide.Glide; // Glide 사용 시 임포트

import java.util.List;

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.PlaceViewHolder> {

    private List<RouletteData.Place> places;
    private OnPlaceCheckedChangeListener listener;
    private Context context; // ⭐ Context 필드 추가 ⭐

    // ⭐ 생성자 변경: Context를 받도록 수정 ⭐
    public PlaceListAdapter(Context context, List<RouletteData.Place> places, OnPlaceCheckedChangeListener listener) {
        this.context = context; // Context 초기화
        this.places = places;
        this.listener = listener;
    }

    // 데이터 업데이트 메서드 추가 (필요시 사용)
    public void setPlaces(List<RouletteData.Place> newPlaces) {
        this.places = newPlaces;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // ⭐ item_place_card.xml 레이아웃 사용 ⭐
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place_card, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        RouletteData.Place place = places.get(position);

        // ⭐ 이미지 로딩 (imgPlace) ⭐
        if (place.imageResId != 0) {
            holder.imgPlace.setImageResource(place.imageResId);
            /*
            // Glide 사용 예시 (더 부드러운 이미지 로딩)
            Glide.with(context) // context 사용
                .load(place.imageResId)
                .placeholder(R.drawable.default_place_image) // 로딩 중 보여줄 이미지 (있다면)
                .error(R.drawable.error_image) // 에러 발생 시 보여줄 이미지 (있다면)
                .into(holder.imgPlace);
            */
        } else {
            // 이미지가 없는 경우를 대비한 기본 이미지 (drawable 폴더에 default_place_image.png 추가 필요)
            holder.imgPlace.setImageResource(R.drawable.default_place_image);
        }

        // ⭐ 장소 이름 (txtPlaceTitle) ⭐
        holder.txtPlaceTitle.setText(place.name);

        // ⭐ 카테고리 (txtPlaceCategory) ⭐
        // Place 클래스에 'category' 필드가 있어야 함
        if (place.category != null && !place.category.isEmpty()) {
            holder.txtPlaceCategory.setText(place.category);
            holder.txtPlaceCategory.setVisibility(View.VISIBLE);
        } else {
            holder.txtPlaceCategory.setVisibility(View.GONE); // 카테고리가 없으면 숨김
        }


        // ⭐ 별점 (txtPlaceRating) ⭐
        // Place 클래스에 'rating' 필드가 있어야 함
        holder.txtPlaceRating.setText(String.format("%.1f", place.rating)); // 소수점 한 자리까지 표시

        // ⭐ 주소 (txtPlaceAddress) ⭐
        // Place 클래스에 'address' 필드가 있어야 함
        if (place.address != null && !place.address.isEmpty()) {
            holder.txtPlaceAddress.setText(place.address);
            holder.txtPlaceAddress.setVisibility(View.VISIBLE);
        } else {
            holder.txtPlaceAddress.setVisibility(View.GONE); // 주소가 없으면 숨김
        }


        // ⭐ 체크박스 (checkBoxSelectPlace) ⭐
        holder.checkBoxSelectPlace.setChecked(place.isChecked);

        // 체크박스 상태 변경 리스너
        holder.checkBoxSelectPlace.setOnCheckedChangeListener((buttonView, isChecked) -> {
            place.isChecked = isChecked; // 데이터 모델 업데이트
            if (listener != null) {
                listener.onPlaceCheckedChange(place.name, isChecked);
            }
            // Toast.makeText(context, place.name + " " + (isChecked ? "선택됨" : "해제됨"), Toast.LENGTH_SHORT).show(); // 테스트용
        });

        // 아이템 전체 클릭 시 체크박스 상태 토글
        holder.itemView.setOnClickListener(v -> {
            holder.checkBoxSelectPlace.toggle();
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
        ImageView imgPlace; // ⭐ 추가 ⭐
        TextView txtPlaceTitle; // ⭐ 이름 변경 (placeNameTextView -> txtPlaceTitle) ⭐
        TextView txtPlaceCategory; // ⭐ 추가 ⭐
        TextView txtPlaceRating; // ⭐ 추가 ⭐
        TextView txtPlaceAddress; // ⭐ 추가 ⭐
        CheckBox checkBoxSelectPlace; // ⭐ 이름 변경 (placeCheckBox -> checkBoxSelectPlace) ⭐

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPlace = itemView.findViewById(R.id.imgPlace); // ⭐ 연결 ⭐
            txtPlaceTitle = itemView.findViewById(R.id.txtPlaceTitle); // ⭐ 연결 ⭐
            txtPlaceCategory = itemView.findViewById(R.id.txtPlaceCategory); // ⭐ 연결 ⭐
            txtPlaceRating = itemView.findViewById(R.id.txtPlaceRating); // ⭐ 연결 ⭐
            txtPlaceAddress = itemView.findViewById(R.id.txtPlaceAddress); // ⭐ 연결 ⭐
            checkBoxSelectPlace = itemView.findViewById(R.id.checkBoxSelectPlace); // ⭐ 연결 ⭐
        }
    }
}