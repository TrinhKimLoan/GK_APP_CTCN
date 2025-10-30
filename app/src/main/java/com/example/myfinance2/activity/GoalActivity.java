package com.example.myfinance2.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfinance2.R;
import com.example.myfinance2.adapter.GoalAdapter;
import com.example.myfinance2.model.Goal;
import com.example.myfinance2.storage.GoalStorage;

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
