package com.tea.store.handler;

import android.content.Context;

import com.tea.store.BuildConfig;
import com.tea.store.utils.PLog;

import java.lang.Thread.UncaughtExceptionHandler;

public class ExceptionHandler implements UncaughtExceptionHandler {
    private static String TAG = ExceptionHandler.class.getSimpleName();

    private static ExceptionHandler mInstance;
    private UncaughtExceptionHandler mDefaultHandler;
    private Context mContext;

    private ExceptionHandler(Context context) {
        mContext = context;
    }

    public static synchronized ExceptionHandler getInstance(Context context) {
        if (mInstance == null)
            mInstance = new ExceptionHandler(context);
        return mInstance;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        PLog.e(TAG, "EHandler error", ex);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public void init() {
        if (BuildConfig.DEBUG) {
            return;
        }

        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }
}
