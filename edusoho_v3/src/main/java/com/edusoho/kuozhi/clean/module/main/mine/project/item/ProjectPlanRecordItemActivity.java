package com.edusoho.kuozhi.clean.module.main.mine.project.item;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.DataPageResult;
import com.edusoho.kuozhi.clean.bean.mystudy.ExamRecord;
import com.edusoho.kuozhi.clean.bean.mystudy.OfflineActivityRecord;
import com.edusoho.kuozhi.clean.bean.mystudy.PostCourseRecord;
import com.edusoho.kuozhi.clean.bean.mystudy.ProjectPlanRecord;
import com.edusoho.kuozhi.clean.module.base.BaseActivity;
import com.edusoho.kuozhi.clean.module.main.study.project.ProjectPlanDetailActivity;
import com.edusoho.kuozhi.clean.utils.Constants;
import com.edusoho.kuozhi.clean.utils.ItemClickSupport;
import com.edusoho.kuozhi.clean.utils.ToastUtils;
import com.edusoho.kuozhi.clean.widget.ESIconView;
import com.edusoho.kuozhi.clean.widget.ESProgressBar;
import com.edusoho.kuozhi.clean.widget.ESRecyclerView.ESPullAndLoadRecyclerView;
import com.edusoho.kuozhi.v3.util.Const;

public class ProjectPlanRecordItemActivity extends BaseActivity<ProjectPlanRecordItemContract.Presenter> implements ProjectPlanRecordItemContract.View {

    public static final String PROJECT_PLAN_ITEM         = "project_plan_item";
    public static final String PROJECT_PLAN_FINISHED_NUM = "project_plan_finished_num";
    public static final String PROJECT_PLAN_ASSIGN_NUM   = "project_plan_assign_num";
    private ESIconView                esivBack;
    private TextView                  tvTitle;
    private TextView                  tvItemNum;
    private ESPullAndLoadRecyclerView rvContent;
    private BaseProjectPlanAdapter    mAdapter;
    private View                      layoutEmpty;
    private int                       mOffset;
    private int                       mTotal;

    private String mType;
    private int    mFinishedNum;
    private int    mAssignNum;

