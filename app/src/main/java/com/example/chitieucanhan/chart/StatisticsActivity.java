package com.example.chitieucanhan.chart;
import com.example.chitieucanhan.R;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chitieucanhan.transaction.Transaction;
import com.example.chitieucanhan.transaction.TransactionStorage;
import com.github.mikephil.charting.charts.*;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView;

public class StatisticsActivity extends AppCompatActivity {
    private PieChart pieChart;
    private Spinner spinnerPieType, spinnerPieMonth, spinnerPieYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        pieChart = findViewById(R.id.pieChart);

        // === Bắt đầu xử lí spinner
        // Spinner refs
        spinnerPieType = findViewById(R.id.spinnerPieType);
        spinnerPieMonth = findViewById(R.id.spinnerPieMonth);
        spinnerPieYear = findViewById(R.id.spinnerPieYear);

        // --- Dữ liệu mẫu cho spinner ---
        String[] pieTypes = {"Chi tiêu", "Thu Nhập"}; // danh mục chi/thu
        String[] months = {"1","2","3","4","5","6","7","8","9","10","11","12"};
        String[] years = {"2024","2025"}; // expand khi cần

        // adapter đơn giản
        spinnerPieType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, pieTypes));
        spinnerPieMonth.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, months));
        spinnerPieYear.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, years));

        // Listener: khi thay đổi => refresh chart tương ứng
        AdapterView.OnItemSelectedListener pieListener = new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String type = spinnerPieType.getSelectedItem().toString();
                int month = Integer.parseInt(spinnerPieMonth.getSelectedItem().toString());
                int year = Integer.parseInt(spinnerPieYear.getSelectedItem().toString());
                refreshPieChart(type, month, year);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        };

        spinnerPieType.setOnItemSelectedListener(pieListener);
        spinnerPieMonth.setOnItemSelectedListener(pieListener);
        spinnerPieYear.setOnItemSelectedListener(pieListener);

        // Khởi tạo lần đầu (mặc định chọn index 0)
        spinnerPieType.setSelection(0);
        spinnerPieMonth.setSelection(0);
        spinnerPieYear.setSelection(1); // ví dụ 2025
        // === Kết thúc xử lí spinner
    }

    private void setupPieChartBase() {
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterTextSize(14f);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.animateY(500);
    }

    // refresh Pie theo type/month/year
    private void refreshPieChart(String type, int month, int year) {
        TransactionStorage storage = new TransactionStorage(this);
        List<Transaction> all = storage.getAllTransactions();

        HashMap<String, Double> map = new HashMap<>();

        for (Transaction t : all) {
            if (!t.getType().equalsIgnoreCase(type)) continue;

            try {
                String[] parts = t.getDate().split("-");
                int y = Integer.parseInt(parts[0]);
                int m = Integer.parseInt(parts[1]);

                if (y == year && m == month) {
                    double current = map.getOrDefault(t.getCategory(), 0.0);
                    map.put(t.getCategory(), current + t.getAmount());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            entries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
        }

        if (entries.isEmpty()) {
            pieChart.clear();
            pieChart.setCenterText("Không có dữ liệu\n" + type + " tháng " + month + "/" + year);
            pieChart.invalidate();
            return;
        }

        PieDataSet dataSet = new PieDataSet(entries, type + " tháng " + month + "/" + year);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.setUsePercentValues(true);
        pieChart.setCenterText(type + " " + month + "/" + year);
        pieChart.animateY(800);
        pieChart.invalidate();
        // Khi nhấn vào lát biểu đồ: hiện số tiền thật ở giữa
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                PieEntry entry = (PieEntry) e;
                String label = entry.getLabel();
                float value = entry.getValue();

                pieChart.setUsePercentValues(false); // đổi sang hiển thị giá trị thực
                pieChart.setCenterText(label + "\n" + String.format("%,.0f đ", value));
                pieChart.invalidate();
            }

            @Override
            public void onNothingSelected() {
                // khi bỏ chọn: quay lại hiển thị %
                pieChart.setUsePercentValues(true);
                pieChart.setCenterText(type + " " + month + "/" + year);
                pieChart.invalidate();
            }
        });
    }
}