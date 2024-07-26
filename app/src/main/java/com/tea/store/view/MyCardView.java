package com.tea.store.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.tea.store.callback.SelectedCallback;

public class MyCardView extends CardView {

    private SelectedCallback callback;

    public MyCardView(@NonNull Context context) {
        this(context, null);
    }

    public MyCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
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
