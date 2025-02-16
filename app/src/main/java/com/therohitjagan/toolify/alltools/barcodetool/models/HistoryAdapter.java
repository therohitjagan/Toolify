package com.therohitjagan.toolify.alltools.barcodetool.models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.therohitjagan.toolify.R;


import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private final ArrayList<HistoryItem> historyList;
    private final OnHistoryItemActionListener listener;

    // Interface for callbacks when a delete or share action occurs.
    public interface OnHistoryItemActionListener {
        void onDelete(HistoryItem item, int position);
        void onShare(HistoryItem item);
    }

    // Constructor receives both the data list and the callback listener.
    public HistoryAdapter(ArrayList<HistoryItem> historyList, OnHistoryItemActionListener listener) {
        this.historyList = historyList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryItem item = historyList.get(position);
        holder.tvData.setText(item.getData());
        holder.tvFormat.setText(item.getFormat());
        holder.tvTime.setText(item.getTimestamp());

        // Set click listeners for the delete and share buttons.
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDelete(item, holder.getAdapterPosition());
            }
        });

        holder.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onShare(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    // Helper method to remove an item from the list.
    public void removeItem(int position) {
        historyList.remove(position);
        notifyItemRemoved(position);
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvData, tvFormat, tvTime;
        ImageButton btnDelete, btnShare;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            tvData = itemView.findViewById(R.id.tv_data);
            tvFormat = itemView.findViewById(R.id.tv_format);
            tvTime = itemView.findViewById(R.id.tv_time);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnShare = itemView.findViewById(R.id.btn_share_history);
        }
    }
}
