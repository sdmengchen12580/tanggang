package com.edusoho.kuozhi.clean.widget.pop;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import myutils.DensityUtil;

public class HorizontalViewGroup extends ViewGroup {

    private int childHorizontalSpace;
    private int childVerticalSpace;
    private int marginWidth;

    public void setValue(Context activity, int num) {
        marginWidth = 0;
        //竖直方向间距
        childVerticalSpace = DensityUtil.dip2px(activity, 12);
        //计算水平间距
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) activity).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        int allViewW = num * DensityUtil.dip2px(activity, 32);
        childHorizontalSpace = (screenW - allViewW) / (num + 1);
        this.setPadding(childHorizontalSpace, 0, 0, 0);
    }

    public int getChildHorizontalSpace() {
        return childHorizontalSpace;
    }

    public HorizontalViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        //fixme 通过传入数组每行的个数，自动计算数值
//        TypedArray attrArray = context.obtainStyledAttributes(attrs, R.styleable.TagsLayout);
//        if (attrArray != null) {
//            childHorizontalSpace = attrArray.getDimensionPixelSize(R.styleable.TagsLayout_tagHorizontalSpaces, 0);
//            childVerticalSpace = attrArray.getDimensionPixelSize(R.styleable.TagsLayout_tagVerticalSpaces, 0);
//            attrArray.recycle();
//        }
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    //设置view的测量模式和大小
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获得测量模式和大小
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        int width = 0;
        int height = 0;//容器的总体高度

        int lineWidth = 0;//记录每一行的宽度，width不断取最大宽度
        int lineHeight = 0;//每一行的高度，会累加至height

        //额外值获取
        int count = getChildCount();
        int left = getPaddingLeft();
        int right = getPaddingRight();
        int top = getPaddingTop();

        int lineNum = 1;//几行

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE)
                continue;
            // 测量child
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            // child宽度+自定义间隔=实际占宽
            int childWidth = child.getMeasuredWidth() + childHorizontalSpace;
            // child高度+自定义间隔=实际占高（还要考虑child自身设置margin影响）
            int childHeight = child.getMeasuredHeight() + childVerticalSpace;
            // child实际宽高+左右margin
            LayoutParams lp = child.getLayoutParams();
            if (lp != null && lp instanceof MarginLayoutParams) {
                MarginLayoutParams params = (MarginLayoutParams) lp;
//                //fixme 每行的第1个添加左边距
//                if (i % 8 == 1) {
//                    params.setMargins(childHorizontalSpace, 0, 0, 0);
//                    child.setLayoutParams(params);
//                }
                childWidth += params.leftMargin + params.rightMargin;
                childHeight += params.topMargin + params.bottomMargin;
            }
            //当前行宽+child宽小于容器宽
            if (lineWidth + childWidth <= sizeWidth - left - right) {
                //将位置信息绑定到child的tag上
                child.setTag(new Location(lineWidth + left, top + height,
                        lineWidth + childWidth + left - childHorizontalSpace,
                        height + childHeight + top - childVerticalSpace));
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }
            //加入当前child，如果超出最大宽度，将没加入前的宽度给width，类加height 然后开启新行
            else {
                lineNum++;
                lineWidth = childWidth; // 记录新行的宽度
                width = Math.max(lineWidth, childWidth); // 取最大的
                height += lineHeight;//总体高度叠加
                lineHeight = childHeight;// 记录新行的高度
                child.setTag(new Location(left, top + height,
                        childWidth + left - childHorizontalSpace, height + childHeight + top - childVerticalSpace));
            }
        }
        width = Math.max(width, lineWidth) + getPaddingLeft() + getPaddingRight();
        sizeHeight += getPaddingTop() + getPaddingBottom();
        height += lineHeight + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension((modeWidth == MeasureSpec.EXACTLY) ? sizeWidth : width, (modeHeight == MeasureSpec.EXACTLY) ? sizeHeight : height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE)
                continue;
            Location location = (Location) child.getTag();
            //子控件layout（左上右下的边距）
            child.layout(location.left, location.top, location.right, location.bottom);
        }
    }

    /**
     * 记录子控件的坐标
     */
    public class Location {
        public Location(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }

        public int left;
        public int top;
        public int right;
        public int bottom;

    }
}
