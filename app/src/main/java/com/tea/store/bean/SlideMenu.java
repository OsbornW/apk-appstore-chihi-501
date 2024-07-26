package com.tea.store.bean;

public class SlideMenu {
    private String name;
    private int type;
    private int icon;

    public SlideMenu(String name, int type, int icon) {
        this.name = name;
        this.type = type;
        this.icon = icon;
    }

    public int getIcon() {
        return icon;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
