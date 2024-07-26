package com.tea.store.ui.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.widget.HorizontalGridView;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.tea.store.R;
import com.tea.store.adapter.BannerAdapter;
import com.tea.store.bean.AppItem;
import com.tea.store.bean.MyRunnable;
import com.tea.store.bean.UDPMessage;
import com.tea.store.config.Config;
import com.tea.store.enums.Atts;
import com.tea.store.enums.IntentAction;
import com.tea.store.handler.PermissionHandler;
import com.tea.store.http.HttpRequest;
import com.tea.store.http.ServiceRequest;
import com.tea.store.http.response.AppListResponse;
import com.tea.store.manager.FilePathMangaer;
import com.tea.store.manager.ServiceBinderWrapper;
import com.tea.store.manager.UDPClient;
import com.tea.store.service.DownloadServier;
import com.tea.store.ui.dialog.DownloadDialog;
import com.tea.store.ui.dialog.ToastDialog;
import com.tea.store.utils.AndroidSystem;
import com.tea.store.utils.AppUtils;
import com.tea.store.utils.GlideUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Response;

public class AppDetialFragment extends AbsFragment implements View.OnClickListener, ServiceConnection {
    private final ExecutorService exec = Executors.newCachedThreadPool();
    private HorizontalGridView mContentGrid;
    private ImageView mIconView;
    private TextView mTitleView;
    private TextView mPKView;
    private TextView mSizeView;
    private TextView mStartView;
    private TextView mDescView;
    private ProgressBar mProgressBar;
    private TextView mProgressTextView;
    private TextView mDonwloadView;
    private View mDivProgress;
    private View mMaskView;
    private View mLoadingView;
    private View mDivContent;
    private View mLoadingIne;

    private BannerAdapter mAdapter;
    private AppItem item;
    private Handler uiHandler;
    private long taskId = -1;
    private String savePath;
    private String basePath;
    private String fileName;
    private String[] pns;
    private int type = 0;
    private boolean isInstalled = false;
    private ActivityResultLauncher baseLauncher;
    private InnerReceiver receiver;
    private boolean isFull = false;
    private MyRunnable timeRunnable;
    private TextView mOptView;
    private int count = 0;
    private int maxItem;
    private boolean isShowDialog = false;

    private Intent intentService;
    private DownloadServier servier;

    public static AppDetialFragment newInstance(AppItem bean, String[] packageName) {

        Bundle args = new Bundle();
        args.putSerializable(Atts.BEAN, bean);
        args.putStringArray(Atts.PACKAGE_NAME, packageName);
        AppDetialFragment fragment = new AppDetialFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intentService = new Intent(getActivity(), DownloadServier.class);
        pns = getArguments().getStringArray(Atts.PACKAGE_NAME);
        item = (AppItem) getArguments().getSerializable(Atts.BEAN);
        uiHandler = new Handler();
        initLauncher();

        receiver = new InnerReceiver();
        IntentFilter filter = new IntentFilter();

        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addAction(Intent.ACTION_UNINSTALL_PACKAGE);
        filter.addDataScheme("package");
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        exec.shutdownNow();
        getActivity().unbindService(this);
        OkGo.getInstance().cancelTag(this);
        if (timeRunnable != null) timeRunnable.interrupt();
        if (receiver != null) getActivity().unregisterReceiver(receiver);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_app_detial;
    }

    @Override
    protected void init(View view, LayoutInflater inflater) {
        super.init(view, inflater);
        mContentGrid = view.findViewById(R.id.banner);
        mIconView = view.findViewById(R.id.icon);
        mTitleView = view.findViewById(R.id.title);
        mPKView = view.findViewById(R.id.pk);
        mSizeView = view.findViewById(R.id.size);
        mStartView = view.findViewById(R.id.start);
        mDescView = view.findViewById(R.id.desc);
        mProgressBar = view.findViewById(R.id.progressBar);
        mProgressTextView = view.findViewById(R.id.mes);
        mDonwloadView = view.findViewById(R.id.donwload);
        mDivProgress = view.findViewById(R.id.div_progress);
        mMaskView  = view.findViewById(R.id.masked);
        mLoadingView = view.findViewById(R.id.loading);
        mDivContent = view.findViewById(R.id.div_content);
        mLoadingIne = view.findViewById(R.id.loading_intercept);
        mOptView = view.findViewById(R.id.opt);

        mAdapter = new BannerAdapter(getActivity(), inflater, new ArrayList<>());
    }

