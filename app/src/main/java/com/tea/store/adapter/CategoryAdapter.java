package com.tea.store.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.tea.store.R;
import com.tea.store.bean.CategoryItem;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.Holder> {
    private Context context;
    private LayoutInflater inflater;
    private List<CategoryItem> dataList;

    private Callback callback;

    public CategoryAdapter(Context context, LayoutInflater inflater, List<CategoryItem> dataList) {
        this.context = context;
        this.inflater = inflater;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(R.layout.holder_category_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void replace(List<CategoryItem> items) {
        dataList.clear();
        dataList.addAll(items);
        notifyDataSetChanged();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onClick(CategoryItem bean);
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView mTitleView;
        private View mRootView;
        private CardView mCardView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            mTitleView = itemView.findViewById(R.id.title);
            mRootView = itemView.findViewById(R.id.root);
            mCardView = itemView.findViewById(R.id.card);
        }

        public void bind(CategoryItem bean) {
            View root = itemView.getRootView();
            mTitleView.setText(bean.getColumnName());
            mCardView.setCardBackgroundColor(context.getResources().getColor(bean.getColor()));
            mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) callback.onClick(bean);
                }
            });
            root.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) mRootView.requestFocus();
                    mTitleView.setSelected(hasFocus);
                    Animation animation = AnimationUtils.loadAnimation(context, hasFocus ? R.anim.zoom_in : R.anim.zoom_out);
                    root.startAnimation(animation);
                    animation.setFillAfter(true);
                }
            });
        }
    }
}
