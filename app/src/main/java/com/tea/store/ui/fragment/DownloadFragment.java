package com.tea.store.ui.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.leanback.widget.VerticalGridView;

import com.lzy.okgo.OkGo;
import com.tea.store.R;
import com.tea.store.adapter.DownloadAdapter;
import com.tea.store.bean.AppItem;
import com.tea.store.bean.MyRunnable;
import com.tea.store.config.Config;
import com.tea.store.enums.Atts;
import com.tea.store.enums.IntentAction;
import com.tea.store.manager.FilePathMangaer;
import com.tea.store.manager.ServiceBinderWrapper;
import com.tea.store.service.DownloadServier;
import com.tea.store.ui.dialog.DownloadDialog;
import com.tea.store.utils.AppUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadFragment extends AbsFragment implements ServiceConnection {

    public static DownloadFragment newInstance() {

        Bundle args = new Bundle();

        DownloadFragment fragment = new DownloadFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private final ExecutorService exec = Executors.newCachedThreadPool();

    private VerticalGridView mContentGrid;
    private View mMaskView;

    private DownloadAdapter mAdapter;
    private MyRunnable runnable;
    private Intent intentService;
    private DownloadServier servier;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intentService = new Intent(getActivity(), DownloadServier.class);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unbindService(this);
        if (runnable != null) runnable.interrupt();
        exec.shutdownNow();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_download;
    }

    @Override
    protected void init(View view, LayoutInflater inflater) {
        super.init(view, inflater);
        mContentGrid = view.findViewById(R.id.content);
        mMaskView = view.findViewById(R.id.masked);

        mAdapter = new DownloadAdapter(getActivity(), inflater, new CopyOnWriteArrayList<>());
    }

    @Override
    protected void initBefore(View view, LayoutInflater inflater) {
        super.initBefore(view, inflater);
        mAdapter.setCallback(new DownloadAdapter.Callback() {
            @Override
            public void onClick(AppItem bean) {
                if (bean.getStatus() == AppItem.STATU_DOWNLOAD_SUCCESS){
                    AppUtils.installApk(getActivity(), FilePathMangaer.getAppDownload(getActivity(), bean));
                }else {
                    if (bean.getStatus() == AppItem.STATU_INSTALLING) return;
                    DownloadDialog dialog = DownloadDialog.newInstance(bean.getAppName());
                    dialog.setCallback(new DownloadDialog.Callback() {
                        @Override
                        public void onClick(int type) {
                            if (type == 1){
                                servier.remove(bean);
                                mAdapter.remove(bean);
                                mMaskView.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
                                mContentGrid.setVisibility(mAdapter.getItemCount() == 0 ? View.GONE : View.VISIBLE);
                            }else if (type == 2){
                                if (bean.getStatus() == AppItem.STATU_INSTALL_FAIL || bean.getStatus() == AppItem.STATU_DOWNLOAD_FAIL){
                                    bean.setProgress(0);
                                    bean.setStatus(AppItem.STATU_IDLE);
                                }
                            }
                        }
                    });
                    dialog.show(getChildFragmentManager(), DownloadDialog.TAG);
                }
            }
        });
    }

    @Override
    protected void initBind(View view, LayoutInflater inflater) {
        super.initBind(view, inflater);

        mContentGrid.post(new Runnable() {
            @Override
            public void run() {
                if (!isAdded()) return;
                float width = getResources().getDimension(R.dimen.holder_local_item_root_total_width);
                float number = mContentGrid.getMeasuredWidth() / width;
                float lost = number - ((int) number);
                int columns = lost >= Config.LOST ? (int) number + 1 : (int) number;
                mContentGrid.setAdapter(mAdapter);
                mContentGrid.setNumColumns(columns);
            }
        });
        getActivity().bindService(intentService, this, Context.BIND_AUTO_CREATE);
        time();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMaskView.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        if (servier != null) replace(servier.getDownloads());
    }

    private void time(){
        if (runnable != null) runnable.interrupt();
        runnable = new MyRunnable() {
            @Override
            public void run() {
                while (!isInterrupt()){
                    SystemClock.sleep(500);
                    mContentGrid.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!isAdded()) return;
                            mAdapter.notifyItemRangeChanged(0, mAdapter.getItemCount(), new Object());
                        }
                    });
                }
            }
        };
        exec.execute(runnable);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder iBinder) {
        servier = (DownloadServier) ((ServiceBinderWrapper) iBinder).getService();
        replace(servier.getDownloads());
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    private void replace(List<AppItem> items){
        mAdapter.replace(items);
        mContentGrid.setVisibility(items.isEmpty() ? View.GONE : View.VISIBLE);
        mMaskView.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
