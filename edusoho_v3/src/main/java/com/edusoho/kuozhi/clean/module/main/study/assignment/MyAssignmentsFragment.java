package com.edusoho.kuozhi.clean.module.main.study.assignment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.bean.mystudy.AssignmentModel;
import com.edusoho.kuozhi.clean.bean.mystudy.AssignmentModel.TodayFocusBean;
import com.edusoho.kuozhi.clean.module.base.BaseFragment;
import com.edusoho.kuozhi.clean.module.main.study.exam.ExamInfoActivity;
import com.edusoho.kuozhi.clean.module.main.study.offlineactivity.OfflineActivityDetailActivity;
import com.edusoho.kuozhi.clean.module.main.study.project.ProjectPlanDetailActivity;
import com.edusoho.kuozhi.clean.module.main.study.survey.SurveyDetailActivity;
import com.edusoho.kuozhi.clean.module.main.study.survey.SurveyResultActivity;
import com.edusoho.kuozhi.clean.utils.biz.BizUtils;
import com.edusoho.kuozhi.clean.widget.ESRecyclerView.ESPullAndLoadRecyclerView;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

/**
 * Created by RexXiang on 2018/1/15.
 */

public class MyAssignmentsFragment extends BaseFragment<MyAssignmentsContract.Presenter> implements MyAssignmentsContract.View {

    private ESPullAndLoadRecyclerView mAssignmentRecyclerView;
    private List<AssignmentModel> mList = new ArrayList<AssignmentModel>();
    private AssignmentsAdapter mAdapter;
    private View layoutEmpty;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_my_assignments, container, false);
        initView(view);
        initData();
        return view;
    }

    private void initView(View view) {
        layoutEmpty = view.findViewById(R.id.rl_empty_view);
        mAssignmentRecyclerView = view.findViewById(R.id.rv_study_my_assignments);
        mAssignmentRecyclerView.setLinearLayout();
        mAssignmentRecyclerView.setOnPullLoadMoreListener(new ESPullAndLoadRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                mPresenter.subscribe();
            }

            @Override
            public void onLoadMore() {
            }
        });
        mAssignmentRecyclerView.setPullRefreshEnable(true);
        mAssignmentRecyclerView.setPushRefreshEnable(false);
    }

    private void initData() {
        mPresenter = new MyAssignmentsPresenter(this);
        mPresenter.subscribe();
    }

    @Override
    public void refreshView(List<AssignmentModel> list) {
        mList = list;
        if (mList.size() > 0) {
            layoutEmpty.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.VISIBLE);
        }
        mAdapter = new AssignmentsAdapter(getContext(), mList);
        mAssignmentRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void clearData() {
        if (mAdapter != null) {
            mAdapter.clearData();
        }
    }

    @Override
    public void refreshCompleted() {
        mAssignmentRecyclerView.setPullLoadMoreCompleted();
    }

    private class AssignmentsAdapter extends RecyclerView.Adapter {

        private static final int EXAM = 0;
        private static final int SURVEY = 1;
        private static final int ACTIVITY = 2;
        private static final int PROJECT = 3;

        private Context mContext;
        private List<AssignmentModel> mList;


        private AssignmentsAdapter(Context context, List<AssignmentModel> list) {
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

        //专项考试exam    培训项目projectPlan     活动offlineActivity
        @Override
        public int getItemViewType(int position) {
            AssignmentModel assignmentModel = mList.get(position);
            if (assignmentModel.getType().equals("exam")) {
                return EXAM;
            } else if (assignmentModel.getType().equals("survey")) {
                return SURVEY;
            } else if (assignmentModel.getType().equals("offlineActivity")) {
                return ACTIVITY;
            } else {
                return PROJECT;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == PROJECT) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_study_assignment_without_button, parent, false);
                return new AssignmentsNoButtonViewHolder(view);
            } else if (viewType == ACTIVITY) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_study_assignment_activity, parent, false);
                return new AssignmentsViewHolder(view);
            } else {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_study_assignment_with_button, parent, false);
                return new AssignmentsViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final AssignmentModel assignmentModel = mList.get(position);
            //fixme 测试代码
