package com.tea.store.bean;

public class HomeNetConfig {
    private final String userId;
    private final String appColumnId;
    private final String tag;
    private final int index;

    public HomeNetConfig(String userId, String appColumnId, String tag, int index) {
        this.userId = userId;
        this.appColumnId = appColumnId;
        this.tag = tag;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String getAppColumnId() {
        return appColumnId;
    }

    public String getTag() {
        return tag;
    }

    public String getUserId() {
        return userId;
    }
}
