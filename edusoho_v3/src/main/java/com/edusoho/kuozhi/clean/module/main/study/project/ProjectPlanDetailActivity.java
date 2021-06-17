package com.edusoho.kuozhi.clean.module.main.study.project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.bean.mystudy.ProjectPlan;
import com.edusoho.kuozhi.clean.bean.mystudy.ProjectPlan.ItemsDetailBean;
import com.edusoho.kuozhi.clean.module.base.BaseActivity;
import com.edusoho.kuozhi.clean.module.course.CourseProjectActivity;
import com.edusoho.kuozhi.clean.module.main.study.exam.ExamInfoActivity;
import com.edusoho.kuozhi.clean.module.main.study.survey.EvaluationSurveyActivity;
import com.edusoho.kuozhi.clean.utils.biz.BizUtils;
import com.edusoho.kuozhi.clean.widget.ESDividerItemDecoration;
import com.edusoho.kuozhi.clean.widget.ESIconView;
import com.edusoho.kuozhi.clean.widget.ESRecyclerView.ESPullAndLoadRecyclerView;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RexXiang on 2018/3/20.
 */

public class ProjectPlanDetailActivity extends BaseActivity<ProjectPlanDetailContract.Presenter> implements ProjectPlanDetailContract.View {

    public static final String ENTER_TYPE = "enter_type";
    public static final String PROJECT_ID = "project_id";
    public static final String WATCH      = "watch";
    public static final String OPERATE    = "operate";

    private String                       mProjectId;
    private String                       mEnterType;
    private LoadDialog                   mProcessDialog;
    private ProjectPlan                  mProjectPlan;
    private ESIconView                   ivback;
    private TextView                     tvtoolbartitle;
    private ESIconView                   ivscan;
    private Toolbar                      tbtoolbar;
    private View                         layoutEmptyView;
    private ESPullAndLoadRecyclerView    rvprojectdetaillist;
    private ProjectPlanDetailListAdapter mAdapter;


    public static void launch(Context context, String projectId, String type) {
        Intent intent = new Intent();
        intent.putExtra(PROJECT_ID, projectId);
        intent.putExtra(ENTER_TYPE, type);
        intent.setClass(context, ProjectPlanDetailActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_plan_detail);
        mProjectId = getIntent().getStringExtra(PROJECT_ID);
        mEnterType = getIntent().getStringExtra(ENTER_TYPE);
        initView();
        initData();
    }

