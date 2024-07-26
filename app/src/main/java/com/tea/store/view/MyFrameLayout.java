package com.tea.store.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tea.store.callback.SelectedCallback;

public class MyFrameLayout extends FrameLayout {
    private SelectedCallback callback;

    public MyFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public MyFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (callback != null) callback.onSelect(selected);
    }

    public void setCallback(SelectedCallback callback) {
        this.callback = callback;
    }
}
