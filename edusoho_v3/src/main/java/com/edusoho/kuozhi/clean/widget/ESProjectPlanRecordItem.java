package com.edusoho.kuozhi.clean.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;

public class ESProjectPlanRecordItem extends RelativeLayout {

    private ImageView ivIcon;
    private TextView  tvTitlle;
    private TextView  tvNum;

    public ESProjectPlanRecordItem(Context context) {
        super(context);
    }

    public ESProjectPlanRecordItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ESProjectPlanRecordItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_project_record, this);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ESProjectPlanRecordItem);
        String title = ta.getString(R.styleable.ESProjectPlanRecordItem_title);
        Drawable drawable = ta.getDrawable(R.styleable.ESProjectPlanRecordItem_icon);

        tvTitlle = view.findViewById(R.id.tv_project_plan_record_title);
        tvNum = view.findViewById(R.id.tv_project_plan_record_num);
        ivIcon = view.findViewById(R.id.iv_project_plan_record_item_icon);

        tvTitlle.setText(title);
        ivIcon.setImageDrawable(drawable);
    }

    public void setNum(String num) {
        tvNum.setText(num);
    }
}
