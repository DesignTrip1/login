package com.example.design.recommend;

import android.content.Context;
import android.graphics.Color; // 필요한 경우
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.design.R;

import java.util.List;

/**
 * FullscreenSliderActivity에서 단순히 이미지 리소스 ID 목록을 슬라이드하는 데 사용될 어댑터.
 */
public class SimpleImageSliderAdapter extends RecyclerView.Adapter<SimpleImageSliderAdapter.ViewHolder> {

    private Context context;
    private List<Integer> imageResIdList; // 이미지 리소스 ID 목록

    public SimpleImageSliderAdapter(Context context, List<Integer> imageResIdList) {
        this.context = context;
        this.imageResIdList = imageResIdList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // item_slide.xml (또는 전체 화면 슬라이드에 맞는 다른 레이아웃) 사용
        View view = LayoutInflater.from(context).inflate(R.layout.item_slide, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int imageResId = imageResIdList.get(position);
        holder.imageView.setImageResource(imageResId);

        // 기존 FullscreenSliderActivity의 이미지 설정 유지
        holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        holder.imageView.setBackgroundColor(Color.WHITE); // 또는 다른 배경색
    }

    @Override
    public int getItemCount() {
        return imageResIdList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // item_slide.xml 레이아웃의 ImageView ID
            imageView = itemView.findViewById(R.id.imageSlide);
        }
    }
}