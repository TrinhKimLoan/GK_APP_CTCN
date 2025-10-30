package com.example.chitieucanhan;

import com.example.chitieucanhan.label.CategoryActivity;
import com.example.chitieucanhan.transaction.TransactionActivity;
import com.example.chitieucanhan.transaction.AddTransactionActivity;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import androidx.core.graphics.Insets;
import android.os.Bundle;
import android.widget.Button;
import androidx.core.view.ViewCompat;
import android.view.MenuItem;
import androidx.activity.EdgeToEdge;

public class MainActivity extends AppCompatActivity {
    private Button btnViewTransactions, btnAddTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        if (nav != null) {
            nav.setOnItemSelectedListener(item -> {
                return onNavItemSelected(item);
            });
            // set default selected to goals
            nav.setSelectedItemId(R.id.nav_goals);
        }

        Button btnCategory = findViewById(R.id.btnOpenCategory);
        btnCategory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
            startActivity(intent);
        });

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
    }

    private boolean onNavItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_goals) {
            startActivity(new Intent(this, GoalActivity.class));
            return true;
        }
        // placeholders for other tabs
        if (id == R.id.nav_transactions) {
            // implement TransactionsActivity later; for now open MainActivity (no-op)
            return true;
        }
        if (id == R.id.nav_categories) {
            return true;
        }
        if (id == R.id.nav_dashboard) {
            return true;
        }
        return false;
    }
}