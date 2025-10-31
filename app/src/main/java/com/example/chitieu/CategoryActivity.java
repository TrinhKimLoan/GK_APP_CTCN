package com.example.chitieu;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import java.util.*;

public class CategoryActivity extends AppCompatActivity {

    private List<Category> categoryList;
    private CategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        categoryList = CategoryStorage.loadCategories(this);

        RecyclerView rv = findViewById(R.id.recyclerViewCategories);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CategoryAdapter(categoryList);
        rv.setAdapter(adapter);

        findViewById(R.id.btnAddCategory).setOnClickListener(v -> showAddDialog());
    }

    private void showAddDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = (int)(16 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad,pad,pad,pad);

        final EditText etName = new EditText(this);
        etName.setHint("Tên danh mục");
        layout.addView(etName);

        RadioGroup rg = new RadioGroup(this);
        rg.setOrientation(RadioGroup.HORIZONTAL);
        RadioButton rbThu = new RadioButton(this); rbThu.setText("Thu");
        RadioButton rbChi = new RadioButton(this); rbChi.setText("Chi");
        rg.addView(rbThu); rg.addView(rbChi);
        rbChi.setChecked(true); // default
        layout.addView(rg);

        Button btnColor = new Button(this);
        btnColor.setText("Chọn màu");
        final int[] selectedColor = { Color.parseColor("#FF7043") };
        btnColor.setOnClickListener(v -> {
            String[] names = {"Cam", "Xanh", "Xanh dương"};
            int[] values = { Color.parseColor("#FF7043"), Color.parseColor("#4CAF50"), Color.parseColor("#2196F3") };
            new AlertDialog.Builder(this)
                    .setTitle("Chọn màu")
                    .setItems(names, (d,i) -> selectedColor[0] = values[i])
                    .show();
        });
        layout.addView(btnColor);

        new AlertDialog.Builder(this)
                .setTitle("Thêm danh mục")
                .setView(layout)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    if (name.isEmpty()) { Toast.makeText(this, "Nhập tên", Toast.LENGTH_SHORT).show(); return; }
                    String type = rbThu.isChecked() ? "Thu" : "Chi";
                    int id = generateId();
                    categoryList.add(new Category(id, name, type, selectedColor[0]));
                    CategoryStorage.saveCategories(this, categoryList);
                    adapter.notifyDataSetChanged();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showEditDialog(int position) {
        Category c = categoryList.get(position);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = (int)(16 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad,pad,pad,pad);

        final EditText etName = new EditText(this);
        etName.setText(c.getName());
        layout.addView(etName);

        RadioGroup rg = new RadioGroup(this);
        rg.setOrientation(RadioGroup.HORIZONTAL);
        RadioButton rbThu = new RadioButton(this); rbThu.setText("Thu");
        RadioButton rbChi = new RadioButton(this); rbChi.setText("Chi");
        rg.addView(rbThu); rg.addView(rbChi);
        if (c.getType().equals("Thu")) rbThu.setChecked(true); else rbChi.setChecked(true);
        layout.addView(rg);

        Button btnColor = new Button(this); btnColor.setText("Chọn màu");
        final int[] selectedColor = { c.getColor() };
        btnColor.setOnClickListener(v -> {
            String[] names = {"Cam", "Xanh", "Xanh dương"};
            int[] values = { Color.parseColor("#FF7043"), Color.parseColor("#4CAF50"), Color.parseColor("#2196F3") };
            new AlertDialog.Builder(this)
                    .setTitle("Chọn màu")
                    .setItems(names, (d,i) -> selectedColor[0] = values[i])
                    .show();
        });
        layout.addView(btnColor);

        new AlertDialog.Builder(this)
                .setTitle("Sửa danh mục")
                .setView(layout)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    if (name.isEmpty()) { Toast.makeText(this, "Nhập tên", Toast.LENGTH_SHORT).show(); return; }
                    c.setName(name);
                    c.setType(rbThu.isChecked() ? "Thu" : "Chi");
                    c.setColor(selectedColor[0]);
                    CategoryStorage.saveCategories(this, categoryList);
                    adapter.notifyDataSetChanged();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showDeleteDialog(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa danh mục")
                .setMessage("Bạn có chắc muốn xóa danh mục này?")
                .setPositiveButton("Xóa", (d,w) -> {
                    categoryList.remove(position);
                    CategoryStorage.saveCategories(this, categoryList);
                    adapter.notifyDataSetChanged();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private int generateId() {
        int max = 0;
        for (Category c : categoryList) if (c.getId() > max) max = c.getId();
        return max + 1;
    }

    // --- Adapter ---
    class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.VH> {
        private final List<Category> data;
        CategoryAdapter(List<Category> list) { this.data = list; }

        class VH extends RecyclerView.ViewHolder {
            TextView tvName, tvType;
            View colorView;
            ImageButton btnEdit, btnDelete;
            VH(View v) {
                super(v);
                tvName = v.findViewById(R.id.tvCategoryName);
                tvType = v.findViewById(R.id.tvCategoryType);
                colorView = v.findViewById(R.id.viewColor);
                btnEdit = v.findViewById(R.id.btnEdit);
                btnDelete = v.findViewById(R.id.btnDelete);
            }
        }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int pos) {
            Category c = data.get(pos);
            holder.tvName.setText(c.getName());
            holder.tvType.setText(c.getType());
            holder.colorView.setBackgroundColor(c.getColor());

            holder.btnEdit.setOnClickListener(v -> showEditDialog(holder.getAdapterPosition()));
            holder.btnDelete.setOnClickListener(v -> showDeleteDialog(holder.getAdapterPosition()));
        }

        @Override public int getItemCount() { return data.size(); }
    }
}
