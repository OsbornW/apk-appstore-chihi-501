package com.tea.store.utils;

import android.app.role.RoleManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.role.RoleManagerCompat;

import com.google.gson.Gson;
import com.tea.store.BuildConfig;
import com.tea.store.R;
import com.tea.store.bean.Version;
import com.tea.store.config.Config;
import com.tea.store.enums.Atts;
import com.tea.store.ui.activity.MainActivity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AndroidSystem {

    public static PackageInfo findPackageInfo(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageList = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);

        for (PackageInfo packageInfo : packageList) {
            if (packageName.equals(packageInfo.packageName)) {
                return packageInfo;
            }
        }
        return null;
    }


    public static Intent getPackageNameIntent(Context context, String packageName){
        PackageManager pm = context.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(packageName);
        if (intent == null) intent = pm.getLeanbackLaunchIntentForPackage(packageName);
        return intent;
    }

    public static boolean openPackageName(Context context, String packageName){
        Intent intent = getPackageNameIntent(context, packageName);
        if (intent == null){
            return false;
        }else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        }
    }

    public static void blur(Context context, View dectorView, View root, ImageView blur) {
        dectorView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(dectorView.getDrawingCache());
        dectorView.setDrawingCacheEnabled(false);
        root.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams lp = blur.getLayoutParams();
                lp.width = root.getMeasuredWidth();
                lp.height = root.getMeasuredHeight();
                Bitmap bit = Bitmap.createBitmap(lp.width, lp.height, Bitmap.Config.RGB_565);
                Rect rect = new Rect();
                root.getGlobalVisibleRect(rect);
                blur.setLayoutParams(lp);
                Canvas canvas = new Canvas(bit);
                canvas.drawBitmap(bitmap, rect, new Rect(0, 0, bit.getWidth(), bit.getHeight()), new Paint());
                canvas.drawARGB(95, 168, 168, 168);
                //bit = Bitmap.createScaledBitmap(bit, (int) (lp.width * 0.7f), (int) (lp.height * 0.7f), false);
                GlideUtils.bindBlur(context, blur, bit, 12, 8);
            }
        });
    }

    public static ResolveInfo findResolveInfoByName(Context context, String name) {
        List<ResolveInfo> infos = AndroidSystem.queryCategoryLauncher(context);
        for (ResolveInfo info : infos) {
            if (info.activityInfo.name.equals(name) || info.activityInfo.packageName.equals(name)) {
                return info;
            }
        }
        return null;
    }

    public static void jumpUpgrade(Context context, Version version) {
        try {
            version.setAppName(context.getString(R.string.app_name));
            version.setActivity(MainActivity.class.getName());
            version.setPackageName(BuildConfig.APPLICATION_ID);
            Gson gson = new Gson();
            PackageInfo info = findPackageInfo(context, Config.UPDATE_PACKAGE_NAME);
            if (info != null) {
                Intent intent = new Intent(Intent.ACTION_MAIN).setClassName(Config.UPDATE_PACKAGE_NAME, Config.UPDATE_CLASS_NAME);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra(Atts.BEAN, gson.toJson(version));
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void requestInstallApk(Context context) {
        Uri packageURI = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static List<ResolveInfo> queryCategoryLauncher(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);

        PackageManager packageManager = context.getPackageManager();
        return packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL);
    }

    public static List<PackageInfo> queryInstalledApps(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> infos = packageManager.getInstalledPackages(0);
        return infos;
    }

    public static void openActivityInfo(Context context, ActivityInfo info) {
        Intent intent = new Intent(Intent.ACTION_MAIN)
                .setClassName(info.applicationInfo.packageName, info.name)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static boolean openActivityName(Context context, String name) {
        boolean success = false;
        List<ResolveInfo> infos = AndroidSystem.queryCategoryLauncher(context);
        for (ResolveInfo info : infos) {
            if (info.activityInfo.name.equals(name)) {
                AndroidSystem.openActivityInfo(context, info.activityInfo);
                success = true;
                break;
            }
        }
        return success;
    }

    public static boolean openActivityNames(Context context, String[] names) {
        boolean success = false;
        List<ResolveInfo> infos = AndroidSystem.queryCategoryLauncher(context);
        outside:
        for (String name : names) {
            for (ResolveInfo info : infos) {
                if (info.activityInfo.name.equals(name)) {
                    AndroidSystem.openActivityInfo(context, info.activityInfo);
                    success = true;
                    break outside;
                }
            }
        }
        return success;
    }

    public static void openApplicationDetials(Context context, ActivityInfo info) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.fromParts("package", info.packageName, null))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void openWifiSetting(Context context) {
        List<ResolveInfo> infos = queryCategoryLauncher(context);
        for (ResolveInfo info : infos) {
            if (info.activityInfo.name.equals(Config.LAUNCHER_PACKAGE_NAME)) {
                Intent intent = new Intent(Intent.ACTION_MAIN)
                        .setClassName(info.activityInfo.applicationInfo.packageName, Config.LAUNCHER_CLASS_NAME);
                context.startActivity(intent);
                return;
            }
        }

        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        context.startActivity(intent);
    }

    public static void openLauncherNotify(Context context) {
        List<ResolveInfo> infos = queryCategoryLauncher(context);
        for (ResolveInfo info : infos) {
            if (info.activityInfo.name.equals(Config.LAUNCHER_NOTIFY_ACTIVITY)) {
                Intent intent = new Intent(Intent.ACTION_MAIN)
                        .setClassName(info.activityInfo.applicationInfo.packageName, Config.LAUNCHER_NOTIFY_ACTIVITY);
                context.startActivity(intent);
                return;
            }
        }
    }

    public static void openBluSetting(Context context) {
        Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        context.startActivity(intent);
    }

    public static void openWirelessSetting(Context context) {
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        context.startActivity(intent);
    }

    public static boolean setDefaultLauncher(Context context, ActivityResultLauncher launcher) {
        boolean success = false;
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                RoleManager manager = context.getSystemService(RoleManager.class);
                launcher.launch(manager.createRequestRoleIntent(RoleManagerCompat.ROLE_HOME));
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS);
                context.startActivity(intent);
            }
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return success;
        }
    }

    public static void openDateSetting(Context context) {
        Intent intent = new Intent(Settings.ACTION_DATE_SETTINGS);
        context.startActivity(intent);
    }

    public static void openInputSetting(Context context) {
        Intent intent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
        context.startActivity(intent);
    }

    public static boolean openLocalSetting(Context context) {
        Intent intent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        return true;
    }

    public static boolean openSystemSetting(Context context) {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        context.startActivity(intent);
        return true;
    }

    public static Bitmap getImageFromAssetsFile(Context context, String filePath) {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(filePath);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public static String[] getAssetsFileNames(Context context, String filePath) {
        String[] array = null;
        try {
            array = context.getAssets().list(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            return array;
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnectedOrConnecting();

        return isNetworkConnected;
    }

    public static boolean jumpYouTube(Context context, String url) {
        boolean success = false;
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> infos = queryCategoryLauncher(context);
        for (ResolveInfo resolveInfo : infos) {
            if ("YouTube".equals(resolveInfo.activityInfo.loadLabel(pm))) {
                ActivityInfo info = resolveInfo.activityInfo;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        .setClassName(info.applicationInfo.packageName, info.name)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                success = true;
                break;
            }
        }
        return success;
    }

    public static boolean jumpNetflix(Context context, String url) {
        boolean success = false;
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> infos = queryCategoryLauncher(context);
        for (ResolveInfo resolveInfo : infos) {
            if ("Netflix".equals(resolveInfo.activityInfo.loadLabel(pm))) {
                ActivityInfo info = resolveInfo.activityInfo;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        .setClassName(info.applicationInfo.packageName, info.name)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                success = true;
                break;
            }
        }
        return success;
    }

    public static boolean jumpDisney(Context context, String url) {
        boolean success = false;
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> infos = queryCategoryLauncher(context);
        for (ResolveInfo resolveInfo : infos) {
            if ("Disney+".equals(resolveInfo.activityInfo.loadLabel(pm))) {
                ActivityInfo info = resolveInfo.activityInfo;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        .setClassName(info.applicationInfo.packageName, info.name)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                success = true;
                break;
            }
        }

        return success;
    }

    public static boolean jumpAppStore(Context context) {
        boolean success = false;
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> infos = getAppStores(context);
        for (ResolveInfo resolveInfo : infos) {
            ActivityInfo info = resolveInfo.activityInfo;
            Intent intent = new Intent(Intent.ACTION_VIEW)
                    .setClassName(info.applicationInfo.packageName, info.name)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            success = true;
            break;
        }

        return success;
    }

    public static List<ResolveInfo> getAppStores(Context context) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_APP_MARKET);

        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
        return infos;
    }

    public static void cleanFocus(View rootView) {
        View v = rootView.findFocus();
        if (v != null) {
            v.clearFocus();
        }
    }

    public static boolean isSystemApp(PackageInfo info) {
        boolean isSysApp = (info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1;
        boolean isSysUpd = (info.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1;
        return isSysApp | isSysUpd;
    }

    public static boolean isSystemApp(ResolveInfo info) {
        boolean isSysApp = (info.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1;
        boolean isSysUpd = (info.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1;
        return isSysApp || isSysUpd;
    }

    public static boolean openProjectorSetting(Context context) {
        return AndroidSystem.openActivityNames(context, new String[]{
                "com.cptp.console.MainActivity",
                "com.softwinner.tcorrection.MainActivity",
        });
    }

    public static boolean openProjectorHDMI(Context context) {
        boolean success = AndroidSystem.openActivityNames(context, new String[]{
                "com.android.rockchip.camera2.RockchipCamera2",
        });

        if (!success) {
            Intent intent = new Intent();
            intent.setClassName("com.android.tv", "com.android.tv.MainActivity");
            context.startActivity(intent);
            success = true;
        }
        return success;
    }

    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    public static List<ResolveInfo> getUserApps(Context context) {
        List<ResolveInfo> infos = AndroidSystem.queryCategoryLauncher(context);
        LauncherApps apps = (LauncherApps) context.getSystemService(Context.LAUNCHER_APPS_SERVICE);
        List<LauncherActivityInfo> launchers = apps.getActivityList(null, android.os.Process.myUserHandle());

        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> result = new ArrayList<>();
        for (LauncherActivityInfo launcher : launchers) {
            for (ResolveInfo info : infos) {
                if (info.activityInfo.applicationInfo.packageName.equals(launcher.getApplicationInfo().packageName)) {
                    if (info.activityInfo.loadBanner(pm) != null) {
                        result.add(0, info);
                    } else {
                        result.add(info);
                    }
                    break;
                }
            }
        }
        return result;
    }
}
