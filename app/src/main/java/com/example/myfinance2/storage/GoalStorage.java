package com.example.myfinance2.storage;

import android.content.Context;

import com.example.myfinance2.model.Goal;
import com.example.myfinance2.utils.FileUtils;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GoalStorage {
    private static final String GOALS_FILE = "goals.json";
    private static final String TOTAL_FILE = "TotalBalance.json";

    public static List<Goal> getAllGoals(Context context) {
        Type type = new TypeToken<List<Goal>>() {}.getType();
        return FileUtils.readListFromJson(context, GOALS_FILE, type);
    }

    public static void addGoal(Context context, Goal goal) {
        List<Goal> goals = getAllGoals(context);
        goals.add(goal);
        Type type = new TypeToken<List<Goal>>() {}.getType();
        FileUtils.writeListToJson(context, GOALS_FILE, goals);
    }

    public static void updateGoal(Context context, Goal goal, int index) {
        List<Goal> goals = getAllGoals(context);
        if (index >= 0 && index < goals.size()) {
            goals.set(index, goal);
            Type type = new TypeToken<List<Goal>>() {}.getType();
            FileUtils.writeListToJson(context, GOALS_FILE, goals);
        }
    }

    public static void deleteGoal(Context context, int index) {
        List<Goal> goals = getAllGoals(context);
        if (index >= 0 && index < goals.size()) {
            goals.remove(index);
            Type type = new TypeToken<List<Goal>>() {}.getType();
            FileUtils.writeListToJson(context, GOALS_FILE, goals);
        }
    }

    /**
     * Transfer amount from TotalBalance.json to the given goal (by name match).
     * This method will deduct amount from total and add to the goal's savedAmount (capped by targetAmount).
     * If insufficient total balance, it will throw IllegalArgumentException.
     */
    public static void transferToGoal(Context context, double amount, Goal goal) {
        if (amount <= 0) return;

        // Read total balance stored as a single-element list (if any)
        Type doubleListType = new TypeToken<List<Double>>() {}.getType();
        List<Double> totals = FileUtils.readListFromJson(context, TOTAL_FILE, doubleListType);
        double total = 0.0;
        if (totals.size() > 0) total = totals.get(0);

        if (amount > total) {
            throw new IllegalArgumentException("Insufficient total balance to transfer.");
        }

        // Find matching goal by name and update
        List<Goal> goals = getAllGoals(context);
        int foundIndex = -1;
        for (int i = 0; i < goals.size(); i++) {
            Goal g = goals.get(i);
            if (g.getName() != null && g.getName().equals(goal.getName())) {
                foundIndex = i;
                break;
            }
        }

        if (foundIndex == -1) {
            // If not found, add as new goal with given saved amount (but still deduct total)
            double toAdd = Math.min(amount, goal.getTargetAmount() - goal.getSavedAmount());
            goal.setSavedAmount(goal.getSavedAmount() + toAdd);
            goals.add(goal);
        } else {
            Goal g = goals.get(foundIndex);
            double canAdd = g.getTargetAmount() - g.getSavedAmount();
            double toAdd = Math.min(canAdd, amount);
            g.setSavedAmount(g.getSavedAmount() + toAdd);
            goals.set(foundIndex, g);
        }

        // Deduct from total and write back
        double newTotal = total - amount;
        List<Double> newTotals = new ArrayList<>();
        newTotals.add(newTotal);
        FileUtils.writeListToJson(context, TOTAL_FILE, newTotals);

        // Persist updated goals
        Type type = new TypeToken<List<Goal>>() {}.getType();
        FileUtils.writeListToJson(context, GOALS_FILE, goals);
    }
}
