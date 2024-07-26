package com.tea.store;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.tea.store.bean.AppItem;
import com.tea.store.bean.CategoryItem;
import com.tea.store.bean.MyRunnable;
import com.tea.store.handler.ExceptionHandler;
import com.tea.store.http.HttpRequest;
import com.tea.store.manager.InstallManager;
import com.tea.store.service.DownloadServier;
import com.tea.store.utils.PLog;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Response;

public class App extends Application {
    private static String TAG = App.class.getSimpleName();
    public static final Map<String, List<AppItem>> HOME_MAP = new ConcurrentHashMap<>();
    public static final List<CategoryItem> CATEGORYS = new CopyOnWriteArrayList<>();

    private static App mApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        HttpRequest.init(this);
        OkGo.init(this);
        PLog.i(TAG, "app store app onCreate ...");
        startService(new Intent(this, DownloadServier.class));
        ExceptionHandler.getInstance(this).init();
    }

    public static Context getContext() {
        return mApp.getApplicationContext();
    }
}
