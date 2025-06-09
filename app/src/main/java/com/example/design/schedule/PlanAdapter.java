package com.example.design.schedule;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.design.R;

import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.PlanViewHolder> {

    private List<PlanItem> planList;
    private OnItemClickListener onItemClickListener;
    private OnDeleteConfirmedListener onDeleteConfirmedListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnDeleteConfirmedListener {
        void onDeleteConfirmed(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnDeleteConfirmedListener(OnDeleteConfirmedListener listener) {
        this.onDeleteConfirmedListener = listener;
    }

    public PlanAdapter(List<PlanItem> planList) {
        this.planList = planList;
    }

    // 어댑터의 데이터를 업데이트하는 메서드
    public void setPlanList(List<PlanItem> newPlanList) {
        this.planList = newPlanList;
        notifyDataSetChanged(); // 데이터가 변경되었음을 RecyclerView에 알림
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plan, parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        PlanItem plan = planList.get(position);
        holder.txtTitle.setText(plan.getTitle());
        holder.txtPeriod.setText(plan.getPeriod()); // getPeriod()는 startDate와 endDate 조합

        // ⭐ groupName 대신 groupId를 사용하며, 사용자에게 보여주기 편하도록 고정 텍스트로 변경
        holder.txtGroup.setText("공유 그룹 일정");
        // 만약 실제 groupId를 보여주고 싶다면: holder.txtGroup.setText("그룹 ID: " + plan.getGroupId());
    }

    @Override
    public int getItemCount() {
        return planList.size();
    }

    class PlanViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtPeriod, txtGroup;
        ImageButton btnDelete;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtPlanTitle);
            txtPeriod = itemView.findViewById(R.id.txtPlanPeriod);
            txtGroup = itemView.findViewById(R.id.txtPlanGroup); // item_plan.xml에 txtPlanGroup ID가 있는지 확인

            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnDelete.setColorFilter(null); // tint 제거 (resource set for bt_del, make sure it's correct)

            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(getAdapterPosition());
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (onDeleteConfirmedListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    int position = getAdapterPosition();
                    new AlertDialog.Builder(itemView.getContext())
                            .setTitle("일정 삭제")
                            .setMessage("이 일정을 삭제하시겠습니까?")
                            .setPositiveButton("삭제", (dialog, which) -> {
                                onDeleteConfirmedListener.onDeleteConfirmed(position);
                            })
                            .setNegativeButton("취소", null)
                            .show();
                }
            });
        }
    }
}