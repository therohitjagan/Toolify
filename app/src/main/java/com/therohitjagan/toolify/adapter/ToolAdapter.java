package com.therohitjagan.toolify.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.therohitjagan.toolify.R;
import com.therohitjagan.toolify.Tool;

import java.util.List;

public class ToolAdapter extends RecyclerView.Adapter<ToolAdapter.ToolViewHolder> {
    private final Context context;
    private List<Tool> tools;
    private final OnToolClickListener listener;

    public interface OnToolClickListener {
        void onToolClick(Tool tool);
    }

    public ToolAdapter(Context context, List<Tool> tools, OnToolClickListener listener) {
        this.context = context;
        this.tools = tools;
        this.listener = listener;
    }

    @Override
    public ToolViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tool, parent, false);
        return new ToolViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ToolViewHolder holder, int position) {
        Tool tool = tools.get(position);
        holder.toolImage.setImageResource(tool.getImageResource());
        holder.toolName.setText(tool.getName());
        holder.categoryChip.setText(tool.getCategory());

        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onToolClick(tool);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tools.size();
    }

    public void updateList(List<Tool> newList) {
        tools = newList;
        notifyDataSetChanged();
    }

    static class ToolViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        ImageView toolImage;
        TextView toolName;
        Chip categoryChip;

        ToolViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            toolImage = itemView.findViewById(R.id.toolImage);
            toolName = itemView.findViewById(R.id.toolName);
            categoryChip = itemView.findViewById(R.id.categoryChip);
        }
    }
}