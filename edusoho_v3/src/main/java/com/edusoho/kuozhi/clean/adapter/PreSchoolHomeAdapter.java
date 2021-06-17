package com.edusoho.kuozhi.clean.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.studyhome.TrainRoom;

import java.util.List;

import myutils.FastClickUtils;

public class PreSchoolHomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TrainRoom> mList;
    private Context context;

    public PreSchoolHomeAdapter(Context context, List<TrainRoom> mList, ClickCallBack clickCallBack) {
        this.clickCallBack = clickCallBack;
        this.context = context;
        this.mList = mList;
    }

    @Override
    public int getItemCount() {
        return (mList == null || mList.size() == 0) ? 0 : mList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.itemlayout_pre_school_home, null);
        return new HasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        if (holder instanceof HasViewHolder) {
            final TrainRoom bean = mList.get(position);
            ((HasViewHolder) holder).tv_name.setText(bean.getName());
            ((HasViewHolder) holder).tv_email.setText(bean.getEmail());
            ((HasViewHolder) holder).tv_home_address.setText(bean.getLocation());
            //进入详情
            ((HasViewHolder) holder).layout_top.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (FastClickUtils.isFastClick()) {
                        return;
                    }
                    if (PreSchoolHomeAdapter.this.clickCallBack != null) {
                        PreSchoolHomeAdapter.this.clickCallBack.lookDetails(bean);
                    }
                }
            });
        }
    }

    //添加数据
    public void addData(List<TrainRoom> list) {
        if (mList != null) {
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    //刷新
    public void refresh(List<TrainRoom> list) {
        if (mList != null) {
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    //--------------------------视图--------------------------
    private class HasViewHolder extends RecyclerView.ViewHolder {
        public View item;
        public ImageView img_pre_school;
        public TextView tv_home_address;
        public TextView tv_name;
        public TextView tv_email;
        public RelativeLayout layout_top;

        public HasViewHolder(View itemView) {
            super(itemView);
            item = itemView;
            layout_top = itemView.findViewById(R.id.layout_top);
            tv_name = itemView.findViewById(R.id.tv_name);
            img_pre_school = itemView.findViewById(R.id.img_pre_school);
            tv_home_address = itemView.findViewById(R.id.tv_home_address);
            tv_email = itemView.findViewById(R.id.tv_email);
        }
    }

    //--------------------------接口--------------------------
    public interface ClickCallBack {
        void lookDetails(TrainRoom bean);
    }

    private ClickCallBack clickCallBack;
}
