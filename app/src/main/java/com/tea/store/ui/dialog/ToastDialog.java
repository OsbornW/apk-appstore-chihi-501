package com.tea.store.ui.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tea.store.R;
import com.tea.store.enums.Atts;


public class ToastDialog extends SingleDialogFragment{
    public static final String TAG = "ToastDialog";

    public static ToastDialog newInstance(String text) {
        return newInstance(text, MODE_DEFAULT);
    }
    public static ToastDialog newInstance(String text, int mode) {

        Bundle args = new Bundle();
        args.putString(Atts.BEAN, text);
        args.putInt(Atts.MODE, mode);
        ToastDialog fragment = new ToastDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public static final int MODE_DEFAULT = 0;
    public static final int MODE_CONFIRM = 1;

    private TextView mTextView;
    private View mConfirmView;
    private View mCancelView;
    private View mRootView;
    private ImageView mBlur;

    private Callback callback;

    private int mode = MODE_DEFAULT;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mode = getArguments().getInt(Atts.MODE);
    }

    @Override
    protected int getLayout() {
        return R.layout.dialog_toast;
    }

    @Override
    protected void init(LayoutInflater inflater, View view) {
        super.init(inflater, view);
        mTextView = view.findViewById(R.id.text);
        mConfirmView = view.findViewById(R.id.confirm);
        mRootView = view.findViewById(R.id.root);
        mBlur = view.findViewById(R.id.blur);
        mCancelView = view.findViewById(R.id.cancel);

        switch (mode){
            case MODE_CONFIRM:
                mCancelView.setVisibility(View.GONE);
                mConfirmView.setVisibility(View.VISIBLE);
                break;
            default:
                mCancelView.setVisibility(View.VISIBLE);
                mConfirmView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (callback != null) callback.onDimess();
    }

    @Override
    protected void initBind(LayoutInflater inflater, View view) {
        super.initBind(inflater, view);
        mTextView.setText(getArguments().getString(Atts.BEAN));
        mConfirmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (callback != null) callback.onClick(1);
            }
        });

        mCancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (callback != null) callback.onClick(0);
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        blur(mRootView, mBlur);
    }

    @Override
    protected int getGravity() {
        return Gravity.CENTER;
    }

    @Override
    public boolean isMaterial() {
        return false;
    }

    @Override
    protected int[] getWidthAndHeight() {
        return new int[]{ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT};
    }

    @Override
    protected float getDimAmount() {
        return 0;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback{
        void onClick(int type);
        void onDimess();
    }
}
