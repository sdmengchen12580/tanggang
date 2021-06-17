package com.edusoho.kuozhi.clean.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.view.Gravity;

/**
 * Created by JesseHuang on 2017/3/27.
 */

public class ESIconView extends AppCompatTextView {
    private Context mContext;

    public ESIconView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public ESIconView(Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView() {
        Typeface iconfont = Typeface.createFromAsset(mContext.getAssets(), "iconfont_new.ttf");
        setTypeface(iconfont);
        setGravity(Gravity.CENTER);
    }
}
