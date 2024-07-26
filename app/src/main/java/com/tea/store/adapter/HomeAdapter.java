package com.tea.store.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.leanback.widget.HorizontalGridView;
import androidx.recyclerview.widget.RecyclerView;

import com.tea.store.R;
import com.tea.store.bean.AppItem;
import com.tea.store.bean.Category;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.Holder> {
    private Context context;
    private LayoutInflater inflater;
    private List<Category> dataList;

    private Callback callback;

    public HomeAdapter(Context context, LayoutInflater inflater, List<Category> dataList) {
        this.context = context;
        this.inflater = inflater;
        this.dataList = dataList;
    }


    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(R.layout.holder_home, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void replace(List<Category> list) {
        dataList.clear();
        dataList.addAll(list);
        notifyDataSetChanged();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onClick(AppItem bean);
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView mTitleView;
        private HorizontalGridView mGridView;

        @SuppressLint("RestrictedApi")
        public Holder(View view) {
            super(view);
            mTitleView = view.findViewById(R.id.title);
            mGridView = view.findViewById(R.id.grid);
        }

        public void bind(Category bean) {
            int index = dataList.indexOf(bean);
            mTitleView.setVisibility(View.VISIBLE);
            mTitleView.setText(bean.getTitle());
            mGridView.setAdapter(new AppItemAdapter(context, inflater, bean.getList(), R.layout.holder_item_app, new AppItemAdapter.Callback() {
                @Override
                public void onClick(AppItem bean) {
                    if (callback != null) callback.onClick(bean);
                }
            }));
        }
    }
}