//            if (position == 0) {
//                assignmentModel.setExamStatus("ongoing");
//                assignmentModel.setMemberStatus("notStart");
//            }
            if (holder instanceof AssignmentsViewHolder) {
                AssignmentsViewHolder assignmentsHolder = (AssignmentsViewHolder) holder;
                assignmentsHolder.mBottomView.setVisibility(View.VISIBLE);
                assignmentsHolder.mAssignmentSubTitle.setVisibility(View.VISIBLE);
                assignmentsHolder.mAssignmentExamCount.setVisibility(GONE);
                assignmentsHolder.mAssignmentExamStatus.setVisibility(GONE);
                assignmentsHolder.mAssignmentExamResult.setVisibility(GONE);
                assignmentsHolder.mTopView.setOnClickListener(null);
                if (assignmentModel.getType().equals("exam")) {
                    assignmentsHolder.mAssignmentImage.setImageDrawable(getResources().getDrawable(R.drawable.study_exam_default));
                    assignmentsHolder.mAssignmentType.setText(R.string.study_assignment_exam);
                    //大于24小时
                    if (Long.parseLong(assignmentModel.getEndTime()) - Long.parseLong(assignmentModel.getStartTime()) > 86400) {
                        assignmentsHolder.mAssignmentTime.setText(String.format("考试时间：%s 至 %s", AppUtil.timeStampToDate(assignmentModel.getStartTime(), "MM-dd"), AppUtil.timeStampToDate(assignmentModel.getEndTime(), "MM-dd")));
                    } else {
                        assignmentsHolder.mAssignmentTime.setText(String.format("考试时间：%s - %s", AppUtil.timeStampToDate(assignmentModel.getStartTime(), "MM-dd HH:mm"), AppUtil.timeStampToDate(assignmentModel.getEndTime(), "HH:mm")));
                    }
                    assignmentsHolder.mAssignmentSubTitle.setText("考试机会：");
                    assignmentsHolder.mAssignmentExamCount.setVisibility(View.VISIBLE);
                    boolean unlimited = false;
                    // 不限制考试机会判断
                    if (assignmentModel.getResitTimes().equals("0") && assignmentModel.getRemainingResitTimes() == 0) {
                        unlimited = true;
                    }
                    // 考试次数状态判断
                    if (unlimited) {
                        assignmentsHolder.mAssignmentExamCount.setBackground(getResources().getDrawable(R.drawable.shape_assignment_exam_green));
                        assignmentsHolder.mAssignmentExamCount.setTextColor(getResources().getColor(R.color.assignment_exam_green));
                        assignmentsHolder.mAssignmentExamCount.setText("不限");
                    } else {
                        if (assignmentModel.getRemainingResitTimes() == 1) {
                            assignmentsHolder.mLayoutSubTitle.setVisibility(View.VISIBLE);
                            assignmentsHolder.mAssignmentExamCount.setBackground(getResources().getDrawable(R.drawable.shape_assignment_exam_red));
                            assignmentsHolder.mAssignmentExamCount.setTextColor(getResources().getColor(R.color.assignment_exam_red));
                            assignmentsHolder.mAssignmentExamCount.setText("1次");
                        } else if (assignmentModel.getRemainingResitTimes() > 0) {
                            assignmentsHolder.mLayoutSubTitle.setVisibility(View.VISIBLE);
                            assignmentsHolder.mAssignmentExamCount.setBackground(getResources().getDrawable(R.drawable.shape_assignment_exam_yellow));
                            assignmentsHolder.mAssignmentExamCount.setTextColor(getResources().getColor(R.color.assignment_exam_yellow));
                            assignmentsHolder.mAssignmentExamCount.setText(String.format("%s次", assignmentModel.getRemainingResitTimes()));
                        } else if (assignmentModel.getRemainingResitTimes() == 0) {
                            assignmentsHolder.mLayoutSubTitle.setVisibility(View.INVISIBLE);
                        }
                    }
                    // 考试结果完成
                    if (!TextUtils.isEmpty(assignmentModel.getPassedStatus()) && assignmentModel.getMemberStatus().equals("finished")) {
                        assignmentsHolder.mAssignmentExamStatus.setVisibility(View.VISIBLE);
                        assignmentsHolder.mAssignmentExamResult.setVisibility(View.VISIBLE);
                        SpannableString spannableString = new SpannableString(String.format("成绩：%s / %s",
                                BizUtils.showTestScore(assignmentModel.getUserScore()),
                                BizUtils.showTestScore(assignmentModel.getExamScore())));
                        if (assignmentModel.getPassedStatus().equals("unpassed")) {
                            assignmentsHolder.mAssignmentExamStatus.setBackground(getResources().getDrawable(R.drawable.shape_assignment_exam_red));
                            assignmentsHolder.mAssignmentExamStatus.setTextColor(getResources().getColor(R.color.assignment_exam_red));
                            assignmentsHolder.mAssignmentExamStatus.setText("未通过");
                            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.assignment_exam_red)),
                                    3, BizUtils.showTestScore(assignmentModel.getUserScore()).length() + 3, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                            assignmentsHolder.mAssignmentExamResult.setText(spannableString);
                        }
                        if (assignmentModel.getPassedStatus().equals("passed")) {
                            assignmentsHolder.mAssignmentExamStatus.setBackground(getResources().getDrawable(R.drawable.shape_assignment_exam_green));
                            assignmentsHolder.mAssignmentExamStatus.setTextColor(getResources().getColor(R.color.assignment_exam_green));
                            assignmentsHolder.mAssignmentExamStatus.setText("通过");
                            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.assignment_exam_green)),
                                    3, BizUtils.showTestScore(assignmentModel.getUserScore()).length() + 3, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                            assignmentsHolder.mAssignmentExamResult.setText(spannableString);
                            if (!TextUtils.isEmpty(assignmentModel.getExamType())) {
                                if (assignmentModel.getExamType().equals("fullMark")) {
                                    assignmentsHolder.mAssignmentBottomButton.setVisibility(View.INVISIBLE);
                                }
                            }
                        }
                    }
                    //批阅中
                    if (assignmentModel.getMemberStatus().equals("reviewing")) {
                        assignmentsHolder.mAssignmentExamStatus.setVisibility(View.VISIBLE);
                        assignmentsHolder.mAssignmentExamStatus.setBackground(getResources().getDrawable(R.drawable.shape_assignment_exam_yellow));
                        assignmentsHolder.mAssignmentExamStatus.setTextColor(getResources().getColor(R.color.assignment_exam_yellow));
                        assignmentsHolder.mAssignmentExamStatus.setText("批阅中");
                        assignmentsHolder.mAssignmentBottomButton.setVisibility(View.INVISIBLE);
                        assignmentsHolder.mAssignmentExamResult.setVisibility(GONE);
                    }
                    //考试按钮状态判断
                    assignmentsHolder.mAssignmentBottomButton.setOnClickListener(null);
                    if (assignmentModel.getExamStatus().equals("notStart")) {
                        assignmentsHolder.mAssignmentBottomButton.setText("暂未开始");
                        assignmentsHolder.mAssignmentBottomButton.setBackground(getResources().getDrawable(R.drawable.shape_bottom_right_corner_radius_grey));
                    }
                    //fixme 可以考试->             专项考试页面：ExamInfoActivity
                    if (assignmentModel.getExamStatus().equals("ongoing")) {
                        if (assignmentModel.getMemberStatus().equals("notStart")) {
                            assignmentsHolder.mAssignmentBottomButton.setText("开始考试");
                        }
                        if (assignmentModel.getMemberStatus().equals("doing")) {
                            assignmentsHolder.mAssignmentBottomButton.setText("继续考试");
                        }
                        assignmentsHolder.mAssignmentBottomButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ExamInfoActivity.launch(mContext, assignmentModel.getId());
                            }
                        });
                    }
                    if (assignmentModel.getExamStatus().equals("expired")) {
                        assignmentsHolder.mAssignmentBottomButton.setVisibility(View.INVISIBLE);
                    }
                    if (assignmentModel.getExamStatus().equals("ongoing") && assignmentModel.getMemberStatus().equals("finished")) {
                        if ((unlimited || assignmentModel.getRemainingResitTimes() != 0)) {
                            assignmentsHolder.mAssignmentBottomButton.setText("重新考试");
                            assignmentsHolder.mAssignmentBottomButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ExamInfoActivity.launch(mContext, assignmentModel.getId());
                                }
                            });
                        } else {
                            if (TextUtils.isEmpty(assignmentModel.getPassedStatus())) {
                                assignmentsHolder.mBottomView.setVisibility(View.GONE);
                            }
                            assignmentsHolder.mAssignmentBottomButton.setVisibility(View.INVISIBLE);
                        }
                    }
                }
                if (assignmentModel.getType().equals("survey")) {
                    assignmentsHolder.mAssignmentBottomButton.setOnClickListener(null);
                    assignmentsHolder.mAssignmentImage.setImageDrawable(getResources().getDrawable(R.drawable.study_survey_default));
                    assignmentsHolder.mAssignmentType.setText(R.string.study_assignment_survey);
                    if (assignmentModel.getStartTime().equals("0") || assignmentModel.getEndTime().equals("0")) {
                        assignmentsHolder.mAssignmentTime.setText("调查时间:不限");
                    } else {
                        if (Long.parseLong(assignmentModel.getEndTime()) - Long.parseLong(assignmentModel.getStartTime()) > 86400) {
                            assignmentsHolder.mAssignmentTime.setText(String.format("调查时间：%s 至 %s", AppUtil.timeStampToDate(assignmentModel.getStartTime(), "MM-dd"), AppUtil.timeStampToDate(assignmentModel.getEndTime(), "MM-dd")));
                        } else {
                            assignmentsHolder.mAssignmentTime.setText(String.format("调查时间：%s", AppUtil.timeStampToDate(assignmentModel.getStartTime(), "MM-dd")));
                        }
                    }
                    if (TextUtils.isEmpty(assignmentModel.getDescription())) {
                        assignmentsHolder.mAssignmentSubTitle.setText("问卷说明：暂无说明");
                    } else {
                        assignmentsHolder.mAssignmentSubTitle.setText(String.format("问卷说明：%s", assignmentModel.getDescription()));
                    }
                    if (assignmentModel.getSurveyStatus().equals("ongoing") && !assignmentModel.getMemberStatus().equals("finished")) {
                        assignmentsHolder.mAssignmentBottomButton.setText("开始答题");
                        assignmentsHolder.mAssignmentBottomButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SurveyDetailActivity.launch(mContext, assignmentModel.getId());
                            }
                        });
                    }
                    if (assignmentModel.getMemberStatus().equals("finished") && assignmentModel.getIsResultVisible().equals("1")) {
                        assignmentsHolder.mAssignmentBottomButton.setText("查看结果");
                        assignmentsHolder.mAssignmentBottomButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SurveyResultActivity.launchWithResult(mContext, assignmentModel.getId(), true);
                            }
                        });
                    }
                    if (assignmentModel.getMemberStatus().equals("finished") && assignmentModel.getIsResultVisible().equals("0")) {
                        assignmentsHolder.mAssignmentExamStatus.setVisibility(View.VISIBLE);
                        assignmentsHolder.mAssignmentExamStatus.setBackground(getResources().getDrawable(R.drawable.shape_assignment_exam_green));
                        assignmentsHolder.mAssignmentExamStatus.setTextColor(getResources().getColor(R.color.assignment_exam_green));
                        assignmentsHolder.mAssignmentExamStatus.setText("已提交");
                        assignmentsHolder.mAssignmentBottomButton.setVisibility(View.INVISIBLE);
                    }
                    if (assignmentModel.getSurveyStatus().equals("notStart")) {
                        assignmentsHolder.mAssignmentBottomButton.setText("暂未开始");
                        assignmentsHolder.mAssignmentBottomButton.setBackground(getResources().getDrawable(R.drawable.shape_bottom_right_corner_radius_grey));
                    }
                }
                if (assignmentModel.getType().equals("offlineActivity")) {
                    assignmentsHolder.mTopView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            OfflineActivityDetailActivity.launch(mContext, assignmentModel.getId());
                        }
                    });
                    ImageLoader.getInstance().displayImage(assignmentModel.getCover().getMiddle(), assignmentsHolder.mAssignmentImage, EdusohoApp.app.mOptions);
                    assignmentsHolder.mAssignmentType.setText(R.string.study_assignment_activity);
                    assignmentsHolder.mAssignmentTime.setText(String.format("活动时间：%s", AppUtil.timeStampToDate(assignmentModel.getStartDate(), "MM-dd HH:mm")));
                    assignmentsHolder.mAssignmentSubTitle.setText(String.format("活动地点：%s", assignmentModel.getAddress()));
                    assignmentsHolder.mBottomView.setVisibility(GONE);
                }
                assignmentsHolder.mAssignmentTitle.setText(assignmentModel.getTitle());
            } else if (holder instanceof AssignmentsNoButtonViewHolder) {
                AssignmentsNoButtonViewHolder assignmentsHolder = (AssignmentsNoButtonViewHolder) holder;
                assignmentsHolder.mTopView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ProjectPlanDetailActivity.launch(mContext, assignmentModel.getId(), ProjectPlanDetailActivity.OPERATE);
                    }
                });
                assignmentsHolder.mAssignmentTitle.setText(assignmentModel.getTitle());
                assignmentsHolder.mAssignmentType.setText(R.string.study_assignment_project);
                ImageLoader.getInstance().displayImage(assignmentModel.getCover().getMiddle(), assignmentsHolder.mAssignmentImage, EdusohoApp.app.mOptions);
                assignmentsHolder.mProgressBar.setProgress((int) assignmentModel.getProgress());
                assignmentsHolder.mPercent.setText(assignmentModel.getProgress() + "%");
                if (Long.parseLong(assignmentModel.getEndDate()) - Long.parseLong(assignmentModel.getStartDate()) > 86400) {
                    assignmentsHolder.mAssignmentTime.setText(String.format("项目时间：%s 至 %s", AppUtil.timeStampToDate(assignmentModel.getStartDate(), "MM-dd"), AppUtil.timeStampToDate(assignmentModel.getEndDate(), "MM-dd")));
                } else {
                    assignmentsHolder.mAssignmentTime.setText(String.format("项目时间：%s", AppUtil.timeStampToDate(assignmentModel.getStartDate(), "MM-dd")));
                }
                assignmentsHolder.mAssignmentSubTitle.setText("项目进度：");
                if (assignmentModel.getTodayFocus() == null) {
                    assignmentsHolder.mBottomView.setVisibility(GONE);
                } else {
                    List<List<TodayFocusBean>> list = getList(assignmentModel.getTodayFocus(), 2);
                    if (list.size() > 0) {
                        assignmentsHolder.mBottomView.setVisibility(View.VISIBLE);
                        for (int i = 0; i < list.size(); i++) {
                            View focusView = View.inflate(mContext, R.layout.item_study_focus, null);
                            List<TodayFocusBean> subList = list.get(i);
                            setTodayFocus(subList, focusView, assignmentsHolder.mFocusFlipper);
                            if (list.size() > 1) {
                                assignmentsHolder.mFocusFlipper.setAutoStart(true);
                                assignmentsHolder.mFocusFlipper.setFlipInterval(3000);
                            }
                        }
                    } else {
                        assignmentsHolder.mBottomView.setVisibility(View.GONE);
                    }
                }
            }
        }

        private void setTodayFocus(List<TodayFocusBean> list, View focusView, ViewFlipper viewFlipper) {
            LinearLayout topLayout = focusView.findViewById(R.id.ll_item_focus_top);
            TextView topTypeText = focusView.findViewById(R.id.tv_focus_top_type);
            TextView topTitleText = focusView.findViewById(R.id.tv_focus_top_title);
            LinearLayout bottomLayout = focusView.findViewById(R.id.ll_item_focus_bottom);
            TextView bottomTypeText = focusView.findViewById(R.id.tv_focus_bottom_type);
            TextView bottomTitleText = focusView.findViewById(R.id.tv_focus_bottom_title);
            if (list.size() == 2) {
                TodayFocusBean topTodayFocusBean = list.get(0);
                TodayFocusBean bottmTodayFocusBean = list.get(1);
                setTodayFocusData(topTodayFocusBean, topTypeText, topTitleText);
                setTodayFocusData(bottmTodayFocusBean, bottomTypeText, bottomTitleText);
            } else {
                TodayFocusBean topTodayFocusBean = list.get(0);
                setTodayFocusData(topTodayFocusBean, topTypeText, topTitleText);
                bottomLayout.setVisibility(View.INVISIBLE);
            }
            viewFlipper.addView(focusView);
        }

        private void setTodayFocusData(TodayFocusBean bean, TextView type, TextView title) {
            type.setText(bean.getTagName());
            title.setText(bean.getTitle());
        }


    }

    public class AssignmentsViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout mBottomView;
        private RelativeLayout mTopView;
        private ImageView mAssignmentImage;
        private TextView mAssignmentTitle;
        private TextView mAssignmentType;
        private TextView mAssignmentTime;
        private TextView mAssignmentSubTitle;
        private TextView mAssignmentExamCount;
        private TextView mAssignmentExamStatus;
        private TextView mAssignmentExamResult;
        private Button mAssignmentBottomButton;
        private View mLayoutSubTitle;

        private AssignmentsViewHolder(View view) {
            super(view);
            mTopView = view.findViewById(R.id.rl_assignment_top);
            mBottomView = view.findViewById(R.id.rl_assignment_bottom);
            mAssignmentImage = view.findViewById(R.id.iv_assignment);
            mAssignmentTitle = view.findViewById(R.id.tv_assignment_title);
            mAssignmentType = view.findViewById(R.id.tv_assignment_type);
            mAssignmentTime = view.findViewById(R.id.tv_assignment_time);
            mAssignmentSubTitle = view.findViewById(R.id.tv_assignment_sub_title);
            mAssignmentBottomButton = view.findViewById(R.id.btn_assignment_bottom);
            mAssignmentExamCount = view.findViewById(R.id.tv_assignment_exam_count);
            mAssignmentExamStatus = view.findViewById(R.id.tv_assignment_exam_status);
            mAssignmentExamResult = view.findViewById(R.id.tv_assignment_exam_result);
            mLayoutSubTitle = view.findViewById(R.id.ll_sub_title);
        }

    }

    private class AssignmentsNoButtonViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout mBottomView;
        private RelativeLayout mTopView;
        private ImageView mAssignmentImage;
        private TextView mAssignmentTitle;
        private TextView mAssignmentType;
        private TextView mAssignmentTime;
        private TextView mAssignmentSubTitle;
        private ViewFlipper mFocusFlipper;
        private ProgressBar mProgressBar;
        private TextView mPercent;

        private AssignmentsNoButtonViewHolder(View view) {
            super(view);
            mTopView = view.findViewById(R.id.rl_assignment_top);
            mBottomView = view.findViewById(R.id.rl_assignment_bottom);
            mAssignmentImage = view.findViewById(R.id.iv_assignment);
            mAssignmentTitle = view.findViewById(R.id.tv_assignment_title);
            mAssignmentType = view.findViewById(R.id.tv_assignment_type);
            mAssignmentTime = view.findViewById(R.id.tv_assignment_time);
            mAssignmentSubTitle = view.findViewById(R.id.tv_assignment_sub_title);
            mFocusFlipper = view.findViewById(R.id.vf_study_focus);
            mProgressBar = view.findViewById(R.id.item_progressbar);
            mPercent = view.findViewById(R.id.tv_percent);
        }

    }

    private List<List<TodayFocusBean>> getList(List<TodayFocusBean> mList, int targ) {

        List<List<TodayFocusBean>> mEndList = new ArrayList<>();
        if (mList.size() % targ != 0) {
            for (int j = 0; j < mList.size() / targ + 1; j++) {
                if ((j * targ + targ) < mList.size()) {
                    mEndList.add(mList.subList(j * targ, j * targ + targ));
                } else if ((j * targ + targ) > mList.size()) {
                    mEndList.add(mList.subList(j * targ, mList.size()));
                } else if (mList.size() < targ) {
                    mEndList.add(mList.subList(0, mList.size()));
                }
            }
        } else if (mList.size() % targ == 0) {
            for (int j = 0; j < mList.size() / targ; j++) {
                if ((j * targ + targ) <= mList.size()) {
                    mEndList.add(mList.subList(j * targ, j * targ + targ));
                } else if ((j * targ + targ) > mList.size()) {
                    mEndList.add(mList.subList(j * targ, mList.size()));
                } else if (mList.size() < targ) {
                    mEndList.add(mList.subList(0, mList.size()));
                }
            }
        }
        return mEndList;
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
}
