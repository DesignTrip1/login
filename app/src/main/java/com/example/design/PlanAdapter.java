package com.example.design;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.PlanViewHolder> {

    private List<PlanItem> planList;
    private OnItemClickListener onItemClickListener; // ✅ 클릭 리스너 인터페이스 선언

    // 인터페이스 정의
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // 클릭 리스너 설정 메서드 추가
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public PlanAdapter(List<PlanItem> planList) {
        this.planList = planList;
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plan, parent, false);
        return new PlanViewHolder(view, onItemClickListener); // 클릭 리스너 넘김
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        PlanItem plan = planList.get(position);
        holder.txtTitle.setText(plan.getTitle());
        holder.txtPeriod.setText(plan.getPeriod());

        // 삭제 버튼 처리 등은 여기서 해도 OK
    }

    @Override
    public int getItemCount() {
        return planList.size();
    }

    static class PlanViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtPeriod;
        ImageButton btnDelete;

        public PlanViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtPlanTitle);
            txtPeriod = itemView.findViewById(R.id.txtPlanPeriod);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getAdapterPosition());
                }
            });
        }
    }
}
