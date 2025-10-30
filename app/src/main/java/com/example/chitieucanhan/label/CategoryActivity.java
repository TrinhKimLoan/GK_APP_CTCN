package com.example.chitieucanhan.label;
import com.example.chitieucanhan.R;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import java.util.*;

public class CategoryActivity extends AppCompatActivity {

    private List<Category> categoryList = new ArrayList<>();
    private CategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        categoryList = CategoryStorage.loadCategories(this);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewCategories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CategoryAdapter(categoryList);
        recyclerView.setAdapter(adapter);

        Button btnAdd = findViewById(R.id.btnAddCategory);
        btnAdd.setOnClickListener(v -> showAddDialog());
    }

    private void showAddDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16,16,16,16);

        EditText etName = new EditText(this);
        etName.setHint("Tên danh mục");
        layout.addView(etName);

        RadioGroup rgType = new RadioGroup(this);
        RadioButton rbThu = new RadioButton(this); rbThu.setText("Thu");
        RadioButton rbChi = new RadioButton(this); rbChi.setText("Chi"); rbChi.setChecked(true);
        rgType.addView(rbThu); rgType.addView(rbChi);
        layout.addView(rgType);

        Button btnColor = new Button(this);
        btnColor.setText("Chọn màu");
        final int[] selectedColor = {Color.RED};
        btnColor.setOnClickListener(v -> {
            String[] colors = {"Đỏ","Xanh","Vàng"};
            int[] colorValues = {Color.RED, Color.BLUE, Color.YELLOW};
            new AlertDialog.Builder(this)
                    .setTitle("Chọn màu")
                    .setItems(colors, (dialog,i)-> selectedColor[0] = colorValues[i])
                    .show();
        });
        layout.addView(btnColor);

        new AlertDialog.Builder(this)
                .setTitle("Thêm danh mục")
                .setView(layout)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String name = etName.getText().toString();
                    String type = rbThu.isChecked() ? "Thu" : "Chi";
                    int id = categoryList.size() + 1;
                    categoryList.add(new Category(id,name,type,selectedColor[0]));
                    saveAndRefresh();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void saveAndRefresh() {
        CategoryStorage.saveCategories(this, categoryList);
        adapter.notifyDataSetChanged();
    }

    // Adapter RecyclerView
    class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
        private List<Category> data;
        CategoryAdapter(List<Category> data) { this.data = data; }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvType;
            LinearLayout container;
            public ViewHolder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvCategoryName);
                tvType = itemView.findViewById(R.id.tvCategoryType);
                container = itemView.findViewById(R.id.containerCategory);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_category,parent,false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Category c = data.get(position);
            holder.tvName.setText(c.getName());
            holder.tvType.setText(c.getType());
            holder.container.setBackgroundColor(c.getColor());
            holder.itemView.setOnClickListener(v -> showEditDialog(position));
            holder.itemView.setOnLongClickListener(v -> { showDeleteDialog(position); return true; });
        }

        @Override
        public int getItemCount() { return data.size(); }
    }

    private void showEditDialog(int position) {
        Category c = categoryList.get(position);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16,16,16,16);

        EditText etName = new EditText(this);
        etName.setText(c.getName());
        layout.addView(etName);

        RadioGroup rgType = new RadioGroup(this);
        RadioButton rbThu = new RadioButton(this); rbThu.setText("Thu");
        RadioButton rbChi = new RadioButton(this); rbChi.setText("Chi");
        if(c.getType().equals("Thu")) rbThu.setChecked(true); else rbChi.setChecked(true);
        rgType.addView(rbThu); rgType.addView(rbChi);
        layout.addView(rgType);

        Button btnColor = new Button(this); btnColor.setText("Chọn màu");
        final int[] selectedColor = {c.getColor()};
        btnColor.setOnClickListener(v -> {
            String[] colors = {"Đỏ","Xanh","Vàng"};
            int[] colorValues = {Color.RED,Color.BLUE,Color.YELLOW};
            new AlertDialog.Builder(this)
                    .setTitle("Chọn màu")
                    .setItems(colors,(d,i)-> selectedColor[0]=colorValues[i])
                    .show();
        });
        layout.addView(btnColor);

        new AlertDialog.Builder(this)
                .setTitle("Sửa danh mục")
                .setView(layout)
                .setPositiveButton("Lưu", (d,w)->{
                    c.setName(etName.getText().toString());
                    c.setType(rbThu.isChecked()?"Thu":"Chi");
                    c.setColor(selectedColor[0]);
                    saveAndRefresh();
                })
                .setNegativeButton("Hủy",null)
                .show();
    }

    private void showDeleteDialog(int position){
        new AlertDialog.Builder(this)
                .setTitle("Xóa danh mục")
                .setMessage("Bạn có chắc muốn xóa?")
                .setPositiveButton("Xóa",(d,w)->{ categoryList.remove(position); saveAndRefresh();})
                .setNegativeButton("Hủy",null)
                .show();
    }
}
