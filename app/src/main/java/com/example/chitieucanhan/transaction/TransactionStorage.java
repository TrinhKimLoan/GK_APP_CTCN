package com.example.chitieucanhan.transaction;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionStorage {
    private static final String FILE_NAME = "transactions.json";
    private Context context;

    // Constructor để giữ context
    public TransactionStorage(Context context) {
        this.context = context;
    }

    // Đọc tất cả giao dịch
    public List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        try {
            FileInputStream fis = context.openFileInput(FILE_NAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            JSONArray arr = new JSONArray(sb.toString());
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                Transaction t = new Transaction(
                        obj.getString("id"),
                        obj.getString("type"),
                        obj.getString("category"),
                        obj.getDouble("amount"),
                        obj.getString("date"),
                        obj.getString("note")
                );
                list.add(t);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            // File chưa tồn tại thì trả về danh sách rỗng
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lưu danh sách giao dịch
    private void saveAllTransactions(List<Transaction> list) {
        try {
            JSONArray arr = new JSONArray();
            for (Transaction t : list) {
                JSONObject obj = new JSONObject();
                obj.put("id", t.getId());
                obj.put("type", t.getType());
                obj.put("category", t.getCategory());
                obj.put("date", t.getDate());
                obj.put("amount", t.getAmount());
                obj.put("note", t.getNote());
                arr.put(obj);
            }
            FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            fos.write(arr.toString().getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Thêm giao dịch mới
    public void addTransaction(Transaction t) {
        List<Transaction> list = getAllTransactions();
        list.add(t);
        saveAllTransactions(list);
    }

    // Sửa giao dịch
    public void editTransaction(Transaction t) {
        List<Transaction> list = getAllTransactions();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(t.getId())) {
                list.set(i, t);
                break;
            }
        }
        saveAllTransactions(list);
    }

    // Xóa giao dịch
    public void deleteTransaction(String id) {
        List<Transaction> list = getAllTransactions();
        list.removeIf(t -> t.getId().equals(id));
        saveAllTransactions(list);
    }

    // Lấy giao dịch theo ID
    public Transaction getTransactionById(String id) {
        List<Transaction> list = getAllTransactions();
        for (Transaction t : list) {
            if (t.getId().equals(id)) return t;
        }
        return null;
    }
}