package com.tea.store.ui.activity;

import androidx.fragment.app.Fragment;

import com.tea.store.R;
import com.tea.store.enums.Atts;
import com.tea.store.ui.fragment.AllAppFragment;

public class AllAppActivity extends AbsActivity {
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
        return AllAppFragment.newInstance(getIntent().getIntExtra(Atts.TYPE, 8), getIntent().getStringExtra(Atts.NAME));
    }
}
