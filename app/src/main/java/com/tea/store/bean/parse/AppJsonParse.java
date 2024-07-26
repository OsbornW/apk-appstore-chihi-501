package com.tea.store.bean.parse;

import com.google.gson.Gson;
import com.tea.store.bean.JsonParse;
import com.tea.store.http.response.AppListResponse;

public class AppJsonParse implements JsonParse<AppListResponse> {
    private final Gson gson = new Gson();

    @Override
    public AppListResponse parse(String json) {
        return gson.fromJson(json, AppListResponse.class);
    }
}
