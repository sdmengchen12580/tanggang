package com.edusoho.kuozhi.v3.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.module.main.mine.project.ProjectPlanRecordActivity;
import com.edusoho.kuozhi.clean.module.main.study.project.ProjectQrCodeScanActivity;
import com.edusoho.kuozhi.clean.widget.FragmentPageActivity;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.provider.AppSettingProvider;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.StatusCallback;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.provider.IMProvider;
import com.edusoho.kuozhi.v3.model.provider.IMServiceProvider;
import com.edusoho.kuozhi.v3.model.provider.SystemProvider;
import com.edusoho.kuozhi.v3.model.sys.AppConfig;
import com.edusoho.kuozhi.v3.model.sys.AppUpdateInfo;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.ui.fragment.FriendFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.VolleySingleton;
import com.edusoho.kuozhi.v3.view.EduSohoTextBtn;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;
import com.edusoho.kuozhi.v3.view.webview.ESWebViewRequestManager;
import com.google.gson.internal.LinkedTreeMap;
import com.tencent.stat.StatConfig;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import java.util.Queue;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by JesseHuang on 15/4/24.
 */
public class DefaultPageActivity extends ActionBarBaseActivity implements MessageEngine.MessageCallback {

    static {
        System.loadLibrary("opencv_java3");
    }

