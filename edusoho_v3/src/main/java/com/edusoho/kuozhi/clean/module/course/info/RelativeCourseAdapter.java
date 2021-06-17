package com.edusoho.kuozhi.clean.module.course.info;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.VipInfo;
import com.edusoho.kuozhi.clean.bean.innerbean.Price;
import com.edusoho.kuozhi.clean.utils.StringUtils;
import com.edusoho.kuozhi.v3.util.CommonUtil;

import java.util.List;

/**
 * Created by JesseHuang on 2017/4/3.
 */

public class RelativeCourseAdapter extends RecyclerView.Adapter<RelativeCourseAdapter.ViewHolder> {

    private static final int FREE = 1;
    private Context             mContext;
    private List<CourseProject> mCourseProjects;
    private List<VipInfo>       mVips;

    public RelativeCourseAdapter(Context context, List<CourseProject> courseProjects, List<VipInfo> vips) {
        this.mContext = context;
        this.mCourseProjects = courseProjects;
        this.mVips = vips;
    }

    @Override
    public RelativeCourseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_relative_course_project, parent, false);
        return new RelativeCourseAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RelativeCourseAdapter.ViewHolder holder, int position) {
        final CourseProject courseProject = mCourseProjects.get(position);
        holder.courseTitle.setText(courseProject.getTitle());
        if (FREE == courseProject.isFree) {
            showPrice(holder, CourseProjectPriceEnum.FREE, courseProject.price2, courseProject.originPrice2);
        } else if (courseProject.originPrice2.getPrice() == courseProject.price2.getPrice()) {
            showPrice(holder, CourseProjectPriceEnum.ORIGINAL, courseProject.price2, courseProject.originPrice2);
        } else {
            showPrice(holder, CourseProjectPriceEnum.SALE, courseProject.price2, courseProject.originPrice2);
        }
        holder.courseTasks.setText(String.format(mContext.getString(R.string.course_task_num), courseProject.publishedTaskNum));
        if (courseProject.services == null || courseProject.services.length == 0) {
            holder.promiseServiceLayout.setVisibility(View.GONE);
        } else {
            holder.promiseServiceLayout.setVisibility(View.VISIBLE);
            for (CourseProject.Service service : courseProject.services) {
                TextView serviceTextView = new TextView(mContext);
                serviceTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                serviceTextView.setTextColor(mContext.getResources().getColor(R.color.primary_color));
                serviceTextView.setText(service.shortName);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.rightMargin = CommonUtil.dip2px(mContext, 10);

                serviceTextView.setLayoutParams(lp);
                serviceTextView.setBackgroundResource(R.drawable.course_project_services);
                holder.promiseServiceLayout.addView(serviceTextView);
            }
        }
        String vipName = getVipName(courseProject.vipLevelId);
        if (StringUtils.isEmpty(vipName)) {
            holder.courseVipAd.setVisibility(View.GONE);
        } else {
            holder.courseVipAd.setVisibility(View.VISIBLE);
            holder.courseVipAd.setText(String.format(mContext.getString(R.string.vip_member_free_to_learn), vipName));
        }
    }

    public String getVipName(int vipId) {
        if (mVips == null) {
            return "";
        }
        for (VipInfo info : mVips) {
            if (vipId == info.id) {
                return info.name;
            }
        }
        return "";
    }

    public void showPrice(RelativeCourseAdapter.ViewHolder holder, CourseProjectPriceEnum type, Price price, Price originPrice) {
        switch (type) {
            case FREE:
                holder.mOriginalPrice.setText(R.string.free_course_project);
                holder.mOriginalPrice.setTextColor(mContext.getResources().getColor(R.color.primary_color));
                holder.mSalePrice.setVisibility(View.GONE);
                break;
            case ORIGINAL:
                holder.mOriginalPrice.setText(originPrice.getPriceWithUnit());
                holder.mOriginalPrice.setTextColor(mContext.getResources().getColor(R.color.secondary_color));
                holder.mSalePrice.setVisibility(View.GONE);
                break;
            case SALE:
                holder.mSalePrice.setText(price.getPriceWithUnit());
                holder.mOriginalPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                holder.mOriginalPrice.setText(originPrice.getPriceWithUnit());
                holder.mSaleWord.setVisibility(View.VISIBLE);
                break;
        }
    }

    public CourseProject getItem(int position) {
        return mCourseProjects.get(position);
    }

    @Override
    public int getItemCount() {
        return mCourseProjects.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView     courseTitle;
        public TextView     courseTasks;
        public LinearLayout promiseServiceLayout;
        public TextView     courseVipAd;
        public TextView     mSalePrice;
        public TextView     mOriginalPrice;
        public TextView     mSaleWord;

        public ViewHolder(View view) {
            super(view);
            courseTitle = (TextView) view.findViewById(R.id.tv_course_title);
            courseTasks = (TextView) view.findViewById(R.id.tv_course_tasks);
            promiseServiceLayout = (LinearLayout) view.findViewById(R.id.ll_promise_layout);
            courseVipAd = (TextView) view.findViewById(R.id.tv_vip_ad);
            mSalePrice = (TextView) view.findViewById(R.id.tv_sale_price);
            mOriginalPrice = (TextView) view.findViewById(R.id.tv_original_price);
            mSaleWord = (TextView) view.findViewById(R.id.tv_sale_word);
        }
    }
}
