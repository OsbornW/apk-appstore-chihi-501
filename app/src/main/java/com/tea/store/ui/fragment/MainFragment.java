package com.tea.store.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.leanback.widget.HorizontalGridView;

import com.tea.store.BuildConfig;
import com.tea.store.R;
import com.tea.store.adapter.SlideMenuAdapter;
import com.tea.store.bean.SlideMenu;
import com.tea.store.bean.Version;
import com.tea.store.config.Config;
import com.tea.store.http.HttpRequest;
import com.tea.store.http.UpgradeRequest;
import com.tea.store.http.response.VersionResponse;
import com.tea.store.utils.AndroidSystem;
import com.tea.store.view.SlideMenuView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class MainFragment extends AbsFragment implements View.OnClickListener, SlideMenuAdapter.Callback {

    private final List<SlideMenu> menus = new ArrayList<>();
    private ViewGroup mDivMenus;
    private View mWifiView;
    private HorizontalGridView mSlideGrid;
    private int lastMenu = -1;

    public static MainFragment newInstance() {

        Bundle args = new Bundle();

        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menus.add(new SlideMenu("Home", 0, R.drawable.baseline_home_100));
        menus.add(new SlideMenu("Category", 1, R.drawable.baseline_category_100));
        menus.add(new SlideMenu("Apps", 2, R.drawable.baseline_apps_100));
        menus.add(new SlideMenu("Download", 4, R.drawable.baseline_download_100));
        menus.add(new SlideMenu("Search", 3, R.drawable.baseline_search_100));
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void init(View view, LayoutInflater inflater) {
        super.init(view, inflater);
        mDivMenus = view.findViewById(R.id.div_menus);
        mWifiView = view.findViewById(R.id.wifi);
        mSlideGrid = view.findViewById(R.id.slide);

        //mTestView.setText(String.format("Chihi store test: %s", BuildConfig.VERSION_NAME));
    }

    @Override
    protected void initBefore(View view, LayoutInflater inflater) {
        super.initBefore(view, inflater);
        mWifiView.setOnClickListener(this);
    }

    @Override
    protected void initBind(View view, LayoutInflater inflater) {
        super.initBind(view, inflater);
        //fillMenu(view, inflater);
        SlideMenuAdapter adapter = new SlideMenuAdapter(getActivity(), inflater, menus);
        adapter.setCallback(this);
        mSlideGrid.setAdapter(adapter);
        mSlideGrid.setNumRows(menus.size());
        mSlideGrid.setRowHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestFocus(mSlideGrid);
        HttpRequest.checkVersion(newVersionCallback());
    }


    private UpgradeRequest.Callback<VersionResponse> newVersionCallback() {
        return new UpgradeRequest.Callback<VersionResponse>() {
            @Override
            public void onCallback(Call call, int status, VersionResponse response) {
                if (!isAdded() || call.isCanceled() || response == null || response.getData() == null)
                    return;
                Version result = response.getData();
                if (result.getVersion() > BuildConfig.VERSION_CODE && Config.CHANNEL.equals(result.getChannel())) {
                    AndroidSystem.jumpUpgrade(getActivity(), result);
                }
            }
        };
    }

    private void fillMenu(View view, LayoutInflater inflater) {
        for (int i = 0; i < menus.size(); i++) {
            final SlideMenu item = menus.get(i);
            SlideMenuView child = (SlideMenuView) inflater.inflate(R.layout.item_slide_menu, mDivMenus, false);
            mDivMenus.addView(child);
            child.setContent(item);
            final int index = i;
            child.getChildAt(0).setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        if (lastMenu == index) return;
                        child.setSelected(true);
                        onMenuClick(item);
                        lastMenu = index;
                        int childCount = mDivMenus.getChildCount();
                        for (int x = 0; x < childCount; x++) {
                            if (x != index) mDivMenus.getChildAt(x).setSelected(false);
                        }
                    }
                }
            });
            if (i == 0) child.requestFocus();
        }
    }

    public void onMenuClick(SlideMenu bean) {
        switch (bean.getType()) {
            case 0:
                commit(HomeFragment.newInstance());
                break;
            case 1:
                commit(CategoryFragment.newInstance());
                break;
            case 2:
                commit(LocalAppFragment.newInstance());
                break;
            case 3:
                commit(SearchFragment.newInstance());
                break;
            case 4:
                commit(DownloadFragment.newInstance());
                break;
        }
    }

    @Override
    protected int getWallpaperView() {
        return R.id.wallpaper;
    }

    private void commit(Fragment fragment) {
        getChildFragmentManager().beginTransaction().replace(R.id.child_container, fragment).commit();
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mWifiView)) {
            AndroidSystem.openWifiSetting(getActivity());
        }
    }

    @Override
    public void onClick(SlideMenu bean) {
        onMenuClick(bean);
    }
}
