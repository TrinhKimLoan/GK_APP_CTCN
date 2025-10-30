package com.example.myfinance2.model;

import java.util.concurrent.TimeUnit;

public class Goal {
    private String name;
    private double targetAmount;
    private double savedAmount;
    private long dueDate; // stored as epoch millis

    public Goal() {
    }

    public Goal(String name, double targetAmount, double savedAmount, long dueDate) {
        this.name = name;
        this.targetAmount = targetAmount;
        this.savedAmount = savedAmount;
        this.dueDate = dueDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public double getSavedAmount() {
        return savedAmount;
    }

    public void setSavedAmount(double savedAmount) {
        this.savedAmount = savedAmount;
    }

    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public int getProgressPercent() {
        if (targetAmount <= 0) return 0;
        return (int) ((savedAmount / targetAmount) * 100);
    }

    public long getDaysLeft() {
        long now = System.currentTimeMillis();
        long diff = dueDate - now;
        return diff <= 0 ? 0 : TimeUnit.MILLISECONDS.toDays(diff);
    }

    public boolean isOverdue() {
        return System.currentTimeMillis() > dueDate;
    }
}
