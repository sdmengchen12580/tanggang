package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.listener.AvatarLoadingListener;
import com.edusoho.kuozhi.v3.model.bal.Friend;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.view.circleImageView.CircularImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Melomelon on 2015/5/26.
 */
public class FriendFragmentAdapter<T extends Friend> extends BaseAdapter {

    private static final int MAX_TYPE_COUNT = 2;
    private static final int TYPE_HEAD      = 0;
    private static final int TYPE_FRIEND    = 1;

    private LayoutInflater mInflater;
    private ArrayList<T>   mList;
    private Context        mContext;
    private EdusohoApp     mApp;
    private View           mHeadView;

    public FriendFragmentAdapter(Context context, EdusohoApp app) {
        mList = new ArrayList();
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mApp = app;
    }

    public int getSectionForPosition(int position) {
        Friend friend = (Friend) (mList.get(position));
        return friend.getSortLetters().charAt(0);
    }

    public int getPositionForSection(int section) {
        for (int i = 0; i < mList.size(); i++) {
            if (getItemViewType(i + 1) != TYPE_FRIEND) {
                continue;
            }
            Friend friend = (Friend) (mList.get(i));
            String sortStr = friend.getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getViewTypeCount() {
        return MAX_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEAD;
        } else {
            return TYPE_FRIEND;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        int type = getItemViewType(position);
        switch (type) {
            case TYPE_HEAD:
                if (convertView == null) {
                    v = mHeadView == null ? new View(mContext) : mHeadView;
                }
                break;

            case TYPE_FRIEND:
                final ItemHolder itemHolder;
                if (convertView == null) {
                    v = mInflater.inflate(R.layout.item_type_friend, null);
                    itemHolder = new ItemHolder();
                    itemHolder.friendName = (TextView) v.findViewById(R.id.friend_name);
                    itemHolder.friendAvatar = (CircularImageView) v.findViewById(R.id.friend_avatar);
                    itemHolder.dividerLine = v.findViewById(R.id.divider_line);
                    itemHolder.catalog = (TextView) v.findViewById(R.id.catalog);
                    v.setTag(itemHolder);
                } else {
                    itemHolder = (ItemHolder) v.getTag();
                }

                final T friend = mList.get(position - 1);
                position--;
                int section = getSectionForPosition(position);
                if (position == getPositionForSection(section)) {
                    itemHolder.catalog.setVisibility(View.VISIBLE);
                    itemHolder.dividerLine.setVisibility(View.VISIBLE);
                    itemHolder.catalog.setText(friend.getSortLetters());
                } else {
                    itemHolder.catalog.setVisibility(View.GONE);
                    itemHolder.dividerLine.setVisibility(View.GONE);
                }

                itemHolder.friendName.setText(friend.getNickname());
                ImageLoader.getInstance().displayImage(
                        friend.getMediumAvatar(),
                        itemHolder.friendAvatar,
                        mApp.mOptions,
                        new AvatarLoadingListener(friend.getType())
                );
                int titleMaxWidth = getTitleMaxWidth();
                itemHolder.friendName.setMaxWidth(titleMaxWidth);
                break;
        }
        return v;
    }

    private int getTitleMaxWidth() {
        WindowManager wm = (WindowManager) (mContext.getSystemService(Context.WINDOW_SERVICE));
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);

        int space = mContext.getResources().getDimensionPixelSize(R.dimen.head_icon_medium);
        return dm.widthPixels - space - AppUtil.dp2px(mContext, 64 + 32);
    }

    public void addFriendList(List<T> list) {
        list.get(0).isTop = true;
        list.get(list.size() - 1).setBottom(true);
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void clearList() {
        mList.clear();
    }

    public void setHeadView(View headView) {
        mHeadView = headView;
    }

    @Override
    public int getCount() {
        return mList.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position - 1);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ItemHolder {
        private CircularImageView friendAvatar;
        private TextView         friendName;
        private View             dividerLine;
        private TextView         catalog;
    }
}
