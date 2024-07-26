package com.tea.store.bean;

import android.content.pm.ResolveInfo;

public class LocalApp {
    private ResolveInfo info;

    public LocalApp(ResolveInfo info) {
        this.info = info;
    }

    public ResolveInfo getInfo() {
        return info;
    }
}
