package com.edusoho.kuozhi.clean.module.main.mine.project.item;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.mystudy.ProjectPlanRecord;
import com.edusoho.kuozhi.clean.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class ProjectPlanRecordAdapter
        extends BaseProjectPlanAdapter<ProjectPlanRecord, ProjectPlanRecordItemActivity.ProjectPlanRecordViewHolder> {

    private Context mContext;

    public ProjectPlanRecordAdapter(Context context) {
        this(context, new ArrayList<ProjectPlanRecord>());
    }

    public ProjectPlanRecordAdapter(Context context, List<ProjectPlanRecord> projectPlanRecords) {
        mList = projectPlanRecords;
        mContext = context;
    }

    @Override
    public ProjectPlanRecordItemActivity.ProjectPlanRecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_project_plan_record, parent, false);
        return new ProjectPlanRecordItemActivity.ProjectPlanRecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProjectPlanRecordItemActivity.ProjectPlanRecordViewHolder holder, int position) {
        final ProjectPlanRecord projectPlanRecord = mList.get(position);
        holder.tvName.setText(projectPlanRecord.getProjectPlanName());
        holder.tvTime.setText(String.format(mContext.getString(R.string.project_plan_time) + "%s 至 %s",
                TimeUtils.getTime(TimeUtils.SIMPLE_DATE_FORMAT, Long.parseLong(projectPlanRecord.getStartTime()) * 1000L),
                TimeUtils.getTime(TimeUtils.SIMPLE_DATE_FORMAT, Long.parseLong(projectPlanRecord.getEndTime()) * 1000L)));
        holder.espbProgress.setProgress(projectPlanRecord.getProgress());
        holder.tvProgressNum.setText(projectPlanRecord.getProgress() + "%");
    }
}
