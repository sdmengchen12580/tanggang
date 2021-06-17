package com.edusoho.kuozhi.clean.module.main.study.project;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.mystudy.ProjectCourseItem;
import com.edusoho.kuozhi.clean.bean.mystudy.ProjectExamItem;
import com.edusoho.kuozhi.clean.bean.mystudy.ProjectOfflineCourseItem;
import com.edusoho.kuozhi.clean.bean.mystudy.ProjectOfflineExamItem;
import com.edusoho.kuozhi.clean.bean.mystudy.ProjectPlan.ItemsDetailBean;
import com.edusoho.kuozhi.clean.widget.ESIconView;
import com.edusoho.kuozhi.v3.util.AppUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RexXiang on 2018/3/22.
 */

public class ProjectPlanEnrollAdapter extends BaseExpandableListAdapter {

    private Context               mContext;
    private List<ItemsDetailBean> mGroupList;
    private ArrayList<List>       mChildList;

    public ProjectPlanEnrollAdapter(Context context, List<ItemsDetailBean> groupList, ArrayList<List> childList) {
        mContext = context;
        mGroupList = groupList;
        mChildList = childList;
    }

    @Override
    public Object getGroup(int i) {
        return mGroupList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        if (mChildList.get(i).get(i1) != null) {
            return mChildList.get(i).get(i1);
        } else {
            return null;
        }
    }

