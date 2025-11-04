package com.example.chartctcn;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.*;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import java.util.ArrayList;

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
        String[] pieTypes = {"Chi", "Thu"}; // danh mục chi/thu
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

        setupPieChart();
    }

    private void setupPieChart() {
        // Giả dữ liệu danh mục
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(2500000, "Ăn uống"));
        entries.add(new PieEntry(1200000, "Đi lại"));
        entries.add(new PieEntry(800000, "Giải trí"));
        entries.add(new PieEntry(500000, "Khác"));
        // END Giả dữ liệu danh mục

        PieDataSet dataSet = new PieDataSet(entries, "Danh mục chi tiêu");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(14f);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.setUsePercentValues(true); // hiển thị % mặc định
        pieChart.setEntryLabelTextSize(12f);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Chi tiêu tháng này");
        pieChart.setCenterTextSize(14f);
        pieChart.animateY(800);

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
                pieChart.setCenterText("Chi tiêu tháng này");
                pieChart.invalidate();
            }
        });

        pieChart.invalidate();
    }

    private void setupPieChartBase() {
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterTextSize(14f);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.animateY(500);
    }

    // refresh Pie theo type/month/year
    private void refreshPieChart(String type, int month, int year) {
        // Giả dữ liệu cho Dữ liệu thu và chi trong PieChart
        ArrayList<PieEntry> entries = new ArrayList<>();
        if (type.equals("Chi")) {
            entries.add(new PieEntry(2500000, "Ăn uống"));
            entries.add(new PieEntry(1200000, "Đi lại"));
            entries.add(new PieEntry(800000, "Giải trí"));
            entries.add(new PieEntry(500000, "Khác"));
        } else {
            // dữ liệu thu ví dụ
            entries.add(new PieEntry(7000000, "Lương"));
            entries.add(new PieEntry(1000000, "Thưởng"));
        }
        // END Giả dữ liệu cho Dữ liệu thu và chi trong PieChart

        PieDataSet dataSet = new PieDataSet(entries, type + " tháng " + month + "/" + year);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.setCenterText(type + " " + month + "/" + year);
        pieChart.invalidate();
    }
}
