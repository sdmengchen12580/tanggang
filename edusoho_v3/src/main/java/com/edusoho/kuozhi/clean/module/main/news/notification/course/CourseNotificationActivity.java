package com.edusoho.kuozhi.clean.module.main.news.notification.course;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.module.base.BaseActivity;
import com.edusoho.kuozhi.clean.module.course.CourseProjectActivity;
import com.edusoho.kuozhi.clean.utils.GsonUtils;
import com.edusoho.kuozhi.clean.utils.ItemClickSupport;
import com.edusoho.kuozhi.clean.utils.StringUtils;
import com.edusoho.kuozhi.clean.utils.biz.NotificationHelper;
import com.edusoho.kuozhi.imserver.IMClient;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.provider.AppSettingProvider;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.push.Notify;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.ui.FragmentPageActivity;
import com.edusoho.kuozhi.v3.ui.ThreadDiscussChatActivity;
import com.edusoho.kuozhi.v3.ui.fragment.test.TestpaperResultFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class CourseNotificationActivity extends BaseActivity<CourseNotificationContract.Presenter> implements CourseNotificationContract.View {
    private static final int LIMIT = 10;

    private TextView                  mBack;
    private TextView                  mTitle;
    private TextView                  mEmptyInfo;
    private RecyclerView              mList;
    private View                      mEmpty;
    private PtrClassicFrameLayout     mPtrFrame;
    private CourseNotificationAdapter mAdapter;
    private int                       mOffset;
    private LinearLayoutManager       mLinearLayoutManager;
    private School                    mCurrentSchool;

    private boolean mBottom = true;

    public static void launch(Context context) {
        context.startActivity(new Intent(context, CourseNotificationActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_layout);
        init();
        IMClient.getClient().getConvManager().clearReadCount(Destination.NOTIFY);
        AppSettingProvider appSettingProvider = FactoryManager.getInstance().create(AppSettingProvider.class);
        mCurrentSchool = appSettingProvider.getCurrentSchool();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IMClient.getClient().getConvManager().clearReadCount(Destination.NOTIFY);
    }

    private void init() {
        mBack = (TextView) findViewById(R.id.iv_back);
        mTitle = (TextView) findViewById(R.id.tv_toolbar_title);
        mPtrFrame = (PtrClassicFrameLayout) findViewById(R.id.rotate_header_list_view_frame);
        mList = (RecyclerView) findViewById(R.id.listview);
        mEmptyInfo = (TextView) findViewById(R.id.tv_empty_text);
        mEmpty = findViewById(R.id.view_empty);
        mTitle.setText(R.string.course_notification);
        mEmptyInfo.setText(R.string.no_course_notification);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        mList.setLayoutManager(mLinearLayoutManager);
        mAdapter = new CourseNotificationAdapter(this);
        mList.setAdapter(mAdapter);
        ItemClickSupport.addTo(mList).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                List<Notify> list = mAdapter.getList();
                Notify notify = list.get(position);
                switch (notify.getType()) {
                    case NotificationHelper.COURSE_LIVE_START:
                    case NotificationHelper.COURSE_LIVE_START1:
                        String url = String.format(Const.MOBILE_APP_URL, mCurrentSchool.url + "/", String.format(Const.HTML5_LESSON, notify.getCourseId(), notify.getLessonId()));
                        if (!StringUtils.isEmpty(url)) {
                            Bundle bundle = new Bundle();
                            bundle.putString(Const.WEB_URL, url);
                            CoreEngine.create(CourseNotificationActivity.this).runNormalPluginWithBundle("WebViewActivity", CourseNotificationActivity.this, bundle);
                        } else {
                            showToast(getString(R.string.live_course_error));
                        }
                        break;
                    case NotificationHelper.CLASSROOM_DEADLINE:
                    case NotificationHelper.CLASSROOM_JOIN:
                    case NotificationHelper.CLASSROOM_QUIT:
                        final String classroomId = StringUtils.isCheckNull(notify.getObjectContent().get("classroomId"));
                        if (!StringUtils.isEmpty(classroomId)) {
                            CoreEngine.create(CourseNotificationActivity.this).runNormalPlugin("ClassroomActivity", CourseNotificationActivity.this, new PluginRunCallback() {
                                @Override
                                public void setIntentDate(Intent startIntent) {
                                    startIntent.putExtra(Const.CLASSROOM_ID, Integer.parseInt(classroomId));
                                }
                            });
                        }
                        break;
                    case NotificationHelper.CLASSROOM_ANNOUNCEMENT_CREATE:

                        final LinkedTreeMap<String, String> classroomAnnouncementBody = GsonUtils.parseJson(notify.getContent(), new TypeToken<LinkedTreeMap<String, String>>() {
                        });
                        CoreEngine.create(CourseNotificationActivity.this).runNormalPlugin("WebViewActivity", CourseNotificationActivity.this, new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                String url = String.format(Const.MOBILE_APP_URL, EdusohoApp.app.schoolHost,
                                        String.format(Const.CLASSROOM_ANNOUNCEMENT, AppUtil.parseInt(classroomAnnouncementBody.get("classroomId"))));
                                startIntent.putExtra(Const.WEB_URL, url);
                            }
                        });
                        break;
                    case NotificationHelper.COURSE_DEADLINE:
                    case NotificationHelper.COURSE_JOIN:
                    case NotificationHelper.COURSE_QUIT:
                        String courseId = StringUtils.isCheckNull(notify.getObjectContent().get("courseId"));
                        if (!StringUtils.isEmpty(courseId)) {
                            CourseProjectActivity.launch(CourseNotificationActivity.this, Integer.parseInt(courseId));
                        }
                        break;
                    case NotificationHelper.COURSE_ANNOUNCEMENT_CREATE:
                        final LinkedTreeMap<String, String> courseAnnouncementBody = GsonUtils.parseJson(notify.getContent(), new TypeToken<LinkedTreeMap<String, String>>() {
                        });
                        CoreEngine.create(CourseNotificationActivity.this).runNormalPlugin("WebViewActivity", CourseNotificationActivity.this, new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                String url = String.format(Const.MOBILE_APP_URL, EdusohoApp.app.schoolHost,
                                        String.format(Const.COURSE_ANNOUNCEMENT, AppUtil.parseInt(courseAnnouncementBody.get("courseId"))));
                                startIntent.putExtra(Const.WEB_URL, url);
                            }
                        });
                        break;
                    case NotificationHelper.QUESTION_ANSWERED:
                    case NotificationHelper.QUESTION_CREATED:
                    case NotificationHelper.COURSE_THREAD_UPDATE:
                    case NotificationHelper.COURSE_THREAD_STICK:
                    case NotificationHelper.COURSE_THREAD_UNSTICK:
                    case NotificationHelper.COURSE_THREAD_ELITE:
                    case NotificationHelper.COURSE_THREAD_UNELITE:
                    case NotificationHelper.COURSE_THREAD_POST_UPDATE:
                    case NotificationHelper.COURSE_THREAD_POST_AT:
                        final LinkedTreeMap<String, String> discussBody = GsonUtils.parseJson(notify.getContent(), new TypeToken<LinkedTreeMap<String, String>>() {
                        });
                        CoreEngine.create(CourseNotificationActivity.this).runNormalPlugin("DiscussDetailActivity", CourseNotificationActivity.this, new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                startIntent.putExtra(ThreadDiscussChatActivity.THREAD_TARGET_ID, AppUtil.parseInt(discussBody.get("courseId")));
                                startIntent.putExtra(ThreadDiscussChatActivity.THREAD_TARGET_TYPE, "course");
                                startIntent.putExtra(ThreadDiscussChatActivity.FROM_ID, AppUtil.parseInt(discussBody.get("threadId")));
                                startIntent.putExtra(ThreadDiscussChatActivity.TARGET_TYPE, discussBody.get("threadType"));
                            }
                        });
                        break;
                    case NotificationHelper.TESTPAPER_REVIEWED:
                        final LinkedTreeMap<String, String> testpaperbody = GsonUtils.parseJson(notify.getContent(), new TypeToken<LinkedTreeMap<String, String>>() {
                        });
                        CoreEngine.create(CourseNotificationActivity.this).runNormalPlugin("FragmentPageActivity", CourseNotificationActivity.this, new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                startIntent.putExtra(FragmentPageActivity.FRAGMENT, "TestpaperResultFragment");
                                startIntent.putExtra(Const.ACTIONBAR_TITLE, testpaperbody.get("title") + "考试结果");
                                startIntent.putExtra(TestpaperResultFragment.RESULT_ID, Integer.parseInt(testpaperbody.get("testpaperResultId")));
                                startIntent.putExtra(Const.MEDIA_ID, Integer.parseInt(testpaperbody.get("testId")));
                                startIntent.putExtra(Const.STATUS, "finished");
                            }
                        });
                        break;
                }
            }
        });
        mPresenter = new CourseNotificationPresenter(this);
        mPresenter.subscribe();
        mPtrFrame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mPresenter.showNotifications(mOffset, LIMIT);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });
    }

    @Override
    public void addNotificationList(List<Notify> list) {
        mPtrFrame.refreshComplete();
        if (list != null) {
            if (list.size() == 0) {
                showToast(getString(R.string.no_more_data));
                return;
            }
            mOffset = mOffset + list.size();
            if (mAdapter != null) {
                mAdapter.addNotificationItems(list);
            }
            if (mBottom) {
                mLinearLayoutManager.scrollToPosition(0);
                mBottom = false;
            } else {
                mLinearLayoutManager.scrollToPosition(mOffset - LIMIT - 1);
            }
        }
        if (mAdapter.getItemCount() == 0) {
            mEmpty.setVisibility(View.VISIBLE);
            mList.setVisibility(View.GONE);
        } else {
            mEmpty.setVisibility(View.GONE);
            mList.setVisibility(View.VISIBLE);
        }
    }
}
