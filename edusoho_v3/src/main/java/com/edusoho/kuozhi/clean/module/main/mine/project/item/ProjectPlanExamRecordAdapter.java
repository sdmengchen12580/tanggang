package com.edusoho.kuozhi.clean.module.main.mine.project.item;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.mystudy.ExamRecord;
import com.edusoho.kuozhi.clean.utils.Constants;
import com.edusoho.kuozhi.clean.utils.biz.BizUtils;
import com.edusoho.kuozhi.v3.util.AppUtil;

import java.util.ArrayList;
import java.util.List;

public class ProjectPlanExamRecordAdapter
        extends BaseProjectPlanAdapter<ExamRecord, ProjectPlanRecordItemActivity.ProjectPlanExamRecordViewHolder> {

    private static final long ONE_DAY = 60 * 60 * 24 * 1000l;
    private Context mContext;

    public ProjectPlanExamRecordAdapter(Context context) {
        this(context, new ArrayList<ExamRecord>());
    }

    public ProjectPlanExamRecordAdapter(Context context, List<ExamRecord> examRecords) {
        mContext = context;
        mList = examRecords;
    }

    @Override
    public ProjectPlanRecordItemActivity.ProjectPlanExamRecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_project_plan_exam_record, parent, false);
        return new ProjectPlanRecordItemActivity.ProjectPlanExamRecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProjectPlanRecordItemActivity.ProjectPlanExamRecordViewHolder holder, int position) {
        final ExamRecord examRecord = mList.get(position);
        holder.tvCourseName.setText(examRecord.getExamName());
        holder.tvScorePassed.setVisibility(View.GONE);
        if (Constants.ProjectPlanExamStatus.FINISHED.equals(examRecord.getStatus())) {
            holder.tvScorePassed.setVisibility(View.VISIBLE);
            if (Constants.ProjectPlanExamPassedStatus.PASSED.equals(examRecord.getPassStatus())) {
                holder.tvScorePassed.setTextColor(mContext.getResources().getColor(R.color.assignment_exam_green));
                holder.tvScorePassed.setText(String.format("%s / %s 已通过", BizUtils.showTestScore(examRecord.getScore()),
                        BizUtils.showTestScore(examRecord.getTestPaperScore())));
            } else {
                holder.tvScorePassed.setTextColor(mContext.getResources().getColor(R.color.assignment_exam_red));
                holder.tvScorePassed.setText(String.format("%s / %s 未通过", BizUtils.showTestScore(examRecord.getScore()),
                        BizUtils.showTestScore(examRecord.getTestPaperScore())));
            }
        } else {
            if (Constants.ProjectPlanExamStatus.ABSENT.equals(examRecord.getStatus())) {
                holder.tvScorePassed.setVisibility(View.VISIBLE);
                holder.tvScorePassed.setTextColor(mContext.getResources().getColor(R.color.assignment_exam_red));
                holder.tvScorePassed.setText("缺考");
            }
        }
        long startTime = Long.parseLong(examRecord.getStartTime());
        long endTime = Long.parseLong(examRecord.getEndTime());
        if ((endTime - startTime) * 1000 > ONE_DAY) {
            holder.tvExamTime.setText(String.format("考试时间：%s 至 %s",
                    AppUtil.timeStampToDate(examRecord.getStartTime(), "MM-dd"),
                    AppUtil.timeStampToDate(examRecord.getEndTime(), "MM-dd")));
        } else {
            holder.tvExamTime.setText(String.format("考试时间：%s - %s",
                    AppUtil.timeStampToDate(examRecord.getStartTime(), "MM-dd HH:mm"),
                    AppUtil.timeStampToDate(examRecord.getEndTime(), "HH:mm")));
        }
    }
}
