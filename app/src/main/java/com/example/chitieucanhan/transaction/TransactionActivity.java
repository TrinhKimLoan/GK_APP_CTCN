package com.example.chitieucanhan.transaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chitieucanhan.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class TransactionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private TransactionStorage storage;
    private List<Transaction> transactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        storage = new TransactionStorage(this);
        transactions = storage.getAllTransactions();

        adapter = new TransactionAdapter(transactions, new TransactionAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(Transaction transaction) {
                // Mở AddTransactionActivity với ID giao dịch để sửa
                Intent intent = new Intent(TransactionActivity.this, AddTransactionActivity.class);
                intent.putExtra("transaction_id", transaction.getId());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Transaction transaction) {
                // Xác nhận trước khi xóa
                new AlertDialog.Builder(TransactionActivity.this)
                        .setTitle("Xóa giao dịch")
                        .setMessage("Bạn có chắc chắn muốn xóa giao dịch này?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            storage.deleteTransaction(transaction.getId());
                            transactions.remove(transaction);
                            adapter.updateData(transactions);
                            Toast.makeText(TransactionActivity.this, "Đã xóa giao dịch", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });

        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> {
            // Thêm giao dịch mới
            startActivity(new Intent(TransactionActivity.this, AddTransactionActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật danh sách khi quay lại Activity
        transactions = storage.getAllTransactions();
        adapter.updateData(transactions);
    }
}
