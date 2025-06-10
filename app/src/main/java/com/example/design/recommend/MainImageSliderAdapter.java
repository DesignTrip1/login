package com.example.design.recommend;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.design.R;
// import com.example.design.roulette.RouletteData; // 이 어댑터에서는 더 이상 TravelDestination을 직접 사용하지 않음

import java.util.List;

// MainActivity에서 단순히 이미지 리스트를 슬라이드하는 데 사용할 어댑터
public class MainImageSliderAdapter extends RecyclerView.Adapter<MainImageSliderAdapter.ViewHolder> {

    private Context context;
    private List<Integer> imageList; // int 리스트를 받음 (원래대로 돌아옴)

    // 클릭 리스너 인터페이스 정의 (클릭된 이미지의 리소스 ID와 위치를 전달)
    private OnImageClickListener listener; // ⭐ 변경 ⭐

    public interface OnImageClickListener { // ⭐ 이름 변경 ⭐
        void onImageClick(int imageResId, int position); // ⭐ 전달 인자 변경 ⭐
    }

    public void setOnImageClickListener(OnImageClickListener listener) { // ⭐ 이름 변경 ⭐
        this.listener = listener;
    }

    public MainImageSliderAdapter(Context context, List<Integer> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_slide, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int imageResId = imageList.get(position);
        holder.imageView.setImageResource(imageResId);

        holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        holder.imageView.setBackgroundColor(Color.WHITE);

        // 클릭 리스너 설정 (클릭된 이미지의 ID와 위치를 MainActivity로 전달)
        holder.imageView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onImageClick(imageResId, position); // ⭐ 전달 ⭐
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);
        }
    }
}