    @Override
    protected void initBefore(View view, LayoutInflater inflater) {
        super.initBefore(view, inflater);
        mDonwloadView.setOnClickListener(this);
        mOptView.setOnClickListener(this);
    }

    @Override
    protected void initBind(View view, LayoutInflater inflater) {
        super.initBind(view, inflater);
        if (item != null){
            setContent();
        }else if (pns != null){
            maxItem = pns.length;
            for (String item : pns){
                search(item, 1);
            }
        }
        getActivity().bindService(intentService, this, Context.BIND_AUTO_CREATE);
    }

    private void runTime(){
        if (timeRunnable != null) timeRunnable.interrupt();
        timeRunnable = new MyRunnable() {
            @Override
            public void run() {
                while (!isInterrupt()){
                    if (item == null || servier == null) continue;
                    SystemClock.sleep(200);
                    AppItem bean = servier.findItem(item);
                    if (bean != null && !isInstalled) upset(bean, false);
                }
            }
        };
        exec.execute(timeRunnable);
    }

    private void search(String word, int page) {
        mLoadingView.setVisibility(View.VISIBLE);
        mMaskView.setVisibility(View.GONE);
        mDivContent.setVisibility(View.GONE);
        HttpRequest.getAppList(new ServiceRequest.Callback<AppListResponse>() {
            @Override
            public void onCallback(retrofit2.Call call, int status, AppListResponse result) {
                if (!isAdded() || call.isCanceled() || isFull) return;
                mLoadingView.setVisibility(View.GONE);
                if (result == null || result.getResult() == null || result.getResult().getAppList() == null || result.getResult().getAppList().isEmpty()) {
                    count ++;
                    if (count >= maxItem){
                        mDivContent.setVisibility(View.GONE);
                        mMaskView.setVisibility(View.VISIBLE);
                    }
                    return;
                }
                isFull = true;
                mMaskView.setVisibility(View.GONE);
                mLoadingView.setVisibility(View.GONE);
                mDivContent.setVisibility(View.VISIBLE);
                item = result.getResult().getAppList().get(0);
                syncMessage();
                setContent();
            }
        }, Config.USER_ID, null, null, word, page, 1);
    }

    private void setContent(){
        isInstalled = isInstalled();
        if (isInstalled) mLoadingIne.setVisibility(View.GONE);
        fileName = String.format("%s.apk", item.getAppName().replaceAll(" ", "_"));
        basePath = FilePathMangaer.getAppDownload(getActivity());
        savePath = basePath + "/" + fileName;

        mLoadingView.setVisibility(View.GONE);
        mMaskView.setVisibility(View.GONE);
        mDivContent.setVisibility(View.VISIBLE);
        String[] banners = item.getAppImg1() == null ? new String[0] : item.getAppImg1().split(",");
        mAdapter.replace(Arrays.asList(banners));
        mContentGrid.setAdapter(mAdapter);
        mContentGrid.setRowHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        GlideUtils.bind(getActivity(), mIconView, item.getAppIcon());
        mPKView.setText(item.getPackageName());
        mTitleView.setText(item.getAppName());
        mSizeView.setText(item.getAppSize());
        mStartView.setText(String.format("%.01f", item.getScore()));
        mDescView.setText(item.getAppState());

        runTime();
    }

