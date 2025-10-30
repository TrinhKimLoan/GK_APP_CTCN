package com.example.myfinance2.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfinance2.R;
import com.example.myfinance2.model.Goal;

import java.util.ArrayList;
import java.util.List;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.ViewHolder> {

    private List<Goal> data = new ArrayList<>();
    private List<Goal> original = new ArrayList<>();
    private OnItemActionListener listener;

    public interface OnItemActionListener {
        void onEdit(int index, Goal goal);

        void onTransfer(int index, Goal goal);
    }

    public void setOnItemActionListener(OnItemActionListener l) {
        this.listener = l;
    }

    public void setData(List<Goal> list) {
        this.data = new ArrayList<>(list);
        this.original = new ArrayList<>(list);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        if (query == null || query.trim().isEmpty()) {
            data = new ArrayList<>(original);
        } else {
            String q = query.toLowerCase();
            List<Goal> filtered = new ArrayList<>();
            for (Goal g : original) {
                if (g.getName() != null && g.getName().toLowerCase().contains(q)) {
                    filtered.add(g);
                }
            }
            data = filtered;
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goal, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Goal g = data.get(position);
        holder.name.setText(g.getName());
        holder.amounts.setText(String.format("%.2f / %.2f", g.getSavedAmount(), g.getTargetAmount()));
        int progress = g.getProgressPercent();
        holder.progressBar.setProgress(progress);
        holder.progressText.setText(progress + "%");

        holder.editBtn.setOnClickListener(v -> {
            if (listener != null) {
                // Need to find index in original list for editing
                int origIndex = findOriginalIndex(g);
                listener.onEdit(origIndex, g);
            }
        });
    }

    private int findOriginalIndex(Goal g) {
        for (int i = 0; i < original.size(); i++) {
            Goal og = original.get(i);
            if (og.getName() != null && og.getName().equals(g.getName())) return i;
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView amounts;
        ProgressBar progressBar;
        TextView progressText;
        ImageButton editBtn;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.goal_name);
            amounts = itemView.findViewById(R.id.goal_amounts);
            progressBar = itemView.findViewById(R.id.goal_progress_bar);
            progressText = itemView.findViewById(R.id.goal_progress_text);
            editBtn = itemView.findViewById(R.id.btn_edit_goal);
        }
    }
}
