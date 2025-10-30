package com.example.myfinance2.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.widget.EditText;

import androidx.annotation.NonNull;

public class TransferDialog {
    public interface Callback {
        void onAmountEntered(double amount);
    }

    public static void show(@NonNull Context ctx, Callback cb) {
        EditText input = new EditText(ctx);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        new AlertDialog.Builder(ctx)
                .setTitle("Chuyển tiền vào mục tiêu")
                .setView(input)
                .setPositiveButton("Chuyển", (d, w) -> {
                    String s = input.getText().toString();
                    try {
                        double amount = Double.parseDouble(s);
                        cb.onAmountEntered(amount);
                    } catch (Exception e) {
                        // ignore invalid
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
