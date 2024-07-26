package com.tea.store.ui.fragment;

import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.leanback.widget.VerticalGridView;

import com.tea.store.R;
import com.tea.store.adapter.LocalAppAdapter;
import com.tea.store.bean.LocalApp;
import com.tea.store.config.Config;
import com.tea.store.utils.AndroidSystem;

import java.util.ArrayList;
import java.util.List;

public class LocalAppFragment extends AbsFragment {

    private final List<LocalApp> localApps = new ArrayList<>();
    private VerticalGridView mContentGrid;
    private LocalAppAdapter mAdapter;

    public static LocalAppFragment newInstance() {

        Bundle args = new Bundle();

        LocalAppFragment fragment = new LocalAppFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<ResolveInfo> infos = AndroidSystem.getUserApps(getActivity());
        for (ResolveInfo info : infos) localApps.add(new LocalApp(info));
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_local_app;
    }

    @Override
    protected void init(View view, LayoutInflater inflater) {
        super.init(view, inflater);
        mContentGrid = view.findViewById(R.id.content);

        mAdapter = new LocalAppAdapter(getActivity(), inflater, localApps, new LocalAppAdapter.Callback() {
            @Override
            public void onClick(LocalApp bean) {
                AndroidSystem.openActivityInfo(getActivity(), bean.getInfo().activityInfo);
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
    }
}
