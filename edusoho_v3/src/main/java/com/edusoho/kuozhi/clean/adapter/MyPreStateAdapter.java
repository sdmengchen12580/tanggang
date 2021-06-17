package com.edusoho.kuozhi.clean.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.ScheduleListBean.SchedulesBean;

import java.util.List;

import myutils.DensityUtil;
import myutils.FastClickUtils;
import myutils.TimeUtils;

public class MyPreStateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SchedulesBean> mList;
    private Context context;
    private ViewGroup.LayoutParams vpView1;
    private ViewGroup.LayoutParams vpView23;
    private int whichType = -1;//"待审核", "已审核", "被驳回"

    public MyPreStateAdapter(Context context, int whichType, List<SchedulesBean> mList, ClickCallBack clickCallBack) {
        this.whichType = whichType;
        this.clickCallBack = clickCallBack;
        this.context = context;
        this.mList = mList;
        vpView1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(context, 182f));
        vpView23 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(context, 117f));
    }

    public void refresh(List<SchedulesBean> list) {
        if (mList != null) {
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void addData(List<SchedulesBean> list) {
        if (list.size() > 0 && list != null) {
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return (mList == null || mList.size() == 0) ? 0 : mList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.itemlayout_pre_my, null);
        return new HasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        if (holder instanceof HasViewHolder) {
            final SchedulesBean bean = mList.get(position);
            ((HasViewHolder) holder).tv_name.setText(bean.getName());
//            ((HasViewHolder) holder).tv_home_address.setText("教室位置:" + bean.getTruename());
            ((HasViewHolder) holder).tv_time.setText(TimeUtils.timeToYMDHMS(bean.getStart_time() + "000") + "~" + TimeUtils.timeToYMDHMS(bean.getEnd_time() + "000"));
            //状态：0申请中，1通过,2未通过，-1已取消
            if (bean.getStatus().equals("0")) {
                ((HasViewHolder) holder).tv_line.setVisibility(View.VISIBLE);
                ((HasViewHolder) holder).rl_cancel_layout.setVisibility(View.VISIBLE);
                ((HasViewHolder) holder).item_layout.setLayoutParams(vpView1);
                ((HasViewHolder) holder).tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (FastClickUtils.isFastClick()) {
                            return;
                        }
                        if (MyPreStateAdapter.this.clickCallBack != null) {
                            MyPreStateAdapter.this.clickCallBack.cancel(bean.getId());
                        }
                    }
                });
                ((HasViewHolder) holder).tv_state.setText("待审核");
                ((HasViewHolder) holder).tv_state.setTextColor(Color.parseColor("#FF753C"));
            } else if (bean.getStatus().equals("1")) {
                ((HasViewHolder) holder).tv_line.setVisibility(View.GONE);
                ((HasViewHolder) holder).rl_cancel_layout.setVisibility(View.GONE);
                ((HasViewHolder) holder).item_layout.setLayoutParams(vpView23);
                ((HasViewHolder) holder).tv_state.setText("已审核");
                ((HasViewHolder) holder).tv_state.setTextColor(Color.parseColor("#62BE62"));
            } else if (bean.getStatus().equals("2")) {
                ((HasViewHolder) holder).tv_line.setVisibility(View.GONE);
                ((HasViewHolder) holder).rl_cancel_layout.setVisibility(View.GONE);
                ((HasViewHolder) holder).item_layout.setLayoutParams(vpView23);
                ((HasViewHolder) holder).tv_state.setText("被驳回");
                ((HasViewHolder) holder).tv_state.setTextColor(Color.parseColor("#DC4C3F"));
            } else if (bean.getStatus().equals("-1")) {
                ((HasViewHolder) holder).tv_line.setVisibility(View.GONE);
                ((HasViewHolder) holder).rl_cancel_layout.setVisibility(View.GONE);
                ((HasViewHolder) holder).item_layout.setLayoutParams(vpView23);
                ((HasViewHolder) holder).tv_state.setText("已取消");
                ((HasViewHolder) holder).tv_state.setTextColor(Color.parseColor("#DC4C3F"));
            } else {
                ((HasViewHolder) holder).tv_line.setVisibility(View.GONE);
                ((HasViewHolder) holder).rl_cancel_layout.setVisibility(View.GONE);
                ((HasViewHolder) holder).item_layout.setLayoutParams(vpView23);
                ((HasViewHolder) holder).tv_state.setVisibility(View.GONE);
            }
        }
    }

    //--------------------------视图--------------------------
    private class HasViewHolder extends RecyclerView.ViewHolder {
        public View item;
        public TextView tv_state;
        public TextView tv_home_address;
        public TextView tv_name;
        public TextView tv_time;
        public TextView tv_line;
        public TextView tv_cancel;
        public LinearLayout item_layout;
        public RelativeLayout rl_cancel_layout;

        public HasViewHolder(View itemView) {
            super(itemView);
            item = itemView;
            tv_line = itemView.findViewById(R.id.tv_line);
            rl_cancel_layout = itemView.findViewById(R.id.rl_cancel_layout);
            item_layout = itemView.findViewById(R.id.item_layout);
            tv_cancel = itemView.findViewById(R.id.tv_cancel);
            tv_state = itemView.findViewById(R.id.tv_state);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_home_address = itemView.findViewById(R.id.tv_home_address);
            tv_time = itemView.findViewById(R.id.tv_time);
        }
    }

    //--------------------------接口--------------------------
    public interface ClickCallBack {

        void cancel(String id);
    }

    private ClickCallBack clickCallBack;
}