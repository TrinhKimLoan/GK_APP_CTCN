package com.example.chitieucanhan.label;

public class Category {
    private int id;
    private String name;
    private String type; // "Thu" hoặc "Chi"
    private int color;   // mã màu

    public Category(int id, String name, String type, int color) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.color = color;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public int getColor() { return color; }

    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setColor(int color) { this.color = color; }
}
