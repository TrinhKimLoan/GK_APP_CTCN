package com.example.chartctcn;

import android.content.Context;
import android.content.SharedPreferences;

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

    // Xóa hạn mức
    public static void clearBudget(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}