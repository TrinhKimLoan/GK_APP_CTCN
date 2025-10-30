package com.example.chitieu;

public class Category {
    private int id;
    private String name;
    private String type; // "Thu" hoặc "Chi"
    private int color;   // ARGB int

    public Category() {}

    public Category(int id, String name, String type, int color) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.color = color;
    }

    // getters / setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getColor() { return color; }
    public void setColor(int color) { this.color = color; }
}
