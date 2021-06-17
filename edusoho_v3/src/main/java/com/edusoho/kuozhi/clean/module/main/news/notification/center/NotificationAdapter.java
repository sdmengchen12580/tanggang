package com.edusoho.kuozhi.clean.module.main.news.notification.center;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.imserver.entity.ConvEntity;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.imserver.entity.message.MessageBody;
import com.edusoho.kuozhi.v3.model.bal.push.New;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.view.EduBadgeView;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private Context          mContext;
    private List<ConvEntity> mList;

    public NotificationAdapter(Context context, List<ConvEntity> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.news_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ConvEntity convEntity = mList.get(position);
        switch (convEntity.getType()) {
            case Destination.NOTIFY:
                holder.ivAvatar.setImageResource(R.drawable.course_notification);
                holder.tvTitle.setText(R.string.course_notification);
                break;
            case Destination.GLOBAL:
            case Destination.BATCH_NOTIFICATION:
                holder.ivAvatar.setImageResource(R.drawable.school_notification);
                holder.tvTitle.setText(R.string.school_notification);
                break;
            case Destination.USER:
                holder.ivAvatar.setImageResource(R.drawable.friend_verified);
                holder.tvTitle.setText(R.string.friend_verified);
                break;
        }
        MessageBody messageBody = new MessageBody(convEntity.getLaterMsg());
        New item = new New();
        item.setContent(messageBody);
        if (messageBody.body != null) {
            holder.tvContent.setText(item.content);
        }
        holder.bvUnread.setBadgeCount(convEntity.getUnRead());
        holder.tvPostTime.setText(AppUtil.convertMills2Date(convEntity.getUpdatedTime() == 0 ? convEntity.getCreatedTime() : convEntity.getUpdatedTime()));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView    ivAvatar;
        public EduBadgeView bvUnread;
        public TextView     tvTitle;
        public TextView     tvContent;
        public TextView     tvPostTime;
        public View         viewAvatar;

        public ViewHolder(View view) {
            super(view);
            ivAvatar = (ImageView) view.findViewById(R.id.iv_avatar);
            viewAvatar = view.findViewById(R.id.view_avatar);
            bvUnread = (EduBadgeView) view.findViewById(R.id.bv_unread);
            bvUnread.setTargetView(viewAvatar);
            bvUnread.setBadgeGravity(Gravity.RIGHT | Gravity.TOP);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
            tvContent = (TextView) view.findViewById(R.id.tv_content);
            tvPostTime = (TextView) view.findViewById(R.id.tv_post_time);
        }
    }
}
