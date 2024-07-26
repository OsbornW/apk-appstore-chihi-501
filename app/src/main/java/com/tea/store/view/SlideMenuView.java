package com.tea.store.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tea.store.R;
import com.tea.store.bean.SlideMenu;

public class SlideMenuView extends FrameLayout {
    private ImageView mIconView;
    private TextView mTitleView;

    public SlideMenuView(@NonNull Context context) {
        this(context, null);
    }

    public SlideMenuView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.holder_slide_menu, this);
        mIconView = findViewById(R.id.icon);
        mTitleView = findViewById(R.id.title);
    }

    public void setContent(SlideMenu bean) {
        if (mIconView == null) mIconView = findViewById(R.id.icon);
        if (mTitleView == null) mTitleView = findViewById(R.id.title);

        mIconView.setImageResource(bean.getIcon());
        mTitleView.setText(bean.getName());
    }
}
