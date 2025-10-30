package com.example.chitieucanhan.transaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chitieucanhan.R;
import com.example.chitieucanhan.label.Category;
import com.example.chitieucanhan.label.CategoryStorage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.*;

public class TransactionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private TransactionStorage storage;
    private List<Transaction> transactions;
    private Spinner spFilterCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        spFilterCategory = findViewById(R.id.spFilterCategory);

        storage = new TransactionStorage(this);
        transactions = storage.getAllTransactions();

        adapter = new TransactionAdapter(transactions, new TransactionAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(Transaction transaction) {
                Intent intent = new Intent(TransactionActivity.this, AddTransactionActivity.class);
                intent.putExtra("transaction_id", transaction.getId());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Transaction transaction) {
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

        setupFilterSpinner();

        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> startActivity(new Intent(TransactionActivity.this, AddTransactionActivity.class)));
    }

    private void setupFilterSpinner() {
        List<Category> cats = CategoryStorage.loadCategories(this);
        List<String> names = new ArrayList<>();
        names.add("Tất cả");
        for(Category c : cats) names.add(c.getName());

        ArrayAdapter<String> adapterFilter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
        adapterFilter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFilterCategory.setAdapter(adapterFilter);

        spFilterCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                if(selected.equals("Tất cả")){
                    adapter.updateData(transactions);
                } else {
                    List<Transaction> filtered = new ArrayList<>();
                    for(Transaction t : transactions){
                        if(t.getCategory().equals(selected)) filtered.add(t);
                    }
                    adapter.updateData(filtered);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        transactions = storage.getAllTransactions();
        adapter.updateData(transactions);
    }
}
