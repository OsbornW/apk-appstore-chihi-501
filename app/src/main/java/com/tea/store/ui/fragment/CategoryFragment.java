package com.tea.store.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tea.store.App;
import com.tea.store.R;
import com.tea.store.adapter.CategoryAdapter;
import com.tea.store.bean.CategoryItem;
import com.tea.store.bean.MyRunnable;
import com.tea.store.config.Config;
import com.tea.store.enums.Atts;
import com.tea.store.http.HttpRequest;
import com.tea.store.http.ServiceRequest;
import com.tea.store.http.response.CategoryResponse;
import com.tea.store.ui.activity.AllAppActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;

public class CategoryFragment extends AbsFragment implements CategoryAdapter.Callback, ServiceRequest.Callback<CategoryResponse> {

    private final int[] color = {
            R.color.background_color_1,
            R.color.background_color_2,
            R.color.background_color_3,
            R.color.background_color_4,
            R.color.background_color_5,
            R.color.background_color_7,
            R.color.background_color_8,
            R.color.background_color_9,
    };
    private final ExecutorService exec = Executors.newCachedThreadPool();
    private MyRunnable runnable;
    private boolean isRetry = false;
    private RecyclerView mContentGrid;
    private CategoryAdapter mAdatper;
    private View mProgressView;

    public static CategoryFragment newInstance() {

        Bundle args = new Bundle();

        CategoryFragment fragment = new CategoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (runnable != null) runnable.interrupt();
        exec.shutdownNow();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_category;
    }

    @Override
    protected void init(View view, LayoutInflater inflater) {
        super.init(view, inflater);
        mContentGrid = view.findViewById(R.id.content);
        mProgressView = view.findViewById(R.id.progressBar);

        mAdatper = new CategoryAdapter(getActivity(), inflater, new ArrayList<>());
    }

    @Override
    protected void initBefore(View view, LayoutInflater inflater) {
        super.initBefore(view, inflater);
        mAdatper.setCallback(this);
    }

    @Override
    protected void initBind(View view, LayoutInflater inflater) {
        super.initBind(view, inflater);

        if (App.CATEGORYS.isEmpty()) {
            mProgressView.setVisibility(View.VISIBLE);
            HttpRequest.getCategorys(this, Config.USER_ID);
        } else {
            mProgressView.setVisibility(View.GONE);
            mAdatper.replace(App.CATEGORYS);
        }

        mContentGrid.post(new Runnable() {
            @Override
            public void run() {
                if (!isAdded()) return;
                float width = getResources().getDimension(R.dimen.holder_category_item_root_total_width);
                float number = mContentGrid.getMeasuredWidth() / width;
                float lost = number - ((int) number);
                int columns = lost >= Config.LOST ? (int) number + 1 : (int) number;
                mContentGrid.setLayoutManager(new GridLayoutManager(getActivity(), columns));
                mContentGrid.setAdapter(mAdatper);
            }
        });
        reconnect();
    }

    @Override
    public void onClick(CategoryItem bean) {
        if (bean.getId() == -1) return;
        Intent intent = new Intent(getActivity(), AllAppActivity.class);
        intent.putExtra(Atts.TYPE, bean.getId());
        intent.putExtra(Atts.NAME, bean.getColumnName());
        startActivity(intent);
    }

    @Override
    public void onCallback(Call call, int status, CategoryResponse result) {
        if (!isAdded() || call.isCanceled()) return;
        if (result == null || result.getResult() == null || result.getResult().getAppColumns() == null || result.getResult().getAppColumns().size() == 0) {
            isRetry = true;
            return;
        }
        for (int i = 0; i < result.getResult().getAppColumns().size(); i++) {
            CategoryItem item = result.getResult().getAppColumns().get(i);
            item.setColor(color[i % (color.length - 1)]);
        }
        App.CATEGORYS.clear();
        App.CATEGORYS.addAll(result.getResult().getAppColumns());
        mAdatper.replace(result.getResult().getAppColumns());
        mProgressView.setVisibility(View.GONE);
    }

    private List<CategoryItem> newList() {
        List<CategoryItem> list = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            list.add(new CategoryItem(-1, null, null, color[i % (color.length - 1)]));
        }
        return list;
    }

    private void reconnect() {
        if (runnable != null) runnable.interrupt();
        runnable = new MyRunnable() {
            @Override
            public void run() {
                while (isAdded()) {
                    SystemClock.sleep(2000);
                    if (isInterrupt() || !isAdded()) return;
                    if (isRetry) {
                        HttpRequest.getCategorys(CategoryFragment.this, Config.USER_ID);
                        isRetry = false;
                    }
                }
            }
        };
        exec.execute(runnable);
    }
}
