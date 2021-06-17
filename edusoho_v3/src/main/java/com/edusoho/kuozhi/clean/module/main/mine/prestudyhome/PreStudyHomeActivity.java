package com.edusoho.kuozhi.clean.module.main.mine.prestudyhome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.adapter.PreSchoolHomeAdapter;
import com.edusoho.kuozhi.clean.api.SpStudyHomeApi;
import com.edusoho.kuozhi.clean.bean.studyhome.TrainRoom;
import com.edusoho.kuozhi.clean.http.newutils.NewHttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.ui.base.BaseActivity;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;

import java.util.ArrayList;
import java.util.List;

import myutils.FastClickUtils;
import myutils.StatusBarUtil;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PreStudyHomeActivity extends BaseActivity {

    private ImageView img_close;
    private RecyclerView rv_pre;
    private PreSchoolHomeAdapter preSchoolHomeAdapter;
    private TextView my_pre;
    private LoadDialog mProcessDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int is_page = 0;//是否还有分页 0否 1是
    private boolean mIsRefreshing = false;//是否刷新中
    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setLightMode(this);
        StatusBarUtil.setTransparent(this);
        setContentView(R.layout.activity_pre_study_home);
        swipeRefreshLayout = findViewById(R.id.refresh);
        my_pre = findViewById(R.id.my_pre);
        img_close = findViewById(R.id.img_close);
        rv_pre = findViewById(R.id.rv_pre);
        rv_pre.setLayoutManager(new LinearLayoutManager(this));
        initClick();
        initRefresh();
        getData(false);
    }

    //----------------------------------工具类----------------------------------
    private void initRefresh() {
        swipeRefreshLayout.setColorSchemeResources(R.color.primary_color);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                is_page = 0;
                if (mIsRefreshing) {
                    return;
                }
                mIsRefreshing = true;
                currentPage = 1;
                getData(false);
            }
        });
    }

    private void initClick() {
        //返回
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FastClickUtils.isFastClick()) {
                    return;
                }
                finish();
            }
        });

        //我的预定
        my_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FastClickUtils.isFastClick()) {
                    return;
                }
                MyPreActivity.launch(PreStudyHomeActivity.this);
            }
        });
    }

    //入口
    public static void launch(Context context) {
        Intent intent = new Intent(context, PreStudyHomeActivity.class);
        context.startActivity(intent);
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

    protected boolean isVisBottom(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        //屏幕中最后一个可见子项的position
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        //当前屏幕所看到的子项个数
        int visibleItemCount = layoutManager.getChildCount();
        //当前RecyclerView的所有子项个数
        int totalItemCount = layoutManager.getItemCount();
        //RecyclerView的滑动状态
        int state = recyclerView.getScrollState();
        if (visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1 && state == recyclerView.SCROLL_STATE_IDLE) {
//            LogUtils.logE("测试刷新: ", "刷新");
            return true;
        } else {
//            LogUtils.logE("测试刷新: ", "不刷新");
            return false;
        }
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
                        currentPage++;
                        mIsRefreshing = true;
                        getData(true);
                    }
                }
            }
        };
    }

    private void showProcessDialog() {
        if (mProcessDialog == null) {
            mProcessDialog = LoadDialog.create(this);
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

    //----------------------------------接口----------------------------------
    private void getData(final boolean isBottom) {
        if(!isBottom){
            showProcessDialog();
        }
        NewHttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .setBaseUrl(NewHttpUtils.BASE_NEW_API_URL)
                .createApi(SpStudyHomeApi.class)
                .trainroom(currentPage, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SubscriberProcessor<List<TrainRoom>>() {

                    @Override
                    public void onError(String message) {
                        stopRefresh();
                        hideProcessDialog();
                        Toast.makeText(PreStudyHomeActivity.this, "message", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(List<TrainRoom> trainRoom) {
                        stopRefresh();
                        hideProcessDialog();
                        PreStudyHomeActivity.this.is_page = (trainRoom.size() == 10) ? 1 : 0;////是否还有分页 0否 1是
                        if (PreStudyHomeActivity.this.is_page == 1) {
                            rv_pre.addOnScrollListener(OnLoadMoreListener());
                        }
                        if (preSchoolHomeAdapter == null) {
                            preSchoolHomeAdapter = new PreSchoolHomeAdapter(PreStudyHomeActivity.this, new ArrayList<TrainRoom>(),
                                    new PreSchoolHomeAdapter.ClickCallBack() {
                                        @Override
                                        public void lookDetails(TrainRoom bean) {
                                            PreStudyActivity.launch(PreStudyHomeActivity.this,
                                                    bean.getName(), bean.getLocation(), bean.getEmail(),bean.getId());
                                        }
                                    });
                            rv_pre.setAdapter(preSchoolHomeAdapter);
                        }
                        if (isBottom) {
                            preSchoolHomeAdapter.addData(trainRoom);
                        } else {
                            preSchoolHomeAdapter.refresh(trainRoom);
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
}
