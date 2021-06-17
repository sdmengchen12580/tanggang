package com.edusoho.kuozhi.clean.module.main.mine.prestudyhome.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.adapter.MyPreStateAdapter;
import com.edusoho.kuozhi.clean.api.SpStudyHomeApi;
import com.edusoho.kuozhi.clean.bean.CheckOrNo;
import com.edusoho.kuozhi.clean.bean.ScheduleListBean;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.http.newutils.NewHttpUtils;
import com.edusoho.kuozhi.clean.module.base.BaseLazyLoadFragment;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 我的预定fra
 */
public class MyPreStateFragment extends BaseLazyLoadFragment {

    private int whichType = -1;//"待审核", "已审核", "被驳回"
    private RecyclerView rv;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MyPreStateAdapter myPreStateAdapter;
    private int is_page = 0;//是否还有分页 0否 1是
    private boolean mIsRefreshing = false;//是否刷新中
    private LoadDialog mProcessDialog;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_my_pre_state;
    }

    @Override
    public void initViews(Bundle savedInstanceState, View view) {
        if (getArguments() != null) {
            whichType = getArguments().getInt("type");
        }
        Log.e("测试小标: ", "whichType = " + whichType);
        swipeRefreshLayout = view.findViewById(R.id.refresh);
        rv = view.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        initRefresh();
    }

    @Override
    public void loadData() {
        get_order_list(false);
    }

    //----------------------------------工具类----------------------------------
    public MyPreStateFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(int whichType) {
        MyPreStateFragment fragment = new MyPreStateFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", whichType);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void initRefresh() {
        swipeRefreshLayout.setColorSchemeResources(R.color.color_0292fd);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                is_page = 0;
                if (mIsRefreshing) {
                    return;
                }
                mIsRefreshing = true;
                get_order_list(false);
            }
        });
    }

    //加载更多监听
    private RecyclerView.OnScrollListener OnLoadMoreListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                Log.e("测试滑动", "滑动中");
                boolean isBottom = isVisBottom(recyclerView);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && isBottom && !mIsRefreshing) {
                    //还有数据
                    if (is_page == 1) {
                        mIsRefreshing = true;
                        get_order_list(true);
                    }
                }
            }
        };
    }

    private void stopRefresh() {
        mIsRefreshing = false;
        if (swipeRefreshLayout != null) {
            //关闭掉刷新的ui
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private void showProcessDialog() {
        if (mProcessDialog == null) {
            mProcessDialog = LoadDialog.create(getContext());
        }
        mProcessDialog.show();
    }

    private void hideProcessDialog() {
        if (mProcessDialog == null) {
            return;
        }
        if (mProcessDialog.isShowing()) {
            mProcessDialog.dismiss();
        }
    }

    //-------------------------------接口-------------------------------
    public void get_order_list(final boolean isBottom) {
        if(!isBottom){
            showProcessDialog();
        }
        NewHttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .setBaseUrl(NewHttpUtils.BASE_NEW_API_URL)
                .createApi(SpStudyHomeApi.class)
                .ScheduleList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SubscriberProcessor<ScheduleListBean>() {

                    @Override
                    public void onError(String message) {
                        stopRefresh();
                        hideProcessDialog();
                        Toast.makeText(getContext(), "message", Toast.LENGTH_SHORT).show();
                        Log.e( "测试代码: ","1111" );
                    }

                    @Override
                    public void onNext(ScheduleListBean trainRoom) {
                        Log.e( "测试代码: ","2222" );
                        stopRefresh();
                        hideProcessDialog();
//                        MyPreStateFragment.this.is_page = (trainRoom.getSchedules().size() == 10) ? 1 : 0;////是否还有分页 0否 1是
//                        if (MyPreStateFragment.this.is_page == 1) {
//                            rv.addOnScrollListener(OnLoadMoreListener());
//                        }
                        if (myPreStateAdapter == null) {
                            myPreStateAdapter = new MyPreStateAdapter(getContext(), whichType, new ArrayList<ScheduleListBean.SchedulesBean>(), new MyPreStateAdapter.ClickCallBack() {
                                @Override
                                public void cancel(String id) {
                                    checkOrNo(id,"-1");
                                }
                            });
                            rv.setAdapter(myPreStateAdapter);
                        }
                        if (isBottom) {
                            myPreStateAdapter.addData(trainRoom.getSchedules());
                        } else {
                            myPreStateAdapter.refresh(trainRoom.getSchedules());
                        }
                        //数据判断
                        /*if (preSchoolHomeAdapter.getItemCount() > 0 || trainRoom.size() > 0) {
                            ll_no_date.setVisibility(View.GONE);
                            rv_pre.setVisibility(View.VISIBLE);
                        } else {
                            ll_no_date.setVisibility(View.VISIBLE);
                            rv_pre.setVisibility(View.GONE);
                        }*/
                    }
                });
    }

    public void checkOrNo(String id, final String state) {
        showProcessDialog();
        NewHttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .setBaseUrl(NewHttpUtils.BASE_NEW_API_URL)
                .createApi(SpStudyHomeApi.class)
                .checkOrNo(id, id, state)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SubscriberProcessor<CheckOrNo>() {

                    @Override
                    public void onError(String message) {
                        hideProcessDialog();
                        Toast.makeText(getContext(), "message", Toast.LENGTH_SHORT).show();
                        Log.e("测试代码: ", "1111");
                    }

                    @Override
                    public void onNext(CheckOrNo checkOrNo) {
                        hideProcessDialog();
                        if (checkOrNo.getCode() == 0) {
                            Toast.makeText(context, "取消预定成功", Toast.LENGTH_SHORT).show();
                            mIsRefreshing = true;
                            get_order_list(false);
                        }
                    }
                });
    }
}
