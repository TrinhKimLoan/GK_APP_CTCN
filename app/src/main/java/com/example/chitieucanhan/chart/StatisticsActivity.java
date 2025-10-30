package com.example.chitieucanhan.chart;
import com.example.chitieucanhan.R;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.*;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import java.util.ArrayList;

public class StatisticsActivity extends AppCompatActivity {
    private PieChart pieChart;
    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        pieChart = findViewById(R.id.pieChart);
        barChart = findViewById(R.id.barChart);

        setupPieChart();
        setupBarChart();
    }

    private void setupPieChart() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(2500000, "Ăn uống"));
        entries.add(new PieEntry(1200000, "Đi lại"));
        entries.add(new PieEntry(800000, "Giải trí"));
        entries.add(new PieEntry(500000, "Khác"));

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

    private void setupBarChart() {
        ArrayList<BarEntry> incomeEntries = new ArrayList<>();
        ArrayList<BarEntry> expenseEntries = new ArrayList<>();

        incomeEntries.add(new BarEntry(5, 7000000));
        incomeEntries.add(new BarEntry(6, 6500000));
        incomeEntries.add(new BarEntry(7, 8000000));
        incomeEntries.add(new BarEntry(8, 6000000));
        incomeEntries.add(new BarEntry(9, 7500000));
        incomeEntries.add(new BarEntry(10, 7000000));

        expenseEntries.add(new BarEntry(5, 4500000));
        expenseEntries.add(new BarEntry(6, 5200000));
        expenseEntries.add(new BarEntry(7, 5000000));
        expenseEntries.add(new BarEntry(8, 4800000));
        expenseEntries.add(new BarEntry(9, 5500000));
        expenseEntries.add(new BarEntry(10, 4900000));

        BarDataSet incomeSet = new BarDataSet(incomeEntries, "Thu nhập");
        incomeSet.setColor(ColorTemplate.COLORFUL_COLORS[2]);

        BarDataSet expenseSet = new BarDataSet(expenseEntries, "Chi tiêu");
        expenseSet.setColor(ColorTemplate.COLORFUL_COLORS[0]);

        BarData data = new BarData(incomeSet, expenseSet);
        data.setBarWidth(0.35f);

        barChart.setData(data);
        barChart.groupBars(4.5f, 0.4f, 0.05f);
        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);
        barChart.animateY(800);
        barChart.invalidate();
    }
}