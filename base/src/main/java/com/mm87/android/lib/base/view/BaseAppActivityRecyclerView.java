package com.mm87.android.lib.base.view;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mm87.android.lib.base.R;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Created by marco.mendoza on 14/07/2017.
 */

public abstract class BaseAppActivityRecyclerView<T, VH extends RecyclerView.ViewHolder> extends BaseActivity
        implements RecyclerViewBaseAdapter.OnItemClickListener<T> {

    protected TextView txtNoHayDatos;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected XRecyclerView recyclerView;

    protected RecyclerAdapter recyclerAdapter;
    protected List<T> lstItems = new ArrayList<T>();
    protected RecyclerView.LayoutManager layoutManager;

    public void setLstItems(List<T> lstItems) {
        this.lstItems.clear();
        this.lstItems.addAll(lstItems);
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    protected int getIdResLayout() {
        return R.layout.fragment_recyclerview;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        txtNoHayDatos = findViewById(R.id.txt_empty_list);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerAdapter = new RecyclerAdapter(this.lstItems);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(this.layoutManager == null ? new LinearLayoutManager(this) : this.layoutManager);
        recyclerView.setAdapter(this.recyclerAdapter);
        recyclerAdapter.setOnItemClickListener(this);
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setColorSchemeResources(R.color.design_default_color_primary);
        update();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // release memory
        if (recyclerView != null) {
            recyclerView.destroy();
            recyclerView = null;
        }
    }

    public void refresh() {
        try {
            recyclerView.refresh();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void update(List<T> lstItems) {
        setLstItems(lstItems);
        update();
    }

    public void update(List<T> lstItems, boolean reset) {
        if (reset) {
            update(lstItems);
        } else {
            this.lstItems.addAll(lstItems);
            update();
        }
    }

    public void update() {
        try {
            recyclerAdapter.notifyDataSetChanged();
            if (this.lstItems != null && !this.lstItems.isEmpty()) {
                txtNoHayDatos.setVisibility(View.GONE);
            } else {
                txtNoHayDatos.setVisibility(View.VISIBLE);
            }
            recyclerView.refreshComplete();
            recyclerView.loadMoreComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract int viewItem();

    public abstract VH newInstanciaVH(View v);

    public abstract void bindValues(VH holder, T item, int position);

    protected class RecyclerAdapter extends RecyclerViewBaseAdapter<VH, T> {

        public RecyclerAdapter(List<T> items) {
            super(items);
        }

        @Override
        public int getResource(int viewType) {
            return viewItem();
        }

        @Override
        public VH newInstanceViewHolder(View view, int viewType) {
            return newInstanciaVH(view);
        }

        @Override
        public void bindValue(VH holder, T item, int position) {
            bindValues(holder, item, position);
        }

        @Override
        protected boolean handleClick(VH holder, View v, boolean isLongClick) {
            /// XRecyclerView aumenta un item al adaptador por lo cual restar -1 en getAdapterPosition
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, items.get(holder.getAdapterPosition() - 1), isLongClick);
                return true;
            }
            return false;
        }
    }

}
