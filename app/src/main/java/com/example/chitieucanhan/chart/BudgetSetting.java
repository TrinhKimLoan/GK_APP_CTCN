package com.example.chitieucanhan.chart;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

public class BudgetSetting {
    private static final String PREF_NAME = "BudgetPrefs";
    private static final String KEY_TOTAL_BUDGET = "total_budget";

    // Lưu hạn mức chi tiêu
    public static void setTotalBudget(Context context, float amount) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putFloat(KEY_TOTAL_BUDGET, amount).apply();
    }

    // Lấy hạn mức đã lưu
    public static float getTotalBudget(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getFloat(KEY_TOTAL_BUDGET, 0);
    }

    // Kiểm tra và thông báo còn lại bao nhiêu tiền so với hạn mức
    public static boolean checkBudgetStatus(Context context, float totalSpent) {
        float limit = getTotalBudget(context);

        if (limit <= 0) {
            Toast.makeText(context, "Chưa đặt hạn mức chi tiêu!", Toast.LENGTH_SHORT).show();
            return false;
        }

        float remaining = limit - totalSpent;

        if (remaining < 0) {
            Toast.makeText(context,
                    "⚠️ Bạn đã vượt hạn mức " + String.format("%,.0f đ!", -remaining),
                    Toast.LENGTH_LONG).show();
            return true; // đã vượt hạn mức
        } else {
            Toast.makeText(context,
                    "✅ Bạn còn " + String.format("%,.0f đ để chi tiêu!", remaining),
                    Toast.LENGTH_LONG).show();
            return false; // còn trong hạn mức
        }
    }


    // Xóa hạn mức
    public static void clearBudget(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}