package com.example.design.recommend;

import android.content.Context;
import android.graphics.Color; // Color 임포트 유지
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.design.R;
import com.example.design.roulette.RouletteData; // RouletteData 임포트

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private Context context;
    // 기존 List<Integer> imageList 대신, List<RouletteData.TravelDestination> 사용
    private List<RouletteData.TravelDestination> destinationList;
    private OnItemClickListener listener; // 클릭 리스너 인터페이스 추가

    /**
     * SliderAdapter의 생성자.
     *
     * @param context       현재 컨텍스트
     * @param destinationList 표시할 TravelDestination 객체 목록 (여행지 이름과 이미지 ID 포함)
     */
    public SliderAdapter(Context context, List<RouletteData.TravelDestination> destinationList) {
        this.context = context;
        this.destinationList = destinationList;
    }

    /**
     * 슬라이드 아이템 클릭 이벤트를 처리하기 위한 인터페이스.
     */
    public interface OnItemClickListener {
        /**
         * 아이템이 클릭되었을 때 호출됩니다.
         * @param destinationName 클릭된 여행지의 이름
         */
        void onItemClick(String destinationName);
    }

    /**
     * OnItemClickListener를 설정합니다.
     * @param listener 클릭 이벤트를 수신할 리스너
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // item_slide.xml 레이아웃을 사용하여 뷰홀더를 생성
        View view = LayoutInflater.from(context).inflate(R.layout.item_slide, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        // 현재 위치의 TravelDestination 객체를 가져옵니다.
        RouletteData.TravelDestination currentDestination = destinationList.get(position);

        // 이미지 뷰에 해당 여행지의 이미지 리소스 설정
        holder.imageView.setImageResource(currentDestination.imageResId);

        // ✅ 이미지 비율 유지 + 배경 검정 + 짤림 방지
        holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER); // 비율 유지하면서 화면에 맞춤
        holder.imageView.setBackgroundColor(Color.WHITE); // 여백은 흰색 (원래 코드에 맞춰 변경)

        // 🔽 클릭 시 PlaceListActivity로 이동 (룰렛 기능과 통합)
        holder.imageView.setOnClickListener(v -> {
            // 리스너가 설정되어 있다면 onItemClick 메서드를 호출하여 여행지 이름 전달
            if (listener != null) {
                listener.onItemClick(currentDestination.name);
            }
        });
    }

    @Override
    public int getItemCount() {
        // 표시할 아이템(여행지)의 총 개수 반환
        return destinationList.size();
    }

    /**
     * RecyclerView의 각 아이템 뷰를 관리하는 뷰홀더 클래스.
     */
    static class SliderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView; // 슬라이드 이미지 뷰 (item_slide.xml의 imageSlide ID에 연결)

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            // item_slide.xml에 정의된 ImageView 연결 (ID가 imageSlide인 것으로 가정)
            imageView = itemView.findViewById(R.id.imageSlide);
        }
    }
}