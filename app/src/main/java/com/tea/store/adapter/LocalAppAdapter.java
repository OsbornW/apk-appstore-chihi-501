package com.tea.store.adapter;

import android.content.Context;
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
import com.tea.store.bean.LocalApp;

import java.util.List;

public class LocalAppAdapter extends RecyclerView.Adapter<LocalAppAdapter.Holder> {

    private Context context;
    private LayoutInflater inflater;
    private List<LocalApp> dataList;

    private Callback callback;

    public LocalAppAdapter(Context context, LayoutInflater inflater, List<LocalApp> dataList, Callback callback) {
        this.context = context;
        this.inflater = inflater;
        this.dataList = dataList;
        this.callback = callback;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(R.layout.holder_local_app, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onClick(LocalApp bean);
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

        public void bind(LocalApp bean) {
            try {
                View root = itemView.getRootView();
                mIconView.setImageDrawable(bean.getInfo().activityInfo.applicationInfo.loadIcon(context.getPackageManager()));
                mTitleView.setText(bean.getInfo().activityInfo.applicationInfo.loadLabel(context.getPackageManager()));
                mMessageView.setText("version: " + context.getPackageManager().getPackageInfo(bean.getInfo().activityInfo.packageName, 0).versionName);
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
