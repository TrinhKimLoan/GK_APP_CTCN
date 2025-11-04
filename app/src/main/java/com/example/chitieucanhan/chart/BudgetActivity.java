package com.example.chitieucanhan.chart;
import com.example.chitieucanhan.R;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.view.View;
import android.text.Editable;
import android.text.TextWatcher;
import java.text.NumberFormat;
import java.util.Locale;

import com.example.chitieucanhan.transaction.Transaction;
import com.example.chitieucanhan.transaction.TransactionStorage;
import java.util.Calendar;
import java.util.List;

public class BudgetActivity extends AppCompatActivity {

    private EditText edtBudget;
    private Button btnSave, btnCheck;
    private float totalSpentThisMonth;
    private ProgressBar progressBarBudget;
    private TextView txtProgressStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        totalSpentThisMonth = getTotalSpentThisMonth(); // Lấy dữ liệu thật từ file JSON
        edtBudget = findViewById(R.id.edtBudget);
        btnSave = findViewById(R.id.btnSave);
        btnCheck = findViewById(R.id.btnCheck);
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
    private float getTotalSpentThisMonth() {
        TransactionStorage storage = new TransactionStorage(this);
        List<Transaction> allTransactions = storage.getAllTransactions();
        float total = 0f;

        Calendar now = Calendar.getInstance();
        int currentMonth = now.get(Calendar.MONTH) + 1; // Tháng bắt đầu từ 0
        int currentYear = now.get(Calendar.YEAR);

        for (Transaction t : allTransactions) {
            try {
                if (!t.getType().toLowerCase().contains("chi")) continue; // Chỉ tính mục CHI

                // Định dạng ngày là "yyyy-MM-dd"
                String[] parts = t.getDate().split("-");
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);

                if (year == currentYear && month == currentMonth) {
                    total += t.getAmount();
                }
            } catch (Exception e) {
                e.printStackTrace(); // phòng lỗi định dạng ngày
            }
        }

        return total;
    }
}