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

    // parse color string (#RRGGBB or RRGGBB or #AARRGGBB)
    private Integer parseColorOrNull(String hex) {
        if (hex == null) return null;
        hex = hex.trim();
        if (hex.startsWith("#")) hex = hex.substring(1);
        if (!(hex.matches("(?i)[0-9A-F]{6}") || hex.matches("(?i)[0-9A-F]{8}"))) return null;
        try {
            if (hex.length() == 6) hex = "FF" + hex;
            long val = Long.parseLong(hex, 16);
            return (int) val;
        } catch (Exception e) { return null; }
    }

    private interface ColorPickCallback { void onPick(int color); }

    private void showColorPickerDialog(int initialColor, final ColorPickCallback callback) {
        final int[] palette = new int[] {
                Color.parseColor("#FF7043"), Color.parseColor("#4CAF50"), Color.parseColor("#2196F3"),
                Color.parseColor("#9C27B0"), Color.parseColor("#F44336"), Color.parseColor("#FFC107"),
                Color.parseColor("#795548"), Color.parseColor("#607D8B"), Color.parseColor("#00BCD4"),
                Color.parseColor("#8BC34A"), Color.parseColor("#E91E63"), Color.parseColor("#3F51B5")
        };

        GridView grid = new GridView(this);
        grid.setNumColumns(4);
        grid.setHorizontalSpacing(12);
        grid.setVerticalSpacing(12);
        int pad = (int)(12 * getResources().getDisplayMetrics().density);
        grid.setPadding(pad,pad,pad,pad);

        List<Integer> boxed = new ArrayList<>();
        for (int c : palette) boxed.add(c);

        ArrayAdapter<Integer> ad = new ArrayAdapter<Integer>(this, android.R.layout.simple_list_item_1, boxed) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    v = new View(parent.getContext());
                    int size = (int) (40 * parent.getContext().getResources().getDisplayMetrics().density);
                    AbsListView.LayoutParams lp = new AbsListView.LayoutParams(size, size);
                    v.setLayoutParams(lp);
                }
                v.setBackgroundColor(palette[position]);
                return v;
            }
        };
        grid.setAdapter(ad);

        AlertDialog dlg = new AlertDialog.Builder(this)
                .setTitle("Chọn màu")
                .setView(grid)
                .setNegativeButton("Hủy", null)
                .create();

        grid.setOnItemClickListener((parent, view, position, id) -> {
            int picked = palette[position];
            callback.onPick(picked);
            dlg.dismiss();
        });

        dlg.show();
    }

    // Add dialog (with RadioGroup properly inside the dialog)
    private void showAddDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = (int)(16 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad,pad,pad,pad);

        final EditText etName = new EditText(this);
        etName.setHint("Tên danh mục");
        layout.addView(etName);

        // RadioGroup with two RadioButtons (ensure same group)
        RadioGroup rg = new RadioGroup(this);
        rg.setOrientation(RadioGroup.HORIZONTAL);
        RadioButton rbThu = new RadioButton(this);
        rbThu.setId(View.generateViewId());
        rbThu.setText("Thu");
        RadioButton rbChi = new RadioButton(this);
        rbChi.setId(View.generateViewId());
        rbChi.setText("Chi");
        rg.addView(rbThu);
        rg.addView(rbChi);
        rbChi.setChecked(true); // default
        layout.addView(rg);

        // color row: preview + hex input + palette button
        LinearLayout colorRow = new LinearLayout(this);
        colorRow.setOrientation(LinearLayout.HORIZONTAL);
        colorRow.setGravity(Gravity.CENTER_VERTICAL);
        colorRow.setPadding(0,16,0,0);

        final View colorPreview = new View(this);
        int previewPx = (int)(36 * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams pvLp = new LinearLayout.LayoutParams(previewPx, previewPx);
        pvLp.setMarginEnd((int)(12 * getResources().getDisplayMetrics().density));
        colorPreview.setLayoutParams(pvLp);
        colorPreview.setBackgroundColor(Color.parseColor("#FF7043"));
        colorRow.addView(colorPreview);

        final EditText etHex = new EditText(this);
        etHex.setHint("#RRGGBB");
        etHex.setSingleLine(true);
        etHex.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        colorRow.addView(etHex);

        Button btnPalette = new Button(this);
        btnPalette.setText("Mở bảng màu");
        colorRow.addView(btnPalette);

        layout.addView(colorRow);

        etHex.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                Integer col = parseColorOrNull(etHex.getText().toString());
                if (col != null) colorPreview.setBackgroundColor(col);
                else if (!etHex.getText().toString().trim().isEmpty())
                    Toast.makeText(this, "Mã màu không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });

        btnPalette.setOnClickListener(v -> {
            Integer parsed = parseColorOrNull(etHex.getText().toString());
            int init = parsed != null ? parsed : Color.parseColor("#FF7043");
            showColorPickerDialog(init, picked -> {
                colorPreview.setBackgroundColor(picked);
                etHex.setText(String.format("#%06X", (0xFFFFFF & picked)));
            });
        });

        AlertDialog dlg = new AlertDialog.Builder(this)
                .setTitle("Thêm danh mục")
                .setView(layout)
                .setPositiveButton("Lưu", null)
                .setNegativeButton("Hủy", (d,i)->d.dismiss())
                .create();

        dlg.setOnShowListener(d -> {
            Button b = dlg.getButton(AlertDialog.BUTTON_POSITIVE);
            b.setOnClickListener(v -> {
                String name = etName.getText().toString().trim();
                if (name.isEmpty()) { Toast.makeText(this, "Nhập tên", Toast.LENGTH_SHORT).show(); return; }

                // get selected radio from the group (ensures mutual exclusion)
                int checkedId = rg.getCheckedRadioButtonId();
                String type = (checkedId == rbThu.getId()) ? "Thu" : "Chi";

                Integer parsed = parseColorOrNull(etHex.getText().toString().trim());
                int color = parsed != null ? parsed : Color.parseColor("#FF7043");
                int id = generateId();
                categoryList.add(new Category(id, name, type, color));
                CategoryStorage.saveCategories(this, categoryList);
                adapter.notifyDataSetChanged();
                dlg.dismiss();
            });
        });

        dlg.show();
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
        RadioButton rbThu = new RadioButton(this);
        rbThu.setId(View.generateViewId());
        rbThu.setText("Thu");
        RadioButton rbChi = new RadioButton(this);
        rbChi.setId(View.generateViewId());
        rbChi.setText("Chi");
        rg.addView(rbThu);
        rg.addView(rbChi);
        if ("Thu".equals(c.getType())) rbThu.setChecked(true); else rbChi.setChecked(true);
        layout.addView(rg);

        LinearLayout colorRow = new LinearLayout(this);
        colorRow.setOrientation(LinearLayout.HORIZONTAL);
        colorRow.setGravity(Gravity.CENTER_VERTICAL);
        colorRow.setPadding(0,16,0,0);

        final View colorPreview = new View(this);
        int previewPx = (int)(36 * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams pvLp = new LinearLayout.LayoutParams(previewPx, previewPx);
        pvLp.setMarginEnd((int)(12 * getResources().getDisplayMetrics().density));
        colorPreview.setLayoutParams(pvLp);
        colorPreview.setBackgroundColor(c.getColor());
        colorRow.addView(colorPreview);

        final EditText etHex = new EditText(this);
        etHex.setHint("#RRGGBB");
        etHex.setSingleLine(true);
        etHex.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        etHex.setText(String.format("#%06X", (0xFFFFFF & c.getColor())));
        colorRow.addView(etHex);

        Button btnPalette = new Button(this);
        btnPalette.setText("Mở bảng màu");
        colorRow.addView(btnPalette);

        layout.addView(colorRow);

        etHex.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                Integer col = parseColorOrNull(etHex.getText().toString());
                if (col != null) colorPreview.setBackgroundColor(col);
                else if (!etHex.getText().toString().trim().isEmpty())
                    Toast.makeText(this, "Mã màu không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });

        btnPalette.setOnClickListener(v -> {
            Integer parsed = parseColorOrNull(etHex.getText().toString());
            int init = parsed != null ? parsed : c.getColor();
            showColorPickerDialog(init, picked -> {
                colorPreview.setBackgroundColor(picked);
                etHex.setText(String.format("#%06X", (0xFFFFFF & picked)));
            });
        });

        AlertDialog dlg = new AlertDialog.Builder(this)
                .setTitle("Sửa danh mục")
                .setView(layout)
                .setPositiveButton("Lưu", null)
                .setNegativeButton("Hủy", (d,i)->d.dismiss())
                .create();

        dlg.setOnShowListener(d -> {
            Button b = dlg.getButton(AlertDialog.BUTTON_POSITIVE);
            b.setOnClickListener(v -> {
                String name = etName.getText().toString().trim();
                if (name.isEmpty()) { Toast.makeText(this, "Nhập tên", Toast.LENGTH_SHORT).show(); return; }

                int checkedId = rg.getCheckedRadioButtonId();
                String type = (checkedId == rbThu.getId()) ? "Thu" : "Chi";

                Integer parsed = parseColorOrNull(etHex.getText().toString().trim());
                if (parsed != null) c.setColor(parsed);
                c.setName(name);
                c.setType(type);

                CategoryStorage.saveCategories(this, categoryList);
                adapter.notifyDataSetChanged();
                dlg.dismiss();
            });
        });

        dlg.show();
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

    // Adapter
    class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.VH> {
        private final List<Category> data;
        CategoryAdapter(List<Category> list) { this.data = list; }

        class VH extends RecyclerView.ViewHolder {
            TextView tvName, tvType;
            View viewColor;
            ImageButton btnEdit, btnDelete;
            VH(View v) {
                super(v);
                tvName = v.findViewById(R.id.tvCategoryName);
                tvType = v.findViewById(R.id.tvCategoryType);
                viewColor = v.findViewById(R.id.viewColor);
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
        public void onBindViewHolder(@NonNull VH holder, int position) {
            Category c = data.get(position);
            holder.tvName.setText(c.getName());
            holder.tvType.setText(c.getType());
            holder.viewColor.setBackgroundColor(c.getColor());

            holder.btnEdit.setOnClickListener(v -> showEditDialog(holder.getAdapterPosition()));
            holder.btnDelete.setOnClickListener(v -> showDeleteDialog(holder.getAdapterPosition()));
        }

        @Override public int getItemCount() { return data.size(); }
    }
}
