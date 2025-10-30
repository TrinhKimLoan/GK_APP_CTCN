package com.example.chitieucanhan.transaction;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.chitieucanhan.R;
import com.example.chitieucanhan.label.Category;
import com.example.chitieucanhan.label.CategoryStorage;

import java.text.DecimalFormat;
import java.util.*;

public class AddTransactionActivity extends AppCompatActivity {

    private EditText etAmount, etNote, etDate, etCategoryOther;
    private Spinner spType, spCategory;
    private Button btnSave;
    private TransactionStorage storage;
    private boolean isEditingText = false;
    private Transaction editTransaction = null;
    private List<Category> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        etAmount = findViewById(R.id.etAmount);
        etNote   = findViewById(R.id.etNote);
        etDate   = findViewById(R.id.etDate);
        etCategoryOther = findViewById(R.id.etCategoryOther);
        spType   = findViewById(R.id.spType);
        spCategory = findViewById(R.id.spCategory);
        btnSave  = findViewById(R.id.btnSave);

        storage = new TransactionStorage(this);

        setupTypeSpinner();
        setupCategorySpinner();
        setupDatePicker();
        setupAmountFormatting();

        if (getIntent().hasExtra("transaction_id")) {
            String id = getIntent().getStringExtra("transaction_id");
            editTransaction = storage.getTransactionById(id);
            if (editTransaction != null) loadTransaction(editTransaction);
        }

        btnSave.setOnClickListener(v -> saveTransaction());
    }

    private void setupTypeSpinner() {
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                this, R.array.transaction_types, android.R.layout.simple_spinner_item
        );
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(typeAdapter);
    }

    private void setupCategorySpinner() {
        categoryList = CategoryStorage.loadCategories(this);
        List<String> names = new ArrayList<>();
        for (Category c : categoryList) {
            names.add(c.getName());
        }
        names.add("Khác");

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, names
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(categoryAdapter);

        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                etCategoryOther.setVisibility(selected.equals("Khác") ? View.VISIBLE : View.GONE);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void setupDatePicker() {
        etDate.setOnClickListener(v -> showDatePicker());
        etDate.setOnFocusChangeListener((v, hasFocus) -> { if (hasFocus) showDatePicker(); });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> etDate.setText(
                        String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                ),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void setupAmountFormatting() {
        etAmount.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (isEditingText) return;
                isEditingText = true;

                String digits = s.toString().replaceAll("[^\\d]", "");
                if (!digits.isEmpty()) {
                    try {
                        long value = Long.parseLong(digits);
                        String formatted = new DecimalFormat("#,###").format(value) + " VNĐ";
                        etAmount.setText(formatted);
                        etAmount.setSelection(formatted.length() - 4);
                    } catch (NumberFormatException e) {
                        etAmount.setText("");
                    }
                }

                isEditingText = false;
            }
        });
    }

    private void loadTransaction(Transaction t) {
        etAmount.setText(new DecimalFormat("#,###").format((long) t.getAmount()) + " VNĐ");
        etAmount.setSelection(etAmount.getText().length() - 4);
        etNote.setText(t.getNote());
        etDate.setText(t.getDate());
        spType.setSelection(t.getType().equals("Thu") ? 0 : 1);

        boolean isOther = true;
        for (int i = 0; i < spCategory.getCount(); i++) {
            if (spCategory.getItemAtPosition(i).toString().equals(t.getCategory())) {
                spCategory.setSelection(i);
                isOther = false;
                break;
            }
        }
        if (isOther) {
            spCategory.setSelection(spCategory.getCount() - 1); // Khác
            etCategoryOther.setText(t.getCategory());
            etCategoryOther.setVisibility(View.VISIBLE);
        }
    }

    private void saveTransaction() {
        String id   = (editTransaction != null) ? editTransaction.getId() : UUID.randomUUID().toString();
        String type = spType.getSelectedItem().toString();
        String rawAmount = etAmount.getText().toString().replace(",", "").replace(" VNĐ", "").trim();
        String date = etDate.getText().toString().trim();
        String note = etNote.getText().toString().trim();

        if (rawAmount.isEmpty()) {
            etAmount.setError("Vui lòng nhập số tiền");
            etAmount.requestFocus();
            return;
        }

        if (date.isEmpty()) {
            etDate.setError("Vui lòng chọn ngày");
            etDate.requestFocus();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(rawAmount);
        } catch (NumberFormatException e) {
            etAmount.setError("Số tiền không hợp lệ");
            etAmount.requestFocus();
            return;
        }

        // Category xử lý
        String categoryName;
        if (spCategory.getSelectedItem().toString().equals("Khác")) {
            categoryName = etCategoryOther.getText().toString().trim();
            if (categoryName.isEmpty()) {
                etCategoryOther.setError("Vui lòng nhập danh mục");
                etCategoryOther.requestFocus();
                return;
            }
            Category newCat = new Category(0, categoryName, type, 0);
            List<Category> cats = CategoryStorage.loadCategories(this);
            cats.add(newCat);
            CategoryStorage.saveCategories(this, cats);
        } else {
            categoryName = spCategory.getSelectedItem().toString();
        }

        Transaction t = new Transaction(id, type, categoryName, amount, date, note);
        if (editTransaction != null) {
            storage.editTransaction(t);
        } else {
            storage.addTransaction(t);
        }

        DecimalFormat formatter = new DecimalFormat("#,###");
        String formattedAmount = formatter.format((long) amount) + " VNĐ";
        Toast.makeText(this, "Giao dịch lưu: " + formattedAmount, Toast.LENGTH_SHORT).show();

        finish();
    }
}
