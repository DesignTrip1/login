package com.example.design.recommend;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.design.R;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private Context context;
    private List<Integer> imageList;

    public SliderAdapter(Context context, List<Integer> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_slide, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        int imageResId = imageList.get(position);
        holder.imageView.setImageResource(imageResId);

        // ✅ 이미지 비율 유지 + 배경 검정 + 짤림 방지
        holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER); // 비율 유지하면서 화면에 맞춤
        holder.imageView.setBackgroundColor(Color.WHITE); // 여백은 검정색

        // 🔽 클릭 시 전체화면 슬라이더로 이동 (선택사항)
        holder.imageView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FullscreenSliderActivity.class);
            intent.putExtra("imageType", position);  // 선택된 이미지 위치
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    static class SliderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);
        }
    }
}
