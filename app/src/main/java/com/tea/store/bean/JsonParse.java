package com.tea.store.bean;

public interface JsonParse<T> {
    T parse(String json);
}
