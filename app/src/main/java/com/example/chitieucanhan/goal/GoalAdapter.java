package com.example.chitieucanhan.goal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.ViewHolder> {
    private final List<Goal> items;
    private final Context ctx;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public GoalAdapter(Context ctx, List<Goal> items, OnItemClickListener listener) {
        this.ctx = ctx;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goal_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Goal g = items.get(position);
        holder.name.setText(g.getName());
        holder.target.setText(String.format("Target: %d", g.getTargetAmount()));
        holder.saved.setText(String.format("Saved: %d", g.getSavedAmount()));
        int progress = (int) Math.round(g.getProgressPercent());
        holder.progress.setProgress(progress);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, target, saved;
        ProgressBar progress;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.goal_name);
            target = itemView.findViewById(R.id.goal_target);
            saved = itemView.findViewById(R.id.goal_saved);
            progress = itemView.findViewById(R.id.goal_progress);
        }
    }
}