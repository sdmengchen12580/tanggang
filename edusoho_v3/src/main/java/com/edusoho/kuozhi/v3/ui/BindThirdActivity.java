package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.ProgressBar;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.Const;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.greenrobot.eventbus.EventBus;


public class BindThirdActivity extends ActionBarBaseActivity {

    private WebView mWebView;
    private String thirdType;
    private Toolbar mToolbar;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_third);
        initView();
    }

    private void initView() {
        mWebView = (WebView) findViewById(R.id.webView);
        mToolbar = (Toolbar) findViewById(R.id.tb_webview_toolbar);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading);
        setSupportActionBar(mToolbar);
        Intent intent = getIntent();
        String url = intent.getStringExtra(Const.WEB_URL);
        thirdType = intent.getStringExtra("type");
        url = String.format(url, app.host, intent.getStringExtra("access_token"), intent.getStringExtra("openid"), thirdType);
        if (thirdType.equals("qq")) {
            url = url + "&appid=1103424113";
        }
        setUpWebView();
        mWebView.loadUrl(url);
    }

    private void setUpWebView() {
        mWebView.setScrollBarSize(0);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String s) {
                webView.loadUrl(s);
                return true;
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView webView, String s) {
                return super.shouldInterceptRequest(webView, s);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView webView, int i) {
                if (i == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(i);
                }
            }
        });

        mWebView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void login(String token) {
                finish();
                EventBus.getDefault().postSticky(new MessageEvent<>(new String[] {token, thirdType}, MessageEvent.BIND_THIRD_SUCCESS));
            }
        }, "android");
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
