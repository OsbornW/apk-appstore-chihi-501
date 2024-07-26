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

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.Holder> {

    private Context context;
    private LayoutInflater inflater;
    private List<AppItem> dataList;

    private Callback callback;

    public DownloadAdapter(Context context, LayoutInflater inflater, List<AppItem> dataList){
        this.context = context;
        this.inflater = inflater;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(R.layout.holder_download, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(dataList.get(position));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position, @NonNull List<Object> payloads) {
        if (payloads == null || payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            holder.upset(dataList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void remove(AppItem bean){
        int index = dataList.indexOf(bean);
        if (index != -1){
            dataList.remove(index);
            notifyItemRemoved(index);
        }
    }

    public void replace(List<AppItem> list){
        dataList.clear();
        dataList.addAll(list);
        notifyDataSetChanged();
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

        public void bind(AppItem bean){
            View root = itemView.getRootView();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) callback.onClick(bean);
                }
            });

            root.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    mRootView.setSelected(hasFocus);
                    Animation animation = AnimationUtils.loadAnimation(context, hasFocus ? R.anim.zoom_in : R.anim.zoom_out);
                    root.startAnimation(animation);
                    animation.setFillAfter(true);
                }
            });
            GlideUtils.bind(context, mIconView, bean.getAppIcon());
            mTitleView.setText(bean.getAppName());
            upset(bean);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) callback.onClick(bean);
                }
            });
        }

        private void upset(AppItem bean){
            switch (bean.getStatus()){
                case AppItem.STATU_IDLE:
                    mMessageView.setText(context.getString(R.string.waiting));
                    break;
                case AppItem.STATU_DOWNLOAD_FAIL:
                    mMessageView.setText(context.getString(R.string.download_fail_mask));
                    break;
                case AppItem.STATU_DOWNLOADING:
                    mMessageView.setText(String.format("%.01f%%", bean.getProgress() * 100f));
                    break;
                case AppItem.STATU_INSTALL_FAIL:
                    mMessageView.setText(context.getString(R.string.install_failed));
                    break;
                case AppItem.STATU_INSTALL_SUCCESS:
                    mMessageView.setText(context.getString(R.string.installed));
                    break;
                case AppItem.STATU_INSTALLING:
                    mMessageView.setText(context.getString(R.string.installing));
                    break;
                case AppItem.STATU_DOWNLOAD_SUCCESS:
                    mMessageView.setText(context.getString(R.string.download_success));
                    break;
            }
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback{
        void onClick(AppItem bean);
    }
}
