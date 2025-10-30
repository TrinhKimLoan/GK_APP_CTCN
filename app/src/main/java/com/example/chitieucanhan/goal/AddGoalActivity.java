package com.example.chitieucanhan.goal;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class AddGoalActivity extends AppCompatActivity {
    private EditText etName, etTarget, etSaved;
    private Button btnSave;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);
        etName = findViewById(R.id.et_goal_name);
        etTarget = findViewById(R.id.et_goal_target);
        etSaved = findViewById(R.id.et_goal_saved);
        btnSave = findViewById(R.id.btn_save_goal);

        // If editing
        int editIndex = getIntent().getIntExtra("edit_index", -1);
        if (editIndex >= 0) {
            etName.setText(getIntent().getStringExtra("goal_name"));
            etTarget.setText(String.valueOf(getIntent().getLongExtra("goal_target", 0L)));
            etSaved.setText(String.valueOf(getIntent().getLongExtra("goal_saved", 0L)));
        }

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String targetS = etTarget.getText().toString().trim();
            String savedS = etSaved.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                etName.setError("Required");
                return;
            }
            long target = 0L;
            long saved = 0L;
            try { target = Long.parseLong(targetS); } catch (Exception ignored) {}
            try { saved = Long.parseLong(savedS); } catch (Exception ignored) {}

            Goal g = new Goal(name, target, saved);
            int idx = getIntent().getIntExtra("edit_index", -1);
            if (idx >= 0) {
                List<Goal> list = GoalStorage.loadGoals(this);
                if (idx < list.size()) {
                    GoalStorage.updateGoal(this, idx, g);
                }
            } else {
                GoalStorage.addGoal(this, g);
            }
            setResult(Activity.RESULT_OK);
            finish();
        });
    }
}