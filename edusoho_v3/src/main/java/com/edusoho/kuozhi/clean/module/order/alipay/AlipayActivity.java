package com.edusoho.kuozhi.clean.module.order.alipay;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.module.base.BaseActivity;
import com.edusoho.kuozhi.clean.module.course.CourseProjectActivity;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;

/**
 * Created by DF on 2017/4/12.
 * 跳转进支付宝界面
 */

public class AlipayActivity extends BaseActivity {

    private static final String TARGET_ID = "targetId";
    private static final String URL_DATA = "urlData";
    private static final String TARGET_TYPE = "targetType";
    private static final String ALIPAY_CALLBACK_SUCCESS = "alipayCallback?1";
    private static final String ALIPAY_CALLBACK_FAIL = "alipayCallback?0";
    private static final String PAYMENT_CALLBACK = "/pay/center/success/show";

    private LoadDialog mProcessDialog;
    private WebView mAlipay;
    private String mData;
    private int mTargetId;
    private String mTargetType;

    public static void launch(Context context, String data, int targetId, String targetType) {
        Intent intent = new Intent(context, AlipayActivity.class);
        intent.putExtra(URL_DATA, data);
        intent.putExtra(TARGET_ID, targetId);
        intent.putExtra(TARGET_TYPE, targetType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alipay);
        mData = getIntent().getStringExtra(URL_DATA);
        mTargetId = getIntent().getIntExtra(TARGET_ID, 0);
        mTargetType = getIntent().getStringExtra(TARGET_TYPE);
        initView();
        initData();
    }

    private void initView() {
        mAlipay = (WebView) findViewById(R.id.wv);
    }

    private void initData() {
        WebSettings ws = mAlipay.getSettings();
        ws.setJavaScriptEnabled(true);
        mAlipay.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // ------  对alipays:相关的scheme处理 -------
                if(url.startsWith("alipays:") || url.startsWith("alipay")) {
                    try {
                        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getBaseContext().startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }

                if (url.contains(ALIPAY_CALLBACK_SUCCESS)) {
                    showToast(R.string.join_success);
                    sendBroad();
                    return true;
                }
                if (url.contains(PAYMENT_CALLBACK) || url.contains(ALIPAY_CALLBACK_FAIL)) {
                    finish();
                    return true;
                }
                view.loadUrl(url);
                return true;
            }
        });
        mAlipay.loadDataWithBaseURL(null, mData, "text/html", "utf-8", null);
    }

    protected void showProcessDialog() {
        if (mProcessDialog == null) {
            mProcessDialog = LoadDialog.create(this);
        }
        mProcessDialog.show();
    }

    protected void hideProcesDialog() {
        if (mProcessDialog == null) {
            return;
        }
        if (mProcessDialog.isShowing()) {
            mProcessDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAlipay != null) {
            mAlipay.destroy();
        }
    }

    private void sendBroad() {
        Intent intent = new Intent();
        intent.setAction("Finish");
        sendBroadcast(intent);
        if ("classroom".equals(mTargetType)) {
            EdusohoApp.app.mEngine.runNormalPlugin("ClassroomActivity", this, new PluginRunCallback() {
                @Override
                public void setIntentDate(Intent startIntent) {
                    startIntent.putExtra(Const.CLASSROOM_ID, mTargetId);
                }
            });
        } else {
            CourseProjectActivity.launch(this, mTargetId);
        }
        finish();
    }
}
