package com.tea.store.ui.fragment;

import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.leanback.widget.VerticalGridView;

import com.google.gson.Gson;
import com.tea.store.App;
import com.tea.store.R;
import com.tea.store.adapter.HomeAdapter;
import com.tea.store.bean.AppItem;
import com.tea.store.bean.Category;
import com.tea.store.bean.HomeNetConfig;
import com.tea.store.bean.MyRunnable;
import com.tea.store.config.Config;
import com.tea.store.http.HttpRequest;
import com.tea.store.http.ServiceRequest;
import com.tea.store.http.response.AppListResponse;
import com.tea.store.ui.activity.AppDetailActivity;
import com.tea.store.utils.FileUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import retrofit2.Call;

public class HomeFragment extends AbsFragment {

    private final ExecutorService exec = Executors.newCachedThreadPool();
    private final LinkedBlockingQueue<HomeNetConfig> configs = new LinkedBlockingQueue<>();
    private final List<Category> categories = new ArrayList<>();
    private VerticalGridView mContentGrid;

    private HomeAdapter mAdapter;
    private MyRunnable runnable;
    private Call call;

    public static HomeFragment newInstance() {

        Bundle args = new Bundle();

        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categories.add(new Category(getString(R.string.recommend_home), newList()));
        categories.add(new Category(getString(R.string.recommend_hot), newList()));
        categories.add(new Category(getString(R.string.recommend_topic_lift), newList()));
        categories.add(new Category(getString(R.string.recommend_topic_video), newList()));
        categories.add(new Category(getString(R.string.recommend_topic_educate), newList()));
        categories.add(new Category(getString(R.string.recommend_topic_tools), newList()));
        categories.add(new Category(getString(R.string.recommend_games), newList()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (runnable != null) runnable.interrupt();
        exec.shutdownNow();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void init(View view, LayoutInflater inflater) {
        super.init(view, inflater);
        mContentGrid = view.findViewById(R.id.content);

        mAdapter = new HomeAdapter(getActivity(), inflater, categories);
    }

    @Override
    protected void initBind(View view, LayoutInflater inflater) {
        super.initBind(view, inflater);
        mContentGrid.setAdapter(mAdapter);
        getAppList(Config.USER_ID, null, "main", 0);
        getAppList(Config.USER_ID, null, "hot", 1);
        getAppList(Config.USER_ID, null, "life", 2);
        getAppList(Config.USER_ID, null, "video", 3);
        getAppList(Config.USER_ID, null, "education", 4);
        getAppList(Config.USER_ID, null, "tools", 5);
        getAppList(Config.USER_ID, null, "game", 6);

        mAdapter.setCallback(new HomeAdapter.Callback() {
            @Override
            public void onClick(AppItem bean) {
                if (TextUtils.isEmpty(bean.getAppDownLink())) return;
                AppDetailActivity.start(getActivity(), bean);
            }
        });
        reconnect();
    }

    private void getAppList(String userId, String appColumnId, String tag, final int index) {
        final String key = TextUtils.isEmpty(tag) ? appColumnId : tag;
        if (App.HOME_MAP.containsKey(key)) {
            categories.get(index).getList().clear();
            categories.get(index).getList().addAll(App.HOME_MAP.get(key));
            mAdapter.notifyItemChanged(index);
            return;
        }
        HttpRequest.getAppList(new ServiceRequest.Callback<AppListResponse>() {
            @Override
            public void onCallback(Call call, int status, AppListResponse result) {
                if (!isAdded() || call.isCanceled()) return;

                if (result == null || result.getResult() == null || result.getResult().getAppList() == null || result.getResult().getAppList().size() == 0) {
                    configs.offer(new HomeNetConfig(userId, appColumnId, tag, index));
                    return;
                }
                App.HOME_MAP.put(key, result.getResult().getAppList());
                categories.get(index).getList().clear();
                categories.get(index).getList().addAll(result.getResult().getAppList());
                mAdapter.notifyItemChanged(index);
                if (tag.equals("game")){
                    try {
                        FileUtils.writeFile(new Gson().toJson(result.getResult().getAppList()).getBytes(StandardCharsets.UTF_8), getActivity().getFilesDir().getAbsolutePath(), "game.json");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }, userId, appColumnId, tag, null, 1, 20);
    }

    private void reconnect() {
        if (runnable != null) runnable.interrupt();
        runnable = new MyRunnable() {
            @Override
            public void run() {
                while (isAdded()) {
                    try {
                        SystemClock.sleep(2000);
                        if (isInterrupt() || !isAdded()) return;
                        HomeNetConfig item = configs.take();
                        getAppList(item.getUserId(), item.getAppColumnId(), item.getTag(), item.getIndex());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        exec.execute(runnable);
    }

    private List<AppItem> newList() {
        List<AppItem> items = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            items.add(new AppItem());
        }
        return items;
    }
}
