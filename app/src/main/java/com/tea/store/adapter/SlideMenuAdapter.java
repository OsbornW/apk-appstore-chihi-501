package com.tea.store.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tea.store.R;
import com.tea.store.bean.SlideMenu;

import java.util.List;

public class SlideMenuAdapter extends RecyclerView.Adapter<SlideMenuAdapter.Holder> {

    private Context context;
    private LayoutInflater inflater;
    private List<SlideMenu> dataList;
    private SlideMenu select;
    private RecyclerView recyclerView;
    private boolean showTitle = false;
    private Callback callback;

    public SlideMenuAdapter(Context context, LayoutInflater inflater, List<SlideMenu> dataList) {
        this.context = context;
        this.inflater = inflater;
        this.dataList = dataList;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
        recyclerView.getItemAnimator().setChangeDuration(0);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(R.layout.holder_slide_menu, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public SlideMenu getSelect() {
        return select;
    }

    public void setSelect(SlideMenu select) {
        this.select = select;
        notifyDataSetChanged();
        if (callback != null) callback.onClick(select);
    }

    public void hindTitle() {
        if (!showTitle || true) return;
        showTitle = false;
        notifyItemRangeChanged(0, getItemCount());
    }

    public void showTitle() {
        if (showTitle || true) return;
        showTitle = true;
        notifyItemRangeChanged(0, getItemCount());
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onClick(SlideMenu bean);
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView mTitleView;
        private ImageView mIconView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            mTitleView = itemView.findViewById(R.id.title);
            mIconView = itemView.findViewById(R.id.icon);
        }

        public void bind(SlideMenu bean) {
            itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        if (bean.equals(select)) return;
                        select = bean;
                        if (recyclerView.isComputingLayout()) {
                            recyclerView.post(new Runnable() {
                                @Override
                                public void run() {
                                    notifyItemRangeChanged(0, getItemCount());
                                }
                            });
                        } else {
                            notifyItemRangeChanged(0, getItemCount());
                        }
                        if (callback != null) callback.onClick(bean);
                    }
                }
            });

            itemView.setSelected(bean.equals(select));
            mTitleView.setVisibility(showTitle ? View.VISIBLE : View.GONE);
            mTitleView.setText(bean.getName());
            mIconView.setImageResource(bean.getIcon());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select = bean;
                    notifyItemRangeChanged(0, dataList.size());
                    if (callback != null) callback.onClick(bean);
                }
            });
        }
    }
}
