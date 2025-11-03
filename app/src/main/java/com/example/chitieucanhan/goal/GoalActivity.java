package com.example.chitieucanhan.goal;
import com.example.chitieucanhan.R;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.RadioGroup;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;

import java.util.List;

public class GoalActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GoalAdapter adapter;
    private SearchView searchView;
    private Button addBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);

        setTitle("Mục tiêu tiết kiệm");

        recyclerView = findViewById(R.id.recycler_goals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new GoalAdapter();
        recyclerView.setAdapter(adapter);

        searchView = findViewById(R.id.search_goals);
        addBtn = findViewById(R.id.btn_add_goal);

        // Xử lí đoạn 3 Radio Button Lọc
        RadioGroup filterGroup = findViewById(R.id.filter_group);
        filterGroup.setOnCheckedChangeListener((group, checkedId) -> {
            List<Goal> allGoals = GoalStorage.getAllGoals(this);// load lại danh sách mới nhất
            List<Goal> filtered = new ArrayList<>();

            long now = System.currentTimeMillis();

            if (checkedId == R.id.filter_all) {
                filtered = allGoals; // tất cả
            } else if (checkedId == R.id.filter_upcoming) {
                for (Goal g : allGoals) {
                    long diffDays = TimeUnit.MILLISECONDS.toDays(g.getDueDate() - now);
                    if (diffDays >= 0 && diffDays <= 7) { // trong 7 ngày tới
                        filtered.add(g);
                    }
                }
            } else if (checkedId == R.id.filter_overdue) {
                for (Goal g : allGoals) {
                    if (g.getDueDate() < now) {
                        filtered.add(g);
                    }
                }
            }

            adapter.setData(filtered);
        });
        // Kết thúc lọc 3 Button

        addBtn.setOnClickListener(v -> {
            Intent i = new Intent(this, AddGoalActivity.class);
            startActivity(i);
        });

        adapter.setOnItemActionListener(new GoalAdapter.OnItemActionListener() {
            @Override
            public void onEdit(int index, Goal goal) {
                Intent i = new Intent(GoalActivity.this, AddGoalActivity.class);
                i.putExtra("editIndex", index);
                i.putExtra("name", goal.getName());
                i.putExtra("target", goal.getTargetAmount());
                i.putExtra("saved", goal.getSavedAmount());
                i.putExtra("dueDate", goal.getDueDate());
                startActivity(i);
            }

            @Override
            public void onTransfer(int index, Goal goal) {
                // Show a simple dialog to ask amount to transfer
                TransferDialog.show(GoalActivity.this, amount -> {
                    try {
                        GoalStorage.transferToGoal(GoalActivity.this, amount, goal);
                        refreshList();
                    } catch (IllegalArgumentException ex) {
                        new AlertDialog.Builder(GoalActivity.this)
                                .setTitle("Lỗi")
                                .setMessage(ex.getMessage())
                                .setPositiveButton("OK", null)
                                .show();
                    }
                });
            }

            // Thêm hàm xử lí xóa
            @Override
            public void onDelete(int index, Goal goal) { // ✅ thêm xử lý xóa
                new AlertDialog.Builder(GoalActivity.this)
                        .setTitle("Xóa mục tiêu")
                        .setMessage("Bạn có chắc muốn xóa \"" + goal.getName() + "\" không?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            GoalStorage.deleteGoal(GoalActivity.this, index);
                            refreshList();
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        List<Goal> goals = GoalStorage.getAllGoals(this);
        adapter.setData(goals);
    }
}