    public static void launch(Context context, String type, int finishedNum, int assignNum) {
        Intent intent = new Intent(context, ProjectPlanRecordItemActivity.class);
        intent.putExtra(PROJECT_PLAN_ITEM, type);
        intent.putExtra(PROJECT_PLAN_FINISHED_NUM, finishedNum);
        intent.putExtra(PROJECT_PLAN_ASSIGN_NUM, assignNum);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_plan_record_item);
        mType = getIntent().getStringExtra(PROJECT_PLAN_ITEM);
        mFinishedNum = getIntent().getIntExtra(PROJECT_PLAN_FINISHED_NUM, 0);
        mAssignNum = getIntent().getIntExtra(PROJECT_PLAN_ASSIGN_NUM, 0);
        init();
        mPresenter = new ProjectPlanRecordItemPresenter(this);
        mPresenter.subscribe();
        mPresenter.init(mType, 0);
    }

    private void init() {
        esivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_toolbar_title);
        tvItemNum = findViewById(R.id.tv_item_num);
        layoutEmpty = findViewById(R.id.rl_empty_view);
        esivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rvContent = findViewById(R.id.rv_content);
        rvContent.setLinearLayout();
        rvContent.setPullRefreshEnable(true);
        rvContent.setPushRefreshEnable(true);
        rvContent.setOnPullLoadMoreListener(new ESPullAndLoadRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                mAdapter.clear();
                rvContent.setHasMore(true);
                mPresenter.init(mType, 0);
                mOffset = 0;
            }

            @Override
            public void onLoadMore() {
                mPresenter.init(mType, mOffset);
            }
        });
        tvItemNum.setText(String.format(getString(R.string.project_plan_num), mFinishedNum, mAssignNum));
        switch (mType) {
            case Constants.ProjectPlanItemType.TYPE_PROJECT_PLAN:
                tvTitle.setText(R.string.project_plan_record);

                ItemClickSupport.addTo(rvContent.getRecyclerView()).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        if (mAdapter != null) {
                            ProjectPlanRecord projectPlanRecord = (ProjectPlanRecord) mAdapter.getAll().get(position);
                            ProjectPlanDetailActivity.launch(ProjectPlanRecordItemActivity.this
                                    , projectPlanRecord.getProjectPlanId() + ""
                                    , ProjectPlanDetailActivity.WATCH);
                        }
                    }
                });

                break;
            case Constants.ProjectPlanItemType.TYPE_POST_COURSE:
                tvTitle.setText(R.string.project_plan_record_item_post_learn);
                break;
            case Constants.ProjectPlanItemType.TYPE_EXAM:
                tvTitle.setText(R.string.project_plan_record_item_special_test);
                break;
            case Constants.ProjectPlanItemType.TYPE_OFFLINEACTIVITY:
                tvTitle.setText(R.string.project_plan_record_item_activity);
                break;
        }
    }

    @Override
    public void showProjectPlanRecords(final DataPageResult<ProjectPlanRecord> projectPlanRecords) {
        if (mAdapter == null) {
            mAdapter = new ProjectPlanRecordAdapter(this);
            rvContent.setAdapter(mAdapter);
        }
        if (projectPlanRecords.data.size() > 0) {
            mAdapter.add(projectPlanRecords.data);
        }
        onRefresh(projectPlanRecords);
    }

    @Override
    public void showPostCourseRecords(final DataPageResult<PostCourseRecord> postCourseRecords) {
        if (mAdapter == null) {
            mAdapter = new ProjectPlanPostCourseAdapter(this);
            rvContent.setAdapter(mAdapter);
        }
        if (postCourseRecords.data.size() > 0) {
            mAdapter.add(postCourseRecords.data);
        }
        onRefresh(postCourseRecords);
    }

    @Override
    public void showExamRecords(DataPageResult<ExamRecord> examRecords) {
        if (mAdapter == null) {
            mAdapter = new ProjectPlanExamRecordAdapter(this);
            rvContent.setAdapter(mAdapter);
        }
        if (examRecords.data.size() > 0) {
            mAdapter.add(examRecords.data);
        }
        onRefresh(examRecords);
    }

    @Override
    public void showOfflineActivityRecord(DataPageResult<OfflineActivityRecord> offlineActivityRecords) {
        if (mAdapter == null) {
            mAdapter = new ProjectPlanOfflineAdapter(this);
            rvContent.setAdapter(mAdapter);
        }
        if (offlineActivityRecords.data.size() > 0) {
            mAdapter.add(offlineActivityRecords.data);
        }
        onRefresh(offlineActivityRecords);
    }

    private <T> void onRefresh(final DataPageResult<T> dataPageResult) {
        rvContent.setPullLoadMoreCompleted();
        if (dataPageResult.paging.offset >= dataPageResult.paging.total) {
            mOffset = dataPageResult.paging.total;
            if (mOffset == mTotal) {
                ToastUtils.show(ProjectPlanRecordItemActivity.this, "没有更多数据了~");
                rvContent.setHasMore(false);
            }
        } else {
            mOffset = dataPageResult.paging.offset + Const.LIMIT;
        }
        mTotal = dataPageResult.paging.total;
    }

    @Override
    public void showEmpty(boolean visible) {
        layoutEmpty.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    static class ProjectPlanRecordViewHolder extends RecyclerView.ViewHolder {
        TextView      tvName;
        TextView      tvTime;
        ESProgressBar espbProgress;
        TextView      tvProgressNum;

        public ProjectPlanRecordViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvName);
            tvTime = view.findViewById(R.id.tvTime);
            espbProgress = view.findViewById(R.id.espb_project_plan_progress);
            tvProgressNum = view.findViewById(R.id.tv_progress_num);
        }
    }

    static class ProjectPlanPostCourseViewHolder extends RecyclerView.ViewHolder {
        TextView      tvCourseName;
        TextView      tvTeacher;
        TextView      tvDuration;
        ESProgressBar espbProgress;
        TextView      tvProgressNum;

        public ProjectPlanPostCourseViewHolder(View view) {
            super(view);
            tvCourseName = view.findViewById(R.id.tv_course_name);
            tvTeacher = view.findViewById(R.id.tv_teacher);
            tvDuration = view.findViewById(R.id.tv_duration);
            espbProgress = view.findViewById(R.id.espb_project_plan_progress);
            tvProgressNum = view.findViewById(R.id.tv_progress_num);
        }
    }

    static class ProjectPlanExamRecordViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseName;
        TextView tvScorePassed;
        TextView tvExamTime;

        public ProjectPlanExamRecordViewHolder(View view) {
            super(view);
            tvCourseName = view.findViewById(R.id.tv_course_name);
            tvScorePassed = view.findViewById(R.id.tv_score_passed);
            tvExamTime = view.findViewById(R.id.tv_exam_time);
        }
    }

    static class ProjectPlanOfflineActivityViewHolder extends RecyclerView.ViewHolder {
        TextView tvActivityName;
        TextView tvCategoryName;
        TextView tvSignIn;
        TextView tvPassed;
        TextView tvTime;
        TextView tvLocation;

        public ProjectPlanOfflineActivityViewHolder(View view) {
            super(view);
            tvActivityName = view.findViewById(R.id.tv_activity_name);
            tvCategoryName = view.findViewById(R.id.tv_category_name);
            tvSignIn = view.findViewById(R.id.tv_sign);
            tvPassed = view.findViewById(R.id.tv_passed);
            tvTime = view.findViewById(R.id.tv_time);
            tvLocation = view.findViewById(R.id.tv_location);
        }
    }
}
