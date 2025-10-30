package com.example.chitieucanhan.goal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Goal implements Serializable {
    private String name;
    private long targetAmount;
    private long savedAmount;

    public Goal(String name, long targetAmount, long savedAmount) {
        this.name = name;
        this.targetAmount = targetAmount;
        this.savedAmount = savedAmount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(long targetAmount) {
        this.targetAmount = targetAmount;
    }

    public long getSavedAmount() {
        return savedAmount;
    }

    public void setSavedAmount(long savedAmount) {
        this.savedAmount = savedAmount;
    }

    public double getProgressPercent() {
        if (targetAmount <= 0) return 0;
        return Math.min(100.0, (savedAmount * 100.0) / targetAmount);
    }

    public JSONObject toJson() {
        JSONObject o = new JSONObject();
        try {
            o.put("name", name);
            o.put("targetAmount", targetAmount);
            o.put("savedAmount", savedAmount);
        } catch (JSONException ignored) {
        }
        return o;
    }

    public static Goal fromJson(JSONObject o) {
        try {
            String name = o.optString("name", "");
            long target = o.optLong("targetAmount", 0);
            long saved = o.optLong("savedAmount", 0);
            return new Goal(name, target, saved);
        } catch (Exception e) {
            return null;
        }
    }
}
