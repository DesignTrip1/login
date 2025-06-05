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
        holder.txtPeriod.setText(plan.getPeriod());
    }

    @Override
    public int getItemCount() {
        return planList.size();
    }

    class PlanViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtPeriod;
        ImageButton btnDelete;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtPlanTitle);
            txtPeriod = itemView.findViewById(R.id.txtPlanPeriod);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnDelete.setColorFilter(null); // tint 제거

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
