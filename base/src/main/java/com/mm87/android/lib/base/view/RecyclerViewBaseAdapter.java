package com.mm87.android.lib.base.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mm87.android.lib.base.util.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by marco.mendoza on 12/01/2017.
 */
public abstract class RecyclerViewBaseAdapter<VH extends RecyclerView.ViewHolder, T>
        extends RecyclerView.Adapter<VH> implements View.OnClickListener, View.OnLongClickListener {

    protected List<T> items = new ArrayList<>();
    protected OnItemClickListener<T> onItemClickListener;

    public RecyclerViewBaseAdapter(List<T> items) {
        this.items = items;
    }

    public static interface OnItemClickListener<T> {
        public void onItemClick(View view, T item, boolean isLongClick);
    }

    public void setOnItemClickListener(final OnItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public abstract int getResource(int viewType);

    public abstract VH newInstanceViewHolder(View view, int viewType);

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext())
                .inflate(getResource(viewType), parent, false);
        convertView.setOnClickListener(this);
        convertView.setOnLongClickListener(this);

        VH holder = newInstanceViewHolder(convertView, viewType);
        convertView.setTag(holder);
        ViewUtils.bindViewAnnotation(parent.getContext(), holder, convertView);
        return  holder;
    }

    @Override
    public void onClick(View v) {
        handleClick((VH)v.getTag(),v, false);
    }

    @Override
    public boolean onLongClick(View v) {
        return handleClick((VH)v.getTag(),v, true);
    }

    protected boolean handleClick(VH holder, View v, boolean isLongClick) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(v, items.get(holder.getLayoutPosition()), isLongClick);
            return true;
        }
        return false;
    }

    public T getItem(int position){
        return items.get(position);
    }


    public abstract void bindValue(VH holder, T item, int position);

    @Override
    public void onBindViewHolder(VH vh, int position){
        bindValue(vh, getItem(position), position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


}
