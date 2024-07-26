package com.tea.store.config;

import com.tea.store.BuildConfig;

public class Config {
    public static boolean IS_SYSTEM_UID = true;
    public static String UPDATE_PACKAGE_NAME = "com.tea.launcher.upgrade";
    public static String UPDATE_CLASS_NAME = "com.tea.launcher.upgrade.UpgradeActivity";

    public static String LAUNCHER_PACKAGE_NAME = "com.tea.launcher";
    public static String LAUNCHER_CLASS_NAME = "com.tea.launcher.ui.activity.WifiListActivity";
    public static String LAUNCHER_NOTIFY_ACTIVITY = "com.tea.launcher.ui.activity.NotifyActivity";
    public static final String APPID = BuildConfig.APP_ID;
    public static final String USER_ID = BuildConfig.USER_ID;
    public static final float LOST = 0.75F;

    public static final String CHANNEL = BuildConfig.CHANNEL;
    public static final String CHIHI_TYPE = BuildConfig.CHIHI_TYPE;
    public static final String MODEL = BuildConfig.MODEL;
}
