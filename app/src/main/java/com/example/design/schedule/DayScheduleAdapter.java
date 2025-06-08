package com.example.design.schedule;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.design.R;

import java.util.List;

public class DayScheduleAdapter extends RecyclerView.Adapter<DayScheduleAdapter.ViewHolder> {

    private List<ScheduleItem> scheduleList;
    private Context context;

    public DayScheduleAdapter(List<ScheduleItem> scheduleList) {
        this.scheduleList = scheduleList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScheduleItem item = scheduleList.get(position);

        holder.txtTime.setText(item.getTimeRangeText());
        holder.txtPlace.setText(item.getPlace());
        holder.txtMemo.setText(item.getMemo());

        // 삭제 버튼 클릭
        holder.btnDelete.setOnClickListener(v -> {
            scheduleList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, scheduleList.size());
        });

        // 수정 버튼 클릭
        holder.btnEdit.setOnClickListener(v -> showEditDialog(position));
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTime, txtPlace, txtMemo;
        ImageButton btnDelete, btnEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtPlace = itemView.findViewById(R.id.txtPlace);
            txtMemo = itemView.findViewById(R.id.txtMemo);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }

    private void showEditDialog(int position) {
        ScheduleItem item = scheduleList.get(position);

        // 다이얼로그용 레이아웃 구성
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_schedule, null);

        EditText editStartTime = dialogView.findViewById(R.id.editStartTime);
        EditText editEndTime = dialogView.findViewById(R.id.editEndTime);
        EditText editPlace = dialogView.findViewById(R.id.editPlace);
        EditText editMemo = dialogView.findViewById(R.id.editMemo);

        // 기존 값 세팅
        editStartTime.setText(item.getStartTime());
        editEndTime.setText(item.getEndTime());
        editPlace.setText(item.getPlace());
        editMemo.setText(item.getMemo());

        new AlertDialog.Builder(context)
                .setTitle("일정 수정")
                .setView(dialogView)
                .setPositiveButton("저장", (dialog, which) -> {
                    String newStartTime = editStartTime.getText().toString().trim();
                    String newEndTime = editEndTime.getText().toString().trim();
                    String newPlace = editPlace.getText().toString().trim();
                    String newMemo = editMemo.getText().toString().trim();

                    // 새 ScheduleItem 만들어서 리스트 업데이트
                    ScheduleItem newItem = new ScheduleItem(newStartTime, newEndTime, newPlace, newMemo);
                    scheduleList.set(position, newItem);
                    notifyItemChanged(position);
                })
                .setNegativeButton("취소", null)
                .show();
    }
}
