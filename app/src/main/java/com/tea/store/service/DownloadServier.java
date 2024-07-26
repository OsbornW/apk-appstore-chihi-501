package com.tea.store.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInstaller;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.tea.store.bean.AppItem;
import com.tea.store.bean.MyRunnable;
import com.tea.store.config.Config;
import com.tea.store.manager.FilePathMangaer;
import com.tea.store.manager.InstallManager;
import com.tea.store.manager.ServiceBinderWrapper;
import com.tea.store.utils.AppUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Response;

public class DownloadServier extends Service implements InstallManager.Callback {
    private final Gson gson = new Gson();
    private final ExecutorService exec = Executors.newCachedThreadPool();
    private final List<AppItem> DOWNLOADS = new CopyOnWriteArrayList<>();
    private boolean isSkipDonwload;
    private MyRunnable downloadRunnable;
    private MyRunnable installRunnable;
    private Handler uiHandler;
    private InstallManager installManager;
    private ServiceBinderWrapper binderWrapper;
    private InnerBroadcast receiver;

    @Override
    public void onCreate() {
        super.onCreate();
        binderWrapper = new ServiceBinderWrapper(this);
        installManager = new InstallManager(this);
        uiHandler = new Handler(getMainLooper());
        if (!Config.IS_SYSTEM_UID) registBroadcast();

        downloadRunnable();
    }

    private void registBroadcast(){
        receiver = new InnerBroadcast();
        IntentFilter filter = new IntentFilter();

        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addAction(Intent.ACTION_UNINSTALL_PACKAGE);
        filter.addDataScheme("package");
        registerReceiver(receiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binderWrapper;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        if (receiver != null) unregisterReceiver(receiver);
        exec.shutdownNow();
    }

    private void downloadRunnable(){
        if (downloadRunnable != null) downloadRunnable.interrupt();
        downloadRunnable = new MyRunnable() {
            @Override
            public void run() {
                while (!isInterrupt()){
                    if (isSkipDonwload || DOWNLOADS.isEmpty()) continue;
                    AppItem item = null;
                    for (AppItem child : DOWNLOADS){
                        if (child != null && child.getStatus() == AppItem.STATU_IDLE){
                            item = child;
                            break;
                        }
                    }

                    if (item != null){
                        downloadApk(item);
                    }
                    SystemClock.sleep(1000);
                }
            }
        };
        exec.execute(downloadRunnable);
    }

    private void downloadApk(final AppItem bean){
        isSkipDonwload = true;
        bean.setStatus(AppItem.STATU_DOWNLOADING);
        OkGo.getInstance().get(bean.getAppDownLink()).tag(bean.getAppDownLink()).execute(new FileCallback(FilePathMangaer.getAppDownload(this), FilePathMangaer.getAppName(bean)) {
            @Override
            public void onError(okhttp3.Call call, Response response, Exception e) {
                bean.setStatus(AppItem.STATU_DOWNLOAD_FAIL);
                isSkipDonwload = false;
            }

            @Override
            public void onSuccess(File file, okhttp3.Call call, Response response) {
                bean.setStatus(AppItem.STATU_DOWNLOAD_SUCCESS);
                bean.setProgress(1f);
                silentInstall(bean, file);
            }

            @Override
            public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                bean.setStatus(AppItem.STATU_DOWNLOADING);
                bean.setProgress(progress);
            }

            @Override
            public void onCacheError(Call call, Exception e) {
                super.onCacheError(call, e);
                bean.setStatus(AppItem.STATU_DOWNLOAD_FAIL);
                isSkipDonwload = false;
            }

            @Override
            public void parseError(Call call, Exception e) {
                super.parseError(call, e);
                bean.setStatus(AppItem.STATU_DOWNLOAD_FAIL);
                isSkipDonwload = false;
            }
        });
    }

    public void push(AppItem bean){
        bean.setStatus(AppItem.STATU_IDLE);
        bean.setProgress(0);
        DOWNLOADS.add(bean);
    }

    public boolean retryDownload(AppItem bean){
        for (AppItem item : DOWNLOADS){
            if (item.getAppDownLink().equals(bean.getAppDownLink()) && item.getStatus() != AppItem.STATU_INSTALLING){
                item.setProgress(0);
                item.setStatus(AppItem.STATU_IDLE);
                return true;
            }
        }
        return false;
    }

    public void remove(AppItem bean){
        List<AppItem> items = new ArrayList<>();
        for (AppItem item : DOWNLOADS){
            if (item.getAppDownLink().equals(bean.getAppDownLink())){
                items.add(item);
                if (item.getStatus() == AppItem.STATU_DOWNLOADING){
                    OkGo.getInstance().cancelTag(item.getAppDownLink());
                }
            }
        }
        DOWNLOADS.removeAll(items);
    }

    public AppItem findItem(AppItem bean){
        for (AppItem item : DOWNLOADS){
            if (item.getAppDownLink().equals(bean.getAppDownLink())){
                return item;
            }
        }
        return null;
    }

    public List<AppItem> getDownloads() {
        return DOWNLOADS;
    }

    private void silentInstall(AppItem bean, final File file){

        if (!Config.IS_SYSTEM_UID){
            AppUtils.installApk(this, file.getAbsolutePath());
            retryDownload();
        }else {
            bean.setStatus(AppItem.STATU_INSTALLING);
            if (installRunnable != null) installRunnable.interrupt();
            installRunnable = new MyRunnable() {
                @Override
                public void run() {
                    try {
                        installManager.installPackage(DownloadServier.this, bean, file, uiHandler);
                    }catch (Exception e){
                        isSkipDonwload = false;
                        bean.setStatus(AppItem.STATU_INSTALL_FAIL);
                        e.printStackTrace();
                    }finally {
                        if (file.exists()) file.delete();
                    }
                }
            };
            exec.execute(installRunnable);
        }
    }

    public void retryDownload(){
        isSkipDonwload = false;
    }

    @Override
    public void onFinal() {
        retryDownload();
    }

    public class InnerBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Intent.ACTION_PACKAGE_ADDED:
                case Intent.ACTION_PACKAGE_REPLACED:
                    if (intent != null && intent.getData() != null){
                        String packageName = intent.getData().getSchemeSpecificPart();
                        if (!TextUtils.isEmpty(packageName)){
                            for (AppItem item : DOWNLOADS){
                                if (item.getPackageName().equals(packageName)){
                                    File file = new File(FilePathMangaer.getAppDownload(context, item));
                                    if (file.exists()) file.delete();
                                    item.setStatus(AppItem.STATU_INSTALL_SUCCESS);
                                }
                            }
                        }
                    }
                    break;
                case Intent.ACTION_PACKAGE_REMOVED:
                case Intent.ACTION_UNINSTALL_PACKAGE:
                    break;
            }
        }
    }
}
