package com.tea.store.ui.activity;

import androidx.fragment.app.Fragment;

import com.tea.store.R;
import com.tea.store.ui.fragment.SearchFragment;

public class SearchActivity extends AbsActivity {
    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public int getContainerId() {
        return R.id.main_browse_fragment;
    }

    @Override
    public Fragment getFragment() {
        return SearchFragment.newInstance();
    }
}
