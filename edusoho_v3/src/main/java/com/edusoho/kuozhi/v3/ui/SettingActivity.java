package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.seting.CloudVideoSetting;
import com.edusoho.kuozhi.clean.bean.seting.CourseSetting;
import com.edusoho.kuozhi.clean.bean.seting.UserSetting;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.utils.GsonUtils;
import com.edusoho.kuozhi.clean.utils.ToastUtils;
import com.edusoho.kuozhi.clean.utils.biz.SettingHelper;
import com.edusoho.kuozhi.clean.utils.biz.SharedPreferencesHelper;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.NotificationProvider;
import com.edusoho.kuozhi.v3.model.provider.IMServiceProvider;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.service.M3U8DownService;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.MediaUtil;
import com.edusoho.kuozhi.v3.util.sql.SqliteUtil;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;
import com.edusoho.sharelib.ThirdPartyLogin;
import com.edusoho.videoplayer.util.VLCOptions;
import com.umeng.analytics.MobclickAgent;

import org.videolan.libvlc.util.AndroidUtil;

import java.io.File;

import rx.Observable;
import rx.functions.Func3;

/**
 * Created by JesseHuang on 15/5/6.
 */
public class SettingActivity extends ActionBarBaseActivity {

    public static final String TAG = "setting";

    private View     viewScan;
    private View     tvMsgNotify;
    private View     tvAbout;
    private View     viewClearCache;
    private View     viewFeedback;
    private View     viewSyncSetting;
    private TextView tvCache;
    private TextView mediaCodecView;
    private Button   btnLogout;
    private CheckBox cbOfflineType;
    private CheckBox cbMediaCoderType;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setBackMode(BACK, "设置");
        initView();
        initData();
    }

    private void initView() {
        viewScan = findViewById(R.id.linear_scan);
        viewScan.setOnClickListener(scanClickListener);
        tvMsgNotify = findViewById(R.id.rl_msg_notify);
        tvMsgNotify.setOnClickListener(msgClickListener);
        tvAbout = findViewById(R.id.rl_about);
        cbMediaCoderType = (CheckBox) findViewById(R.id.cb_mediacodec_type);
        mediaCodecView = (TextView) findViewById(R.id.cb_mediacodec_txt);

        tvAbout.setOnClickListener(aboutClickListener);
        cbOfflineType = (CheckBox) findViewById(R.id.cb_offline_type);
        cbOfflineType.setOnClickListener(setOfflineTypeListener);
        tvCache = (TextView) findViewById(R.id.tv_cache);
        viewClearCache = findViewById(R.id.rl_clear_cache);
        viewClearCache.setOnClickListener(cleanCacheListener);
        viewFeedback = findViewById(R.id.rl_feedback);
        viewFeedback.setOnClickListener(feedbackClickListener);
        viewSyncSetting = findViewById(R.id.rl_sync_setting);
        viewSyncSetting.setOnClickListener(syncSettingClickListener);

        btnLogout = (Button) findViewById(R.id.setting_logout_btn);
        btnLogout.setOnClickListener(logoutClickLister);
        if (app.loginUser != null) {
            btnLogout.setVisibility(View.VISIBLE);
        } else {
            btnLogout.setVisibility(View.INVISIBLE);
        }

        cbMediaCoderType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MediaUtil.saveMediaSupportType(getBaseContext(), isChecked ? VLCOptions.SUPPORT_RATE : VLCOptions.DISABLED_RATE);
            }
        });
        initMediaSupportType();
    }

    private void initMediaSupportType() {
        int type = MediaUtil.getMediaSupportType(getBaseContext());
        if (type == VLCOptions.NONE_RATE && AndroidUtil.isKitKatOrLater()) {
            type = VLCOptions.SUPPORT_RATE;
            MediaUtil.saveMediaSupportType(getBaseContext(), type);
        }
        cbMediaCoderType.setChecked(type == VLCOptions.SUPPORT_RATE);
    }

    private void initData() {
        float size = getCacheSize(EdusohoApp.getWorkSpace()) / 1024.0f / 1024.0f;
        if (size == 0) {
            tvCache.setText("0M");
        } else {
            tvCache.setText(String.format("%.1f%s", size, "M"));
        }

        cbOfflineType.setChecked(app.config.offlineType == 1);
    }

    private View.OnClickListener setOfflineTypeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (cbOfflineType.isChecked()) {
                MobclickAgent.onEvent(mContext, "i_mySetting_4gCachESwitch_on");
            } else {
                MobclickAgent.onEvent(mContext, "i_mySetting_4gCacheSwitch_off");
            }
            app.config.offlineType = cbOfflineType.isChecked() ? 1 : 0;
            app.saveConfig();
        }
    };

    private View.OnClickListener cleanCacheListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PopupDialog.createMuilt(
                    mActivity,
                    "清理缓存",
                    "是否清理文件缓存",
                    new PopupDialog.PopupClickListener() {
                        @Override
                        public void onClick(int button) {
                            if (button == PopupDialog.OK) {
                                clearCache();
                            }
                        }
                    }).show();
        }
    };

    private View.OnClickListener scanClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MobclickAgent.onEvent(mContext, "i_mySetting_sweep");
            mActivity.app.mEngine.runNormalPlugin("QrSchoolActivity", mActivity, null);
        }
    };

    private View.OnClickListener msgClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MobclickAgent.onEvent(mContext, "i_mySetting_newMessageNotification");
            mActivity.app.mEngine.runNormalPlugin("MsgReminderActivity", mActivity, null);
        }
    };

    private View.OnClickListener aboutClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MobclickAgent.onEvent(mContext, "i_mySetting_about");
            mActivity.app.mEngine.runNormalPlugin("AboutActivity", mActivity, null);
        }
    };

    private View.OnClickListener feedbackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            app.mEngine.runNormalPlugin("SuggestionActivity", mActivity, null);
        }
    };

    private View.OnClickListener syncSettingClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Observable
                    .zip(SettingHelper.getCourseSetting(), SettingHelper.getUserSetting(), SettingHelper.getCloudVideoSetting(), new Func3<CourseSetting, UserSetting, CloudVideoSetting, Object>() {
                        @Override
                        public Object call(CourseSetting courseSetting, UserSetting userSetting, CloudVideoSetting cloudVideoSetting) {
                            getApplicationContext()
                                    .getSharedPreferences(SharedPreferencesHelper.CourseSetting.XML_NAME, 0)
                                    .edit()
                                    .putString(SharedPreferencesHelper.CourseSetting.SHOW_STUDENT_NUM_ENABLED_KEY, courseSetting.showStudentNumEnabled)
                                    .putString(SharedPreferencesHelper.CourseSetting.CHAPTER_NAME_KEY, courseSetting.chapterName)
                                    .putString(SharedPreferencesHelper.CourseSetting.PART_NAME_KEY, courseSetting.partName)
                                    .apply();
                            getApplicationContext()
                                    .getSharedPreferences(SharedPreferencesHelper.SchoolSetting.XML_NAME, 0)
                                    .edit()
                                    .putString(SharedPreferencesHelper.SchoolSetting.USER_SETTING_KEY, GsonUtils.parseString(userSetting))
                                    .apply();
                            getApplicationContext()
                                    .getSharedPreferences(SharedPreferencesHelper.SchoolSetting.XML_NAME, 0)
                                    .edit()
                                    .putString(SharedPreferencesHelper.SchoolSetting.Cloud_VIDEO_SETTING, GsonUtils.parseString(cloudVideoSetting))
                                    .apply();
                            return null;
                        }
                    })
                    .subscribe(new SubscriberProcessor<Object>() {
                        @Override
                        public void onCompleted() {
                            ToastUtils.show(mContext, "同步成功");
                        }

                        @Override
                        public void onError(String message) {
                            ToastUtils.show(mContext, "同步失败：" + message);
                            UserSetting userSetting = new UserSetting();
                            userSetting.init();
                            getApplicationContext()
                                    .getSharedPreferences(SharedPreferencesHelper.SchoolSetting.XML_NAME, 0)
                                    .edit()
                                    .putString(SharedPreferencesHelper.SchoolSetting.USER_SETTING_KEY, GsonUtils.parseString(userSetting))
                                    .apply();
                        }
                    });
        }
    };

    private View.OnClickListener logoutClickLister = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MobclickAgent.onEvent(mContext, "i_my_Setting_logout");
            if (TextUtils.isEmpty(app.loginUser.thirdParty)) {
                RequestUrl requestUrl = app.bindUrl(Const.LOGOUT, true);
                mActivity.ajaxPostWithLoading(requestUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Bundle bundle = new Bundle();
                        bundle.putString(Const.BIND_USER_ID, app.loginUser.id + "");

                        new IMServiceProvider(getBaseContext()).unBindServer();
                        getAppSettingProvider().setUser(null);
                        app.removeToken();
                        btnLogout.setVisibility(View.INVISIBLE);
                        app.sendMessage(Const.LOGOUT_SUCCESS, null);
                        app.sendMsgToTarget(Const.SWITCH_TAB, null, DefaultPageActivity.class);
                        getNotificationProvider().cancelAllNotification();
                        M3U8DownService service = M3U8DownService.getService();
                        if (service != null) {
                            service.cancelAllDownloadTask();
                        }
                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.setClass(SettingActivity.this, LoginActivity.class);
                        intent.putExtra(TAG, "set");
                        startActivity(intent);
                        finish();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }, "");
            } else {
                new IMServiceProvider(getBaseContext()).unBindServer();
                getAppSettingProvider().setUser(null);
                ThirdPartyLogin.Builder().setPlatformName(app.loginUser.thirdParty).build().logout();
                app.removeToken();
                btnLogout.setVisibility(View.INVISIBLE);
                app.sendMessage(Const.LOGOUT_SUCCESS, null);
                app.sendMsgToTarget(Const.SWITCH_TAB, null, DefaultPageActivity.class);
                finish();
            }
        }
    };

    private long getCacheSize(File workSpace) {
        long totalSize = 0;
        for (File file : workSpace.listFiles()) {
            if (CommonUtil.inArray(file.getName(), new String[]{"videos", "appZip"})) {
                continue;
            }

            if (!file.isDirectory()) {
                totalSize = totalSize + file.length();
            } else {
                totalSize = totalSize + getCacheSize(file);
            }
        }
        return totalSize;
    }

    private void clearCache() {
        deleteFile(EdusohoApp.getWorkSpace());
        mContext.deleteDatabase("webview.db");
        mContext.deleteDatabase("webviewCache.db");

        SqliteUtil.getUtil(mContext).delete("lesson_resource", "", null);

        float size = getCacheSize(EdusohoApp.getWorkSpace()) / 1024.0f / 1024.0f;
        if (size == 0) {
            tvCache.setText("0M");
        } else {
            tvCache.setText(String.format("%.1f%s", size, "M"));
        }

        app.sendMessage(Const.CLEAR_APP_CACHE, null);
    }

    private void deleteFile(File workSpace) {
        for (File file : workSpace.listFiles()) {
            if (file.getName().equals("videos")) {
                continue;
            }
            if (file.isDirectory()) {
                deleteFile(file);
                file.delete();
            } else {
                file.delete();
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    protected NotificationProvider getNotificationProvider() {
        return FactoryManager.getInstance().create(NotificationProvider.class);
    }
}
