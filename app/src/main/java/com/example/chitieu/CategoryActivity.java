package com.example.chitieu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import java.util.*;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private List<Category> categoryList;

    // radio/deselection helpers
    private RadioGroup radioGroupType;
    private RadioButton radioThu, radioChi;
    private int lastCheckedId = -1; // to handle deselect on second click

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // load data
        categoryList = CategoryStorage.loadCategories(this);
        if (categoryList.isEmpty()) {
            categoryList.add(new Category(1,"Lương","Thu", Color.parseColor("#4CAF50")));
            categoryList.add(new Category(2,"Ăn uống","Chi", Color.parseColor("#FF7043")));
            CategoryStorage.saveCategories(this, categoryList);
        }

        recyclerView = findViewById(R.id.recyclerViewCategories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CategoryAdapter(categoryList);
        recyclerView.setAdapter(adapter);

        // RadioGroup and radio buttons (top)
        radioGroupType = findViewById(R.id.radioGroupType);
        radioThu = findViewById(R.id.radioThu);
        radioChi = findViewById(R.id.radioChi);

        // Allow deselect by second click: set onClick listeners on radio buttons
        View.OnClickListener radioClick = v -> {
            int id = v.getId();
            if (radioGroupType.getCheckedRadioButtonId() == id) {
                // clicked the already-checked radio -> clear selection
                radioGroupType.clearCheck();
                lastCheckedId = -1;
            } else {
                // new selection
                lastCheckedId = id;
                radioGroupType.check(id);
            }
        };
        radioThu.setOnClickListener(radioClick);
        radioChi.setOnClickListener(radioClick);

        // Add button
        findViewById(R.id.btnAddCategory).setOnClickListener(v -> {
            // Determine current type (if any)
            int checked = radioGroupType.getCheckedRadioButtonId();
            String currentType = "";
            if (checked == R.id.radioThu) currentType = "Thu";
            else if (checked == R.id.radioChi) currentType = "Chi";

            showAddDialog(currentType);
        });
    }

    private void showAddDialog(String defaultType) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = (int)(16 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad,pad,pad,pad);

        final EditText etName = new EditText(this);
        etName.setHint("Tên danh mục");
        layout.addView(etName);

        // radio inside dialog
        final RadioGroup rg = new RadioGroup(this);
        rg.setOrientation(RadioGroup.HORIZONTAL);
        RadioButton rbThu = new RadioButton(this);
        rbThu.setText("Thu nhập");
        RadioButton rbChi = new RadioButton(this);
        rbChi.setText("Chi tiêu");
        rg.addView(rbThu);
        rg.addView(rbChi);
        layout.addView(rg);

        if ("Thu".equals(defaultType)) rg.check(rbThu.getId());
        else if ("Chi".equals(defaultType)) rg.check(rbChi.getId());

        // color chooser simple
        Button btnColor = new Button(this);
        btnColor.setText("Chọn màu");
        layout.addView(btnColor);
        final int[] selectedColor = { Color.parseColor("#FF7043") };

        btnColor.setOnClickListener(v -> {
            String[] names = {"Cam", "Xanh", "Xanh lá", "Đỏ"};
            int[] vals = { Color.parseColor("#FF7043"), Color.parseColor("#2196F3"), Color.parseColor("#4CAF50"), Color.parseColor("#F44336") };
            new AlertDialog.Builder(this)
                    .setTitle("Chọn màu")
                    .setItems(names, (d,i) -> selectedColor[0] = vals[i])
                    .show();
        });

        AlertDialog dlg = new AlertDialog.Builder(this)
                .setTitle("Thêm danh mục")
                .setView(layout)
                .setPositiveButton("Lưu", null)
                .setNegativeButton("Hủy", (d,i) -> d.dismiss())
                .create();

        dlg.setOnShowListener(dialog -> {
            Button save = dlg.getButton(AlertDialog.BUTTON_POSITIVE);
            save.setOnClickListener(v -> {
                String name = etName.getText().toString().trim();
                if (name.isEmpty()) {
                    Toast.makeText(this, "Nhập tên danh mục", Toast.LENGTH_SHORT).show();
                    return;
                }
                int checked = rg.getCheckedRadioButtonId();
                String type = "";
                if (checked == rbThu.getId()) type = "Thu";
                else if (checked == rbChi.getId()) type = "Chi";
                else {
                    Toast.makeText(this, "Chọn Thu hoặc Chi", Toast.LENGTH_SHORT).show();
                    return;
                }
                int newId = generateNewId();
                Category c = new Category(newId, name, type, selectedColor[0]);
                categoryList.add(c);
                CategoryStorage.saveCategories(this, categoryList);
                adapter.notifyItemInserted(categoryList.size()-1);
                dlg.dismiss();
            });
        });

        dlg.show();
    }

    private int generateNewId() {
        int max = 0;
        for (Category c : categoryList) if (c.getId() > max) max = c.getId();
        return max + 1;
    }

    // Edit dialog (similar to add)
    private void showEditDialog(int position) {
        Category c = categoryList.get(position);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = (int)(16 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad,pad,pad,pad);

        final EditText etName = new EditText(this);
        etName.setText(c.getName());
        layout.addView(etName);

        final RadioGroup rg = new RadioGroup(this);
        rg.setOrientation(RadioGroup.HORIZONTAL);
        RadioButton rbThu = new RadioButton(this); rbThu.setText("Thu");
        RadioButton rbChi = new RadioButton(this); rbChi.setText("Chi");
        rg.addView(rbThu); rg.addView(rbChi);
        if ("Thu".equals(c.getType())) rg.check(rbThu.getId());
        else if ("Chi".equals(c.getType())) rg.check(rbChi.getId());
        layout.addView(rg);

        Button btnColor = new Button(this);
        btnColor.setText("Chọn màu (mẫu)");
        layout.addView(btnColor);
        final int[] selectedColor = { c.getColor() };

        btnColor.setOnClickListener(v -> {
            String[] names = {"Cam", "Xanh", "Xanh lá", "Đỏ"};
            int[] vals = { Color.parseColor("#FF7043"), Color.parseColor("#2196F3"), Color.parseColor("#4CAF50"), Color.parseColor("#F44336") };
            new AlertDialog.Builder(this)
                    .setTitle("Chọn màu")
                    .setItems(names, (d,i) -> selectedColor[0] = vals[i])
                    .show();
        });

        AlertDialog dlg = new AlertDialog.Builder(this)
                .setTitle("Sửa danh mục")
                .setView(layout)
                .setPositiveButton("Lưu", null)
                .setNegativeButton("Hủy", (d,i) -> d.dismiss())
                .create();

        dlg.setOnShowListener(dialog -> {
            Button save = dlg.getButton(AlertDialog.BUTTON_POSITIVE);
            save.setOnClickListener(v -> {
                String name = etName.getText().toString().trim();
                if (name.isEmpty()) { Toast.makeText(this, "Nhập tên", Toast.LENGTH_SHORT).show(); return; }
                int checked = rg.getCheckedRadioButtonId();
                String type = "";
                if (checked == rbThu.getId()) type = "Thu";
                else if (checked == rbChi.getId()) type = "Chi";
                else { Toast.makeText(this, "Chọn Thu hoặc Chi", Toast.LENGTH_SHORT).show(); return; }

                c.setName(name);
                c.setType(type);
                c.setColor(selectedColor[0]);
                CategoryStorage.saveCategories(this, categoryList);
                adapter.notifyItemChanged(position);
                dlg.dismiss();
            });
        });

        dlg.show();
    }

    private void showDeleteConfirm(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa danh mục")
                .setMessage("Bạn có chắc muốn xóa danh mục này?")
                .setPositiveButton("Xóa", (d,w) -> {
                    categoryList.remove(position);
                    CategoryStorage.saveCategories(this, categoryList);
                    adapter.notifyItemRemoved(position);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // Adapter
    class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.VH> {
        private final List<Category> data;
        CategoryAdapter(List<Category> data) { this.data = data; }

        class VH extends RecyclerView.ViewHolder {
            LinearLayout container;
            TextView tvName, tvType;
            ImageButton btnEdit, btnDelete;
            VH(@NonNull View v) {
                super(v);
                container = v.findViewById(R.id.containerCategory);
                tvName = v.findViewById(R.id.tvCategoryName);
                tvType = v.findViewById(R.id.tvCategoryType);
                btnEdit = v.findViewById(R.id.btnEdit);
                btnDelete = v.findViewById(R.id.btnDelete);
            }
        }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
            return new VH(v);
        }

        @Override public void onBindViewHolder(@NonNull VH h, int pos) {
            Category c = data.get(pos);
            h.tvName.setText(c.getName());
            h.tvType.setText(c.getType());
            // subtle background color (use translucent)
            int bg = adjustAlpha(c.getColor(), 0.15f);
            h.container.setBackgroundColor(bg);

            h.btnEdit.setOnClickListener(v -> showEditDialog(pos));
            h.btnDelete.setOnClickListener(v -> showDeleteConfirm(pos));
        }

        @Override public int getItemCount() { return data.size(); }

        // helper to make translucent color
        private int adjustAlpha(int color, float factor) {
            int alpha = Math.round(android.graphics.Color.alpha(color) * factor);
            int red = android.graphics.Color.red(color);
            int green = android.graphics.Color.green(color);
            int blue = android.graphics.Color.blue(color);
            return android.graphics.Color.argb(alpha, red, green, blue);
        }
    }
}
