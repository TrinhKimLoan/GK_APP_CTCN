package com.example.chitieucanhan.goal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class GoalActivity extends AppCompatActivity {
    private static final int REQ_ADD = 1001;
    private RecyclerView rv;
    private GoalAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);
        rv = findViewById(R.id.goals_recycler);
        rv.setLayoutManager(new LinearLayoutManager(this));
        // Add spacing between items to avoid overlap
        int spacingDp = 8;
        final float scale = getResources().getDisplayMetrics().density;
        int spacingPx = (int) (spacingDp * scale + 0.5f);
        rv.addItemDecoration(new SpacesItemDecoration(spacingPx));
        FloatingActionButton fab = findViewById(R.id.fab_add_goal);
        fab.setOnClickListener(v -> openAdd());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadGoals();
    }

    private void loadGoals() {
        List<Goal> list = GoalStorage.loadGoals(this);
        adapter = new GoalAdapter(this, list, position -> {
            // open edit flow, for now reuse AddGoalActivity with extras
            Goal g = list.get(position);
            Intent i = new Intent(this, AddGoalActivity.class);
            i.putExtra("edit_index", position);
            i.putExtra("goal_name", g.getName());
            i.putExtra("goal_target", g.getTargetAmount());
            i.putExtra("goal_saved", g.getSavedAmount());
            startActivityForResult(i, REQ_ADD);
        });
        rv.setAdapter(adapter);
    }

    private void openAdd() {
        Intent i = new Intent(this, AddGoalActivity.class);
        startActivityForResult(i, REQ_ADD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_ADD && resultCode == Activity.RESULT_OK) {
            // reload list
            loadGoals();
        }
    }
}