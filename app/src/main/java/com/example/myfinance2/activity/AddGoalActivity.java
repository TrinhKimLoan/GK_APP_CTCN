package com.example.myfinance2.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myfinance2.R;
import com.example.myfinance2.model.Goal;
import com.example.myfinance2.storage.GoalStorage;

public class AddGoalActivity extends AppCompatActivity {

    private EditText nameEt, targetEt, savedEt;
    private Button saveBtn;
    private DatePicker datePicker;
    private int editIndex = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);

        nameEt = findViewById(R.id.et_goal_name);
        targetEt = findViewById(R.id.et_goal_target);
        savedEt = findViewById(R.id.et_goal_saved);
        saveBtn = findViewById(R.id.btn_save_goal);
        datePicker = findViewById(R.id.datePicker);

        if (getIntent() != null && getIntent().hasExtra("editIndex")) {
            editIndex = getIntent().getIntExtra("editIndex", -1);
            String name = getIntent().getStringExtra("name");
            double target = getIntent().getDoubleExtra("target", 0);
            double saved = getIntent().getDoubleExtra("saved", 0);
            nameEt.setText(name);
            targetEt.setText(String.valueOf(target));
            savedEt.setText(String.valueOf(saved));
            
            long dueDate = getIntent().getLongExtra("dueDate", -1);
            if (dueDate != -1) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(dueDate);
                datePicker.updateDate(cal.get(Calendar.YEAR), 
                                    cal.get(Calendar.MONTH),
                                    cal.get(Calendar.DAY_OF_MONTH));
            }
        }

        saveBtn.setOnClickListener(v -> {
            String name = nameEt.getText().toString().trim();
            String tS = targetEt.getText().toString().trim();
            String sS = savedEt.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                nameEt.setError("Tên không được để trống");
                return;
            }
            double target;
            double saved;
            try {
                target = Double.parseDouble(tS);
            } catch (Exception e) {
                targetEt.setError("Số tiền mục tiêu không hợp lệ");
                return;
            }
            try {
                saved = Double.parseDouble(sS);
            } catch (Exception e) {
                savedEt.setError("Số tiền đã tiết kiệm không hợp lệ");
                return;
            }

            if (target <= 0) {
                targetEt.setError("Mục tiêu phải lớn hơn 0");
                return;
            }
            if (saved < 0 || saved > target) {
                savedEt.setError("Số tiền đã tiết kiệm phải >=0 và <= mục tiêu");
                return;
            }

            Calendar selectedCal = Calendar.getInstance();
            selectedCal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
            selectedCal.set(Calendar.HOUR_OF_DAY, 23);
            selectedCal.set(Calendar.MINUTE, 59);
            selectedCal.set(Calendar.SECOND, 59);
            
            Goal g = new Goal(name, target, saved, selectedCal.getTimeInMillis());
            
            if (editIndex >= 0) {
                GoalStorage.updateGoal(this, g, editIndex);
                Toast.makeText(this, "Cập nhật mục tiêu", Toast.LENGTH_SHORT).show();
            } else {
                GoalStorage.addGoal(this, g);
                Toast.makeText(this, "Thêm mục tiêu", Toast.LENGTH_SHORT).show();
            }
            finish();
        });
    }
}