    private void upset(AppItem bean, boolean useSuccess){
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!isAdded()) return;
                switch (bean.getStatus()){
                    case AppItem.STATU_IDLE:
                        mOptView.setVisibility(View.VISIBLE);
                        mOptView.setText(getString(R.string.cancel));
                        mDonwloadView.setText(getString(R.string.waiting));
                        break;
                    case AppItem.STATU_DOWNLOAD_FAIL:
                        mOptView.setVisibility(View.VISIBLE);
                        mOptView.setText(getString(R.string.retry));
                        mDonwloadView.setText(getString(R.string.download_fail_mask));
                        break;
                    case AppItem.STATU_DOWNLOADING:
                        mOptView.setVisibility(View.VISIBLE);
                        mOptView.setText(getString(R.string.cancel));
                        mDonwloadView.setText(String.format("%.01f%%", bean.getProgress() * 100f));
                        break;
                    case AppItem.STATU_INSTALL_FAIL:
                        mDonwloadView.setText(getString(R.string.install_failed));
                        break;
                    case AppItem.STATU_INSTALL_SUCCESS:
                        if (useSuccess) mDonwloadView.setText(getString(isInstalled ? R.string.install_success : R.string.donwload));
                        break;
                    case AppItem.STATU_INSTALLING:
                        mOptView.setVisibility(View.GONE);
                        mDonwloadView.setText(getString(R.string.installing));
                        break;
                    case AppItem.STATU_DOWNLOAD_SUCCESS:
                        mOptView.setVisibility(View.VISIBLE);
                        mOptView.setText(getString(R.string.install));
                        mDonwloadView.setText(getString(R.string.download_success));
                        break;
                }
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestFocus(isInstalled ? mOptView : mDonwloadView);
    }

    @Override
    protected int getWallpaperView() {
        return R.id.wallpaper;
    }

    @Override
    public void onStart() {
        super.onStart();
        isInstalled = isInstalled();
        if (isInstalled){
            mOptView.setVisibility(View.VISIBLE);
            mOptView.setText(getString(R.string.open));
            mDonwloadView.setText(getString(R.string.install_success));
        }
    }

    private void initLauncher() {
        baseLauncher = PermissionHandler.createPermissionsWithArray(this, new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {
                for (Boolean success : result.values()) {
                    if (!success) {
                        Toast.makeText(getActivity(), "ERROR", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });
    }

    @SuppressLint("NewApi")
    @Override
    public void onClick(View v) {
        if (v.equals(mDonwloadView)) {
            if (isInstalled || servier == null) return;

            if (!Config.IS_SYSTEM_UID){
                PackageManager pm = getActivity().getPackageManager();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !pm.canRequestPackageInstalls()) {
                    AndroidSystem.requestInstallApk(getActivity());
                    return;
                }
            }

            AppItem bean = servier.findItem(item);
            if (bean == null){
                servier.push(item);
            }else if (bean.getStatus() == AppItem.STATU_INSTALL_SUCCESS && !isInstalled){
                servier.retryDownload(bean);
            }
        }else if (v.equals(mOptView)){
            if (isInstalled){
                Intent i = AndroidSystem.getPackageNameIntent(getActivity(), item.getPackageName());
                if (i != null){
                    AndroidSystem.openPackageName(getActivity(), item.getPackageName());
                }
            }else {
                AppItem bean = servier.findItem(item);

                if (bean == null) {
                    servier.push(item);
                    return;
                }

                if (bean.getStatus() == AppItem.STATU_DOWNLOAD_FAIL || bean.getStatus() == AppItem.STATU_INSTALL_FAIL){
                    servier.retryDownload(bean);
                }else if (bean.getStatus() == AppItem.STATU_DOWNLOAD_SUCCESS){
                    AppUtils.installApk(getActivity(), FilePathMangaer.getAppDownload(getActivity(), bean));
                }else if (bean.getStatus() == AppItem.STATU_DOWNLOADING){
                    servier.remove(bean);
                    mDonwloadView.setText(getString(R.string.donwload));
                    mOptView.setVisibility(View.GONE);
                }
            }
        }
    }

    private boolean isInstalled() {
        if (item == null) return false;
        List<ResolveInfo> infos = AndroidSystem.queryCategoryLauncher(getActivity());
        for (ResolveInfo info : infos) {
            if (info.activityInfo.packageName.equals(item.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder iBinder) {
        servier = (DownloadServier) ((ServiceBinderWrapper) iBinder).getService();
        mLoadingIne.setVisibility(View.GONE);
        syncMessage();
    }

    private void syncMessage(){
        if (isInstalled){
            mDonwloadView.setText(getString(R.string.install_success));
        }else if (item != null){
            AppItem bean = servier.findItem(item);
            if (bean == null){
                mDonwloadView.setText(getString(R.string.donwload));
            }else {
                upset(bean, true);
            }
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    public class InnerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Intent.ACTION_PACKAGE_ADDED:
                case Intent.ACTION_PACKAGE_REPLACED:
                case Intent.ACTION_PACKAGE_REMOVED:
                case Intent.ACTION_UNINSTALL_PACKAGE:
                    isInstalled = isInstalled();
                    if (isInstalled && isAdded()) {

                        File file = new File(FilePathMangaer.getAppDownload(getActivity(), item));
                        if (file.exists()) file.delete();

                        mOptView.setVisibility(View.VISIBLE);
                        mOptView.setText(getString(R.string.open));
                        mDonwloadView.setText(getString(R.string.install_success));
                    }
                    break;
            }
        }
    }
}
