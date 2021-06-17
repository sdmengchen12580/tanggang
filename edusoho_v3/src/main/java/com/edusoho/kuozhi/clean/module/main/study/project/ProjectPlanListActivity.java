package com.edusoho.kuozhi.clean.module.main.study.project;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.mystudy.OfflineActivitiesResult;
import com.edusoho.kuozhi.clean.bean.mystudy.OfflineActivityCategory;
import com.edusoho.kuozhi.clean.module.main.study.offlineactivity.OfflineListActivity;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by RexXiang on 2018/3/21.
 */

public class ProjectPlanListActivity extends OfflineListActivity {


    public static void launch(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, ProjectPlanListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void initView() {
        super.initView();
        mBarTitle.setText("项目列表");
    }

    @Override
    protected void initData() {
        mCurrentType = "";
        mCurrentStatus = "进行中";
        mPresenter = new ProjectPlanListPresenter(this);
        mPresenter.subscribe();
    }

    @Override
    public void refreshView(List<OfflineActivitiesResult.DataBean> list, List<OfflineActivityCategory> categoryList) {
        mList = list;
        mCategoryList = categoryList;
        mAdapter = new ProjectListAdapter(this, mList);
        mListRecyclerView.setAdapter(mAdapter);
        if (!TextUtils.isEmpty(mCurrentType)) {
            filterByType(mCurrentType);
        }
    }

    private void projectEnrollDetail(String id) {
        ProjectPlanEnrollDetailActivity.launch(ProjectPlanListActivity.this, id);
    }

    private class ProjectListAdapter extends OfflineListAdapter {

        private ProjectListAdapter(Context context, List<OfflineActivitiesResult.DataBean> list) {
            super(context, list);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            OfflineListViewHolder offlineListViewHolder = (OfflineListViewHolder) holder;
            final OfflineActivitiesResult.DataBean dataBean = mList.get(position);
            ImageLoader.getInstance().displayImage(dataBean.getCover().getMiddle(), offlineListViewHolder.mAssignmentImage, EdusohoApp.app.mOptions);
            offlineListViewHolder.mAssignmentTitle.setText(dataBean.getName());
            if (TextUtils.isEmpty(dataBean.getCategoryName())) {
                offlineListViewHolder.mAssignmentType.setVisibility(View.GONE);
            } else {
                offlineListViewHolder.mAssignmentType.setVisibility(View.VISIBLE);
                offlineListViewHolder.mAssignmentType.setText(dataBean.getCategoryName());
            }
            if (Long.parseLong(dataBean.getEndTime()) - Long.parseLong(dataBean.getStartTime()) > 86400) {
                offlineListViewHolder.mAssignmentTime.setText(String.format("项目时间：%s至%s", AppUtil.timeStampToDate(dataBean.getStartTime(), "MM-dd"), AppUtil.timeStampToDate(dataBean.getEndTime(), "MM-dd")));
            } else {
                offlineListViewHolder.mAssignmentTime.setText(String.format("项目时间：%s-%s", AppUtil.timeStampToDate(dataBean.getStartTime(), "MM-dd HH:mm"), AppUtil.timeStampToDate(dataBean.getEndTime(), "HH:mm")));
            }
            if (TextUtils.isEmpty(dataBean.getAddress())) {
                offlineListViewHolder.mAssignmentSubTitle.setText("项目简介：暂无简介");
            } else {
                offlineListViewHolder.mAssignmentSubTitle.setText(String.format("项目简介：%s", dataBean.getSummary()));
            }

            //报名人数判断
            if (!dataBean.getMaxStudentNum().equals("0")) {
                SpannableString spannableString = new SpannableString(String.format("报名人数：%s/%s", dataBean.getStudentNum(), dataBean.getMaxStudentNum()));
                spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.assignment_exam_blue)), 5, dataBean.getStudentNum().length() + 5, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                offlineListViewHolder.mAssignmentExamResult.setText(spannableString);
            }

            offlineListViewHolder.mAssignmentExamResult.setVisibility(View.GONE);
            offlineListViewHolder.mAssignmentBottomButton.setVisibility(View.INVISIBLE);
            offlineListViewHolder.mAssignmentExamStatus.setVisibility(View.VISIBLE);
            offlineListViewHolder.mAssignmentExamStatus.setBackground(getResources().getDrawable(R.drawable.shape_assignment_exam_grey));
            offlineListViewHolder.mAssignmentExamStatus.setTextColor(getResources().getColor(R.color.assignment_exam_grey));

            if (dataBean.getProjectPlanTimeStatus().equals("ongoing") || dataBean.getProjectPlanTimeStatus().equals("notStart")) {
                if (dataBean.getApplyStatus().equals("submitted")) {
                    offlineListViewHolder.mAssignmentExamStatus.setBackground(getResources().getDrawable(R.drawable.shape_assignment_exam_yellow));
                    offlineListViewHolder.mAssignmentExamStatus.setTextColor(getResources().getColor(R.color.assignment_exam_yellow));
                    offlineListViewHolder.mAssignmentExamStatus.setText("审核中");
                }
                if (dataBean.getApplyStatus().equals("success")) {
                    offlineListViewHolder.mAssignmentExamStatus.setBackground(getResources().getDrawable(R.drawable.shape_assignment_exam_green));
                    offlineListViewHolder.mAssignmentExamStatus.setTextColor(getResources().getColor(R.color.assignment_exam_green));
                    offlineListViewHolder.mAssignmentExamStatus.setText("报名成功");
                }
                if (dataBean.getApplyStatus().equals("enrollmentEnd")) {
                    offlineListViewHolder.mAssignmentExamStatus.setText("报名结束");
                }
                if (dataBean.getApplyStatus().equals("enrollUnable")) {
                    offlineListViewHolder.mAssignmentExamStatus.setText("名额已满");
                }
                if (dataBean.getApplyStatus().equals("enrollmentUnStart")) {
                    offlineListViewHolder.mAssignmentExamStatus.setVisibility(View.GONE);
                    offlineListViewHolder.mAssignmentBottomButton.setVisibility(View.VISIBLE);
                    offlineListViewHolder.mAssignmentExamResult.setVisibility(View.VISIBLE);
                    offlineListViewHolder.mAssignmentBottomButton.setText("暂未开始");
                    offlineListViewHolder.mAssignmentBottomButton.setBackground(getResources().getDrawable(R.drawable.shape_bottom_right_corner_radius_grey));
                    offlineListViewHolder.mAssignmentBottomButton.setEnabled(false);
                }
                if (dataBean.getApplyStatus().equals("enrollInWeb")) {
                    offlineListViewHolder.mAssignmentExamResult.setVisibility(View.VISIBLE);
                    offlineListViewHolder.mAssignmentExamStatus.setVisibility(View.GONE);
                }
                if (dataBean.getApplyStatus().equals("enrollAble") || dataBean.getApplyStatus().equals("rejected") || dataBean.getApplyStatus().equals("reset")) {
                    offlineListViewHolder.mAssignmentExamStatus.setVisibility(View.GONE);
                    offlineListViewHolder.mAssignmentBottomButton.setVisibility(View.VISIBLE);
                    offlineListViewHolder.mAssignmentExamResult.setVisibility(View.VISIBLE);
                    offlineListViewHolder.mAssignmentBottomButton.setText("立即报名");
                    offlineListViewHolder.mAssignmentBottomButton.setBackground(getResources().getDrawable(R.drawable.shape_bottom_right_corner_radius));
                    offlineListViewHolder.mAssignmentBottomButton.setEnabled(true);
                    offlineListViewHolder.mAssignmentBottomButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            joinActivity(dataBean.getId());
                        }
                    });
                }
            }

            if (dataBean.getProjectPlanTimeStatus().equals("end")) {
                offlineListViewHolder.mAssignmentExamStatus.setText("已结束");
            }

            offlineListViewHolder.mTopView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    projectEnrollDetail(dataBean.getId());
                }
            });
        }
    }
}