    private void initView() {
        this.rvprojectdetaillist = findViewById(R.id.rv_project_detail_list);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) rvprojectdetaillist.getLayoutParams();
        layoutParams.gravity = Gravity.CENTER;
        rvprojectdetaillist.setLayoutParams(layoutParams);
        this.tbtoolbar = findViewById(R.id.tb_toolbar);
        this.layoutEmptyView = findViewById(R.id.rl_empty_view);
        this.ivscan = findViewById(R.id.iv_scan);
        this.tvtoolbartitle = findViewById(R.id.tv_toolbar_title);
        this.ivback = findViewById(R.id.iv_back);
        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProjectPlanDetailActivity.this.finish();
            }
        });
        rvprojectdetaillist.setLinearLayout();
        ESDividerItemDecoration esDividerItemDecoration = new ESDividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        esDividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.project_plan_divider));
        rvprojectdetaillist.addItemDecoration(esDividerItemDecoration);
        rvprojectdetaillist.setPullRefreshEnable(true);
        rvprojectdetaillist.setPushRefreshEnable(false);
        rvprojectdetaillist.setOnPullLoadMoreListener(new ESPullAndLoadRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                mPresenter.subscribe();
                rvprojectdetaillist.setPullLoadMoreCompleted();
            }

            @Override
            public void onLoadMore() {

            }
        });
        this.ivscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProjectPlanDetailActivity.this, ProjectQrCodeScanActivity.class);
                startActivity(intent);
            }
        });
        ivscan.setVisibility(mEnterType.equals(WATCH) ? View.INVISIBLE : View.VISIBLE);
    }

    private void initData() {
        mPresenter = new ProjectPlanDetailPresenter(this, mProjectId);
        mPresenter.subscribe();
    }

    @Override
    public void refreshView(ProjectPlan projectPlan) {
        mProjectPlan = projectPlan;
        tvtoolbartitle.setText(mProjectPlan.getName());
        rvprojectdetaillist.setVisibility(View.VISIBLE);
        mAdapter = new ProjectPlanDetailListAdapter(this, mProjectPlan.getItemsDetail());
        rvprojectdetaillist.setAdapter(mAdapter);
        rvprojectdetaillist.showEmpty(projectPlan.getItemsDetail() != null &&
                projectPlan.getItemsDetail().size() == 0);
    }

    @Override
    public void showProcessDialog(boolean isShow) {
        if (isShow) {
            showProcessDialog();
        } else {
            hideProcessDialog();
        }
    }

    @Override
    public void clearData() {
        if (mAdapter != null) {
            mAdapter.clearData();
        }
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

    private class ProjectPlanDetailListAdapter extends RecyclerView.Adapter {

        private Context               mContext;
        private List<ItemsDetailBean> mList;

        private ProjectPlanDetailListAdapter(Context context, List<ItemsDetailBean> list) {
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
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_project_detail_list, parent, false);
            return new ProjectPlanDetailListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ProjectPlanDetailListViewHolder viewHolder = (ProjectPlanDetailListViewHolder) holder;
            ItemsDetailBean detailBean = mList.get(position);
            viewHolder.mItemTitle.setText(detailBean.getTitle());
            viewHolder.mBottomItemListView.setVisibility(View.GONE);
            viewHolder.mItemExamCount.setVisibility(View.GONE);
            viewHolder.itemView.setOnClickListener(null);
            if (detailBean.getTaskInfo() instanceof ArrayList) {
                if (((ArrayList) detailBean.getTaskInfo()).size() > 0) {
                    viewHolder.mBottomItemListView.setVisibility(View.VISIBLE);
                    viewHolder.mItemListView.setLayoutManager(new LinearLayoutManager(mContext));
                    viewHolder.mItemListView.setAdapter(new ItemDetailAdapter(mContext, (ArrayList) detailBean.getTaskInfo()));
                }
            }
            ItemsDetailBean.StudyResultBean studyResultBean = null;
            if (detailBean.getStudyResult() instanceof LinkedTreeMap) {
                Object object = detailBean.getStudyResult();
                Gson gson = new Gson();
                studyResultBean = gson.fromJson(gson.toJson(object), ItemsDetailBean.StudyResultBean.class);
            }
            if (detailBean.getTargetType().equals("course")) {
                final String courseId = detailBean.getTargetId();
                viewHolder.mTypeIcon.setText(R.string.new_font_type_course);
                viewHolder.mItemTeacher.setText(String.format("课程讲师：%s", detailBean.getTeacherName()));
                if (Long.parseLong(detailBean.getEndTime()) - Long.parseLong(detailBean.getStartTime()) > 86400) {
                    viewHolder.mItemTime.setText(String.format("计划时间：%s-%s", AppUtil.timeStampToDate(detailBean.getStartTime(), "MM-dd HH:mm"), AppUtil.timeStampToDate(detailBean.getEndTime(), "MM-dd HH:mm")));
                } else {
                    viewHolder.mItemTime.setText(String.format("计划时间：%s-%s", AppUtil.timeStampToDate(detailBean.getStartTime(), "MM-dd HH:mm"), AppUtil.timeStampToDate(detailBean.getEndTime(), "HH:mm")));
                }
                if (studyResultBean != null) {
                    if (studyResultBean.getStatus().equals("ongoing") && isShowAction()) {
                        viewHolder.mItemStatus.setTextColor(getResources().getColor(R.color.primary));
                        viewHolder.mItemStatus.setText("继续学习");
                    }
                    if (studyResultBean.getStatus().equals("finished")) {
                        viewHolder.mItemStatus.setTextColor(getResources().getColor(R.color.button_success_color));
                        viewHolder.mItemStatus.setText("已完成");
                    }
                    if (studyResultBean.getStatus().equals("notStart") && isShowAction()) {
                        viewHolder.mItemStatus.setTextColor(getResources().getColor(R.color.primary));
                        viewHolder.mItemStatus.setText("开始学习");
                    }
                    if (isShowAction()) {
                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CourseProjectActivity.launch(mContext, Integer.parseInt(courseId));
                            }
                        });
                    }
                }
            }
            if (detailBean.getTargetType().equals("offline_course")) {
                viewHolder.mTypeIcon.setText(R.string.new_font_type_offline_course);
                viewHolder.mItemTeacher.setText(String.format("课程讲师：%s", detailBean.getTeacherName()));
                if (Long.parseLong(detailBean.getEndTime()) - Long.parseLong(detailBean.getStartTime()) > 86400) {
                    viewHolder.mItemTime.setText(String.format("计划时间：%s-%s", AppUtil.timeStampToDate(detailBean.getStartTime(), "MM-dd HH:mm"), AppUtil.timeStampToDate(detailBean.getEndTime(), "MM-dd HH:mm")));
                } else {
                    viewHolder.mItemTime.setText(String.format("计划时间：%s-%s", AppUtil.timeStampToDate(detailBean.getStartTime(), "MM-dd HH:mm"), AppUtil.timeStampToDate(detailBean.getEndTime(), "HH:mm")));
                }
            }
            if (detailBean.getTargetType().equals("exam")) {
                viewHolder.mTypeIcon.setText(R.string.new_font_type_exam);
                final String examId = detailBean.getTargetId();
                if (Long.parseLong(detailBean.getEndTime()) - Long.parseLong(detailBean.getStartTime()) > 86400) {
                    viewHolder.mItemTeacher.setText(String.format("考试时间：%s 至 %s",
                            AppUtil.timeStampToDate(detailBean.getStartTime(), "MM-dd"),
                            AppUtil.timeStampToDate(detailBean.getEndTime(), "MM-dd")));
                } else {
                    viewHolder.mItemTeacher.setText(String.format("考试时间：%s - %s",
                            AppUtil.timeStampToDate(detailBean.getStartTime(), "MM-dd HH:mm"),
                            AppUtil.timeStampToDate(detailBean.getEndTime(), "HH:mm")));
                }

                viewHolder.mItemStatus.setOnClickListener(null);

                if (detailBean.getTaskInfo() instanceof LinkedTreeMap) {
                    Object object = detailBean.getTaskInfo();
                    Gson gson = new Gson();
                    ProjectPlan.ExamTaskInfo examTaskInfo = gson.fromJson(gson.toJson(object), ProjectPlan.ExamTaskInfo.class);
                    viewHolder.mItemTime.setText("考试次数：");
                    viewHolder.mLayoutSubTitle.setVisibility(View.VISIBLE);
                    viewHolder.mItemTime.setVisibility(View.VISIBLE);
                    viewHolder.mItemExamCount.setVisibility(View.VISIBLE);
                    if (examTaskInfo.getResitTimes().equals("0")) {
                        viewHolder.mItemExamCount.setBackground(getResources().getDrawable(R.drawable.shape_assignment_exam_green));
                        viewHolder.mItemExamCount.setTextColor(getResources().getColor(R.color.assignment_exam_green));
                        viewHolder.mItemExamCount.setText("不限");
                    } else {
                        if (examTaskInfo.getRemainingResitTimes() == 0 || examTaskInfo.getExamTimeStatus().equals("expired")) {
                            viewHolder.mLayoutSubTitle.setVisibility(View.GONE);
                        } else {
                            if (examTaskInfo.getRemainingResitTimes() == 1) {
                                viewHolder.mItemExamCount.setBackground(getResources().getDrawable(R.drawable.shape_assignment_exam_red));
                                viewHolder.mItemExamCount.setTextColor(getResources().getColor(R.color.assignment_exam_red));
                                viewHolder.mItemExamCount.setText("1次");
                            } else if (examTaskInfo.getRemainingResitTimes() > 1) {
                                viewHolder.mItemExamCount.setBackground(getResources().getDrawable(R.drawable.shape_assignment_exam_yellow));
                                viewHolder.mItemExamCount.setTextColor(getResources().getColor(R.color.assignment_exam_yellow));
                                viewHolder.mItemExamCount.setText(String.format("%d次", examTaskInfo.getRemainingResitTimes()));
                            }
                        }
                    }
                    if (studyResultBean != null) {
                        boolean unlimited = examTaskInfo.getResitTimes().equals("0");
                        if (studyResultBean.getStatus().equals("reviewing")) {
                            viewHolder.mItemStatus.setTextColor(getResources().getColor(R.color.assignment_exam_yellow));
                            viewHolder.mItemStatus.setBackground(getResources().getDrawable(R.drawable.shape_assignment_exam_yellow));
                            viewHolder.mItemStatus.setText("批阅中");
                            viewHolder.mItemStatus.setOnClickListener(null);
                        } else {
                            if (studyResultBean.getPassStatus().equals("unpassed")) {
                                if (examTaskInfo.getExamTimeStatus().equals("expired") && unlimited &&
                                        examTaskInfo.getRemainingResitTimes() != 0) {
                                    viewHolder.mItemStatus.setText("已过期");
                                    viewHolder.mItemStatus.setTextColor(getResources().getColor(R.color.assignment_exam_red));
                                    viewHolder.mItemStatus.setOnClickListener(null);
                                } else if (studyResultBean.getStatus().equals("doing") && isShowAction()) {
                                    viewHolder.mItemStatus.setText("继续考试");
                                    viewHolder.mItemStatus.setTextColor(getResources().getColor(R.color.assignment_exam_blue));
                                    viewHolder.mItemStatus.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ExamInfoActivity.launch(mContext, examId);
                                        }
                                    });
                                } else if (examTaskInfo.getExamTimeStatus().equals("ongoing")) {
                                    String scoreString = String.format("%s / %s  未通过",
                                            BizUtils.showTestScore(studyResultBean.getScore()), BizUtils.showTestScore(examTaskInfo.getTestPaperScore()));
                                    if ((examTaskInfo.getRemainingResitTimes() != 0 || unlimited) && isShowAction()) {
                                        SpannableString spannableString = new SpannableString(String.format("%s    重新考试", scoreString));
                                        ForegroundColorSpan scoreColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.assignment_exam_red));
                                        ForegroundColorSpan buttonColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.assignment_exam_blue));
                                        spannableString.setSpan(scoreColorSpan, 0, scoreString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                        spannableString.setSpan(buttonColorSpan, scoreString.length(), spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        viewHolder.mItemStatus.setText(spannableString);
                                        viewHolder.mItemStatus.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                ExamInfoActivity.launch(mContext, examId);
                                            }
                                        });
                                    } else {
                                        viewHolder.mItemStatus.setText(scoreString);
                                        viewHolder.mItemStatus.setTextColor(getResources().getColor(R.color.assignment_exam_red));
                                    }
                                } else {
                                    String scoreString = String.format("%s / %s  未通过", BizUtils.showTestScore(studyResultBean.getScore()),
                                            BizUtils.showTestScore(examTaskInfo.getTestPaperScore()));
                                    viewHolder.mItemStatus.setText(scoreString);
                                    viewHolder.mItemStatus.setTextColor(getResources().getColor(R.color.assignment_exam_red));
                                }
                            } else if (studyResultBean.getPassStatus().equals("passed")) {
                                if (examTaskInfo.getExamTimeStatus().equals("ongoing")) {
                                    String scoreString = String.format("%s / %s  已通过",
                                            BizUtils.showTestScore(studyResultBean.getScore()), BizUtils.showTestScore(examTaskInfo.getTestPaperScore()));
                                    if ((examTaskInfo.getRemainingResitTimes() != 0 || unlimited) && isShowAction()) {
                                        SpannableString spannableString = new SpannableString(String.format("%s   重新考试", scoreString));
                                        ForegroundColorSpan scoreColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.assignment_exam_green));
                                        ForegroundColorSpan buttonColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.assignment_exam_blue));
                                        spannableString.setSpan(scoreColorSpan, 0, scoreString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                        spannableString.setSpan(buttonColorSpan, scoreString.length(), spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        viewHolder.mItemStatus.setText(spannableString);
                                        viewHolder.mItemStatus.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                ExamInfoActivity.launch(mContext, examId);
                                            }
                                        });
                                    } else {
                                        viewHolder.mItemStatus.setText(scoreString);
                                        viewHolder.mItemStatus.setTextColor(getResources().getColor(R.color.assignment_exam_green));
                                    }
                                } else {
                                    String scoreString = String.format("%s / %s  已通过",
                                            BizUtils.showTestScore(studyResultBean.getScore()), BizUtils.showTestScore(examTaskInfo.getTestPaperScore()));
                                    viewHolder.mItemStatus.setText(scoreString);
                                    viewHolder.mItemStatus.setTextColor(getResources().getColor(R.color.assignment_exam_green));
                                }
                            }
                        }
                    } else {
                        if (examTaskInfo.getExamTimeStatus().equals("notStart")) {
                            viewHolder.mItemStatus.setText("暂未开始");
                            viewHolder.mItemStatus.setTextColor(getResources().getColor(R.color.default_grey_text_color));
                            viewHolder.mItemStatus.setOnClickListener(null);
                        }
                        if (examTaskInfo.getExamTimeStatus().equals("ongoing") && isShowAction()) {
                            viewHolder.mItemStatus.setText("开始考试");
                            viewHolder.mItemStatus.setTextColor(getResources().getColor(R.color.primary));
                            viewHolder.mItemStatus.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ExamInfoActivity.launch(mContext, examId);
                                }
                            });
                        }
                        if (examTaskInfo.getExamTimeStatus().equals("expired")) {
                            viewHolder.mItemStatus.setText("缺考");
                            viewHolder.mItemStatus.setTextColor(getResources().getColor(R.color.assignment_exam_red));
                            viewHolder.mItemStatus.setOnClickListener(null);
                        }
                    }
                }
            }

            if (detailBean.getTargetType().equals("offline_exam")) {
                viewHolder.mTypeIcon.setText(R.string.new_font_type_offline_exam);
                viewHolder.mItemExamCount.setVisibility(View.GONE);
                if (Long.parseLong(detailBean.getEndTime()) - Long.parseLong(detailBean.getStartTime()) > 86400) {
                    viewHolder.mItemTeacher.setText(String.format("考试时间：%s 至 %s",
                            AppUtil.timeStampToDate(detailBean.getStartTime(), "MM-dd"),
                            AppUtil.timeStampToDate(detailBean.getEndTime(), "MM-dd")));
                } else {
                    viewHolder.mItemTeacher.setText(String.format("考试时间：%s - %s",
                            AppUtil.timeStampToDate(detailBean.getStartTime(), "MM-dd HH:mm"),
                            AppUtil.timeStampToDate(detailBean.getEndTime(), "HH:mm")));
                }
                viewHolder.mItemTime.setText(String.format("考试地点：%s", detailBean.getPlace()));
            }
        }
    }

    private class ItemDetailAdapter extends RecyclerView.Adapter {

        private Context mContext;
        private List    mList;

        private ItemDetailAdapter(Context context, List list) {
            mContext = context;
            mList = list;
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_project_item_detail, parent, false);
            return new ProjectPlanItemListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ProjectPlanItemListViewHolder viewHolder = (ProjectPlanItemListViewHolder) holder;
            Object object = mList.get(position);
            Gson gson = new Gson();
            final ItemsDetailBean.TaskInfoBean taskInfoBean = gson.fromJson(gson.toJson(object), ItemsDetailBean.TaskInfoBean.class);
            viewHolder.mItemDetailTitle.setText(taskInfoBean.getTitle());
            viewHolder.mItemLocationView.setVisibility(View.VISIBLE);
            viewHolder.mItemTimeView.setVisibility(View.VISIBLE);
            viewHolder.mItemHomeworkView.setVisibility(View.VISIBLE);
            viewHolder.mItemDetailStatus.setVisibility(View.VISIBLE);
            if (taskInfoBean.getMediaType().equals("offlineCourse")) {
                if (Long.parseLong(taskInfoBean.getEndTime()) - Long.parseLong(taskInfoBean.getStartTime()) > 86400) {
                    viewHolder.mItemDetailTime.setText(String.format("%s-%s", AppUtil.timeStampToDate(taskInfoBean.getStartTime(), "MM-dd HH:mm"), AppUtil.timeStampToDate(taskInfoBean.getEndTime(), "MM-dd HH:mm")));
                } else {
                    viewHolder.mItemDetailTime.setText(String.format("%s-%s", AppUtil.timeStampToDate(taskInfoBean.getStartTime(), "MM-dd HH:mm"), AppUtil.timeStampToDate(taskInfoBean.getEndTime(), "HH:mm")));
                }
                if (TextUtils.isEmpty(taskInfoBean.getPlace())) {
                    viewHolder.mItemLocationView.setVisibility(View.GONE);
                } else {
                    viewHolder.mItemLocationView.setVisibility(View.VISIBLE);
                    viewHolder.mItemDetailLocation.setText(taskInfoBean.getPlace());
                }
                if (taskInfoBean.getHasHomework().equals("1") && isShowAction()) {
                    viewHolder.mItemHomeworkView.setVisibility(View.VISIBLE);
                    viewHolder.mItemDetailHomeworkStatus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ProjectOfflineHomeWorkActivity.launch(mContext, taskInfoBean.getTitle(), taskInfoBean.getHomeworkDemand(), taskInfoBean.getHomeworkDeadline());
                        }
                    });
                    if (taskInfoBean.getHomeworkStatus().equals("unsubmit")) {
                        viewHolder.mItemDetailHomework.setText("含课后作业（未提交）");
                    }
                    if (taskInfoBean.getHomeworkStatus().equals("submitted")) {
                        SpannableString spannableString = new SpannableString("含课后作业（已提交）");
                        ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.button_success_color));
                        spannableString.setSpan(colorSpan, 5, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        viewHolder.mItemDetailHomework.setText(spannableString);
                    }
                    if (taskInfoBean.getHomeworkStatus().equals("passed")) {
                        SpannableString spannableString = new SpannableString("含课后作业（已通过）");
                        ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.button_success_color));
                        spannableString.setSpan(colorSpan, 5, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        viewHolder.mItemDetailHomework.setText(spannableString);
                        viewHolder.mItemDetailHomework.setText(spannableString);
                    }
                    if (taskInfoBean.getHomeworkStatus().equals("unpassed")) {
                        SpannableString spannableString = new SpannableString("含课后作业（未通过）");
                        ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.assignment_exam_red));
                        spannableString.setSpan(colorSpan, 5, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        viewHolder.mItemDetailHomework.setText(spannableString);
                        viewHolder.mItemDetailHomework.setText(spannableString);
                    }
                } else {
                    viewHolder.mItemHomeworkView.setVisibility(View.GONE);
                }
                if (taskInfoBean.getAttendStatus().equals("attended")) {
                    viewHolder.mItemDetailStatus.setText("已签到");
                    viewHolder.mItemDetailStatus.setTextColor(getResources().getColor(R.color.button_success_color));
                    if (isShowAction()) {
                        viewHolder.mStatusIcon.setText(R.string.new_font_status_finish);
                        viewHolder.mStatusIcon.setTextColor(getResources().getColor(R.color.primary));
                    } else {
                        viewHolder.mStatusIcon.setText(R.string.new_font_status_unfinish);
                        viewHolder.mStatusIcon.setTextColor(getResources().getColor(R.color.default_grey_text_color));
                    }
                } else if (taskInfoBean.getAttendStatus().equals("absent")) {
                    viewHolder.mStatusIcon.setText(R.string.new_font_status_unfinish);
                    viewHolder.mStatusIcon.setTextColor(getResources().getColor(R.color.default_grey_text_color));
                    viewHolder.mItemDetailStatus.setText("缺勤");
                    viewHolder.mItemDetailStatus.setTextColor(getResources().getColor(R.color.assignment_exam_red));
                } else {
                    if (isShowAction()) {
                        viewHolder.mStatusIcon.setText(R.string.new_font_status_unfinish);
                        viewHolder.mStatusIcon.setTextColor(getResources().getColor(R.color.default_grey_text_color));
                        viewHolder.mItemDetailStatus.setText("未签到");
                        viewHolder.mItemDetailStatus.setTextColor(getResources().getColor(R.color.default_grey_text_color));
                    } else {
                        viewHolder.mStatusIcon.setText(R.string.new_font_status_unfinish);
                        viewHolder.mStatusIcon.setTextColor(getResources().getColor(R.color.default_grey_text_color));
                        viewHolder.mItemDetailStatus.setVisibility(View.GONE);
                        viewHolder.mItemDetailStatus.setTextColor(getResources().getColor(R.color.default_grey_text_color));
                    }
                }
            }
            if (taskInfoBean.getMediaType().equals("offlineCourseQuestionnaire")) {
                viewHolder.mItemLocationView.setVisibility(View.GONE);
                viewHolder.mItemTimeView.setVisibility(View.GONE);
                viewHolder.mItemHomeworkView.setVisibility(View.GONE);
                final String mediaId = taskInfoBean.getMediaId();
                if (taskInfoBean.getQuestionnaireStatus().equals("finished")) {
                    if (isShowAction()) {
                        viewHolder.mStatusIcon.setText(R.string.new_font_status_finish);
                        viewHolder.mStatusIcon.setTextColor(getResources().getColor(R.color.primary));
                        viewHolder.mItemDetailStatus.setText("已评价");
                        viewHolder.mItemDetailStatus.setTextColor(getResources().getColor(R.color.button_success_color));
                        viewHolder.mItemDetailStatus.setOnClickListener(null);
                    } else {
                        viewHolder.mStatusIcon.setText(R.string.new_font_status_unfinish);
                        viewHolder.mStatusIcon.setTextColor(getResources().getColor(R.color.default_grey_text_color));
                        viewHolder.mItemDetailStatus.setText("已评价");
                        viewHolder.mItemDetailStatus.setTextColor(getResources().getColor(R.color.button_success_color));
                        viewHolder.mItemDetailStatus.setOnClickListener(null);
                    }
                } else {
                    if (isShowAction()) {
                        viewHolder.mStatusIcon.setText(R.string.new_font_status_unfinish);
                        viewHolder.mStatusIcon.setTextColor(getResources().getColor(R.color.default_grey_text_color));
                        viewHolder.mItemDetailStatus.setText("去评价");
                        viewHolder.mItemDetailStatus.setTextColor(getResources().getColor(R.color.primary));
                        viewHolder.mItemDetailStatus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                EvaluationSurveyActivity.launch(mContext, mediaId, 0, 0);
                            }
                        });
                    } else {
                        viewHolder.mStatusIcon.setText(R.string.new_font_status_unfinish);
                        viewHolder.mStatusIcon.setTextColor(getResources().getColor(R.color.default_grey_text_color));
                        viewHolder.mItemDetailStatus.setVisibility(View.GONE);
                        viewHolder.mItemDetailStatus.setTextColor(getResources().getColor(R.color.primary));
                        viewHolder.mItemDetailStatus.setOnClickListener(null);
                    }
                }
            }
        }
    }

    private class ProjectPlanDetailListViewHolder extends RecyclerView.ViewHolder {

        private ESIconView   mTypeIcon;
        private TextView     mItemTitle;
        private TextView     mItemStatus;
        private TextView     mItemTeacher;
        private TextView     mItemTime;
        private TextView     mItemExamCount;
        private View         mLayoutSubTitle;
        private LinearLayout mBottomItemListView;
        private RecyclerView mItemListView;

        private ProjectPlanDetailListViewHolder(View view) {
            super(view);
            mTypeIcon = view.findViewById(R.id.tv_type_icon);
            mItemTitle = view.findViewById(R.id.tv_project_item_title);
            mItemStatus = view.findViewById(R.id.tv_project_item_status);
            mItemTeacher = view.findViewById(R.id.tv_project_item_teacher);
            mItemTime = view.findViewById(R.id.tv_project_item_time);
            mLayoutSubTitle = view.findViewById(R.id.rl_sub_title);
            mBottomItemListView = view.findViewById(R.id.ll_item_detail_view);
            mItemListView = view.findViewById(R.id.rv_item_detail);
            mItemExamCount = view.findViewById(R.id.tv_project_item_exam_count);
            mItemListView.setEnabled(false);
        }
    }

    private class ProjectPlanItemListViewHolder extends RecyclerView.ViewHolder {

        private ESIconView     mStatusIcon;
        private TextView       mItemDetailTitle;
        private TextView       mItemDetailStatus;
        private RelativeLayout mItemTimeView;
        private TextView       mItemDetailTime;
        private RelativeLayout mItemLocationView;
        private TextView       mItemDetailLocation;
        private RelativeLayout mItemHomeworkView;
        private TextView       mItemDetailHomework;
        private TextView       mItemDetailHomeworkStatus;

        private ProjectPlanItemListViewHolder(View view) {
            super(view);
            mStatusIcon = view.findViewById(R.id.tv_icon_status);
            mItemDetailTitle = view.findViewById(R.id.tv_item_detail_title);
            mItemDetailStatus = view.findViewById(R.id.tv_item_detail_status);
            mItemTimeView = view.findViewById(R.id.rl_item_detail_time);
            mItemDetailTime = view.findViewById(R.id.tv_item_detail_time);
            mItemLocationView = view.findViewById(R.id.rl_item_detail_location);
            mItemDetailLocation = view.findViewById(R.id.tv_item_detail_location);
            mItemHomeworkView = view.findViewById(R.id.rl_item_detail_homework);
            mItemDetailHomework = view.findViewById(R.id.tv_item_detail_homework);
            mItemDetailHomeworkStatus = view.findViewById(R.id.tv_item_detail_homework_status);
        }
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onReceiveStickyNessage(MessageEvent messageEvent) {
        if (messageEvent.getType() == MessageEvent.FINISH_SURVEY) {
            EventBus.getDefault().removeAllStickyEvents();
            if (mPresenter != null) {
                mPresenter.subscribe();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isShowAction() {
        return OPERATE.equals(mEnterType);
    }
}
