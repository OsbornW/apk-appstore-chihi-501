package com.tea.store.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.widget.VerticalGridView;

import com.tea.store.R;
import com.tea.store.adapter.AllAppAdapter;
import com.tea.store.adapter.TextWatcherAdapter;
import com.tea.store.bean.AppItem;
import com.tea.store.config.Config;
import com.tea.store.http.HttpRequest;
import com.tea.store.http.ServiceRequest;
import com.tea.store.http.response.AppListResponse;
import com.tea.store.ui.activity.AppDetailActivity;
import com.tea.store.ui.dialog.KeyboardDialog;

import java.util.ArrayList;

import retrofit2.Call;

public class SearchFragment extends AbsFragment implements TextView.OnEditorActionListener, View.OnClickListener {

    private VerticalGridView mContentGrid;
    private EditText mEditText;
    private View mMaskView;
    private View mProgressView;
    private View mDivSearch;
    private AllAppAdapter mAdapter;
    private Call call;
    private int page = 1;
    private int maxSize = 50;

    public static SearchFragment newInstance() {

        Bundle args = new Bundle();

        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (call != null) call.cancel();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_search;
    }

    @Override
    protected void init(View view, LayoutInflater inflater) {
        super.init(view, inflater);
        mEditText = view.findViewById(R.id.edit_query);
        mContentGrid = view.findViewById(R.id.content);
        mMaskView = view.findViewById(R.id.none);
        mProgressView = view.findViewById(R.id.progressBar);
        mDivSearch = view.findViewById(R.id.div_search);

        mAdapter = new AllAppAdapter(getActivity(), inflater, new ArrayList<>());
    }

    @Override
    protected void initBefore(View view, LayoutInflater inflater) {
        super.initBefore(view, inflater);
        mEditText.setOnEditorActionListener(this);
        mDivSearch.setOnClickListener(this);
        mEditText.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) return;
                mEditText.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });


        mEditText.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_TV_INPUT &&
                    event.getAction() == KeyEvent.ACTION_DOWN) {
                // Done键被按下
                // 在这里添加你的逻辑
                hideKeyboard(mEditText);
                return true;
            }
            return false;
        });
    }

    private void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v

                .getContext().getSystemService(

                        Context.INPUT_METHOD_SERVICE);

        if (imm.isActive()) {

            imm.hideSoftInputFromWindow(

                    v.getApplicationWindowToken(), 0);
        }
    }

        @Override
        protected void initBind (View view, LayoutInflater inflater){
            super.initBind(view, inflater);
            mContentGrid.post(new Runnable() {
                @Override
                public void run() {
                    float width = getResources().getDimension(R.dimen.holder_all_app_root_total_width);
                    float number = mContentGrid.getMeasuredWidth() / width;
                    float lost = number - ((int) number);
                    int columns = lost >= Config.LOST ? (int) number + 1 : (int) number;
                    mContentGrid.setAdapter(mAdapter);
                    mContentGrid.setNumColumns(columns);
                    mContentGrid.setVisibility(View.GONE);
                }
            });

            mAdapter.setCallback(new AllAppAdapter.Callback() {
                @Override
                public void onClick(AppItem bean) {
                    AppDetailActivity.start(getActivity(), bean);
                }
            });
        }

        @Override
        public boolean onEditorAction (TextView v,int actionId, KeyEvent event){
            String word = mEditText.getText().toString();
            if (!TextUtils.isEmpty(word)) {
                mMaskView.setVisibility(View.GONE);
                if(!word.endsWith(" ")){
                    search(word, page);
                }

            }
            return false;
        }

        @Override
        public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState){
            super.onViewCreated(view, savedInstanceState);
            requestFocus(mDivSearch);
            mDivSearch.callOnClick();
        }

        private void search (String word,int page){
            if (call != null) call.cancel();
            mProgressView.setVisibility(View.VISIBLE);
            mMaskView.setVisibility(View.GONE);
            mContentGrid.setVisibility(View.GONE);
            call = HttpRequest.getAppList(new ServiceRequest.Callback<AppListResponse>() {
                @Override
                public void onCallback(Call call, int status, AppListResponse result) {
                    if (!isAdded() || call.isCanceled()) return;
                    mProgressView.setVisibility(View.GONE);
                    if (result == null || result.getResult() == null || result.getResult().getAppList() == null || result.getResult().getAppList().isEmpty()) {
                        mContentGrid.setVisibility(View.GONE);
                        mAdapter.replace(new ArrayList<>());
                        mMaskView.setVisibility(View.VISIBLE);
                        return;
                    }
                    mMaskView.setVisibility(View.GONE);
                    mContentGrid.setVisibility(View.VISIBLE);
                    mAdapter.replace(result.getResult().getAppList());
                }
            }, Config.USER_ID, null, null, word, page, maxSize);
        }

    private KeyboardDialog dialog = null;
        @Override
        public void onClick (View v){
            if (v.equals(mDivSearch)) {
                 dialog = KeyboardDialog.newInstance();
                dialog.setTargetView(mEditText);
                dialog.show(getChildFragmentManager(), KeyboardDialog.TAG);
            }
        }
    }
