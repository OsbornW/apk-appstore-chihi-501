package com.tea.store.ui.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.tea.store.R;
import com.tea.store.bean.AppItem;
import com.tea.store.enums.Atts;
import com.tea.store.ui.fragment.AppDetialFragment;

public class AppDetailActivity extends AbsActivity {

    public static void start(Context context, AppItem bean) {
        start(context, bean, null);
    }

    public static void start(Context context, AppItem bean, String[] packageName) {
        Intent intent = new Intent(context, AppDetailActivity.class);
        intent.putExtra(Atts.BEAN, new Gson().toJson(bean));
        intent.putExtra(Atts.PACKAGE_NAME, packageName);
        context.startActivity(intent);
    }

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
        Gson gson = new Gson();
        return AppDetialFragment.newInstance(gson.fromJson(getIntent().getStringExtra(Atts.BEAN), AppItem.class), getIntent().getStringArrayExtra(Atts.PACKAGE_NAME));
    }
}
