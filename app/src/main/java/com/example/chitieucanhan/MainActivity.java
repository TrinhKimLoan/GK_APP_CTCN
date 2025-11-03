package com.example.chitieucanhan;

import com.example.chitieucanhan.label.CategoryActivity;
import com.example.chitieucanhan.transaction.TransactionActivity;
import com.example.chitieucanhan.transaction.AddTransactionActivity;
import com.example.chitieucanhan.goal.GoalActivity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button btnViewTransactions, btnAddTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnCategory = findViewById(R.id.btnOpenCategory);
        btnCategory.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CategoryActivity.class)));

        btnViewTransactions = findViewById(R.id.btnViewTransactions);
        btnAddTransaction = findViewById(R.id.btnAddTransaction);

        // Mở danh sách giao dịch
        btnViewTransactions.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TransactionActivity.class);
            startActivity(intent);
        });

        // Mở màn hình thêm giao dịch
        btnAddTransaction.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
            startActivity(intent);
        });

        // Mở màn hình SET GOAL
        Button btnGoGoal = findViewById(R.id.btn_go_goal);
        btnGoGoal.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GoalActivity.class);
            startActivity(intent);
        });

        Button btnOpenBudget = findViewById(R.id.btnOpenBudget);
        btnOpenBudget.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, com.example.chitieucanhan.chart.BudgetActivity.class);
            startActivity(intent);
        });
    }
}