package com.tea.store.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MarqueeTextView extends androidx.appcompat.widget.AppCompatTextView {
    public MarqueeTextView(@NonNull Context context) {
        this(context, null);
    }

    public MarqueeTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
}
