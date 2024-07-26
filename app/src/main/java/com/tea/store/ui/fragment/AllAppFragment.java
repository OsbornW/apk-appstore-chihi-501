package com.tea.store.ui.fragment;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.leanback.widget.VerticalGridView;

import com.tea.store.R;
import com.tea.store.adapter.AllAppAdapter;
import com.tea.store.bean.AppItem;
import com.tea.store.bean.MyRunnable;
import com.tea.store.config.Config;
import com.tea.store.enums.Atts;
import com.tea.store.http.HttpRequest;
import com.tea.store.http.ServiceRequest;
import com.tea.store.http.response.AppListResponse;
import com.tea.store.ui.activity.AppDetailActivity;
import com.tea.store.ui.dialog.ToastDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;

public class AllAppFragment extends AbsFragment implements View.OnClickListener {

    private final ExecutorService exec = Executors.newCachedThreadPool();
    private final Map<Integer, List<AppItem>> cacheMap = new ConcurrentHashMap<>();
    private VerticalGridView mContentGrid;
    private View mNextView;
    private TextView mPageView;
    private View mPreviousView;
    private TextView mTitleView;
    private View mNoneView;
    private AllAppAdapter mAdapter;
    private MyRunnable retryRunnable;
    private Call call;
    private int type;
    private int maxSize = 20;
    private int page = 1;
    private int maxPage = -1;
    private boolean isWork = false;
    private boolean isRetry = false;
    private String tag = "";

    public static AllAppFragment newInstance(int type, String name) {

        Bundle args = new Bundle();
        args.putInt(Atts.TYPE, type);
        args.putString(Atts.NAME, name);
        AllAppFragment fragment = new AllAppFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (retryRunnable != null) retryRunnable.interrupt();
        exec.shutdownNow();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getInt(Atts.TYPE, 8);
        tag = getArguments().getString(Atts.NAME, "");
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_app_list;
    }

    @Override
    protected void init(View view, LayoutInflater inflater) {
        super.init(view, inflater);
        mContentGrid = view.findViewById(R.id.content);
        mNextView = view.findViewById(R.id.next);
        mPageView = view.findViewById(R.id.page);
        mPreviousView = view.findViewById(R.id.previous);
        mTitleView = view.findViewById(R.id.title);
        mNoneView = view.findViewById(R.id.none);

        mAdapter = new AllAppAdapter(getActivity(), inflater, new ArrayList<>());
    }

    @Override
    protected void initBefore(View view, LayoutInflater inflater) {
        super.initBefore(view, inflater);

        mPreviousView.setOnClickListener(this);
        mNextView.setOnClickListener(this);
    }

    @Override
    protected void initBind(View view, LayoutInflater inflater) {
        super.initBind(view, inflater);
        mTitleView.setText(getArguments().getString(Atts.NAME));

        mAdapter.setCallback(new AllAppAdapter.Callback() {
            @Override
            public void onClick(AppItem bean) {
                AppDetailActivity.start(getActivity(), bean);
            }
        });

        mContentGrid.post(new Runnable() {
            @Override
            public void run() {
                float width = getResources().getDimension(R.dimen.holder_all_app_root_total_width);
                float number = mContentGrid.getMeasuredWidth() / width;
                float lost = number - ((int) number);
                int columns = lost >= Config.LOST ? (int) number + 1 : (int) number;
                mContentGrid.setAdapter(mAdapter);
                mContentGrid.setNumColumns(columns);
                maxSize = 5 * columns;
                getAppList(page);
            }
        });
    }

    @Override
    protected int getWallpaperView() {
        return R.id.wallpaper;
    }

    private void runRetry() {
        if (retryRunnable != null) retryRunnable.interrupt();
        retryRunnable = new MyRunnable() {
            @Override
            public void run() {
                while (!isInterrupt()) {
                    if (!isAdded()) return;
                    if (!isRetry) continue;
                    SystemClock.sleep(5000);
                    if (isRetry && isAdded() && !isInterrupt()) {
                        getAppList(page);
                    }
                }
            }
        };
        exec.execute(retryRunnable);
    }

    private void getAppList(final int page) {
        if (call != null) call.cancel();
        mNoneView.setVisibility(View.GONE);
        //mAdapter.replace(newList());
        //mContentGrid.scrollToPosition(0);
        getAppList(Config.USER_ID, String.valueOf(type), tag, page, maxSize);
    }

    private void getAppList(String userId, String appColumnId, String tag, final int mpage, int maxSize) {
        isWork = true;
        isRetry = false;
        call = HttpRequest.getAppList(new ServiceRequest.Callback<AppListResponse>() {
            @Override
            public void onCallback(Call call, int status, AppListResponse result) {
                if (call.isCanceled()) return;
                if (!isAdded() || result == null || result.getResult() == null || result.getResult().getAppList() == null) {
                    isRetry = true;
                    return;
                }

                isWork = false;
                isRetry = false;
                if (result.getResult().getAppList().isEmpty()) {
                    --page;
                    maxPage = page;
                    mPageView.setText(String.valueOf(page));
                    ToastDialog dialog = ToastDialog.newInstance(getString(R.string.no_data));
                    dialog.show(getChildFragmentManager(), ToastDialog.TAG);
                    //Toast.makeText(getContext(), getString(R.string.no_data), Toast.LENGTH_SHORT).show();
                }else {
                    mPageView.setText(String.valueOf(page));
                    cacheMap.put(page, result.getResult().getAppList());

                    // mNoneView.setVisibility(result.getResult().getAppList().isEmpty() ? View.VISIBLE : View.GONE);
                    mAdapter.replace(result.getResult().getAppList());
                }


            }
        }, userId, null, tag.toLowerCase(), null, page, maxSize);
    }

    private List<AppItem> newList() {
        List<AppItem> items = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            items.add(new AppItem());
        }
        return items;
    }

    @Override
    public void onClick(View v) {
        if (isWork) return;
        if (v.equals(mNextView)) {
            page++;
        } else if (v.equals(mPreviousView)) {
            page--;
        }



        if (page <= 0) {
            page = 1;
            mPageView.setText(String.valueOf(page));
            return;
        }
        Log.d("zy2001", "onClick: 当前的page是"+page+"===="+maxPage);

        if (maxPage != -1 && page > maxPage) {
            page = maxPage;
            mPageView.setText(String.valueOf(page));
            ToastDialog dialog = ToastDialog.newInstance(getString(R.string.no_data));
            dialog.show(getChildFragmentManager(), ToastDialog.TAG);
            return;
        }else {
            if(maxPage!=-1){
                mPageView.setText(String.valueOf(page));
            }

        }


        if (cacheMap.containsKey(page)) {
            mAdapter.replace(cacheMap.get(page));
            if(mAdapter.getItemCount() != 0 )mNoneView.setVisibility(View.GONE);
            //mNoneView.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        } else {
            getAppList(page);
        }
    }
}
