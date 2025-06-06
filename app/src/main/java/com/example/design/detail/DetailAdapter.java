package com.example.design.detail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.design.R;

import java.util.List;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder> {

    private final List<DetailItem> detailList;

    public DetailAdapter(List<DetailItem> detailList) {
        this.detailList = detailList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtInfo = itemView.findViewById(R.id.txtDetail);
        }
    }

    @NonNull
    @Override
    public DetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailAdapter.ViewHolder holder, int position) {
        DetailItem item = detailList.get(position);
        holder.txtInfo.setText(item.day + " | " + item.time + " | " + item.place + (item.memo.isEmpty() ? "" : " (" + item.memo + ")"));
    }

    @Override
    public int getItemCount() {
        return detailList.size();
    }
}
