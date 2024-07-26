package com.tea.store.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tea.store.R;
import com.tea.store.bean.AppItem;
import com.tea.store.utils.GlideUtils;

import java.util.List;

public class AllAppAdapter extends RecyclerView.Adapter<AllAppAdapter.Holder> {
    private Context context;
    private LayoutInflater inflater;
    private List<AppItem> dataList;

    private Callback callback;

    public AllAppAdapter(Context context, LayoutInflater inflater, List<AppItem> dataList) {
        this.context = context;
        this.inflater = inflater;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(R.layout.holder_all_app, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void replace(List<AppItem> list) {
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
        private ImageView mIconView;
        private TextView mTitleView;
        private TextView mMessageView;
        private View mRootView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            mIconView = itemView.findViewById(R.id.icon);
            mTitleView = itemView.findViewById(R.id.title);
            mRootView = itemView.findViewById(R.id.root);
            mMessageView = itemView.findViewById(R.id.message);
        }

        public void bind(AppItem bean) {
            View root = itemView.getRootView();
            GlideUtils.bind(context, mIconView, bean.getAppIcon());
            mTitleView.setText(bean.getAppName());
            if (!TextUtils.isEmpty(bean.getAppSize()))
                mMessageView.setText(String.format("%.01f★ | %s", bean.getScore(), bean.getAppSize()));
            else
                mMessageView.setText("");

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) callback.onClick(bean);
                }
            });

            root.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    mTitleView.setSelected(hasFocus);
                    mRootView.setSelected(hasFocus);
                    Animation animation = AnimationUtils.loadAnimation(context, hasFocus ? R.anim.zoom_in : R.anim.zoom_out);
                    root.startAnimation(animation);
                    animation.setFillAfter(true);
                }
            });
        }
    }
}
