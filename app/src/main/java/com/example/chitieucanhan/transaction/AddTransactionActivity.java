package com.example.chitieucanhan.transaction;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.chitieucanhan.R;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.UUID;

public class AddTransactionActivity extends AppCompatActivity {

    private EditText etAmount, etNote, etDate, etCategoryOther;
    private Spinner spType, spCategory;
    private Button btnSave;
    private TransactionStorage storage;
    private boolean isEditingText = false; // tránh loop format tiền
    private Transaction editTransaction = null; // nếu sửa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        // Ánh xạ
        etAmount = findViewById(R.id.etAmount);
        etNote = findViewById(R.id.etNote);
        etDate = findViewById(R.id.etDate);
        etCategoryOther = findViewById(R.id.etCategoryOther);
        spType = findViewById(R.id.spType);
        spCategory = findViewById(R.id.spCategory);
        btnSave = findViewById(R.id.btnSave);

        storage = new TransactionStorage(this);

        setupSpinners();
        setupDatePicker();
        setupAmountFormatting();

        // Nếu intent có Transaction để sửa
        if(getIntent().hasExtra("transaction_id")){
            String id = getIntent().getStringExtra("transaction_id");
            editTransaction = storage.getTransactionById(id);
            if(editTransaction != null) loadTransaction(editTransaction);
        }

        btnSave.setOnClickListener(v -> saveTransaction());
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                this, R.array.transaction_types, android.R.layout.simple_spinner_item
        );
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(typeAdapter);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this, R.array.categories, android.R.layout.simple_spinner_item
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
        etDate.setOnFocusChangeListener((v, hasFocus) -> { if(hasFocus) showDatePicker(); });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            etDate.setText(String.format("%04d-%02d-%02d", year, month+1, dayOfMonth));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void setupAmountFormatting() {
        etAmount.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(isEditingText) return;
                isEditingText = true;

                String digits = s.toString().replaceAll("[^\\d]", "");
                if(!digits.isEmpty()){
                    try{
                        long value = Long.parseLong(digits);
                        String formatted = new DecimalFormat("#,###").format(value) + " VNĐ";
                        etAmount.setText(formatted);
                        etAmount.setSelection(formatted.length() - 4);
                    }catch(NumberFormatException e){
                        etAmount.setText("");
                    }
                }

                isEditingText = false;
            }
        });
    }

    private void loadTransaction(Transaction t){
        etAmount.setText(new DecimalFormat("#,###").format((long)t.getAmount()) + " VNĐ");
        etAmount.setSelection(etAmount.getText().length() - 4);
        etNote.setText(t.getNote());
        etDate.setText(t.getDate());
        spType.setSelection(t.getType().equals("Thu") ? 0 : 1);

        // danh mục
        boolean isOther = true;
        for(int i=0; i<spCategory.getCount(); i++){
            if(spCategory.getItemAtPosition(i).toString().equals(t.getCategory())){
                spCategory.setSelection(i);
                isOther = false;
                break;
            }
        }
        if(isOther){
            spCategory.setSelection(spCategory.getCount()-1); // Khác
            etCategoryOther.setText(t.getCategory());
            etCategoryOther.setVisibility(View.VISIBLE);
        }
    }

    private void saveTransaction(){
        String id = (editTransaction != null) ? editTransaction.getId() : UUID.randomUUID().toString();
        String type = spType.getSelectedItem().toString();
        String rawAmount = etAmount.getText().toString().replace(",", "").replace(" VNĐ", "").trim();
        String date = etDate.getText().toString().trim();
        String note = etNote.getText().toString().trim();

        String category;
        if(spCategory.getSelectedItem().toString().equals("Khác")){
            category = etCategoryOther.getText().toString().trim();
            if(category.isEmpty()){
                etCategoryOther.setError("Vui lòng nhập danh mục");
                etCategoryOther.requestFocus();
                return;
            }
        } else category = spCategory.getSelectedItem().toString();

        if(rawAmount.isEmpty()){
            etAmount.setError("Vui lòng nhập số tiền");
            etAmount.requestFocus();
            return;
        }

        if(date.isEmpty()){
            etDate.setError("Vui lòng chọn ngày");
            etDate.requestFocus();
            return;
        }

        double amount;
        try{ amount = Double.parseDouble(rawAmount); }
        catch(NumberFormatException e){
            etAmount.setError("Số tiền không hợp lệ");
            etAmount.requestFocus();
            return;
        }

        Transaction t = new Transaction(id, type,  date, amount, category, note);
        if(editTransaction != null) storage.editTransaction(t);
        else storage.addTransaction(t);

        Toast.makeText(this, "Giao dịch lưu thành công", Toast.LENGTH_SHORT).show();
        finish();
    }
}