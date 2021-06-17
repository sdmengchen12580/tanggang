package com.edusoho.kuozhi.clean.module.order.confirm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;
import android.view.View;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.Classroom;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by RexXiang on 2017/7/5.
 */

public class ConfirmClassOrderActivity extends ConfirmOrderActivity {

    private static final String CLASSROOM_ID = "classroom_id";

    private int mClassroomId;
    private TextView mFromHint;
    private View mFromLine;

    public static void launch(Context context, int classroomId) {
        Intent intent = new Intent(context, ConfirmClassOrderActivity.class);
        intent.putExtra(CLASSROOM_ID, classroomId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mClassroomId = getIntent().getIntExtra(CLASSROOM_ID, 0);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        super.initView();
        mFromLine = findViewById(R.id.from_line);
        mFromHint = (TextView) findViewById(R.id.tv_from);
    }

    @Override
    protected void initData() {
        ConfirmOrderContract.Presenter mPresenter = new ConfirmClassOrderPresenter(this, mClassroomId);
        mPresenter.subscribe();
    }

    @Override
    public void showTopView(Classroom classroom) {
        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.default_course)
                .showImageOnFail(R.drawable.default_course)
                .showImageOnLoading(R.drawable.default_course)
                .build();
        ImageLoader.getInstance().displayImage(classroom.cover.middle, mCourseImg, imageOptions);
        mCourseProjectFrom.setVisibility(View.GONE);
        mFromHint.setVisibility(View.GONE);
        mFromLine.setVisibility(View.GONE);
    }

    @Override
    public void showTopView(CourseSet courseSet) {

    }
}
