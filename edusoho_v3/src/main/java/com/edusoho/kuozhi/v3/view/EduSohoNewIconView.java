package com.edusoho.kuozhi.v3.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.view.Gravity;

/**
 * Created by howzhi on 14-5-12.
 */
public class EduSohoNewIconView extends AppCompatTextView {

    private Context mContext;

    public EduSohoNewIconView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public EduSohoNewIconView(Context context, android.util.AttributeSet attrs) {
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