    public static final String TAG = "DefaultPageActivity";
    public static final int LOGIN_CANCEL = 0x001;
    public String mCurrentTag;
    private int mSelectBtn;
    private LinearLayout mNavLayout;
    private EduSohoTextBtn mDownTabNews;
    private EduSohoTextBtn mDownTabFind;
    private EduSohoTextBtn mDownTabMine;
    private EduSohoTextBtn mDownTabStudy;
    private Toolbar tbActionBar;
    public TextView tvTitle;
    public TextView tvSitting;
    private ImageButton ibFriend;
    private View viewTitleLoading;
    private NavDownTabClickListener mNavDownTabClickListener;
    private Queue<Request<String>> mAjaxQueue;
    private boolean mLogoutFlag = false;
    private RelativeLayout mTopStudyNav;
    private RelativeLayout rlFriend;
    private String mCurrentStudyTag;
    private TextView mStudyAssignments;
    private TextView mStudyPostCourse;
    private TextView mStudyCourseSet;
    private int mSelectedTextId;
    private View vToolbarBreakline;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);
        initView();
        AppUtil.checkUpateApp(mActivity, new StatusCallback<AppUpdateInfo>() {
            @Override
            public void success(AppUpdateInfo obj) {
                if (obj.show) {
                    showUpdateDlg(obj);
                }
                app.addNotify("app_update", null);
            }
        });
        if (getIntent().hasExtra(Const.INTENT_TARGET) || getIntent().hasExtra(Const.SWITCH_NEWS_TAB)) {
            processIntent(getIntent());
        }
        int userId = 0;
        if (EdusohoApp.app.loginUser != null) {
            userId = EdusohoApp.app.loginUser.id;
        }
        StatConfig.setCrashKeyValue(StatConfig.getInstallChannel(mContext), EdusohoApp.app.host + "|" + userId);
        syncSchoolIMSetting();
    }

    @Override
    public void setTitle(CharSequence title) {
        tvTitle.setText(title);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reConnectServer();
        User user = getAppSettingProvider().getCurrentUser();
        if (user != null) {
            new IMProvider(mContext).syncIM().fail(new NormalCallback<VolleyError>() {
                @Override
                public void success(VolleyError volleyError) {
                    volleyError.printStackTrace();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ESWebViewRequestManager.clear();
        VolleySingleton.getInstance(getApplicationContext()).cancelAll();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                moveTaskToBack(true);
                return true;
            case KeyEvent.KEYCODE_MENU:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_CANCEL) {
            selectDownTab(R.id.nav_tab_find);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processIntent(intent);
        setIntent(intent);
    }

    private void processIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        if (intent.hasExtra(Const.INTENT_TARGET)) {
            Class target = (Class) intent.getSerializableExtra(Const.INTENT_TARGET);
            Intent targetIntent = new Intent(mContext, target);
            targetIntent.putExtras(intent.getExtras());
            targetIntent.setFlags(intent.getFlags());
            startActivity(targetIntent);
        } else if (!intent.hasExtra(Const.SWITCH_NEWS_TAB)) {
            selectDownTab(R.id.nav_tab_find);
        }
    }

    //--------------------------------底部切换tab逻辑------------------------
    private class NavDownTabClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            selectDownTab(v.getId());
        }
    }

    //切换页面
    private void selectDownTab(int id) {
        String tag;
        mActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary_color)));
        tvTitle.setTextColor(Color.parseColor("#ffffff"));
        tvSitting.setVisibility(GONE);
        rlFriend.setVisibility(GONE);
        vToolbarBreakline.setVisibility(GONE);
        mTopStudyNav.setVisibility(GONE);
        //交流
        if (id == R.id.nav_tab_news) {
            if (app.loginUser == null) {
                tag = "NotLoginFragment";
            } else {
                tag = "NewsFragment";
            }
            mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ccf9f9f9")));
            tvTitle.setTextColor(getResources().getColor(R.color.primary_font_color));
            setTitle(getString(R.string.title_news));
            rlFriend.setVisibility(VISIBLE);
            vToolbarBreakline.setVisibility(VISIBLE);
        }
        //发现
        //fixme 专项考试前置页面：ExamInfoActivity    暂时新增测试模式
        //fixme 专项考试页面：ExamActivity
        else if (id == R.id.nav_tab_find) {
            tag = "FindFragment";
            setTitle(getSchoolTitle());
            setTitleLoading(false);
        }
        //学习
        else if (id == R.id.nav_tab_study) {
            tag = "StudyFragment";
            setTitle("");
            setTitleLoading(false);
            mTopStudyNav.setVisibility(VISIBLE);
        }
        //我的
        else {
            MobclickAgent.onEvent(this, "i_userInformationPortal");
            tag = "CTMineFragment";
            setTitle("");
            tvSitting.setVisibility(VISIBLE);
            setTitleLoading(false);
        }
        if (tag.equals(mCurrentTag)) {
            return;
        }
        hideFragment(mCurrentTag);
        showFragment(tag);
        changeNavBtn(id);
        changeBtnIcon(id);
        mSelectBtn = id;
    }

    //--------------------------------工具类--------------------------------
    //初始化
    private void initView() {
        mNavLayout = findViewById(R.id.nav_bottom_layout);
        mDownTabNews = findViewById(R.id.nav_tab_news);
        mDownTabFind = findViewById(R.id.nav_tab_find);
        mDownTabMine = findViewById(R.id.nav_tab_mine);
        mDownTabStudy = findViewById(R.id.nav_tab_study);
        tbActionBar = findViewById(R.id.tb_action_bar);
        tvTitle = findViewById(R.id.tv_title);
        tvSitting = findViewById(R.id.tv_sitting);
        ibFriend = findViewById(R.id.ib_friend);
        viewTitleLoading = findViewById(R.id.ll_title_loading);
        vToolbarBreakline = findViewById(R.id.v_line);
        mTopStudyNav = findViewById(R.id.rl_top_nav);
        rlFriend = findViewById(R.id.rl_friend);
        mStudyAssignments = findViewById(R.id.tv_my_assignments);
        mStudyPostCourse = findViewById(R.id.tv_my_post_course);
        mStudyCourseSet = findViewById(R.id.tv_my_course_set);
        setSupportActionBar(tbActionBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mNavDownTabClickListener = new NavDownTabClickListener();

        mToast = Toast.makeText(getApplicationContext(), getString(R.string.app_exit_msg), Toast.LENGTH_SHORT);

        int count = mNavLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mNavLayout.getChildAt(i);
            child.setOnClickListener(mNavDownTabClickListener);
        }
        for (int i = 0; i < mTopStudyNav.getChildCount(); i++) {
            View child = mTopStudyNav.getChildAt(i);
            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickTopStudyNav(view.getId());
                }
            });
        }
        selectDownTab(R.id.nav_tab_find);
        clickTopStudyNav(R.id.tv_my_assignments);
        mSelectedTextId = R.id.tv_my_assignments;
        mDownTabNews.setUpdateIcon(0);
        tvSitting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(mContext, "i_settings");
                mActivity.app.mEngine.runNormalPlugin("SettingActivity", mContext, null);
            }
        });
        ibFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (app.loginUser == null) {
                    loginAction();
                    return;
                }
                FragmentPageActivity.launch(mContext, FriendFragment.class.getName(), null);
            }
        });
    }

    //启动
    public static void launch(Context context, int flags) {
        Intent intent = new Intent(context, DefaultPageActivity.class);
        intent.setFlags(flags);
        context.startActivity(intent);
    }

    //刷新im
    private void syncSchoolIMSetting() {
        User user = getAppSettingProvider().getCurrentUser();
        if (user == null) {
            return;
        }
        new SystemProvider(mContext).getIMSetting()
                .success(new NormalCallback<LinkedTreeMap>() {
                    @Override
                    public void success(LinkedTreeMap map) {
                        AppConfig appConfig = getAppSettingProvider().getAppConfig();
                        boolean isEnableIMChat = false;
                        if (map != null && map.containsKey("enabled")) {
                            isEnableIMChat = AppConfig.IM_OPEN.equals(map.get("enabled"));
                        }
                        if (appConfig.isEnableIMChat != isEnableIMChat) {
                            appConfig.isEnableIMChat = isEnableIMChat;
                            getAppSettingProvider().saveConfig(appConfig);
                        }

                        if (isEnableIMChat) {
                            reConnectServer();
                        }
                    }
                }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError volleyError) {
                if (volleyError instanceof TimeoutError || volleyError instanceof NoConnectionError) {
                    return;
                }
                AppConfig appConfig = getAppSettingProvider().getAppConfig();
                appConfig.isEnableIMChat = false;
                getAppSettingProvider().saveConfig(appConfig);
            }
        });
    }

    //图标切换
    private void changeBtnIcon(int id) {
        mDownTabNews.setIcon(getResources().getString(R.string.font_news));
        mDownTabFind.setIcon(getResources().getString(R.string.font_find));
        mDownTabMine.setIcon(getResources().getString(R.string.font_mine));
        mDownTabStudy.setIcon(getResources().getString(R.string.font_study));
        if (id == R.id.nav_tab_news) {
            mDownTabNews.setIcon(getResources().getString(R.string.font_news_pressed));
        } else if (id == R.id.nav_tab_find) {
            mDownTabFind.setIcon(getResources().getString(R.string.font_find_pressed));
        } else if (id == R.id.nav_tab_mine) {
            mDownTabMine.setIcon(getResources().getString(R.string.font_mine_pressed));
        } else if (id == R.id.nav_tab_study) {
            mDownTabStudy.setIcon(getResources().getString(R.string.font_study_pressed));
        }
    }

    public void setTitleLoading(boolean isLoading) {
        if (isLoading && app.loginUser != null) {
            tvTitle.setVisibility(GONE);
            viewTitleLoading.setVisibility(VISIBLE);
        } else {
            tvTitle.setVisibility(VISIBLE);
            viewTitleLoading.setVisibility(GONE);
        }
    }

    public String getCurrentFragment() {
        return mCurrentTag;
    }

    protected String[] getFragments() {
        return new String[]{
                "NewsFragment",
                "FindFragment",
                "FriendFragment",
                "CTMineFragment"
        };
    }

    protected String getSchoolTitle() {
        return app.defaultSchool == null ? getString(R.string.title_find) : app.defaultSchool.name;
    }

    private void hideFragment(String tag) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        Fragment fragment = mFragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            fragmentTransaction.hide(fragment);
            if (!mLogoutFlag) {
                fragmentTransaction.commit();
            } else {
                fragmentTransaction.commitAllowingStateLoss();
            }
        }
    }

    private void showFragment(String tag) {
        Fragment fragment;
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragment = mFragmentManager.findFragmentByTag(tag);

        if (fragment != null) {
            fragmentTransaction.show(fragment);
        } else {
            fragment = app.mEngine.runPluginWithFragment(tag, mActivity, null);
            fragmentTransaction.add(R.id.fragment_container, fragment, tag);
        }
        if (!mLogoutFlag) {
            fragmentTransaction.commit();
        } else {
            fragmentTransaction.commitAllowingStateLoss();
        }
        mCurrentTag = tag;
    }

    private void changeNavBtn(int id) {
        int count = mNavLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mNavLayout.getChildAt(i);
            if (child.getId() == id) {
                child.setEnabled(false);
            } else {
                child.setEnabled(true);
            }
        }
    }

    private void clickTopStudyNav(int id) {
        if (id == mSelectedTextId) {
            return;
        }
        if (id == R.id.ib_study_scan) {
            Intent intent = new Intent(this, ProjectQrCodeScanActivity.class);
            startActivity(intent);
            return;
        }
        if (id == R.id.ib_study_archives) {
            ProjectPlanRecordActivity.launch(this);
            return;
        }
        if (id == R.id.ib_study_archives) {
            return;
        }
        if (id == R.id.tv_my_assignments) {
            mCurrentStudyTag = "Assignments";
        } else if (id == R.id.tv_my_post_course) {
            mCurrentStudyTag = "PostCourse";
        } else if (id == R.id.tv_my_course_set) {
            mCurrentStudyTag = "CourseSet";
        }
        mSelectedTextId = id;
        changeTopText(id);
        EventBus.getDefault().postSticky(new MessageEvent<>(mCurrentStudyTag, MessageEvent.SWITCH_STUDY));
    }

    private void changeTopText(int id) {
        mStudyAssignments.setTextColor(getResources().getColor(R.color.my_assignment_unchecked));
        mStudyPostCourse.setTextColor(getResources().getColor(R.color.my_assignment_unchecked));
        mStudyCourseSet.setTextColor(getResources().getColor(R.color.my_assignment_unchecked));
        mStudyAssignments.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.my_assignment_unchecked_font_size));
        mStudyPostCourse.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.my_assignment_unchecked_font_size));
        mStudyCourseSet.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.my_assignment_unchecked_font_size));
        if (id == R.id.tv_my_assignments) {
            mStudyAssignments.setTextColor(getResources().getColor(R.color.white));
            mStudyAssignments.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_xl));
        } else if (id == R.id.tv_my_post_course) {
            mStudyPostCourse.setTextColor(getResources().getColor(R.color.white));
            mStudyPostCourse.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_xl));
        } else if (id == R.id.tv_my_course_set) {
            mStudyCourseSet.setTextColor(getResources().getColor(R.color.white));
            mStudyCourseSet.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_xl));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.home) {
            Log.d("onOptionsItemSelected", "home");
        }
        return false;
    }

    @Override
    public void invoke(final WidgetMessage message) {
        processMessage(message);
        final MessageType messageType = message.type;
        switch (messageType.code) {
            case Const.OPEN_COURSE_CHAT:
                app.mEngine.runNormalPlugin("ChatActivity", mContext, null);
                break;
            case Const.SWITCH_TAB:
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mLogoutFlag = true;
                            selectDownTab(R.id.nav_tab_find);
                            mLogoutFlag = false;
                        } catch (Exception ex) {
                            Log.d(TAG, ex.getMessage());
                        }
                    }
                });
                break;
            default:
        }

        if (messageType.type.equals(Const.BADGE_UPDATE)) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mDownTabNews.setUpdateIcon(message.data.getInt("badge"));
                }
            });
            return;
        }
        if (messageType.type.equals(Const.LOGIN_SUCCESS)) {
            mLogoutFlag = true;
            new Handler(getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    syncSchoolIMSetting();
                    if (getIntent().hasExtra(Const.SWITCH_NEWS_TAB)) {
                        selectDownTab(R.id.nav_tab_find);
                    } else {
                        selectDownTab(R.id.nav_tab_news);
                    }
                    mLogoutFlag = false;
                }
            });
        }
        if (messageType.type.equals(Const.LOGOUT_SUCCESS)) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mDownTabNews.setUpdateIcon(0);
                }
            });
        }
    }

    @Override
    protected void processMessage(WidgetMessage message) {
        MessageType messageType = message.type;
        if (Const.TOKEN_LOSE.equals(messageType.type)) {
            CommonUtil.longToast(getBaseContext(), getString(R.string.token_lose_notice));
            handleTokenLostMsg();
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        return new MessageType[]{
                new MessageType(Const.OPEN_COURSE_CHAT, source),
                new MessageType(Const.SWITCH_TAB, source),
                new MessageType(Const.LOGIN_SUCCESS),
                new MessageType(Const.LOGOUT_SUCCESS),
                new MessageType(Const.TOKEN_LOSE),
                new MessageType(Const.BADGE_UPDATE),
                new MessageType(Const.NEW_FANS)
        };
    }

    private void showUpdateDlg(final AppUpdateInfo result) {
        PopupDialog popupDialog = PopupDialog.createMuilt(
                mActivity,
                "版本更新",
                "更新内容\n" + result.updateInfo, new PopupDialog.PopupClickListener() {
                    @Override
                    public void onClick(int button) {
                        if (button == PopupDialog.OK) {
                            app.startUpdateWebView(result.updateUrl);
                            app.removeNotify("app_update");
                        }
                    }
                });

        popupDialog.setOkText("更新");
        popupDialog.show();
    }

    private boolean checkSchoolHasLogined(String host) {
        if (host.startsWith("http://")) {
            host = host.substring(7);
            Log.d(null, "host->" + host);
        }
        SharedPreferences sp = getSharedPreferences("search_history", MODE_PRIVATE);
        if (sp.contains(host)) {
            return true;
        }
        return false;
    }

    private void reConnectServer() {
        User user = getAppSettingProvider().getCurrentUser();
        if (user == null) {
            return;
        }
        new IMServiceProvider(getBaseContext()).reConnectServer(user.id, user.nickname);
    }

    public AppSettingProvider getAppSettingProvider() {
        return FactoryManager.getInstance().create(AppSettingProvider.class);
    }

    private void loginAction() {
        app.mEngine.runNormalPluginWithAnim("LoginActivity", mContext, null, new NormalCallback() {
            @Override
            public void success(Object obj) {
                mActivity.overridePendingTransition(R.anim.down_to_up, R.anim.none);
            }
        });
    }
}
