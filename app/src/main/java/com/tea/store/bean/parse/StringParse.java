package com.tea.store.bean.parse;

import com.tea.store.bean.JsonParse;

public class StringParse implements JsonParse<String> {
    @Override
    public String parse(String json) {
        return json;
    }
}
