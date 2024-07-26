package com.tea.store.ui.dialog;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.tea.store.R;
import com.tea.store.utils.AndroidSystem;

public abstract class SingleDialogFragment extends DialogFragment {
    public static final int NO_ANIMATION = -1;

    protected abstract int getLayout();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
            设置全屏
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
         */
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayout(), container, false);
        init(inflater, view);
        initBefore(inflater, view);
        initBind(inflater, view);
        return view;
    }

    protected void init(LayoutInflater inflater, View view) {
    }

    ;

    protected void initBefore(LayoutInflater inflater, View view) {
    }

    protected void initBind(LayoutInflater inflater, View view) {
    }

    ;

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        window.setBackgroundDrawable(isMaterial() ? getResources().getDrawable(R.drawable.dialog_material_radius_white_background, Resources.getSystem().newTheme()) : getResources().getDrawable(R.drawable.dialog_material_transparent_background, Resources.getSystem().newTheme()));
        window.setGravity(getGravity());
        if (getAnimation() != NO_ANIMATION) {
            window.setWindowAnimations(getAnimation());
        }
        window.setDimAmount(getDimAmount());
        getDialog().setCanceledOnTouchOutside(canOutSide());
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = getWidthAndHeight()[0];
        lp.height = getWidthAndHeight()[1];
        window.setAttributes(lp);
    }

    public boolean isMaterial() {
        return false;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    protected void blur(View root, ImageView blur) {
        View decorView = getActivity().getWindow().getDecorView();
        AndroidSystem.blur(getActivity(), decorView, root, blur);
    }

    protected View[] getClickViews() {
        return null;
    }

    ;

    protected void setVisible(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void toast(String msg, int duration) {
        Toast.makeText(getActivity(), msg, duration).show();
    }

    public void toast(String msg) {
        toast(msg, Toast.LENGTH_SHORT);
    }

    public Fragment getThis() {
        return this;
    }

    protected int[] getWidthAndHeight() {
        return new int[]{
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        };
    }

    public void standShow(FragmentManager manager) {
        show(manager, getClass().getName());
    }

    protected float getDimAmount() {
        return 0.5f;
    }

    public void setDimAmount(float val) {
        Window window = getDialog().getWindow();
        window.setDimAmount(val);
    }

    public int getAnimation() {
        return NO_ANIMATION;
    }

    protected int getGravity() {
        return Gravity.CENTER;
    }

    protected void setGravity(int gravity) {
        Window window = getDialog().getWindow();
        window.setGravity(gravity);
    }

    protected boolean canOutSide() {
        return false;
    }
}
