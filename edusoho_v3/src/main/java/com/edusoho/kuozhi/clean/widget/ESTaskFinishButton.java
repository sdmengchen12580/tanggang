package com.edusoho.kuozhi.clean.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.edusoho.kuozhi.R;

/**
 * Created by JesseHuang on 2017/6/27.
 */

public class ESTaskFinishButton extends TextView {

    private Context mContext;

    public ESTaskFinishButton(Context context) {
        super(context);
        mContext = context;
    }

    public ESTaskFinishButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    public ESTaskFinishButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.ESTaskFinishButton);
        boolean isFinish = ta.getBoolean(R.styleable.ESTaskFinishButton_finish, false);
        setFinish(isFinish);
        ta.recycle();
    }

    public void setFinish(boolean isFinish) {
        if (isFinish) {
            this.setTextColor(mContext.getResources().getColor(R.color.disabled2_hint_color));
            this.setCompoundDrawablesWithIntrinsicBounds(R.drawable.task_finish_left_icon, 0, 0, 0);
            this.setBackground(getResources().getDrawable(R.drawable.task_finish_button_bg));
        } else {
            this.setTextColor(mContext.getResources().getColor(R.color.primary_font_color));
            this.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            this.setBackground(getResources().getDrawable(R.drawable.task_unfinish_button_grey_bg));
        }
    }
}
