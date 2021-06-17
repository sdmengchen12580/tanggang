package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.utils.biz.SettingHelper;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.factory.NotificationProvider;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.SystemInfo;
import com.edusoho.kuozhi.v3.model.result.SchoolResult;
import com.edusoho.kuozhi.v3.model.result.UserResult;
import com.edusoho.kuozhi.v3.model.sys.AppConfig;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;


public class StartActivity extends ActionBarBaseActivity implements MessageEngine.MessageCallback {

    public static final String INIT_APP = "init_app";

    protected Intent mCurrentIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentIntent = getIntent();
        if (mCurrentIntent != null && mCurrentIntent.hasExtra(NotificationProvider.ACTION_TAG)) {
            startApp();
            return;
        }
        initView();
        app.registMsgSource(this);
        startAnim();
    }

    @Override
    protected int getStatusBarColor() {
        return Color.TRANSPARENT;
    }

    protected void initView() {
        setContentView(R.layout.activity_start);
        findViewById(R.id.li_start_load).setBackgroundResource(R.drawable.load_bg);
    }

    protected void startAnim() {
        final View nameView = findViewById(R.id.tv_start_name);
        final View titleView = findViewById(R.id.tv_start_title);
        View iconView = findViewById(R.id.tv_start_icon);

        iconView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.activity_start_icon_anim));
        nameView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.alpha_top_to_bottom));

        Animation titleAnimation = AnimationUtils.loadAnimation(mContext, R.anim.alpha_bottom_to_top);
        titleView.setAnimation(titleAnimation);
        titleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startIconRotateAnim();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        titleAnimation.start();
    }

    private void startIconRotateAnim() {
        View iconBgView = findViewById(R.id.tv_start_icon_bg);
        iconBgView.setBackgroundResource(R.drawable.start_app_splash);

        Animation rotateAnimation = AnimationUtils.loadAnimation(mContext, R.anim.alpha_rotate);
        iconBgView.setAnimation(rotateAnimation);
        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startSplash();
                    }
                }, 200);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        rotateAnimation.start();
    }

    public void startSplash() {
//        if (app.config.showSplash) {
//            app.mEngine.runNormalPlugin("SplashActivity", this, null);
//            app.config.showSplash = false;
//            app.saveConfig();
//            return;
//        }
        app.sendMessage(INIT_APP, null);
    }

    protected void startLoading(String loadText) {
        View loadLayoutView = findViewById(R.id.li_start_load);
        TextView loadTextTv = (TextView) findViewById(R.id.loading_txt);
        loadTextTv.setText(loadText);
        loadLayoutView.setVisibility(View.VISIBLE);
    }

    protected void hideLoading() {
        View loadLayoutView = findViewById(R.id.li_start_load);
        loadLayoutView.setVisibility(View.GONE);
    }

    protected void initApp() {
        if (!AppUtil.isNetConnect(mContext)) {
            CommonUtil.longToast(this, getString(R.string.network_does_not_work));
            startApp();
            return;
        }
        if (app.host == null || "".equals(app.host)) {
            startApp();
            return;
        }

        checkSchoolApiVersion();
    }

    @Override
    public void invoke(WidgetMessage message) {
        if (message.type.type == INIT_APP) {
            initApp();
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        MessageType[] messageTypes = {new MessageType(MessageType.NONE, INIT_APP)};
        return messageTypes;
    }

    @Override
    protected void onDestroy() {
        app.unRegistMsgSource(this);
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    protected void checkSchoolAndUserToken(final SystemInfo systemInfo) {
        startLoading("登录用户");
        ajaxGet(String.format("%s/%s?version=2&token=%s", systemInfo.mobileApiUrl, Const.CHECKTOKEN, app.token), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                SettingHelper.sync(mContext);
                UserResult userResult = parseJsonValue(response.toString(), new TypeToken<UserResult>() {
                });

                if (userResult == null || userResult.site == null) {
                    showSchoolErrorDlg();
                    return;
                }
                School site = userResult.site;
                site.version = systemInfo.version;
                if (!checkMobileVersion(site.apiVersionRange)) {
                    return;
                }

                getAppSettingProvider().setCurrentSchool(site);
                app.setCurrentSchool(site);
                if (userResult.user != null) {
                    app.saveToken(userResult);
                } else {
                    app.removeToken();
                }
                startApp();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoading();
            }
        });
    }

    /**
     * 检查网校版本
     */
    private void checkSchoolVersion(final SystemInfo systemInfo) {
        startLoading("检查App版本");
        ajaxGet(systemInfo.mobileApiUrl + Const.VERIFYSCHOOL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideLoading();
                SchoolResult schoolResult = parseJsonValue(response, new TypeToken<SchoolResult>() {
                });

                if (schoolResult == null || schoolResult.site == null) {
                    showSchoolErrorDlg();
                    return;
                }
                School site = schoolResult.site;
                app.setLoginType(schoolResult.loginType);
                site.version = systemInfo.version;
                if (!checkMobileVersion(site.apiVersionRange)) {
                    return;
                }

                getAppSettingProvider().setCurrentSchool(site);
                app.setCurrentSchool(site);
                SettingHelper.sync(mContext);
                startApp();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoading();
            }
        });
    }

    private boolean checkMobileVersion(
            HashMap<String, String> versionRange) {
        String min = versionRange.get("min");
        String max = versionRange.get("max");

        int result = CommonUtil.compareVersion(app.apiVersion, min);
        if (result == Const.LOW_VERSIO) {
            PopupDialog popupDialog = PopupDialog.createMuilt(
                    mContext,
                    "网校提示",
                    "您的客户端版本过低，无法登录该网校，请立即更新至最新版本。\n或选择其他网校",
                    new PopupDialog.PopupClickListener() {
                        @Override
                        public void onClick(int button) {
                            if (button == PopupDialog.OK) {
                                String code = getResources().getString(R.string.app_code_v3);
                                String updateUrl = String.format(
                                        "%s%s?code=%s",
                                        app.schoolHost,
                                        Const.DOWNLOAD_URL,
                                        code
                                );
                                app.startUpdateWebView(updateUrl);
                            } else {
                                QrSchoolActivity.start(mActivity);
                                finish();
                            }
                        }
                    });
            popupDialog.setCancelText("选择新网校");
            popupDialog.setOkText("立即下载");
            popupDialog.show();
            return false;
        }

        result = CommonUtil.compareVersion(app.apiVersion, max);
        if (result == Const.HEIGHT_VERSIO) {
            PopupDialog popupDialog = PopupDialog.createMuilt(
                    mContext,
                    "网校提示",
                    "网校服务器版本过低，无法继续登录！请重新尝试。\n或选择其他网校",
                    new PopupDialog.PopupClickListener() {
                        @Override
                        public void onClick(int button) {
                            if (button == PopupDialog.OK) {
                                QrSchoolActivity.start(mActivity);
                                finish();
                            }
                        }
                    });

            popupDialog.setOkText("选择新网校");
            popupDialog.show();
            return false;
        }

        return true;
    }

    /**
     * 检查网校Api版本
     */
    protected void checkSchoolApiVersion() {
        startLoading("检查网校信息");
        RequestUrl requestUrl = new RequestUrl(app.host + Const.VERIFYVERSION);
        app.getUrl(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideLoading();
                SystemInfo systemInfo = parseJsonValue(response.toString(), new TypeToken<SystemInfo>() {
                });
                if (systemInfo == null || systemInfo.mobileApiUrl == null || "".equals(systemInfo.mobileApiUrl)) {
                    showSchoolErrorDlg();
                    return;
                }

                app.schoolVersion = systemInfo.version;

                if (TextUtils.isEmpty(app.token)) {
                    checkSchoolVersion(systemInfo);
                    return;
                }
                checkSchoolAndUserToken(systemInfo);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoading();
                showSchoolErrorDlg();
            }
        });
    }

    protected void startApp() {
        if (app.config.startWithSchool && app.defaultSchool != null && !TextUtils.isEmpty(app.token)) {
            CoreEngine.create(mContext).registPlugin("DefaultPageActivity", DefaultPageActivity.class);
            app.mEngine.runNormalPlugin("DefaultPageActivity", this, new PluginRunCallback() {
                @Override
                public void setIntentDate(Intent startIntent) {
                    if (mCurrentIntent.hasCategory(Intent.CATEGORY_LAUNCHER)) {
                        startIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    }
                    startIntent.putExtras(mCurrentIntent);
                }
            });
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
            return;
        }
        app.mEngine.runNormalPlugin("QrSchoolActivity", this, null);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    /**
     * 处理网校异常dlg
     */
    protected void showSchoolErrorDlg() {
        if (!this.isFinishing()) {
            PopupDialog popupDialog = PopupDialog.createMuilt(
                    mContext,
                    "提示信息",
                    "网校客户端已关闭或网校服务器出现异常。\n请联系管理员！或选择新网校",
                    new PopupDialog.PopupClickListener() {
                        @Override
                        public void onClick(int button) {
                            if (button == PopupDialog.OK) {
                                QrSchoolActivity.start(mActivity);
                                finish();
                            }
                        }
                    }
            );
            popupDialog.setOkText("选择新网校");
            popupDialog.show();
        }
    }
}
