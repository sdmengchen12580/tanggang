package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.module.course.CourseProjectActivity;
import com.edusoho.kuozhi.clean.utils.ToastUtils;
import com.edusoho.kuozhi.imserver.IMClient;
import com.edusoho.kuozhi.imserver.entity.ConvEntity;
import com.edusoho.kuozhi.imserver.entity.Role;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.imserver.managar.IMConvManager;
import com.edusoho.kuozhi.imserver.ui.MessageListFragment;
import com.edusoho.kuozhi.imserver.ui.MessageListPresenterImpl;
import com.edusoho.kuozhi.imserver.ui.data.DefautlMessageDataProvider;
import com.edusoho.kuozhi.imserver.ui.view.IMessageInputView;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.entity.error.Error;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.provider.AppSettingProvider;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.PluginFragmentCallback;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.bal.course.Course;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailsResult;
import com.edusoho.kuozhi.v3.model.provider.CourseProvider;
import com.edusoho.kuozhi.v3.model.provider.IMProvider;
import com.edusoho.kuozhi.v3.model.provider.IMServiceProvider;
import com.edusoho.kuozhi.v3.model.provider.UserProvider;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.chat.AbstractIMChatActivity;
import com.edusoho.kuozhi.v3.ui.fragment.NewsFragment;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.Promise;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;
import com.google.gson.internal.LinkedTreeMap;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by JesseHuang on 15/9/16.
 */
public class NewsCourseActivity extends AbstractIMChatActivity implements MessageEngine.MessageCallback {

    public static final int CLEAR = 0x10;

    public static final String COURSE_ID = "courseId";
    public static final String SHOW_TYPE = "show_type";

    public static final  int    DISCUSS_TYPE  = 0;
    public static final  int    LEARN_TYPE    = 1;
    private static final String FRAGMENT_NAME = "DiscussFragment";

    private static final String mFragmentTags[]     = {"DiscussFragment", "CourseStudyFragment", "TeachFragment"};
    private static final String mEntranceType[]     = {"Discuss", "StudyOrTeacher"};
    private static final String mRadioButtonTitle[] = {"学习", "教学"};

    private int      mCourseId;
    private Course   mCourse;
    private Handler  mHandler;
    private TextView tvGotoDetail;

