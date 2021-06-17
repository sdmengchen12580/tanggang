package com.edusoho.kuozhi.clean.module.main.mine.project.item;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.mystudy.PostCourseRecord;

import java.util.ArrayList;
import java.util.List;

public class ProjectPlanPostCourseAdapter
        extends BaseProjectPlanAdapter<PostCourseRecord, ProjectPlanRecordItemActivity.ProjectPlanPostCourseViewHolder> {
    private Context mContext;

    public ProjectPlanPostCourseAdapter(Context context) {
        this(context, new ArrayList<PostCourseRecord>());
    }

    public ProjectPlanPostCourseAdapter(Context context, List<PostCourseRecord> postCourseRecords) {
        this.mContext = context;
        this.mList = postCourseRecords;
    }

    @Override
    public ProjectPlanRecordItemActivity.ProjectPlanPostCourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_project_plan_post_course, parent, false);
        return new ProjectPlanRecordItemActivity.ProjectPlanPostCourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProjectPlanRecordItemActivity.ProjectPlanPostCourseViewHolder holder, int position) {
        final PostCourseRecord postCourseRecord = mList.get(position);
        holder.tvCourseName.setText(postCourseRecord.getCourseName());
        holder.tvTeacher.setText(postCourseRecord.getTeacherName());
        holder.tvDuration.setText(String.format("学习时长：%.1f小时", postCourseRecord.getTotalLearnTime() / 3600f));
        holder.espbProgress.setProgress(postCourseRecord.getProgress());
        holder.tvProgressNum.setText(postCourseRecord.getProgress() + "%");
    }
}
