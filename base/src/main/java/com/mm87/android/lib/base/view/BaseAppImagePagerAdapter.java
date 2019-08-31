package com.mm87.android.lib.base.view;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

/**
 * Created by mendoza on 29/09/2018.
 */
public abstract class BaseAppImagePagerAdapter<T> extends PagerAdapter {

    protected List<T> items = new ArrayList<>();
    protected OnItemPageClickListener<T> onItemPageClickListener;

    public BaseAppImagePagerAdapter(List<T> items) {
        this.items = items;
    }

    public void setLstItems(List<T> lstItems) {
        this.items.clear();
        this.items.addAll(lstItems);
    }

    public interface OnItemPageClickListener<T> {
        void onItemPageClick(View view, T item, boolean isLongClick);
    }

    public void setOnItemPageClickListener(final OnItemPageClickListener<T> onItemPageClickListener) {
        this.onItemPageClickListener = onItemPageClickListener;
    }

    public T getItem(int position){
        return items.get(position);
    }


    @Override
    public int getCount() {
        return this.items.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        final LinearLayout linearLayout= new LinearLayout(container.getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        final TouchImageView imageView = new TouchImageView(container.getContext());
        LinearLayout.LayoutParams imageViewLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        imageViewLayoutParams.weight = 1f;
        imageViewLayoutParams.gravity = Gravity.CENTER;
        imageView.setLayoutParams(imageViewLayoutParams);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setAdjustViewBounds(true);


        linearLayout.setTag(getItem(position));

        if (onItemPageClickListener != null) {
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemPageClickListener.onItemPageClick(imageView, (T)linearLayout.getTag(), false);
                }
            });
            linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onItemPageClickListener.onItemPageClick(imageView, (T)linearLayout.getTag(), true);
                    return true;
                }
            });
        }

        linearLayout.addView(imageView);
        bindImage(imageView, items.get(position), position);
        container.addView(linearLayout);
        return linearLayout;
    }

    public void update(List<T> lstItems) {
        setLstItems(lstItems);
        update();
    }

    public void update() {
        try {
            this.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object view) {
        container.removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public abstract void bindImage(ImageView imageView, T item, int position);
}
