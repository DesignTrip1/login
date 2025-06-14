package com.example.design.schedule;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast; // Toast 추가

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.design.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class DayScheduleAdapter extends RecyclerView.Adapter<DayScheduleAdapter.ViewHolder> {

    private List<ScheduleItem> scheduleList;
    private Context context;
    private OnItemDeleteListener onItemDeleteListener;
    private OnItemEditListener onItemEditListener;

    // 삭제 콜백 인터페이스
    public interface OnItemDeleteListener {
        void onDelete(int position);
    }

    // 수정 콜백 인터페이스
    public interface OnItemEditListener {
        void onEdit(int position, String newStartTime, String newEndTime, String newPlace, String newMemo);
    }

    public void setOnItemDeleteListener(OnItemDeleteListener listener) {
        this.onItemDeleteListener = listener;
    }

    public void setOnItemEditListener(OnItemEditListener listener) {
        this.onItemEditListener = listener;
    }

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
            if (onItemDeleteListener != null) {
                new AlertDialog.Builder(context)
                        .setTitle("일정 삭제")
                        .setMessage("이 일정을 삭제하시겠습니까?")
                        .setPositiveButton("삭제", (dialog, which) -> {
                            onItemDeleteListener.onDelete(holder.getAdapterPosition());
                        })
                        .setNegativeButton("취소", null)
                        .show();
            }
        });

        // 수정 버튼 클릭
        holder.btnEdit.setOnClickListener(v -> {
            showEditScheduleDialog(holder.getAdapterPosition(), item);
        });
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTime;
        TextView txtPlace;
        TextView txtMemo;
        ImageButton btnDelete;
        ImageButton btnEdit; // 수정 버튼 추가

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtPlace = itemView.findViewById(R.id.txtPlace);
            txtMemo = itemView.findViewById(R.id.txtMemo);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit); // 수정 버튼 초기화
        }
    }

    // 수정 다이얼로그 표시 메서드
    private void showEditScheduleDialog(int position, ScheduleItem item) {
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

        // 시간 선택 다이얼로그 연결
        editStartTime.setOnClickListener(v -> showTimePicker(context, editStartTime));
        editEndTime.setOnClickListener(v -> showTimePicker(context, editEndTime));


        new AlertDialog.Builder(context)
                .setTitle("일정 수정")
                .setView(dialogView)
                .setPositiveButton("저장", (dialog, which) -> {
                    String newStartTime = editStartTime.getText().toString().trim();
                    String newEndTime = editEndTime.getText().toString().trim();
                    String newPlace = editPlace.getText().toString().trim();
                    String newMemo = editMemo.getText().toString().trim();

                    // 시간 유효성 검사 (DayFragment와 동일하게)
                    if (compareTimes(newStartTime, newEndTime) > 0) {
                        Toast.makeText(context, "시작 시간은 종료 시간보다 빨라야 합니다.", Toast.LENGTH_SHORT).show();
                        return; // 저장하지 않고 종료
                    }

                    if (onItemEditListener != null) {
                        onItemEditListener.onEdit(position, newStartTime, newEndTime, newPlace, newMemo);
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }

    // 시간 선택기 메서드 (DayFragment에서 복사하여 사용)
    private void showTimePicker(Context context, TextView targetView) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, (view, hourOfDay, minute) -> {
            String formattedTime = String.format(Locale.KOREA, "%02d:%02d", hourOfDay, minute);
            targetView.setText(formattedTime);
        }, 12, 0, false); // 24시간 형식 여부 (false = AM/PM)
        timePickerDialog.show();
    }

    // 시간 비교 메서드 (DayFragment에서 복사하여 사용)
    private int compareTimes(String time1, String time2) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.KOREA);
            java.util.Date date1 = sdf.parse(time1);
            java.util.Date date2 = sdf.parse(time2);
            return date1.compareTo(date2);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0; // 에러 시 동일한 것으로 간주
        }
    }
}