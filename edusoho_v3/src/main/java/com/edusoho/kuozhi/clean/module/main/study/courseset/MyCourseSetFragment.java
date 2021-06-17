package com.edusoho.kuozhi.clean.module.main.study.courseset;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.StudyCourse;
import com.edusoho.kuozhi.clean.module.base.BaseFragment;
import com.edusoho.kuozhi.clean.module.course.CourseProjectActivity;
import com.edusoho.kuozhi.clean.widget.ESRecyclerView.ESPullAndLoadRecyclerView;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by RexXiang on 2018/1/15.
 */

public class MyCourseSetFragment extends BaseFragment<MyCourseSetContract.Presenter> implements MyCourseSetContract.View {

    final static int STUDY_ING      = 0;
    final static int STUDY_FINISHED = 1;

    private ESPullAndLoadRecyclerView mCourseSetRecyclerView;
    private TextView                  mStudying;
    private TextView                  mStudyFinished;
    private CourseSetAdapter          mAdapter;
    private List<StudyCourse> mList = new ArrayList<StudyCourse>();
    private int                       mCurrentStatus;
    private StatusChangeClickListener clickListener;
    private int                       mSelectedTextId;
    private View                      layoutEmpty;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_my_course_set, container, false);
        layoutEmpty = view.findViewById(R.id.rl_empty_view);
        mCourseSetRecyclerView = view.findViewById(R.id.rv_course_set);
        mStudying = view.findViewById(R.id.tv_study_ing);
        mStudyFinished = view.findViewById(R.id.tv_study_finished);
        mCourseSetRecyclerView.setGridLayout(2);
        mCourseSetRecyclerView.setOnPullLoadMoreListener(new ESPullAndLoadRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                mPresenter.getCourseSet(mCurrentStatus);
            }

            @Override
            public void onLoadMore() {

            }
        });
        mCourseSetRecyclerView.setPullRefreshEnable(true);
        mCourseSetRecyclerView.setPushRefreshEnable(false);
        initData();
        return view;
    }

    private void initData() {
        mPresenter = new MyCourseSetPresenter(this);
        mCurrentStatus = STUDY_ING;
        mSelectedTextId = R.id.tv_study_ing;
        clickListener = new StatusChangeClickListener();
        mStudying.setOnClickListener(clickListener);
        mStudyFinished.setOnClickListener(clickListener);
        mPresenter.subscribe();
    }

    private class StatusChangeClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (view.getId() == mSelectedTextId) {
                return;
            }
            if (view.getId() == R.id.tv_study_ing) {
                mStudying.setBackground(getResources().getDrawable(R.drawable.shape_study_selected));
                mStudying.setTextColor(getResources().getColor(R.color.white));
                mStudyFinished.setBackground(getResources().getDrawable(R.drawable.shape_study_un_selected));
                mStudyFinished.setTextColor(getResources().getColor(R.color.primary_color));
                mCurrentStatus = STUDY_ING;
            }
            if (view.getId() == R.id.tv_study_finished) {
                mStudyFinished.setBackground(getResources().getDrawable(R.drawable.shape_study_selected));
                mStudyFinished.setTextColor(getResources().getColor(R.color.white));
                mStudying.setBackground(getResources().getDrawable(R.drawable.shape_study_un_selected));
                mStudying.setTextColor(getResources().getColor(R.color.primary_color));
                mCurrentStatus = STUDY_FINISHED;
            }
            mSelectedTextId = view.getId();
            mPresenter.getCourseSet(mCurrentStatus);
        }
    }

    @Override
    public void refreshView(List<StudyCourse> studyCourses, int status) {
        mList = studyCourses;
        mCurrentStatus = status;
        if (mList.size() > 0) {
            layoutEmpty.setVisibility(View.GONE);
            mCourseSetRecyclerView.setVisibility(View.VISIBLE);
            mAdapter = new CourseSetAdapter(getContext(), mList, mCurrentStatus);
            mCourseSetRecyclerView.setAdapter(mAdapter);
        } else {
            layoutEmpty.setVisibility(View.VISIBLE);
            mCourseSetRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void refreshCompleted() {
        mCourseSetRecyclerView.setPullLoadMoreCompleted();
    }

    @Override
    public void clearData() {
        if (mAdapter != null) {
            mAdapter.clearData();
        }
    }

    private class CourseSetAdapter extends RecyclerView.Adapter {

        private Context           mContext;
        private List<StudyCourse> mList;
        private int               studyStatus;

        private CourseSetAdapter(Context context, List<StudyCourse> list, int status) {
            mContext = context;
            mList = list;
            studyStatus = status;
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
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_study_course_set, parent, false);
            return new CourseSetViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            StudyCourse studyCourse = mList.get(position);
            final int courseId = studyCourse.id;
            CourseSetViewHolder viewHolder = (CourseSetViewHolder) holder;
            ImageLoader.getInstance().displayImage(studyCourse.courseSet.cover.middle, viewHolder.mCourseSetImage, EdusohoApp.app.mOptions);
            viewHolder.mCourseSetTitle.setText(studyCourse.title);
            viewHolder.mItemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CourseProjectActivity.launch(mContext, courseId);
                }
            });
            if (studyStatus == STUDY_ING) {
                viewHolder.mCourseSetStudyTime.setVisibility(View.GONE);
                viewHolder.mProgressBar.setVisibility(View.VISIBLE);
//                原代码
//                float progress = (studyCourse.learnedCompulsoryTaskNum / studyCourse.compulsoryTaskNum) * 100;
//                修改后代码
                float progress = 0f;
                if (studyCourse.compulsoryTaskNum > 0) {
                    progress = (studyCourse.learnedCompulsoryTaskNum / studyCourse.compulsoryTaskNum) * 100;
                }
//                截止

                viewHolder.mProgressBar.setProgress((int) progress);
            } else if (studyStatus == STUDY_FINISHED) {
                viewHolder.mProgressBar.setVisibility(View.GONE);
                viewHolder.mCourseSetStudyTime.setVisibility(View.VISIBLE);
                BigDecimal bigDecimal = new BigDecimal(studyCourse.totalLearnTime / 3600);
                viewHolder.mCourseSetStudyTime.setText("学习总时长：" + bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP) + "小时");
            }
        }
    }

    private class CourseSetViewHolder extends RecyclerView.ViewHolder {

        private ImageView    mCourseSetImage;
        private ProgressBar  mProgressBar;
        private TextView     mCourseSetTitle;
        private TextView     mCourseSetStudyTime;
        private LinearLayout mItemLayout;

        private CourseSetViewHolder(View view) {
            super(view);
            mItemLayout = (LinearLayout) view.findViewById(R.id.ll_item_study_course_set);
            mCourseSetImage = (ImageView) view.findViewById(R.id.iv_study_course_set);
            mProgressBar = (ProgressBar) view.findViewById(R.id.pb_study_course_set);
            mCourseSetTitle = (TextView) view.findViewById(R.id.tv_study_course_set_title);
            mCourseSetStudyTime = (TextView) view.findViewById(R.id.tv_study_course_set_time);
        }
    }
}
