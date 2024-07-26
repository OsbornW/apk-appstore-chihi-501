package com.tea.store.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tea.store.R;
import com.tea.store.utils.GlideUtils;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.Holder> {
    private Context context;
    private LayoutInflater inflater;
    private List<String> dataList;

    public BannerAdapter(Context context, LayoutInflater inflater, List<String> dataList) {
        this.context = context;
        this.inflater = inflater;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(R.layout.holder_banner, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void replace(List<String> list) {
        dataList.clear();
        dataList.addAll(list);
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private ImageView mIconView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            mIconView = itemView.findViewById(R.id.icon);
        }

        public void bind(String bean) {
            View root = itemView.getRootView();
            GlideUtils.bind(context, mIconView, bean);
            root.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    Animation animation = AnimationUtils.loadAnimation(context, hasFocus ? R.anim.zoom_in : R.anim.zoom_out);
                    root.startAnimation(animation);
                    animation.setFillAfter(true);
                }
            });
        }
    }
}
