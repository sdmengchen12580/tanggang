package com.edusoho.kuozhi.clean.widget.ESRecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;

/**
 * Created by JesseHuang on 2017/4/8.
 */

public class ESPullAndLoadRecyclerView extends FrameLayout {
    private RecyclerView         mRecyclerView;
    private SwipeRefreshLayout   mSwipeRefreshLayout;
    private PullLoadMoreListener mPullLoadMoreListener;
    private boolean hasMore           = true;
    private boolean isRefresh         = false;
    private boolean isLoadMore        = false;
    private boolean pullRefreshEnable = true;
    private boolean pushRefreshEnable = true;
    private View         mFooterView;
    private Context      mContext;
    private TextView     loadMoreText;
    private LinearLayout loadMoreLayout;
    private View         layoutEmpty;
    private View         layoutList;

    public ESPullAndLoadRecyclerView(Context context) {
        super(context);
        initView(context);
    }

    public ESPullAndLoadRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;

        layoutList = LayoutInflater.from(context).inflate(R.layout.pull_loadmore_layout, null);
        mSwipeRefreshLayout = layoutList.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_green_dark,
                android.R.color.holo_blue_dark, android.R.color.holo_orange_dark);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayoutOnRefresh(this));

        mRecyclerView = layoutList.findViewById(R.id.recycler_view);
        mRecyclerView.setVerticalScrollBarEnabled(true);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addOnScrollListener(new RecyclerViewOnScroll(this));

        mRecyclerView.setOnTouchListener(new onTouchRecyclerView());

        mFooterView = layoutList.findViewById(R.id.footerView);

        loadMoreLayout = layoutList.findViewById(R.id.loadMoreLayout);
        loadMoreText = layoutList.findViewById(R.id.loadMoreText);

        mFooterView.setVisibility(View.GONE);
        this.addView(layoutList);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(400,
                400);
        layoutParams.gravity = Gravity.CENTER;
        layoutEmpty = LayoutInflater.from(context).inflate(R.layout.view_empty, null);
        layoutEmpty.setVisibility(View.GONE);
        this.addView(layoutEmpty, layoutParams);
    }


    public void showEmpty(boolean visible) {
        layoutEmpty.setVisibility(visible ? View.VISIBLE : View.GONE);
    }


    /**
     * LinearLayoutManager
     */
    public void setLinearLayout() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
    }

    /**
     * GridLayoutManager
     */

    public void setGridLayout(int spanCount) {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, spanCount);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(gridLayoutManager);
    }


    /**
     * StaggeredGridLayoutManager
     */

    public void setStaggeredGridLayout(int spanCount) {
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(spanCount, LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return mRecyclerView.getLayoutManager();
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void setItemAnimator(RecyclerView.ItemAnimator animator) {
        mRecyclerView.setItemAnimator(animator);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration decor, int index) {
        mRecyclerView.addItemDecoration(decor, index);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration decor) {
        mRecyclerView.addItemDecoration(decor);
    }

    public void scrollToTop() {
        mRecyclerView.scrollToPosition(0);
    }


    public void setAdapter(RecyclerView.Adapter adapter) {
        if (adapter != null) {
            mRecyclerView.setAdapter(adapter);
        }
    }

    public void setPullRefreshEnable(boolean enable) {
        pullRefreshEnable = enable;
        setSwipeRefreshEnable(enable);
    }

    public boolean getPullRefreshEnable() {
        return pullRefreshEnable;
    }

    public void setSwipeRefreshEnable(boolean enable) {
        mSwipeRefreshLayout.setEnabled(enable);
    }

    public boolean getSwipeRefreshEnable() {
        return mSwipeRefreshLayout.isEnabled();
    }

    public void setColorSchemeResources(int... colorResIds) {
        mSwipeRefreshLayout.setColorSchemeResources(colorResIds);

    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return mSwipeRefreshLayout;
    }

    public void setRefreshing(final boolean isRefreshing) {
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                if (pullRefreshEnable)
                    mSwipeRefreshLayout.setRefreshing(isRefreshing);
            }
        });

    }

    /**
     * Solve IndexOutOfBoundsException exception
     */
    public class onTouchRecyclerView implements OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return isRefresh || isLoadMore;
        }
    }


    public boolean getPushRefreshEnable() {
        return pushRefreshEnable;
    }

    public void setPushRefreshEnable(boolean pushRefreshEnable) {
        this.pushRefreshEnable = pushRefreshEnable;
    }

    public LinearLayout getFooterViewLayout() {
        return loadMoreLayout;
    }

    public void setFooterViewBackgroundColor(int color) {
        loadMoreLayout.setBackgroundColor(ContextCompat.getColor(mContext, color));
    }

    public void setFooterViewText(CharSequence text) {
        loadMoreText.setText(text);
    }

    public void setFooterViewText(int resid) {
        loadMoreText.setText(resid);
    }

    public void setFooterViewTextColor(int color) {
        loadMoreText.setTextColor(ContextCompat.getColor(mContext, color));
    }


    public void refresh() {
        if (mPullLoadMoreListener != null) {
            mPullLoadMoreListener.onRefresh();
        }
    }

    public void loadMore() {
        if (mPullLoadMoreListener != null && hasMore) {
            mFooterView.animate()
                    .translationY(0)
                    .setDuration(300)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            mFooterView.setVisibility(View.VISIBLE);
                        }
                    })
                    .start();
            invalidate();
            mPullLoadMoreListener.onLoadMore();

        }
    }


    public void setPullLoadMoreCompleted() {
        isRefresh = false;
        setRefreshing(false);

        isLoadMore = false;
        mFooterView.animate()
                .translationY(mFooterView.getHeight())
                .setDuration(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

    }


    public void setOnPullLoadMoreListener(PullLoadMoreListener listener) {
        mPullLoadMoreListener = listener;
    }


    public boolean isLoadMore() {
        return isLoadMore;
    }

    public void setIsLoadMore(boolean isLoadMore) {
        this.isLoadMore = isLoadMore;
    }

    public boolean isRefresh() {
        return isRefresh;
    }

    public void setIsRefresh(boolean isRefresh) {
        this.isRefresh = isRefresh;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public interface PullLoadMoreListener {
        void onRefresh();

        void onLoadMore();
    }

    public void moveToPosition(int n) {
        final LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        int firstItem = manager.findFirstVisibleItemPosition();
        int lastItem = manager.findLastVisibleItemPosition();
        if (n <= firstItem) {
            mRecyclerView.smoothScrollToPosition(n);
        } else if (n <= lastItem) {
            int top = mRecyclerView.getChildAt(n - firstItem).getTop();
            mRecyclerView.scrollBy(0, top);
        } else {
            mRecyclerView.smoothScrollToPosition(n);
        }
    }
}
