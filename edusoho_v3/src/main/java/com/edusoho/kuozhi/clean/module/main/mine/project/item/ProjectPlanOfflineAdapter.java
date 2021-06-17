package com.edusoho.kuozhi.clean.module.main.mine.project.item;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.mystudy.OfflineActivityRecord;
import com.edusoho.kuozhi.clean.utils.Constants;
import com.edusoho.kuozhi.clean.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class ProjectPlanOfflineAdapter
        extends BaseProjectPlanAdapter<OfflineActivityRecord, ProjectPlanRecordItemActivity.ProjectPlanOfflineActivityViewHolder> {

    private Context mContext;
    public int mMaxLength = 8;

    public ProjectPlanOfflineAdapter(Context context) {
        this(context, new ArrayList<OfflineActivityRecord>());
    }

    public ProjectPlanOfflineAdapter(Context context, List<OfflineActivityRecord> offlineActivityRecords) {
        mContext = context;
        mList = offlineActivityRecords;
    }

    @Override
    public ProjectPlanRecordItemActivity.ProjectPlanOfflineActivityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_project_plan_offline_activity, parent, false);
        return new ProjectPlanRecordItemActivity.ProjectPlanOfflineActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProjectPlanRecordItemActivity.ProjectPlanOfflineActivityViewHolder holder, int position) {
        OfflineActivityRecord offlineActivityRecord = mList.get(position);
        holder.tvActivityName.setText(offlineActivityRecord.getOfflineActivityName());
        holder.tvCategoryName.setText(offlineActivityRecord.getCategoryName());
        RelativeLayout.LayoutParams signInLayoutParams = (RelativeLayout.LayoutParams) holder.tvSignIn.getLayoutParams();
        mMaxLength = 8;
        switch (offlineActivityRecord.getAttendedStatus()) {
            case Constants.ActivityAttentStatus.ATTENDED:
                signInLayoutParams.setMargins(mContext.getResources().getDimensionPixelSize(R.dimen.offline_activity_status_space), 0, 0, 0);
                holder.tvSignIn.setText(R.string.activity_sign_in);
                holder.tvSignIn.setTextColor(mContext.getResources().getColor(R.color.assignment_exam_green));
                break;
            case Constants.ActivityAttentStatus.UNATTENDED:
                signInLayoutParams.setMargins(mContext.getResources().getDimensionPixelSize(R.dimen.offline_activity_status_space), 0, 0, 0);
                holder.tvSignIn.setText(R.string.activity_unattend);
                holder.tvSignIn.setTextColor(mContext.getResources().getColor(R.color.assignment_exam_red));
                break;
            case Constants.ActivityAttentStatus.NONE:
                signInLayoutParams.setMargins(0, 0, 0, 0);
                mMaxLength = mMaxLength + 2;
                holder.tvSignIn.setText("");
                holder.tvSignIn.setTextColor(mContext.getResources().getColor(R.color.assignment_exam_red));
                break;
        }
        holder.tvSignIn.setLayoutParams(signInLayoutParams);
        RelativeLayout.LayoutParams passedLayoutParams = (RelativeLayout.LayoutParams) holder.tvPassed.getLayoutParams();
        switch (offlineActivityRecord.getPassedStatus()) {
            case Constants.ActivityPassedStatus.PASSED:
                passedLayoutParams.setMargins(mContext.getResources().getDimensionPixelSize(R.dimen.offline_activity_status_space), 0, 0, 0);
                holder.tvPassed.setText(R.string.activity_passed);
                holder.tvPassed.setTextColor(mContext.getResources().getColor(R.color.assignment_exam_green));
                break;
            case Constants.ActivityPassedStatus.UNPASSED:
                passedLayoutParams.setMargins(mContext.getResources().getDimensionPixelSize(R.dimen.offline_activity_status_space), 0, 0, 0);
                holder.tvPassed.setText(R.string.activity_unpassed);
                holder.tvPassed.setTextColor(mContext.getResources().getColor(R.color.assignment_exam_red));
                break;
            case Constants.ActivityPassedStatus.NONE:
                passedLayoutParams.setMargins(0, 0, 0, 0);
                holder.tvPassed.setText("");
                holder.tvPassed.setTextColor(mContext.getResources().getColor(R.color.assignment_exam_red));
                mMaxLength = mMaxLength + 2;
                break;
        }
        holder.tvActivityName.setMaxEms(mMaxLength);
        holder.tvPassed.setLayoutParams(passedLayoutParams);
        holder.tvTime.setText(String.format("活动时间：%s %s - %s"
                , TimeUtils.getTime(TimeUtils.SIMPLE_DATE_FORMAT, Long.parseLong(offlineActivityRecord.getStartTime()) * 1000L)
                , TimeUtils.getTime(TimeUtils.TIME_FORMAT, Long.parseLong(offlineActivityRecord.getStartTime()) * 1000L)
                , TimeUtils.getTime(TimeUtils.TIME_FORMAT, Long.parseLong(offlineActivityRecord.getEndTime()) * 1000L)));
        holder.tvLocation.setText(String.format("活动地点：%s", offlineActivityRecord.getOfflineActivityPlace()));
    }
}
