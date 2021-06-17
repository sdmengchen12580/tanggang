package com.edusoho.kuozhi.clean.module.classroom.review;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.module.classroom.BaseLazyFragment;
import com.edusoho.kuozhi.clean.module.classroom.BaseStudyDetailActivity;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.entity.course.DiscussDetail;
import com.edusoho.kuozhi.v3.handler.CourseStateCallback;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.DiscussDetailActivity;
import com.edusoho.kuozhi.v3.ui.WebViewActivity;
import com.edusoho.kuozhi.v3.ui.chat.AbstractIMChatActivity;
import com.edusoho.kuozhi.v3.ui.course.ICourseStateListener;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;

import java.util.List;

/**
 * Created by DF on 2017/1/4.
 */

public class ClassDiscussFragment extends BaseLazyFragment<ClassDiscussContract.Presenter> implements
        MessageEngine.MessageCallback, SwipeRefreshLayout.OnRefreshListener, ICourseStateListener, BaseStudyDetailActivity.WidgtState, ClassDiscussContract.View {

    private View                mLoadView;
    private View                mEmpty;
    private RecyclerView        mRvDiscuss;
    private SwipeRefreshLayout  mSwipe;
    private ClassDiscussAdapter catalogueAdapter;

    private int     mRunStatus;
    private int     mClassroomId;
    private boolean isJoin;
    private boolean isFirst = true;
    private boolean isHave  = true;
    private View mUnLoginView;
    private int i     = 0;
    private int start = 20;
    private CourseStateCallback mCourseStateCallback;

    public ClassDiscussFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((EdusohoApp) getActivity().getApplication()).registMsgSource(this);
        mClassroomId = getArguments().getInt(Const.CLASSROOM_ID);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCourseStateCallback = (CourseStateCallback) activity;
    }

    @Override
    protected void initView(View view) {
        mUnLoginView = view.findViewById(R.id.ll_no_login);
        mRvDiscuss = (RecyclerView) view.findViewById(R.id.lv_discuss);
        mLoadView = view.findViewById(R.id.ll_frame_load);
        mEmpty = view.findViewById(R.id.ll_discuss_empty);
        mSwipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mSwipe.setColorSchemeResources(R.color.primary_color);
        mSwipe.setOnRefreshListener(this);
        catalogueAdapter = new ClassDiscussAdapter(getActivity());
        mRvDiscuss.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvDiscuss.setAdapter(catalogueAdapter);
        mPresenter = new ClassDiscussPresenter(mClassroomId, this);
        setRecyclerViewListener();
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected int initContentView() {
        return R.layout.fragment_discuss;
    }

    private void setRecyclerViewListener() {
        mRvDiscuss.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem == catalogueAdapter.getItemCount() - 1) {
                    catalogueAdapter.changeMoreStatus(ClassDiscussAdapter.LOADING_MORE);
                    if (!isHave && !mEmpty.isShown()) {
                        if (isFirst) {
                            isFirst = false;
                            CommonUtil.shortCenterToast(getContext(), getString(R.string.discuss_load_data_finish));
                        }
                        catalogueAdapter.changeMoreStatus(ClassDiscussAdapter.NO_LOAD_MORE);
                        return;
                    }
                    mPresenter.loadMore(start);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //最后一个可见的ITEM
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });

        catalogueAdapter.setOnItemClickListener(new ClassDiscussAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, DiscussDetail.ResourcesBean resourcesBean) {
                if (mCourseStateCallback.isExpired()) {
                    mCourseStateCallback.handlerCourseExpired();
                    return;
                }
                startThreadActivity(resourcesBean);
            }
        });
    }

    private void initData() {
        mLoadView.setVisibility(View.VISIBLE);
        mSwipe.setEnabled(true);
        mUnLoginView.setVisibility(View.GONE);
        mPresenter.subscribe();
    }

    @Override
    protected void lazyLoad() {
        super.lazyLoad();
        if (TextUtils.isEmpty(((EdusohoApp) getActivity().getApplication()).token)) {
            mUnLoginView.setVisibility(View.VISIBLE);
            catalogueAdapter.changeMoreStatus(ClassDiscussAdapter.NO_LOAD_MORE);
            mSwipe.setEnabled(false);
        } else {
            initData();
        }
    }

    @Override
    public void initDiscuss(final DiscussDetail discussDetail) {
        mLoadView.setVisibility(View.GONE);
        if (discussDetail == null) {
            isHave = false;
            mLoadView.setVisibility(View.GONE);
            mEmpty.setVisibility(View.VISIBLE);
            catalogueAdapter.changeMoreStatus(ClassDiscussAdapter.NO_LOAD_MORE);
            return;
        }
        if (discussDetail.getResources().size() < 20) {
            isHave = false;
        }
        catalogueAdapter.setStatus(ClassDiscussAdapter.NO_LOAD_MORE);
        catalogueAdapter.setDataAndNotifyData(discussDetail.getResources());
    }

    @Override
    public void reFreshView(boolean isJoin) {
        this.isJoin = isJoin;
    }

    @Override
    public MessageType[] getMsgTypes() {
        return new MessageType[]{new MessageType(WebViewActivity.SEND_EVENT)};
    }

    @Override
    public int getMode() {
        return 0;
    }

    public void invoke(WidgetMessage message) {
        if (WebViewActivity.SEND_EVENT.equals(message.type.type)) {
            i = 1;
        }
    }

    private void startThreadActivity(DiscussDetail.ResourcesBean resourcesBean) {
        if (isJoin) {
            Bundle bundle = new Bundle();
            bundle.putString(DiscussDetailActivity.THREAD_TARGET_TYPE, "classroom");
            bundle.putInt(DiscussDetailActivity.THREAD_TARGET_ID, Integer.parseInt(resourcesBean.getTargetId()));
            bundle.putInt(AbstractIMChatActivity.FROM_ID, Integer.parseInt(resourcesBean.getId()));
            bundle.putString(AbstractIMChatActivity.TARGET_TYPE, resourcesBean.getType());
            CoreEngine.create(getContext()).runNormalPluginWithBundle("DiscussDetailActivity", getActivity(), bundle);
        } else {
            CommonUtil.shortCenterToast(getContext(), getString(R.string.discuss_join_hint));
        }
    }

    public void onRefresh() {
        mPresenter.refresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        mRunStatus = MSG_RESUME;
        if (i == 1) {
            initData();
            mEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mRunStatus = MSG_PAUSE;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((EdusohoApp) getActivity().getApplication()).unRegistMsgSource(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        i = 0;
    }

    @Override
    public void setTopViewVisibility(boolean enabled) {
        if (mSwipe != null && mUnLoginView.getVisibility() == View.GONE) {
            mSwipe.setEnabled(enabled);
        }
    }

    @Override
    public void setLoadViewVisibleible(int isVis) {
        mLoadView.setVisibility(isVis);
    }

    @Override
    public void setEmptyViewVis(int isVis) {
        mEmpty.setVisibility(isVis);
    }

    @Override
    public void showCompanies(DiscussDetail discussDetail) {
        start += 20;
        isHave = discussDetail.getResources().size() == 20;
        catalogueAdapter.setStatus(ClassDiscussAdapter.NO_LOAD_MORE);
        catalogueAdapter.AddFooterItem(discussDetail.getResources());
    }

    @Override
    public void setRecyclerViewStatus(int status) {
        catalogueAdapter.changeMoreStatus(status);
    }

    @Override
    public void setSwipeStatus(boolean status) {
        mSwipe.setRefreshing(status);
    }

    @Override
    public void adapterRefresh(List<DiscussDetail.ResourcesBean> list) {
        if (catalogueAdapter != null) {
            catalogueAdapter.reFreshData(list);
        }
    }
}
