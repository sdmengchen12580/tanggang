package com.edusoho.kuozhi.clean.module.main.news.notification.course;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.utils.biz.NotificationHelper;
import com.edusoho.kuozhi.v3.model.bal.push.Notify;
import com.edusoho.kuozhi.v3.util.AppUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CourseNotificationAdapter extends RecyclerView.Adapter<CourseNotificationAdapter.ViewHolder> {

    private Context      mContext;
    private List<Notify> mList;
    private SimpleDateFormat mSimlpeDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public CourseNotificationAdapter(Context context) {
        this.mContext = context;
        mList = new ArrayList<>();
    }

    public void addNotificationItems(List<Notify> addLists) {
        mList.addAll(addLists);
        notifyDataSetChanged();
    }

    public List<Notify> getList() {
        return mList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_notify_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Notify notify = mList.get(position);
        holder.titleView.setText(NotificationHelper.getName(notify.getType()));
        switch (notify.getType()) {
            case NotificationHelper.COURSE_LIVE_START:
            case NotificationHelper.COURSE_LIVE_START1:
            case NotificationHelper.CLASSROOM_DEADLINE:
            case NotificationHelper.CLASSROOM_JOIN:
            case NotificationHelper.CLASSROOM_QUIT:
            case NotificationHelper.CLASSROOM_ANNOUNCEMENT_CREATE:
            case NotificationHelper.COURSE_DEADLINE:
            case NotificationHelper.COURSE_JOIN:
            case NotificationHelper.COURSE_QUIT:
            case NotificationHelper.COURSE_ANNOUNCEMENT_CREATE:
            case NotificationHelper.QUESTION_ANSWERED:
            case NotificationHelper.QUESTION_CREATED:
            case NotificationHelper.COURSE_THREAD_UPDATE:
            case NotificationHelper.COURSE_THREAD_STICK:
            case NotificationHelper.COURSE_THREAD_UNSTICK:
            case NotificationHelper.COURSE_THREAD_ELITE:
            case NotificationHelper.COURSE_THREAD_UNELITE:
            case NotificationHelper.COURSE_THREAD_POST_UPDATE:
            case NotificationHelper.COURSE_THREAD_POST_AT:
                holder.contentView.setText(coverContentColor(Html.fromHtml(notify.getMessage()).toString(), mContext.getString(R.string.check)));
                break;
            default:
                holder.contentView.setText(notify.getMessage());
        }
        holder.timeView.setText(AppUtil.convertMills2Date((notify.getCreatedTime())));
    }

    private SpannableString coverContentColor(String content, String doChange) {
        StringBuffer stringBuffer = new StringBuffer(content + doChange);
        int start = content.length();
        int end = (content + doChange).length();
        SpannableString spannableString = new SpannableString(stringBuffer);
        int color = mContext.getResources().getColor(R.color.primary_color);
        spannableString.setSpan(
                new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        return spannableString;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView timeView;
        TextView contentView;
        TextView titleView;

        public ViewHolder(View view) {
            super(view);
            timeView = (TextView) view.findViewById(R.id.tv_nofity_time);
            titleView = (TextView) view.findViewById(R.id.tv_nofity_title);
            contentView = (TextView) view.findViewById(R.id.tv_nofity_content);
        }
    }
}
