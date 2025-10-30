package com.example.chitieucanhan.goal;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class GoalStorage {
    private static final String FILE_NAME = "goals.json";
    private static final String PREF_TOTAL = "total_balance";

    public static List<Goal> loadGoals(Context context) {
        List<Goal> list = new ArrayList<>();
        try (FileInputStream fis = context.openFileInput(FILE_NAME)) {
            byte[] b = new byte[fis.available()];
            fis.read(b);
            String s = new String(b);
            JSONArray arr = new JSONArray(s);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.optJSONObject(i);
                Goal g = Goal.fromJson(o);
                if (g != null) list.add(g);
            }
        } catch (Exception ignored) {
        }
        return list;
    }

    public static void saveGoals(Context context, List<Goal> goals) {
        try (FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)) {
            JSONArray arr = new JSONArray();
            for (Goal g : goals) arr.put(g.toJson());
            fos.write(arr.toString().getBytes());
        } catch (Exception ignored) {
        }
    }

    public static void addGoal(Context context, Goal goal) {
        List<Goal> list = loadGoals(context);
        list.add(goal);
        saveGoals(context, list);
    }

    public static void updateGoal(Context context, int index, Goal goal) {
        List<Goal> list = loadGoals(context);
        if (index >= 0 && index < list.size()) {
            list.set(index, goal);
            saveGoals(context, list);
        }
    }

    public static void transferToGoal(Context context, int index, long amount) {
        List<Goal> list = loadGoals(context);
        if (index >= 0 && index < list.size()) {
            Goal g = list.get(index);
            long newSaved = g.getSavedAmount() + amount;
            g.setSavedAmount(newSaved);
            list.set(index, g);
            saveGoals(context, list);
            subtractFromTotal(context, amount);
        }
    }

    // Subtract amount from a stored total_balance in shared prefs if present
    public static void subtractFromTotal(Context context, long amount) {
        try {
            android.content.SharedPreferences p = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
            long total = p.getLong(PREF_TOTAL, 0L);
            total = Math.max(0L, total - amount);
            p.edit().putLong(PREF_TOTAL, total).apply();
        } catch (Exception ignored) {
        }
    }
}