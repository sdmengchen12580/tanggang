package com.edusoho.kuozhi.clean.module.order.dialog.coupons;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.OrderInfo;
import com.edusoho.kuozhi.clean.bean.OrderInfo.Coupon;

import java.util.List;

/**
 * Created by DF on 2017/4/14.
 */

class CouponsAdapter extends BaseAdapter {

    private static final String MINUS           = "minus";
    private static final String ALL_STATION_USE = "all";
    private static final String CLASSROOM_USE   = "classroom";
    private OrderInfo mOrderInfo;

    private List<OrderInfo.Coupon> mCoupons;
    private Context                mContext;

    CouponsAdapter(Context mContext, List<Coupon> list, OrderInfo orderInfo) {
        this.mOrderInfo = orderInfo;
        this.mCoupons = list;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mCoupons == null ? 0 : mCoupons.size();
    }

    @Override
    public Object getItem(int position) {
        return mCoupons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CouponsHolder mHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_coupons_layout, parent, false);
            mHolder = new CouponsHolder(convertView);
            convertView.setTag(mHolder);
        } else {
            mHolder = (CouponsHolder) convertView.getTag();
        }
        Coupon coupon = mCoupons.get(position);
        renderView(mHolder, coupon);
        return convertView;
    }

    private void renderView(CouponsHolder holder, Coupon coupon) {
        holder.mType.setText(coupon.toCouponString(mOrderInfo));
        holder.mDate.setText(String.format(mContext.getString(R.string.valid_date), coupon.deadline.split("T")[0]));
        if (ALL_STATION_USE.equals(coupon.targetType)) {
            holder.mApplicationType.setText(R.string.all_station_use);
        } else if (CLASSROOM_USE.equals(coupon.targetType)) {
            holder.mApplicationType.setText(R.string.classroom_application);
        } else {
            holder.mApplicationType.setText(R.string.course_application);
        }
        holder.mIsSelector.setBackgroundResource(coupon.isSelector ? R.drawable.item_selector
                : R.drawable.item_unselector);
    }

    private static class CouponsHolder {

        private final TextView  mType;
        private final TextView  mDate;
        private final TextView  mApplicationType;
        private final ImageView mIsSelector;

        private CouponsHolder(View view) {
            mType = (TextView) view.findViewById(R.id.tv_type);
            mDate = (TextView) view.findViewById(R.id.tv_validity);
            mApplicationType = (TextView) view.findViewById(R.id.tv_application_type);
            mIsSelector = (ImageView) view.findViewById(R.id.iv_selector);
        }
    }

}
