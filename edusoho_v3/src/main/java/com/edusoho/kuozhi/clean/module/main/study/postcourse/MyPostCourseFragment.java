package com.edusoho.kuozhi.clean.module.main.study.postcourse;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.mystudy.PostCourseModel;
import com.edusoho.kuozhi.clean.bean.mystudy.PostCoursesProgress;
import com.edusoho.kuozhi.clean.module.base.BaseFragment;
import com.edusoho.kuozhi.clean.module.course.CourseProjectActivity;
import com.edusoho.kuozhi.clean.widget.ESRecyclerView.ESPullAndLoadRecyclerView;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RexXiang on 2018/1/15.
 */

public class MyPostCourseFragment extends BaseFragment<MyPostCourseContract.Presenter> implements MyPostCourseContract.View {

    private ESPullAndLoadRecyclerView mPostCourseRecyclerView;
    private TextView                  mPostNameView;
    private TextView                  mFinishedCount;
    private TextView                  mTotalCount;
    private View                      layoutEmpty;
    private List<PostCourseModel> mList = new ArrayList<PostCourseModel>();
    private String              mPostName;
    private PostCoursesProgress mProgress;
    private PostCourseAdapter   mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_my_post_course, container, false);
        layoutEmpty = view.findViewById(R.id.rl_empty_view);
        mPostCourseRecyclerView = view.findViewById(R.id.rv_post_course);
        mPostNameView = view.findViewById(R.id.tv_me_post_name);
        mFinishedCount = view.findViewById(R.id.tv_post_course_finished_count);
        mTotalCount = view.findViewById(R.id.tv_post_course_total_count);
        mPostCourseRecyclerView.setLinearLayout();
        mPostCourseRecyclerView.setOnPullLoadMoreListener(new ESPullAndLoadRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                mPresenter.subscribe();
            }

            @Override
            public void onLoadMore() {

            }
        });
        mPostCourseRecyclerView.setPullRefreshEnable(true);
        mPostCourseRecyclerView.setPushRefreshEnable(false);
        initData();
        return view;
    }

    private void initData() {
        mPresenter = new MyPostCoursePresenter(this);
        mPresenter.subscribe();
    }

    @Override
    public void refreshCompleted() {
        mPostCourseRecyclerView.setPullLoadMoreCompleted();
    }

    @Override
    public void refreshView(List<PostCourseModel> list, String postName, PostCoursesProgress progress) {
        mList = list;
        mPostName = postName;
        mProgress = progress;
        if (mList.size() > 0) {
            layoutEmpty.setVisibility(View.GONE);
            mPostCourseRecyclerView.setVisibility(View.VISIBLE);
            mAdapter = new PostCourseAdapter(getContext(), mList);
            mPostCourseRecyclerView.setAdapter(mAdapter);
        } else {
            layoutEmpty.setVisibility(View.VISIBLE);
            mPostCourseRecyclerView.setVisibility(View.GONE);
        }
        if (postName == null) {
            mPostNameView.setText("暂无岗位");
        } else {
            SpannableString spannableString = new SpannableString(String.format("我的岗位：%s", mPostName));
            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.primary_font_color)), 5, mPostName.length() + 5, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            mPostNameView.setText(spannableString);
        }
        mFinishedCount.setText(mProgress.getFinishedCount());
        mTotalCount.setText("/ " + mProgress.getTotalCount());
    }

    @Override
    public void clearData() {
        if (mAdapter != null) {
            mAdapter.clearData();
        }
    }

    private class PostCourseAdapter extends RecyclerView.Adapter {

        private Context               mContext;
        private List<PostCourseModel> mList;

        private PostCourseAdapter(Context context, List<PostCourseModel> list) {
            mContext = context;
            mList = list;
        }

        private void clearData() {
            mList.clear();
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_study_assignment_without_button, parent, false);
            return new PostCourseViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final PostCourseModel postCourseModel = mList.get(position);
            PostCourseViewHolder viewHolder = (PostCourseViewHolder) holder;
            viewHolder.mBottomView.setVisibility(View.GONE);
            viewHolder.mAssignmentType.setVisibility(View.GONE);
            ImageLoader.getInstance().displayImage(postCourseModel.getCourseSet().getCover().getMiddle(), viewHolder.mAssignmentImage, EdusohoApp.app.mOptions);
            viewHolder.mAssignmentTitle.setText(postCourseModel.getCourseSet().getTitle());
            viewHolder.mAssignmentTime.setText(String.format("学习时长：%.1f小时", (float) postCourseModel.getTotalLearnTime() / 3600));
            viewHolder.mAssignmentSubTitle.setText("学习进度：");
            int percent = (int) (postCourseModel.getLearnedCompulsoryTaskNum() / postCourseModel.getCompulsoryTaskNum() * 100);
            viewHolder.mProgressBar.setProgress(percent);
            viewHolder.mPercent.setText((percent > 100 ? 100 : percent) + "%");
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CourseProjectActivity.launch(mContext, Integer.parseInt(postCourseModel.getCourseId()));
                }
            });
        }
    }


    private class PostCourseViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout mBottomView;
        private ImageView      mAssignmentImage;
        private TextView       mAssignmentTitle;
        private TextView       mAssignmentType;
        private TextView       mAssignmentTime;
        private TextView       mAssignmentSubTitle;
        private ProgressBar    mProgressBar;
        private TextView       mPercent;

        private PostCourseViewHolder(View view) {
            super(view);
            mBottomView = (RelativeLayout) view.findViewById(R.id.rl_assignment_bottom);
            mAssignmentImage = (ImageView) view.findViewById(R.id.iv_assignment);
            mAssignmentTitle = (TextView) view.findViewById(R.id.tv_assignment_title);
            mAssignmentType = (TextView) view.findViewById(R.id.tv_assignment_type);
            mAssignmentTime = (TextView) view.findViewById(R.id.tv_assignment_time);
            mAssignmentSubTitle = (TextView) view.findViewById(R.id.tv_assignment_sub_title);
            mProgressBar = (ProgressBar) view.findViewById(R.id.item_progressbar);
            mPercent = (TextView) view.findViewById(R.id.tv_percent);
        }
    }
}
