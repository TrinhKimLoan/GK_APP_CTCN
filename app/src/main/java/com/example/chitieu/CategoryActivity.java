package com.example.chitieu;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private List<Category> categoryList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        listView = findViewById(R.id.categoryListView);
        categoryList = CategoryStorage.loadCategories(this);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getCategoryNames());
        listView.setAdapter(adapter);

        Button btnAdd = findViewById(R.id.btnAddCategory);
        btnAdd.setOnClickListener(v -> showAddDialog());

        listView.setOnItemClickListener((parent, view, position, id) -> showEditDialog(position));
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            showDeleteDialog(position);
            return true;
        });
    }

    private void showAddDialog() {
        EditText input = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("Thêm danh mục")
                .setView(input)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    int id = categoryList.size() + 1;
                    categoryList.add(new Category(id, input.getText().toString(), "Chi", 0));
                    saveAndRefresh();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showEditDialog(int position) {
        EditText input = new EditText(this);
        input.setText(categoryList.get(position).getName());
        new AlertDialog.Builder(this)
                .setTitle("Sửa danh mục")
                .setView(input)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    categoryList.get(position).setName(input.getText().toString());
                    saveAndRefresh();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showDeleteDialog(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa danh mục")
                .setMessage("Bạn có chắc muốn xóa danh mục này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    categoryList.remove(position);
                    saveAndRefresh();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void saveAndRefresh() {
        CategoryStorage.saveCategories(this, categoryList);
        adapter.clear();
        adapter.addAll(getCategoryNames());
        adapter.notifyDataSetChanged();
    }

    private List<String> getCategoryNames() {
        List<String> names = new ArrayList<>();
        for (Category c : categoryList) names.add(c.getName());
        return names;
    }
}