    @Override
    public int getGroupCount() {
        return mGroupList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return mChildList.get(i).size();
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        GroupViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_project_enroll_group, null);
            holder = new GroupViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (GroupViewHolder) view.getTag();
        }
        ItemsDetailBean bean = mGroupList.get(i);
        if (b) {
            holder.ivIndicator.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_prject_item_expand));
        } else {
            holder.ivIndicator.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_prject_item_unexpand));
        }
        holder.tvGroupItemTitle.setText(bean.getTitle());
        holder.tvGroupItemTeacher.setVisibility(View.GONE);
        if (bean.getTargetType().equals("course")) {
            holder.typeIcon.setText(R.string.new_font_type_course);
        }
        if (bean.getTargetType().equals("offline_course")) {
            holder.typeIcon.setText(R.string.new_font_type_offline_course);
            if (!TextUtils.isEmpty(bean.getTeacherName())) {
                holder.tvGroupItemTeacher.setVisibility(View.VISIBLE);
                holder.tvGroupItemTeacher.setText("讲师：" + bean.getTeacherName());
            }
        }
        if (bean.getTargetType().equals("exam")) {
            holder.typeIcon.setText(R.string.new_font_type_exam);
        }
        if (bean.getTargetType().equals("offline_exam")) {
            holder.typeIcon.setText(R.string.new_font_type_offline_exam);
        }
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        ChildViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_project_enroll_child, null);
            viewHolder = new ChildViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ChildViewHolder) view.getTag();
        }
        ItemsDetailBean bean = (ItemsDetailBean) getGroup(i);
        switch (bean.getTargetType()) {
            case "course": {
                ProjectCourseItem item = (ProjectCourseItem) mChildList.get(i).get(i1);
                viewHolder.tvChildItemTitle.setText(item.getTitle());
                viewHolder.childItemDetailView.setVisibility(View.GONE);
                break;
            }
            case "offline_course": {
                ProjectOfflineCourseItem item = (ProjectOfflineCourseItem) mChildList.get(i).get(i1);
                viewHolder.tvChildItemTitle.setText(item.getTitle());
                if (!item.getType().equals("questionnaire")) {
                    viewHolder.childItemDetailView.setVisibility(View.VISIBLE);
                    if (Long.parseLong(item.getEndTime()) - Long.parseLong(item.getStartTime()) >= 86400) {
                        viewHolder.tvTimeTitle.setText(String.format("%s-%s", AppUtil.timeStampToDate(item.getStartTime(), "MM-dd HH:mm"), AppUtil.timeStampToDate(item.getEndTime(), "MM-dd HH:mm")));
                    } else {
                        viewHolder.tvTimeTitle.setText(String.format("%s-%s", AppUtil.timeStampToDate(item.getStartTime(), "MM-dd HH:mm"), AppUtil.timeStampToDate(item.getEndTime(), "HH:mm")));
                    }
                    viewHolder.tvLocationTitle.setText(item.getPlace());
                } else {
                    viewHolder.childItemDetailView.setVisibility(View.GONE);
                }
                break;
            }
            case "exam": {
                ProjectExamItem item = (ProjectExamItem) mChildList.get(i).get(i1);
                viewHolder.tvChildItemTitle.setText(item.getName());
                viewHolder.childItemDetailView.setVisibility(View.VISIBLE);
                if (Long.parseLong(item.getEndTime()) - Long.parseLong(item.getStartTime()) >= 86400) {
                    viewHolder.tvTimeTitle.setText(String.format("%s-%s", AppUtil.timeStampToDate(item.getStartTime(), "MM-dd HH:mm"), AppUtil.timeStampToDate(item.getEndTime(), "MM-dd HH:mm")));
                } else {
                    viewHolder.tvTimeTitle.setText(String.format("%s-%s", AppUtil.timeStampToDate(item.getStartTime(), "MM-dd HH:mm"), AppUtil.timeStampToDate(item.getEndTime(), "HH:mm")));
                }
                viewHolder.tvLocationTitle.setText(item.getName());
                break;
            }
            default: {
                ProjectOfflineExamItem item = (ProjectOfflineExamItem) mChildList.get(i).get(i1);
                viewHolder.tvChildItemTitle.setText(item.getTitle());
                viewHolder.childItemDetailView.setVisibility(View.VISIBLE);
                if (Long.parseLong(item.getEndTime()) - Long.parseLong(item.getStartTime()) >= 86400) {
                    viewHolder.tvTimeTitle.setText(String.format("%s-%s", AppUtil.timeStampToDate(item.getStartTime(), "MM-dd HH:mm"), AppUtil.timeStampToDate(item.getEndTime(), "MM-dd HH:mm")));
                } else {
                    viewHolder.tvTimeTitle.setText(String.format("%s-%s", AppUtil.timeStampToDate(item.getStartTime(), "MM-dd HH:mm"), AppUtil.timeStampToDate(item.getEndTime(), "HH:mm")));
                }
                viewHolder.tvLocationTitle.setText(item.getPlace());
            }
        }
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    static class GroupViewHolder {
        ESIconView typeIcon;
        TextView   tvGroupItemTitle;
        TextView   tvGroupItemTeacher;
        ImageView  ivIndicator;

        public GroupViewHolder(View view) {
            typeIcon = (ESIconView) view.findViewById(R.id.tv_icon_item_type);
            tvGroupItemTitle = (TextView) view.findViewById(R.id.tv_project_item_title);
            tvGroupItemTeacher = (TextView) view.findViewById(R.id.tv_project_item_teacher);
            ivIndicator = (ImageView) view.findViewById(R.id.iv_group_expand_status);
        }
    }


    static class ChildViewHolder {
        TextView     tvChildItemTitle;
        ESIconView   statusIcon;
        LinearLayout childItemDetailView;
        TextView     tvTimeTitle;
        TextView     tvLocationTitle;

        public ChildViewHolder(View view) {
            tvChildItemTitle = (TextView) view.findViewById(R.id.tv_icon_item_child_title);
            statusIcon = (ESIconView) view.findViewById(R.id.tv_icon_item_child_status);
            childItemDetailView = (LinearLayout) view.findViewById(R.id.ll_item_child_detail);
            tvTimeTitle = (TextView) view.findViewById(R.id.tv_item_detail_title_time);
            tvLocationTitle = (TextView) view.findViewById(R.id.tv_item_detail_title_location);
        }
    }


}
