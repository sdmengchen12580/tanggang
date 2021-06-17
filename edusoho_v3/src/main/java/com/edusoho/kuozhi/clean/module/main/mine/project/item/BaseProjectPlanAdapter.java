package com.edusoho.kuozhi.clean.module.main.mine.project.item;

import android.support.v7.widget.RecyclerView;

import java.util.List;

public abstract class BaseProjectPlanAdapter<T, R extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<R> {

    protected List<T> mList;

    public void clear() {
        if (mList != null) {
            mList.clear();
            notifyDataSetChanged();
        }
    }

    public void add(List<T> list) {
        if (mList != null) {
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public List<T> getAll() {
        return mList;
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }
}
