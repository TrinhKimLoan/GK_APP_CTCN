package com.example.chartctcn;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.view.View;
import android.text.Editable;
import android.text.TextWatcher;
import java.text.NumberFormat;
import java.util.Locale;

public class BudgetActivity extends AppCompatActivity {

    private EditText edtBudget;
    private Button btnSave, btnCheck, btnViewChart;
    private float totalSpentThisMonth = 4800000f; // 👈 Giả lập tổng chi tháng này
    private ProgressBar progressBarBudget;
    private TextView txtProgressStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        edtBudget = findViewById(R.id.edtBudget);
        btnSave = findViewById(R.id.btnSave);
        btnCheck = findViewById(R.id.btnCheck);
        btnViewChart = findViewById(R.id.btnViewChart);
        progressBarBudget = findViewById(R.id.progressBarBudget);
        txtProgressStatus = findViewById(R.id.txtProgressStatus);

        // Hiện hạn mức hiện tại
        float currentBudget = BudgetSetting.getTotalBudget(this);
        if (currentBudget > 0) {
            edtBudget.setText(formatMoney(currentBudget));
            updateProgressBar(currentBudget, totalSpentThisMonth);
        }

        // 🪄 Tự động format tiền khi nhập
        edtBudget.addTextChangedListener(new TextWatcher() {
            private String current = "";
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    edtBudget.removeTextChangedListener(this);
                    String clean = s.toString().replaceAll("[,.đ\\s]", "");
                    if (clean.isEmpty()) clean = "0";
                    double parsed = Double.parseDouble(clean);
                    String formatted = NumberFormat.getNumberInstance(Locale.US).format(parsed);
                    current = formatted;
                    edtBudget.setText(formatted);
                    edtBudget.setSelection(formatted.length());
                    edtBudget.addTextChangedListener(this);
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Lưu hạn mức
        btnSave.setOnClickListener(v -> {
            String budgetStr = edtBudget.getText().toString().replaceAll("[,.đ\\s]", "");
            if (budgetStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập hạn mức!", Toast.LENGTH_SHORT).show();
                return;
            }
            float budget = Float.parseFloat(budgetStr);
            BudgetSetting.setTotalBudget(this, budget);
            Toast.makeText(this, "Đã lưu hạn mức: " + formatMoney(budget), Toast.LENGTH_SHORT).show();
        });

        // Kiểm tra chi tiêu tự động (dùng dữ liệu giả)
        btnCheck.setOnClickListener(v -> {
            updateProgressBar(BudgetSetting.getTotalBudget(this), totalSpentThisMonth);
        });

        // Xem biểu đồ
        btnViewChart.setOnClickListener(v -> {
            Toast.makeText(this, "Đang mở biểu đồ...", Toast.LENGTH_SHORT).show();
            startActivity(new android.content.Intent(this, StatisticsActivity.class));
        });
    }

    private String formatMoney(float amount) {
        return String.format("%,.0f đ", amount);
    }

    private void updateProgressBar(float limit, float spent) {
        if (limit <= 0) {
            progressBarBudget.setVisibility(View.GONE);
            txtProgressStatus.setVisibility(View.GONE);
            return;
        }

        int percent = Math.round((spent / limit) * 100);
        if (percent > 100) percent = 100;

        // Hiển thị thanh tiến độ
        progressBarBudget.setVisibility(View.VISIBLE);
        txtProgressStatus.setVisibility(View.VISIBLE);
        progressBarBudget.setProgress(percent);

        // Đổi màu theo phần trăm
        int colorRes;
        if (percent < 70) {
            colorRes = android.R.color.holo_green_dark; // an toàn
        } else if (percent < 100) {
            colorRes = android.R.color.holo_orange_dark; // cảnh báo
        } else {
            colorRes = android.R.color.holo_red_dark; // vượt hạn mức
        }

        progressBarBudget.setProgressTintList(android.content.res.ColorStateList.valueOf(
                getResources().getColor(colorRes)));

        // Cập nhật text trạng thái
        String statusText;
        int percentUsed = Math.round((spent / limit) * 100);

        if (spent < limit) {
            float remaining = limit - spent;
            statusText = String.format("Đã dùng %d%% hạn mức (còn %s)",
                    percentUsed, formatMoney(remaining));
        } else {
            float over = spent - limit;
            statusText = String.format("⚠️ Đã vượt %s (%d%%)",
                    formatMoney(over), percentUsed);
        }

        txtProgressStatus.setText(statusText);
    }

}