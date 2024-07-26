package com.tea.store.manager;

import android.content.Context;

import com.tea.store.bean.AppItem;

public class FilePathMangaer {

    public static final String getJsonPath(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }

    public static final String getAppDownload(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }

    public static final String getAppDownload(Context context, AppItem bean) {
        return getAppDownload(context) + "/" + getAppName(bean);
    }

    public static final String getAppName(AppItem bean){
        return bean.getAppName()+".apk";
    }
}
