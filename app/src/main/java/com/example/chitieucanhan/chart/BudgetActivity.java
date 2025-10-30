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

public class BudgetActivity extends AppCompatActivity {

    private EditText edtBudget;
    private Button btnSave, btnCheck, btnViewChart;
    private float totalSpentThisMonth = 4800000f; // 👈 Giả lập tổng chi tháng này

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        edtBudget = findViewById(R.id.edtBudget);
        btnSave = findViewById(R.id.btnSave);
        btnCheck = findViewById(R.id.btnCheck);
        btnViewChart = findViewById(R.id.btnViewChart);

        // Hiện hạn mức hiện tại
        float currentBudget = BudgetSetting.getTotalBudget(this);
        if (currentBudget > 0)
            edtBudget.setText(formatMoney(currentBudget));

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
            boolean over = BudgetSetting.checkBudgetStatus(this, totalSpentThisMonth);
            if (over) {
                Toast.makeText(this, "⚠️ Vượt hạn mức chi tiêu! (Chi: " + formatMoney(totalSpentThisMonth) + ")", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "✅ Còn trong hạn mức. (Chi: " + formatMoney(totalSpentThisMonth) + ")", Toast.LENGTH_SHORT).show();
            }
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
}