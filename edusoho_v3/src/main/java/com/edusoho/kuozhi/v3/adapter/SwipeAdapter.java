package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.listener.AvatarLoadingListener;
import com.edusoho.kuozhi.v3.model.bal.push.New;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.EduBadgeView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by JesseHuang on 15/6/7.
 */
public class SwipeAdapter extends BaseAdapter {
    private Context             mContext;
    private int                 mLayoutId;
    private List<New>           mList;
    private DisplayImageOptions mOptions;
    private int                 mTitleRestWidth;

    public SwipeAdapter(Context ctx, int id) {
        mContext = ctx;
        mLayoutId = id;
        mList = new CopyOnWriteArrayList<>();
        mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).showImageForEmptyUri(R.drawable.user_avatar).
                showImageOnFail(R.drawable.user_avatar).showImageOnLoading(null).build();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        Bundle bundle = new Bundle();
        bundle.putInt("badge", getAllUnreadNum());
        MessageEngine.getInstance().sendMsg(Const.BADGE_UPDATE, bundle);
    }

    private int getAllUnreadNum() {
        int total = 0;
        for (New item : mList) {
            total += item.unread;
        }
        return total;
    }

    public void update(List<New> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public boolean isNotifyExist() {
        for (New model : mList) {
            if (model.type.equals(Destination.NOTIFY)) {
                return true;
            }
        }
        return false;
    }

    public void addItem(New newModel) {
        if (isNotifyExist()) {
            mList.add(1, newModel);
        } else {
            mList.add(0, newModel);
        }
        notifyDataSetChanged();
    }

    /**
     * for type:news,notify
     *
     * @param newModel
     */
    public void push2Top(New newModel) {
        if (newModel == null) {
            throw new RuntimeException("item can not be null");
        }
        if (!(Destination.NOTIFY.equals(newModel.getType())
                || Destination.GLOBAL.equals(newModel.getType())
                || Destination.ARTICLE.equals(newModel.getType())
                || Destination.BATCH_NOTIFICATION.equals(newModel.getType()))) {
            throw new RuntimeException("item type must be in 'notify, news'");
        }
        New item = null;
        int size = mList.size();
        for (int i = 0; i < size; i++) {
            if (newModel.type.equals(mList.get(i).type)) {
                item = mList.get(i);
                break;
            }
        }
        if (item == null) {
            mList.add(0, newModel);
        } else {
            mList.remove(item);
            mList.add(0, newModel);
        }
        notifyDataSetChanged();
    }

    public void updateItem(New newModel) {
        int size = mList.size();
        for (int i = 0; i < size; i++) {
            New item = mList.get(i);
            if (item.fromId == newModel.fromId) {
                mList.remove(i);
                if (isNotifyExist()) {
                    mList.add(1, newModel);
                } else {
                    mList.add(0, newModel);
                }
                notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public New getItem(int position) {
        if (position < 0 || position > mList.size()) {
            return null;
        }
        return mList.get(position) != null ? mList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mLayoutId, null);
            viewHolder = new ViewHolder(convertView);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final New item = mList.get(position);
        switch (item.convNo) {
            case Destination.NOTIFY:
            case Destination.BATCH_NOTIFICATION:
            case Destination.GLOBAL:
            case Destination.USER:
                viewHolder.tvTitle.setText(mContext.getString(R.string.notification_center));
                viewHolder.ivAvatar.setImageResource(R.drawable.news_global);
                break;
            case Destination.ARTICLE:
                viewHolder.tvTitle.setText(item.title);
                viewHolder.ivAvatar.setImageResource(R.drawable.news_shcool_artical);
                break;
            default:
                viewHolder.tvTitle.setText(item.title);
                ImageLoader.getInstance().displayImage(item.imgUrl, viewHolder.ivAvatar, mOptions, new AvatarLoadingListener(item.type));
        }
        viewHolder.bvUnread.setBadgeCount(item.unread);
        calculateTitleMaxWidth();
        viewHolder.tvTitle.setMaxWidth(mTitleRestWidth);
        viewHolder.tvContent.setText(item.content);
        viewHolder.tvPostTime.setText(AppUtil.convertMills2Date(item.createdTime));
        return convertView;
    }

    public void removeItem(int position) {
        mList.remove(position);
        notifyDataSetChanged();
    }

    static class ViewHolder {
        public ImageView    ivAvatar;
        public EduBadgeView bvUnread;
        public TextView     tvTitle;
        public TextView     tvContent;
        public TextView     tvPostTime;
        public View         viewAvatar;
        public TextView     tvParent;

        public ViewHolder(View view) {
            ivAvatar = (ImageView) view.findViewById(R.id.iv_avatar);
            viewAvatar = view.findViewById(R.id.view_avatar);
            bvUnread = (EduBadgeView) view.findViewById(R.id.bv_unread);
            bvUnread.setTargetView(viewAvatar);
            bvUnread.setBadgeGravity(Gravity.RIGHT | Gravity.TOP);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
            tvContent = (TextView) view.findViewById(R.id.tv_content);
            tvPostTime = (TextView) view.findViewById(R.id.tv_post_time);
            tvParent = (TextView) view.findViewById(R.id.tv_parent);
            view.setTag(this);
        }
    }

    private void calculateTitleMaxWidth() {
        if (mTitleRestWidth == 0) {
            //时间
            float timeWidth = 5 * mContext.getResources().getDimension(R.dimen.new_item_time_size);
            //标签
            float roleWidth = 2 * mContext.getResources().getDimensionPixelSize(R.dimen.x_small_font_size);
            //头像
            float avatarWidth = mContext.getResources().getDimension(R.dimen.head_icon_news_item);
            //根据layout计算  10*2+6*2+4+2*6+2*8
            float marginWidth = CommonUtil.dip2px(mContext, 2 * 6 + 2 * 8 + 4 + 2 * 6 + 2 * 10);
            mTitleRestWidth = EdusohoApp.screenW - (int) Math.ceil(timeWidth + roleWidth + avatarWidth + marginWidth);
        }
    }
}


