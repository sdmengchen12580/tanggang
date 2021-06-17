package com.edusoho.kuozhi.clean.module.order.payments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.bean.OrderInfo;
import com.edusoho.kuozhi.clean.module.course.CourseProjectActivity;
import com.edusoho.kuozhi.clean.module.courseset.BaseFinishActivity;
import com.edusoho.kuozhi.clean.module.order.alipay.AlipayActivity;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.InputUtils;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by DF on 2017/4/7.
 * 支付选择界面
 */

public class PaymentsActivity extends BaseFinishActivity<PaymentsContract.Presenter> implements View.OnClickListener, PaymentsContract.View {

    private static final String ORDER_INFO                 = "order_info";
    private static final int    FULL_COIN_PAYABLE          = 1;
    private static final String COUPON_POSITION_IN_COUPONS = "position";

    private Toolbar    mToolbar;
    private View       mAlipay;
    private TextView   mVirtualCoin;
    private TextView   mDiscount;
    private TextView   mBalance;
    private TextView   mAvailableName;
    private TextView   mPay;
    private Dialog     mDialog;
    private LoadDialog mProcessDialog;
    private EditText   mInputPw;
    private View       mAvailableLayout;

    private OrderInfo mOrderInfo;
    private int       mPosition;

    public static void launch(Context context, OrderInfo orderInfo, int position) {
        Intent intent = new Intent(context, PaymentsActivity.class);
        intent.putExtra(ORDER_INFO, orderInfo);
        intent.putExtra(COUPON_POSITION_IN_COUPONS, position);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_way);
        mOrderInfo = (OrderInfo) getIntent().getSerializableExtra(ORDER_INFO);
        mPosition = getIntent().getIntExtra(COUPON_POSITION_IN_COUPONS, -1);

        initView();
        initEvent();
        initShow();
    }

    private void initView() {
        mAlipay = findViewById(R.id.iv_alipay);
        mAvailableLayout = findViewById(R.id.available_layout);
        mToolbar = (Toolbar) findViewById(R.id.tb_toolbar);
        mVirtualCoin = (TextView) findViewById(R.id.tv_virtual_coin);
        mDiscount = (TextView) findViewById(R.id.tv_discount);
        mBalance = (TextView) findViewById(R.id.tv_available_balance);
        mAvailableName = (TextView) findViewById(R.id.tv_available_name);
        mPay = (TextView) findViewById(R.id.tv_pay);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
        mPresenter = new PaymentsPresenter(this, mOrderInfo, mPosition);
        mPresenter.subscribe();
    }

    private void initEvent() {
        mAlipay.setOnClickListener(this);
        mVirtualCoin.setOnClickListener(this);
        mPay.setOnClickListener(this);
        if (OrderInfo.RMB.equals(mOrderInfo.priceType)) {
            mAlipay.performClick();
        } else {
            mVirtualCoin.performClick();
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initShow() {
        if (FULL_COIN_PAYABLE == mOrderInfo.fullCoinPayable) {
            mVirtualCoin.setVisibility(View.VISIBLE);
            mAvailableLayout.setVisibility(View.VISIBLE);
            mVirtualCoin.setText(mOrderInfo.coinName.length() != 0 ? mOrderInfo.coinName : getString(R.string.virtual_coin_pay));
            mAvailableName.setText(String.format(getString(R.string.available_balance),
                    mOrderInfo.coinName.length() != 0 ? mOrderInfo.coinName : getString(R.string.virtual_coin)));
            mBalance.setText(String.format("%.2f", mOrderInfo.account.cash));
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_alipay) {
            clickAlipay();
        } else if (id == R.id.tv_virtual_coin) {
            clickVirtual();
        } else if (id == R.id.tv_pay) {
            goPay();
        }
    }

    private void clickAlipay() {
        mAlipay.setSelected(true);
        mVirtualCoin.setSelected(false);
        mPay.setEnabled(true);
        mPay.setText(R.string.go_pay);
        mPay.setBackgroundColor(ContextCompat.getColor(this, R.color.primary));
        mDiscount.setText(mOrderInfo.getSumPriceByTypeWithUnit(OrderInfo.RMB));
    }

    private void clickVirtual() {
        mAlipay.setSelected(false);
        mVirtualCoin.setSelected(true);
        if (FULL_COIN_PAYABLE == mOrderInfo.fullCoinPayable && mOrderInfo.getSumPriceByType(OrderInfo.COIN) > mOrderInfo.account.cash) {
            mPay.setBackgroundColor(ContextCompat.getColor(this, R.color.secondary_font_color));
            mPay.setText(R.string.insufficient_balance);
            mPay.setEnabled(false);
        }
        if (FULL_COIN_PAYABLE == mOrderInfo.fullCoinPayable) {
            mDiscount.setText(mOrderInfo.getSumPriceByTypeWithUnit(OrderInfo.COIN));
        }
    }

    private void goPay() {
        if (mAlipay.isSelected()) {
            showProcessDialog();
            mPresenter.createOrderAndPay(PaymentsPresenter.ALIPAY, null, -1);
        } else {
            showDialog();
        }
    }

    private void showDialog() {
        if (mDialog == null) {
            mDialog = new Dialog(this, R.style.dialog_custom);
            mDialog.setContentView(R.layout.dialog_input_pay_pw);
            mDialog.setCanceledOnTouchOutside(true);
            Window window = mDialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.width = getResources().getDisplayMetrics().widthPixels;
                window.setAttributes(lp);
                window.setGravity(Gravity.BOTTOM);
            }
            mInputPw = (EditText) mDialog.findViewById(R.id.et_input_pw);
            mInputPw.setOnEditorActionListener(getOnEditorActionListener());
        }
        InputUtils.showKeyBoard(mInputPw, this);
        mDialog.show();
    }

    @NonNull
    private TextView.OnEditorActionListener getOnEditorActionListener() {
        return new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String pw = mInputPw.getText().toString().trim();
                if (mOrderInfo.hasPayPassword != 1) {
                    showToast(R.string.unset_pw_hint);
                    return true;
                }
                if (pw.length() < 5) {
                    showToast(R.string.pw_long_wrong_hint);
                    return true;
                }
                showProcessDialog();
                mPresenter.createOrderAndPay(PaymentsPresenter.COIN, mInputPw.getText().toString().trim(),
                        mOrderInfo.getSumPriceByType(OrderInfo.COIN) > 0 ? mOrderInfo.getSumPriceByType(OrderInfo.COIN) : 0);
                mDialog.dismiss();
                return true;
            }
        };
    }

    @Override
    public void goToAlipay(final String data) {
        AlipayActivity.launch(this, data, mOrderInfo.targetId, mOrderInfo.targetType);
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
    public void showLoadDialog(boolean isShow) {
        if (isShow) {
            showProcessDialog();
        } else {
            hideProcessDialog();
        }
    }

    @Override
    public void sendBroad() {
        Intent intent = new Intent();
        intent.setAction("Finish");
        sendBroadcast(intent);
        if ("classroom".equals(mOrderInfo.targetType)) {
            EventBus.getDefault().postSticky(new MessageEvent(MessageEvent.PAY_SUCCESS));
            EdusohoApp.app.mEngine.runNormalPlugin("ClassroomActivity", this, new PluginRunCallback() {
                @Override
                public void setIntentDate(Intent startIntent) {
                    startIntent.putExtra(Const.CLASSROOM_ID, mOrderInfo.targetId);
                }
            });
        } else {
            CourseProjectActivity.launch(this, mOrderInfo.targetId);
        }
        finish();
    }
}
