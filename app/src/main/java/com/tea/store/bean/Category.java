package com.tea.store.bean;

import java.util.List;

public class Category {
    private String title;
    private List<AppItem> list;

    public Category(String title, List<AppItem> list) {
        this.title = title;
        this.list = list;
    }

    public String getTitle() {
        return title;
    }

    public List<AppItem> getList() {
        return list;
    }
}
