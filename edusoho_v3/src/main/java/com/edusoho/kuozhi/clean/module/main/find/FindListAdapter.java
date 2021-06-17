package com.edusoho.kuozhi.clean.module.main.find;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.AppChannel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JesseHuang on 2017/5/22.
 */

public class FindListAdapter extends BaseAdapter {

    private Context mContext;
    private List<AppChannel> mList;

    public FindListAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
    }

    public void addDataList(List<AppChannel> appChannelses) {
        int i = 0;
        for (AppChannel appChannel : appChannelses) {
            appChannel.id = i++;
        }
        mList.addAll(appChannelses);
        notifyDataSetChanged();
    }

    public void clear() {
        mList.clear();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_find_cardview, parent, false);
            viewHolder = new ViewHolder(convertView);
            viewHolder.findCardView.setAdapter(new FindCardItemAdapter(mContext));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        AppChannel appChannel = mList.get(position);
        viewHolder.findCardView.setDiscoveryCardEntity(appChannel);
        viewHolder.findCardView.setMoreClickListener(appChannel.orderType == null ? "recommend" : appChannel.orderType, appChannel.type, appChannel.categoryId);
        return convertView;
    }

    public static class ViewHolder {
        FindCardView findCardView;

        public ViewHolder(View view) {
            findCardView = (FindCardView) view.findViewById(R.id.cardview);
        }
    }
}
