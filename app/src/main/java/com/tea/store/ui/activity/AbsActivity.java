package com.tea.store.ui.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public abstract class AbsActivity extends FragmentActivity {
    public abstract int getLayoutId();

    public abstract int getContainerId();

    public abstract Fragment getFragment();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(getContainerId(), getFragment())
                    .commitAllowingStateLoss();
        }
    }
}
