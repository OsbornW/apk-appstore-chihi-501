package com.tea.store.bean;

public class CategoryItem {
    private int id;
    private String columnIcon;
    private String columnName;
    private int color;

    public CategoryItem(int id, String columnIcon, String columnName, int color) {
        this.id = id;
        this.columnIcon = columnIcon;
        this.columnName = columnName;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public String getColumnIcon() {
        return columnIcon;
    }

    public String getColumnName() {
        return columnName;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
