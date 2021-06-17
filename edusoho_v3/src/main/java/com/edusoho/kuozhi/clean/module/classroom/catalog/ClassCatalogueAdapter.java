package com.edusoho.kuozhi.clean.module.classroom.catalog;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by DF on 2016/12/15.
 */

class ClassCatalogueAdapter extends RecyclerView.Adapter<ClassCatalogueAdapter.ClassHolder> {
    private Context             mContext;
    private boolean             mIsJoin;
    private List<CourseProject> mCourseProject;
    private OnItemClickListener mOnItemClickListener;


    ClassCatalogueAdapter(List<CourseProject> courseProjects, boolean isJoin) {
        this.mCourseProject = courseProjects;
        this.mIsJoin = isJoin;
    }

    public void setData(List<CourseProject> courseProjects, boolean isJoin) {
        this.mCourseProject = courseProjects;
        mIsJoin = isJoin;
        notifyDataSetChanged();
    }

    @Override
    public ClassCatalogueAdapter.ClassHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new ClassHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_study, parent, false));
    }

    @Override
    public void onBindViewHolder(ClassCatalogueAdapter.ClassHolder holder, final int position) {
        render(holder, mCourseProject.get(position));
        holder.mRLayoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.click(position);
                }
            }
        });
    }

    private void render(ClassCatalogueAdapter.ClassHolder holder, CourseProject courseProject) {
        holder.mTvMore.setVisibility(View.GONE);
        holder.mLayoutLive.setVisibility(View.GONE);
        ImageLoader.getInstance().displayImage(courseProject.courseSet.cover.large, holder.mIvPic,
                EdusohoApp.app.mOptions);
        holder.mTvTitle.setText(String.valueOf(courseProject.getTitle()));
        holder.mTvFrom.setText(courseProject.courseSet.title);
        holder.mTvFree.setVisibility(View.GONE);
//        if (!mIsJoin) {
//            if (courseProject.isFree == 1) {
//                holder.mTvFree.setText(R.string.class_catalog_free);
//                holder.mTvFree.setTextColor(ContextCompat.getColor(mContext, R.color.primary_color));
//            } else {
//                holder.mTvFree.setText(String.format("¥%.2f", courseProject.price));
//                holder.mTvFree.setTextColor(ContextCompat.getColor(mContext, R.color.secondary_color));
//            }
//        }
    }

    @Override
    public int getItemCount() {
        return mCourseProject == null ? 0 : mCourseProject.size();
    }

    static class ClassHolder extends RecyclerView.ViewHolder {
        ImageView mIvPic;
        View      mLayoutLive;
        TextView  mTvFrom;
        TextView  mTvTitle;
        TextView  mTvFree;
        TextView  mTvMore;
        View      mRLayoutItem;

        ClassHolder(View view) {
            super(view);
            mIvPic = (ImageView) view.findViewById(R.id.iv_pic);
            mLayoutLive = view.findViewById(R.id.layout_live);
            mTvFrom = (TextView) view.findViewById(R.id.tv_course_set_name);
            mTvTitle = (TextView) view.findViewById(R.id.tv_title);
            mTvFree = (TextView) view.findViewById(R.id.tv_study_state);
            mTvMore = (TextView) view.findViewById(R.id.tv_more);
            mRLayoutItem = view.findViewById(R.id.rlayout_item);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void click(int position);
    }
}
