package com.edusoho.kuozhi.clean.module.order.confirm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.Classroom;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.bean.OrderInfo;
import com.edusoho.kuozhi.clean.module.courseset.BaseFinishActivity;
import com.edusoho.kuozhi.clean.module.order.dialog.coupons.CouponsDialog;
import com.edusoho.kuozhi.clean.module.order.payments.PaymentsActivity;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by DF on 2017/3/25.
 * 确认订单界面
 */

public class ConfirmOrderActivity extends BaseFinishActivity<ConfirmOrderContract.Presenter>
        implements View.OnClickListener, ConfirmOrderContract.View, CouponsDialog.ModifyView {

    private static final String COURSE_SET_ID = "course_set_id";
    private static final String COURSE_ID     = "course_id";

    private   Toolbar    mToolbar;
    protected ImageView  mCourseImg;
    private   TextView   mCourseProjectTitle;
    private   TextView   mPriceTextView;
    protected TextView   mCourseProjectFrom;
    private   ViewGroup  mRlCoupon;
    private   TextView   mCouponValueTextView;
    private   View       mPay;
    private   TextView   mTotalPriceTextView;
    private   LoadDialog mProcessDialog;

    private int              mCourseSetId;
    private int              mCourseId;
    private OrderInfo.Coupon mSelectedCoupon;
    private OrderInfo        mOrderInfo;
    private CouponsDialog    mCouponsDialog;
    private int              mCouponPosition;

    public static void launch(Context context, int courseSetId, int courseId) {
        Intent intent = new Intent(context, ConfirmOrderActivity.class);
        intent.putExtra(COURSE_SET_ID, courseSetId);
        intent.putExtra(COURSE_ID, courseId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        showProcessDialog();
        Intent intent = getIntent();
        mCourseSetId = intent.getIntExtra(COURSE_SET_ID, 0);
        mCourseId = intent.getIntExtra(COURSE_ID, 0);
        initView();
        initEvent();
    }

    protected void initView() {
        mCourseImg = (ImageView) findViewById(R.id.iv_course_image);
        mCourseProjectTitle = (TextView) findViewById(R.id.tv_title);
        mPriceTextView = (TextView) findViewById(R.id.tv_price);
        mCourseProjectFrom = (TextView) findViewById(R.id.tv_from_course);
        mRlCoupon = (ViewGroup) findViewById(R.id.rl_coupon);
        mCouponValueTextView = (TextView) findViewById(R.id.tv_coupon_value);
        mPay = findViewById(R.id.tv_pay);
        mTotalPriceTextView = (TextView) findViewById(R.id.tv_total_price);
        mToolbar = (Toolbar) findViewById(R.id.tb_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
        initData();
    }

    protected void initData() {
        mPresenter = new ConfirmOrderPresenter(this, mCourseSetId, mCourseId);
        mPresenter.subscribe();
    }

    private void initEvent() {
        mRlCoupon.setOnClickListener(this);
        mPay.setOnClickListener(this);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void showPriceView(OrderInfo orderInfo) {
        mOrderInfo = orderInfo;
        mCourseProjectTitle.setText(mOrderInfo.title);
        mPriceTextView.setText(mOrderInfo.getPriceWithUnit());
        if (orderInfo.availableCoupons != null && orderInfo.availableCoupons.size() != 0) {
            mSelectedCoupon = orderInfo.availableCoupons.get(0);
            mSelectedCoupon.isSelector = true;
            showCouponPrice(mSelectedCoupon);
            mOrderInfo.check(mSelectedCoupon);
        } else {
            mOrderInfo.check(null);
        }
        mTotalPriceTextView.setText(String.format(getString(R.string.order_sum_price), mOrderInfo.getSumPriceWithUnit()));
    }

    private void showCouponPrice(OrderInfo.Coupon coupon) {
        mRlCoupon.setVisibility(View.VISIBLE);
        if (coupon != null) {
            mCouponValueTextView.setText(coupon.toString(mOrderInfo));
        } else {
            mCouponValueTextView.setText(String.format(getString(R.string.no_coupon)));
        }
    }

    @Override
    public void showTopView(CourseSet courseSet) {
        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.default_course)
                .showImageOnFail(R.drawable.default_course)
                .showImageOnLoading(R.drawable.default_course)
                .build();
        ImageLoader.getInstance().displayImage(courseSet.cover.middle, mCourseImg, imageOptions);
        mCourseProjectFrom.setText(courseSet.title);
    }

    @Override
    public void showTopView(Classroom classroom) {

    }

    @Override
    public void showToastAndFinish(int content) {
        showToast(content);
        finish();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_coupon) {
            showCouponDialog();
        } else if (id == R.id.tv_pay) {
            PaymentsActivity.launch(this, mOrderInfo, mCouponPosition);
        }
    }

    private void showCouponDialog() {
        if (mCouponsDialog == null) {
            mCouponsDialog = new CouponsDialog();
            mCouponsDialog.setData(mOrderInfo.availableCoupons, mOrderInfo);
        }
        mCouponsDialog.show(getSupportFragmentManager(), "CouponsDialog");
    }

    @Override
    public void showProcessDialog(boolean isShow) {
        if (isShow) {
            showProcessDialog();
        } else {
            hideProcessDialog();
        }
    }

    protected void showProcessDialog() {
        if (mProcessDialog == null) {
            mProcessDialog = LoadDialog.create(this);
        }
        mProcessDialog.show();
    }

    protected void hideProcessDialog() {
        if (mProcessDialog == null) {
            return;
        }
        if (mProcessDialog.isShowing()) {
            mProcessDialog.dismiss();
        }
    }

    @Override
    public void setPriceView(OrderInfo.Coupon coupon) {
        mCouponPosition = mOrderInfo.availableCoupons.indexOf(coupon);
        showCouponPrice(coupon);
        mOrderInfo.check(coupon);
        mTotalPriceTextView.setText(String.format(getString(R.string.order_sum_price), mOrderInfo.getSumPriceWithUnit()));
    }
}
