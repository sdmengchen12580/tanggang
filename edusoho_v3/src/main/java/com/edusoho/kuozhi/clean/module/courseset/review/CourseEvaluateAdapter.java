package com.edusoho.kuozhi.clean.module.courseset.review;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseReview;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.view.ReviewStarView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DF on 2017/3/23.
 */

class CourseEvaluateAdapter extends RecyclerView.Adapter {

    private List<CourseReview> mList;
    private Context            mContext;

    private static final int TYPE_ITEM        = 0;
    private static final int TYPE_FOOTER      = 1;
    //上拉加载更多
    private static final int PULLUP_LOAD_MORE = 0;
    //正在加载中
    static final         int LOADING_MORE     = 1;
    //没有加载更多 隐藏
    static final         int NO_LOAD_MORE     = 2;

    //上拉加载更多状态-默认为0
    private int mLoadMoreStatus = 0;

    CourseEvaluateAdapter(Context context) {
        this.mContext = context;
        this.mList = new ArrayList<>();
    }

    void reFreshData(List<CourseReview> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    void addData(List<CourseReview> list) {
        this.mList.addAll(list);
        notifyDataSetChanged();
    }

    void changeMoreStatus(int status) {
        mLoadMoreStatus = status;
        notifyDataSetChanged();
    }

    public void setStatus(int status) {
        mLoadMoreStatus = status;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mItem;
        if (viewType == TYPE_FOOTER) {
            mItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.foot_item, parent, false);
            return new FooterViewHolder(mItem);
        } else {
            mItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_unjoin_evaluate, parent, false);
            return new EvaluateViewHolder(mItem);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EvaluateViewHolder) {
            CourseReview courseReview = mList.get(position);
            EvaluateViewHolder evaluateViewHolder = (EvaluateViewHolder) holder;
            evaluateViewHolder.mFrom.setText(String.format(mContext.getString(R.string.review_free), courseReview.course.getTitle()));
            evaluateViewHolder.mName.setText(courseReview.user.nickname);
            evaluateViewHolder.mTime.setText(CommonUtil.convertWeekTime(courseReview.updatedTime));
            evaluateViewHolder.mDesc.setText(courseReview.content);
            evaluateViewHolder.mStar.setRating(Integer.parseInt(courseReview.rating));
            ImageLoader.getInstance().displayImage(courseReview.user.avatar.middle, evaluateViewHolder.mUserIcon, EdusohoApp.app.mAvatarOptions);
        } else {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            switch (mLoadMoreStatus) {
                case PULLUP_LOAD_MORE:
                    footerViewHolder.mLoadText.setText("上拉加载更多...");
                    break;
                case LOADING_MORE:
                    footerViewHolder.mLoadText.setText("正加载更多...");
                    break;
                case NO_LOAD_MORE:
                    footerViewHolder.mLoadLayout.setVisibility(View.GONE);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        int count = mList.size();
        return count == 0 ? 0 : count + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mList.size()) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    private static class EvaluateViewHolder extends RecyclerView.ViewHolder {

        private ImageView      mUserIcon;
        private TextView       mName;
        private TextView       mTime;
        private TextView       mFrom;
        private TextView       mDesc;
        private ReviewStarView mStar;

        private EvaluateViewHolder(View itemView) {
            super(itemView);
            mUserIcon = (ImageView) itemView.findViewById(R.id.iv_user_icon);
            mName = (TextView) itemView.findViewById(R.id.tv_evaluate_name);
            mTime = (TextView) itemView.findViewById(R.id.tv_evaluate_time);
            mFrom = (TextView) itemView.findViewById(R.id.tv_evaluate_from);
            mDesc = (TextView) itemView.findViewById(R.id.tv_evaluate_desc);
            mStar = (ReviewStarView) itemView.findViewById(R.id.rv_evaluate_star);
        }
    }

    private static class FooterViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout mLoadLayout;
        private TextView     mLoadText;

        private FooterViewHolder(View itemView) {
            super(itemView);
            mLoadLayout = (LinearLayout) itemView.findViewById(R.id.ll_load);
            mLoadText = (TextView) itemView.findViewById(R.id.tv_load);
        }
    }
}
