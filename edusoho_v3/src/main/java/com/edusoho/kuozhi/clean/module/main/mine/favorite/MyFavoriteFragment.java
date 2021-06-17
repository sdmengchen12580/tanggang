package com.edusoho.kuozhi.clean.module.main.mine.favorite;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.innerbean.Study;
import com.edusoho.kuozhi.clean.module.base.BaseFragment;
import com.edusoho.kuozhi.clean.module.main.mine.MineFragment;

import java.util.List;

/**
 * Created by JesseHuang on 2017/2/7.
 */

public class MyFavoriteFragment extends BaseFragment<MyFavoriteContract.Presenter> implements MineFragment.RefreshFragment, MyFavoriteContract.View {

    private SwipeRefreshLayout srlContent;
    private RecyclerView       rvContent;
    private MyFavoriteAdapter  myFavoriteAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_my_favorite, container, false);
        srlContent = view.findViewById(R.id.srl_content);
        srlContent.setColorSchemeResources(R.color.primary_color);

        rvContent = view.findViewById(R.id.rv_content);
        rvContent.setLayoutManager(new LinearLayoutManager(getActivity()));

        view.findViewById(R.id.v_breakline).setVisibility(View.GONE);

        initData();
        srlContent.setRefreshing(true);
        mPresenter.subscribe();
        srlContent.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.subscribe();
            }
        });
        return view;
    }

    private void initData() {
        myFavoriteAdapter = new MyFavoriteAdapter(getActivity());
        rvContent.setAdapter(myFavoriteAdapter);
        mPresenter = new MyFavoritePresenter(this);
    }

    @Override
    public void refreshData() {
        srlContent.setRefreshing(true);
        mPresenter.subscribe();
    }

    @Override
    public void showComplete(List<Study> courseSets) {
        myFavoriteAdapter.setData(courseSets);
    }

    @Override
    public void setSwpFreshing(boolean isRefreshing) {
        srlContent.setRefreshing(isRefreshing);
    }

    @Override
    public void showToast(int resId) {

    }

    @Override
    public void showToast(String msg) {

    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        View      recyclerViewItem;
        ImageView ivPic;
        TextView  tvAddNum;
        TextView  tvTitle;
        TextView  tvMore;
        View      layoutLive;
        View      vLine;

        FavoriteViewHolder(View view) {
            super(view);
            recyclerViewItem = view.findViewById(R.id.llayout_favorite_content);
            ivPic = (ImageView) view.findViewById(R.id.iv_pic);
            tvAddNum = (TextView) view.findViewById(R.id.tv_add_num);
            tvMore = (TextView) view.findViewById(R.id.tv_more);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
            vLine = view.findViewById(R.id.v_line);
            layoutLive = view.findViewById(R.id.layout_live);
        }
    }
}
