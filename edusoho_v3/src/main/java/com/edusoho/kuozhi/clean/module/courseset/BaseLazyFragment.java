package com.edusoho.kuozhi.clean.module.courseset;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edusoho.kuozhi.clean.module.base.BaseFragment;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;

/**
 * Created by DF on 2017/3/21.
 */

public abstract class BaseLazyFragment<T extends BasePresenter> extends BaseFragment<T> {

    protected View    mContentView;
    private   boolean mIsViewCreated;
    private   boolean mIsLoadDataCompleted;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (mContentView == null) {
            mContentView = inflater.inflate(initContentView(), null);
            initView(mContentView);
            initEvent();
        } else {
            ViewGroup viewGroup = (ViewGroup) mContentView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(mContentView);
            }
        }
        mIsViewCreated = true;
        return mContentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getUserVisibleHint()) {
            lazyLoad();
        }
    }

    protected abstract void initView(View view);

    protected abstract void initEvent();

    protected abstract int initContentView();

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && mIsViewCreated && !mIsLoadDataCompleted) {
            lazyLoad();
        }
    }

    protected void lazyLoad() {
        mIsLoadDataCompleted = true;
    }
}
