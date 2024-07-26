package com.tea.store.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tea.store.R;
import com.tea.store.utils.GlideUtils;

public abstract class AbsFragment extends Fragment {
    private ImageView mWallpaperView;

    public abstract int getLayoutId();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        init(view, inflater);
        initBefore(view, inflater);
        initBind(view, inflater);
        return view;
    }

    protected void init(View view, LayoutInflater inflater) {
        if (getWallpaperView() != -1) {
            mWallpaperView = view.findViewById(getWallpaperView());
            setWallpaper(mWallpaperView);
        }
    }

    ;

    protected void initBefore(View view, LayoutInflater inflater) {
    }

    protected void initBind(View view, LayoutInflater inflater) {
    }

    protected void requestFocus(View view) {
        requestFocus(view, 0);
    }

    protected void requestFocus(View view, int delayed) {
        if (delayed > 0) {
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.requestFocus();
                }
            }, delayed);
        } else {
            view.post(new Runnable() {
                @Override
                public void run() {
                    view.requestFocus();
                }
            });
        }
    }

    protected int getWallpaperView() {
        return -1;
    }

    protected void updateWallpaper() {
        setWallpaper(mWallpaperView);
    }

    protected void setWallpaper(ImageView view) {
        GlideUtils.bindBlur(getActivity(), view, R.drawable.wallpaper_5);
    }
}