    protected FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentManager = getSupportFragmentManager();
        MessageEngine.getInstance().registMessageSource(this);
        initData();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected View createView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_news_course, null);
        tvGotoDetail = (TextView) view.findViewById(R.id.tv_goto_detail);
        return view;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MessageEngine.getInstance().unRegistMessageSource(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.postDelayed(mNewFragment2UpdateItemBadgeRunnable, 500);

        String convNo = getIntent().getStringExtra(CONV_NO);
        if (!TextUtils.isEmpty(convNo)) {
            getNotificationProvider().cancelNotification(convNo.hashCode());
        }
    }

    @Override
    protected void attachMessageListFragment() {
    }

    private void initData() {
        mHandler = new Handler();
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        mCourseId = intent.getIntExtra(COURSE_ID, 0);
        if (mCourseId == 0) {
            ToastUtils.show(mContext, "课程信息不存在!");
            return;
        }

        final LoadDialog loadDialog = LoadDialog.create(this);
        loadDialog.show();
        new CourseProvider(mContext).getCourse(mCourseId)
                .success(new NormalCallback<CourseDetailsResult>() {
                    @Override
                    public void success(CourseDetailsResult courseDetailsResult) {
                        Log.d(TAG, "load course");
                        loadDialog.dismiss();
                        if (courseDetailsResult == null || courseDetailsResult.course == null) {
                            ToastUtils.show(mContext, "课程信息不存在!");
                            return;
                        }
                        mCourse = courseDetailsResult.course;
                        int userId = getAppSettingProvider().getCurrentUser().id;
                        isCourseMember(userId);
                    }
                });
        tvGotoDetail.setOnClickListener(getGotoDetailClickListener());
    }

    private void isCourseMember(int userId) {
        getRoleInCourse(mCourseId, userId, new NormalCallback<String>() {
            @Override
            public void success(String role) {
                if ("none".equals(role)) {
                    CommonUtil.longToast(mContext, "您不是该课程的学生");
                    finish();
                } else {
                    showDiscussFragment();
                }
            }
        });
    }

    private void showDiscussFragment() {
        Fragment fragment;
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragment = CoreEngine.create(mContext).runPluginWithFragment(FRAGMENT_NAME, this, mStudyPluginFragmentCallback);
        mMessageListFragment = (MessageListFragment) fragment;
        initChatRoomController((MessageListFragment) fragment);
        fragmentTransaction.add(R.id.fragment_container, fragment, FRAGMENT_NAME);
        fragmentTransaction.commit();
    }

    private PluginFragmentCallback mStudyPluginFragmentCallback = new PluginFragmentCallback() {
        @Override
        public void setArguments(Bundle bundle) {
            bundle.putString(MessageListFragment.CONV_NO, getIntent().getStringExtra(CONV_NO));
            bundle.putInt(MessageListFragment.TARGET_ID, mCourseId);
            bundle.putInt(Const.COURSE_ID, mCourseId);
            bundle.putString(MessageListFragment.TARGET_TYPE, Destination.COURSE);
        }
    };

    private View.OnClickListener getGotoDetailClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CourseProjectActivity.launch(NewsCourseActivity.this, mCourseId);
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.news_course_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (item.getItemId() == R.id.news_course_profile) {
            CoreEngine.create(mContext).runNormalPlugin("CourseDetailActivity", mContext, new PluginRunCallback() {
                @Override
                public void setIntentDate(Intent startIntent) {
                    MobclickAgent.onEvent(mContext, "dynamic_topRightCourseDetailsButton");
                    MobclickAgent.onEvent(mContext, "chatWindow_topRightCourseDetailsButton");
                    startIntent.putExtra(Const.FROM_ID, mCourseId);
                    startIntent.putExtra(Const.ACTIONBAR_TITLE, mCourse == null
                            ? getResources().getString(R.string.news_course_profile) : mCourse.title);

                    String convNo = getIntent().getStringExtra(CONV_NO);
                    if (TextUtils.isEmpty(convNo)) {
                        ConvEntity convEntity = new IMConvManager(mContext).getConvByTypeAndId(mTargetType, mCourseId);
                        convNo = convEntity == null ? null : convEntity.getConvNo();
                    }
                    startIntent.putExtra(ChatItemBaseDetail.CONV_NO, convNo);
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        return new MessageType[]{
                new MessageType(Const.ADD_COURSE_DISCUSS_MSG, source),
                new MessageType(Const.TOKEN_LOSE),
                new MessageType(CLEAR)
        };
    }

    protected void handleTokenLostMsg() {
        Bundle bundle = new Bundle();
        bundle.putString(Const.BIND_USER_ID, "");

        new IMServiceProvider(getBaseContext()).unBindServer();
        getAppSettingProvider().removeToken();
        MessageEngine.getInstance().sendMsg(Const.LOGOUT_SUCCESS, null);
        MessageEngine.getInstance().sendMsgToTaget(Const.SWITCH_TAB, null, DefaultPageActivity.class);
    }

    protected void processMessage(WidgetMessage message) {
        MessageType messageType = message.type;
        if (Const.TOKEN_LOSE.equals(messageType.type)) {
            PopupDialog dialog = PopupDialog.createNormal(NewsCourseActivity.this, "提示", getString(R.string.token_lose_notice));
            dialog.setOkListener(new PopupDialog.PopupClickListener() {
                @Override
                public void onClick(int button) {
                    handleTokenLostMsg();
                    finish();
                }
            });
            dialog.show();
        }
    }

    @Override
    public void invoke(WidgetMessage message) {
        processMessage(message);
        if (message.type.code == CLEAR) {
            mIMessageListPresenter.refresh();
        }
    }

    private String getFragmentType(int showType) {
        if (showType == LEARN_TYPE) {
            return mEntranceType[1];
        }

        return mEntranceType[0];
    }

    protected AppSettingProvider getAppSettingProvider() {
        return FactoryManager.getInstance().create(AppSettingProvider.class);
    }

    private Runnable mNewFragment2UpdateItemBadgeRunnable = new Runnable() {
        @Override
        public void run() {
            Bundle bundle = new Bundle();
            bundle.putInt(Const.FROM_ID, mCourseId);
            bundle.putString(Const.NEWS_TYPE, PushUtil.ChatUserType.COURSE);
            MessageEngine.getInstance().sendMsgToTaget(NewsFragment.UPDATE_UNREAD_MSG, bundle, NewsFragment.class);
        }
    };

    private void getRoleInCourse(int courseId, int userId, final NormalCallback<String> normalCallback) {
        new CourseProvider(mContext).getMembership(courseId, userId)
                .success(new NormalCallback<LinkedTreeMap>() {
                    @Override
                    public void success(LinkedTreeMap jsonObject) {
                        if ("teacher".equals(jsonObject.get("membership"))) {
                            normalCallback.success("teacher");
                        } else if ("student".equals(jsonObject.get("membership"))) {
                            normalCallback.success("student");
                        } else {
                            normalCallback.success("none");
                        }
                    }
                });
    }

    /*
        MessageControllerListener
     */
    private void initChatRoomController(MessageListFragment messageListFragment) {
        messageListFragment.setInputTextMode(IMessageInputView.INPUT_MULTIPLE_TEXT);
        mIMessageListPresenter = new ChatMessageListPresenterImpl(
                messageListFragment.getArguments(),
                IMClient.getClient().getConvManager(),
                IMClient.getClient().getRoleManager(),
                IMClient.getClient().getResourceHelper(),
                new DefautlMessageDataProvider(),
                messageListFragment);
        mIMessageListPresenter.addMessageControllerListener(getMessageControllerListener());
    }

    protected Promise createChatConvNo() {
        final Promise promise = new Promise();
        User currentUser = getAppSettingProvider().getCurrentUser();
        if (currentUser == null || currentUser.id == 0) {
            ToastUtils.show(getBaseContext(), "用户未登录");
            promise.resolve(null);
            return promise;
        }

        new IMProvider(mContext).joinIMConvNo(mCourseId, "course")
                .success(new NormalCallback<LinkedTreeMap>() {
                    @Override
                    public void success(LinkedTreeMap map) {
                        if (map == null) {
                            ToastUtils.show(getBaseContext(), "加入课程聊天失败!");
                            mIMessageListPresenter.unEnableChatView();
                            promise.resolve(null);
                            return;
                        }
                        if (map.containsKey("error")) {
                            Error error = getUtilFactory().getJsonParser().fromJson(map.get("error").toString(), Error.class);
                            if (error != null) {
                                ToastUtils.show(getBaseContext(), error.message);
                                mIMessageListPresenter.unEnableChatView();
                                promise.resolve(null);
                            }
                            return;
                        }
                        String convNo = map.get("convNo").toString();
                        promise.resolve(convNo);
                    }
                }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError obj) {
                ToastUtils.show(getBaseContext(), "加入课程聊天失败!");
                mIMessageListPresenter.unEnableChatView();
                promise.resolve(null);
            }
        });

        return promise;
    }

    @Override
    protected void createTargetRole(String type, int rid, MessageListPresenterImpl.RoleUpdateCallback callback) {
        if (Destination.USER.equals(type)) {
            createTargetRoleFromUser(rid, callback);
            return;
        }
        Role role = new Role();
        role.setRid(mCourse.id);
        role.setAvatar(mCourse.middlePicture);
        role.setType(Destination.COURSE);
        role.setNickname(mCourse.title);
        callback.onCreateRole(role);
    }

    protected void createTargetRoleFromUser(int rid, final MessageListPresenterImpl.RoleUpdateCallback callback) {
        new UserProvider(mContext).getUserInfo(rid)
                .success(new NormalCallback<User>() {
                    @Override
                    public void success(User user) {
                        Role role = new Role();
                        if (user == null) {
                            callback.onCreateRole(role);
                            return;
                        }
                        role.setRid(user.id);
                        role.setAvatar(user.getMediumAvatar());
                        role.setType(Destination.USER);
                        role.setNickname(user.nickname);
                        callback.onCreateRole(role);
                    }
                });
    }

    @Override
    public int getMode() {
        return REGIST_CLASS;
    }

    @Override
    public void openQuestionActivity(final String fromType) {
        CoreEngine.create(getApplicationContext()).runNormalPluginForResult("ThreadCreateActivity", this, MessageListFragment.SEND_THREAD, new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(ThreadCreateActivity.TARGET_ID, mCourseId);
                startIntent.putExtra(ThreadCreateActivity.TARGET_TYPE, fromType);
                startIntent.putExtra(ThreadCreateActivity.TYPE, "question");
                startIntent.putExtra(ThreadCreateActivity.THREAD_TYPE, fromType);
            }
        });
    }

    @Override
    public void openDiscussActivity(final String fromType) {
        CoreEngine.create(getApplicationContext()).runNormalPluginForResult("ThreadCreateActivity", this, MessageListFragment.SEND_THREAD, new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(ThreadCreateActivity.TARGET_ID, mCourseId);
                startIntent.putExtra(ThreadCreateActivity.TARGET_TYPE, fromType);
                startIntent.putExtra(ThreadCreateActivity.TYPE, "discussion");
                startIntent.putExtra(ThreadCreateActivity.THREAD_TYPE, fromType);
            }
        });
    }
}
