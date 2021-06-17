package com.edusoho.kuozhi.v3.view;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by JesseHuang on 15/8/10.
 */
public class CleanCacheTextView extends AppCompatTextView {

    public CleanCacheTextView(Context context) {
        super(context);
    }

    public CleanCacheTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CleanCacheTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

}
