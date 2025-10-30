package com.example.chitieucanhan.transaction;
public class Transaction {
    private String id; // UUID
    private String type; // "Thu" hoặc "Chi"
    private String category;
    private double amount;
    private String date; // "yyyy-MM-dd"
    private String note;


    public Transaction(String id, String type, String category, double amount, String date, String note) {
        this.id = id;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.note = note;
    }

    // Constructor cho thêm mới
    public Transaction(String type, String category, double amount, String date, String note) {
        this(java.util.UUID.randomUUID().toString(), type, category, amount, date, note);
    }

    // Getters & Setters
    public String getId() { return id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}