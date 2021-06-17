package com.edusoho.kuozhi.clean.module.course;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.edusoho.eslive.athena.entity.LiveTicket;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseMember;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.bean.TaskEvent;
import com.edusoho.kuozhi.clean.bean.TaskResultEnum;
import com.edusoho.kuozhi.clean.bean.innerbean.Teacher;
import com.edusoho.kuozhi.clean.module.base.BaseActivity;
import com.edusoho.kuozhi.clean.module.course.dialog.TaskFinishDialog;
import com.edusoho.kuozhi.clean.module.course.task.catalog.CourseTasksFragment;
import com.edusoho.kuozhi.clean.module.course.task.catalog.TaskTypeEnum;
import com.edusoho.kuozhi.clean.module.course.tasks.ppt.PPTLessonFragment;
import com.edusoho.kuozhi.clean.module.course.tasks.webvideo.WebVideoLessonFragment;
import com.edusoho.kuozhi.clean.module.main.mine.examine.fra.InnerSpFragment;
import com.edusoho.kuozhi.clean.module.main.study.exam.videofra.VideoPlayFragment;
import com.edusoho.kuozhi.clean.module.main.study.survey.EvaluationSurveyActivity;
import com.edusoho.kuozhi.clean.module.order.confirm.ConfirmOrderActivity;
import com.edusoho.kuozhi.clean.utils.AppUtils;
import com.edusoho.kuozhi.clean.utils.ToastUtils;
import com.edusoho.kuozhi.clean.utils.biz.LiveTaskLauncher;
import com.edusoho.kuozhi.clean.utils.biz.ShareHelper;
import com.edusoho.kuozhi.clean.utils.biz.TaskFinishActionHelper;
import com.edusoho.kuozhi.clean.utils.biz.TaskFinishHelper;
import com.edusoho.kuozhi.clean.widget.ESAlertDialog;
import com.edusoho.kuozhi.clean.widget.ESContentLoadingLayout;
import com.edusoho.kuozhi.clean.widget.ESIconTextButton;
import com.edusoho.kuozhi.clean.widget.ESIconView;
import com.edusoho.kuozhi.clean.widget.ESPlayerModeDialog;
import com.edusoho.kuozhi.clean.widget.FragmentPageActivity;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.entity.lesson.LessonItem;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.provider.AppSettingProvider;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.bal.m3u8.M3U8DbModel;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.ui.ImChatActivity;
import com.edusoho.kuozhi.v3.ui.LessonActivity;
import com.edusoho.kuozhi.v3.ui.LessonDownloadingActivity;
import com.edusoho.kuozhi.v3.ui.LoginActivity;
import com.edusoho.kuozhi.v3.ui.fragment.lesson.LessonAudioPlayerFragment;
import com.edusoho.kuozhi.v3.ui.fragment.video.LessonVideoPlayerFragment;
import com.edusoho.kuozhi.v3.util.ActivityUtil;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.CourseCacheHelper;
import com.edusoho.kuozhi.v3.util.M3U8Util;
import com.edusoho.kuozhi.v3.util.sql.SqliteUtil;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.stat.StatService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.edusoho.kuozhi.clean.module.course.task.catalog.TaskTypeEnum.AUDIO;
import static com.edusoho.kuozhi.clean.module.course.task.catalog.TaskTypeEnum.VIDEO;


/**
 * Created by JesseHuang on 2017/3/22.
 */

public class CourseProjectActivity extends BaseActivity<CourseProjectContract.Presenter> implements
        CourseProjectContract.View {

    public static final String COURSE_PROJECT_ID = "CourseProjectId";
    private static final String FRAGMENT_VIDEO_TAG = "video";
    private static final String FRAGMENT_AUDIO_TAG = "audio";
    private static final String HOMEWORK_SUMMARY_ACTIVITY = "com.edusoho.kuozhi.homework.HomeworkSummaryActivity";
    private static final String EXERCISE_SUMMARY_ACTIVITY = "com.edusoho.kuozhi.homework.ExerciseSummaryActivity";

    private int mCourseProjectId;
    private String mCourseCoverImageUrl;
    private CourseProjectViewPagerAdapter mAdapter;
    private ESContentLoadingLayout mLoading;
    private Toolbar mToolbar;
    private ImageView mCourseCover;
    private View mFragmentsLayout;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private View mBottomView;
    private ESIconTextButton mConsult;
    private TextView mLearnTextView;
    private ESIconView mBack;
    private TextView mAudioMode;
    private ESIconView mShare;
    private ESIconView mCache;
    private View mPlayLayout;
    private View mLine;
    private TextView mLatestTaskTitle;
    private TextView mImmediateLearn;
    private TextView mFinishTask;
    private FrameLayout mTaskPlayContainer;
    private int mCurrentTaskId;

    private CourseProjectPresenter.ShowActionHelper mShowDialogHelper;

    public static void launch(Context context, int courseProjectId) {
        Intent intent = new Intent(context, CourseProjectActivity.class);
        intent.putExtra(COURSE_PROJECT_ID, courseProjectId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_project);
        if (getIntent() != null) {
            mCourseProjectId = getIntent().getIntExtra(COURSE_PROJECT_ID, 0);
        }
        init();
        StatService.trackCustomEvent(this, "course_enter");
    }

    @Override
    protected void onDestroy() {
        clearTaskFragment();
        super.onDestroy();
    }

    //fixme mImmediateLearn点击开始/继续学习->播放视频流程
    private void init() {
        mTaskPlayContainer = findViewById(R.id.task_container);
        mLoading = findViewById(R.id.loading);
        mLoading.setReloadClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.subscribe();
            }
        });
        mToolbar = findViewById(R.id.toolbar);
        mCourseCover = findViewById(R.id.iv_course_cover);
        mFragmentsLayout = findViewById(R.id.layout_fragments);
        mTabLayout = findViewById(R.id.tl_task);
        mViewPager = findViewById(R.id.vp_content);
        mBottomView = findViewById(R.id.tl_bottom);
        mLine = findViewById(R.id.line);
        mConsult = findViewById(R.id.tb_consult);
        mConsult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mShowDialogHelper != null &&
                        mShowDialogHelper.getErrorType() == CourseProjectPresenter.ShowActionHelper.TYPE_NOT_LOGIN) {
                    mShowDialogHelper.doAction();
                } else {
                    mPresenter.consult();
                }
            }
        });
        mLearnTextView = findViewById(R.id.tv_learn);

        mLearnTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mShowDialogHelper != null && !mShowDialogHelper.isLearnClick()) {
                    mShowDialogHelper.doAction();
                } else {
                    mPresenter.joinCourseProject();
                }
            }
        });

        mBack = findViewById(R.id.iv_back);
        mShare = findViewById(R.id.icon_share);
        mCache = findViewById(R.id.icon_cache);
        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });
        mCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mShowDialogHelper != null) {
                    mShowDialogHelper.doAction();
                } else {
                    if (AppUtils.getRomAvailableSize(getApplicationContext()).contains("M")) {
                        if (Float.parseFloat(AppUtils.getRomAvailableSize(getApplicationContext())
                                .replaceAll("[a-zA-Z]", "").trim()) < 100) {
                            showToast(R.string.cache_hint);
                            return;
                        }
                    }
                    LessonDownloadingActivity.launch(CourseProjectActivity.this, mCourseProjectId, mPresenter.getCourseProject());
                    stopPlay();
                }
            }
        });
        mAudioMode = findViewById(R.id.icon_audio);
        mAudioMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying() && getSupportFragmentManager().findFragmentByTag(FRAGMENT_VIDEO_TAG) != null &&
                        getSupportFragmentManager().findFragmentByTag(FRAGMENT_VIDEO_TAG) instanceof LessonVideoPlayerFragment) {
                    LessonVideoPlayerFragment fragment = (LessonVideoPlayerFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_VIDEO_TAG);
                    fragment.play(mAudioMode.getCurrentTextColor() == getResources().getColor(R.color.primary_color) ?
                            LessonVideoPlayerFragment.PlayerMode.VIDEO : LessonVideoPlayerFragment.PlayerMode.AUDIO);
                } else if (getSupportFragmentManager().findFragmentByTag(FRAGMENT_AUDIO_TAG) != null &&
                        getSupportFragmentManager().findFragmentByTag(FRAGMENT_AUDIO_TAG) instanceof LessonAudioPlayerFragment) {
                    LessonAudioPlayerFragment fragment = (LessonAudioPlayerFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_AUDIO_TAG);
                    fragment.setAudioOn(mAudioMode.getCurrentTextColor() != getResources().getColor(R.color.primary_color));
                }
                if (mAudioMode.getCurrentTextColor() == getResources().getColor(R.color.primary_color)) {
                    mAudioMode.setTextColor(getResources().getColor(R.color.disabled2_hint_color));
                } else {
                    mAudioMode.setTextColor(getResources().getColor(R.color.primary_color));
                }
                if (isAudioMode() && getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    fullScreen();
                }
            }
        });

        mPlayLayout = findViewById(R.id.rl_play_layout);
        mLatestTaskTitle = findViewById(R.id.tv_latest_task_title);
        mImmediateLearn = findViewById(R.id.tv_immediate_learn);
        mImmediateLearn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag(R.id.tv_immediate_learn) != null) {
                    CourseTask trialTask = (CourseTask) v.getTag(R.id.tv_immediate_learn);
                    learnTrialTask(trialTask, mPresenter.getCourseProject());
                    EventBus.getDefault().post(new MessageEvent<>(trialTask, MessageEvent.COURSE_TASK_ITEM_UPDATE));
                    return;
                }
                if (mShowDialogHelper != null) {
                    mShowDialogHelper.doAction();
                } else {
                    CourseTask task = (CourseTask) v.getTag();
                    if (task != null && task.id != 0) {
                        mPresenter.learnTask(task.id);
                        EventBus.getDefault().post(new MessageEvent<>(task, MessageEvent.COURSE_TASK_ITEM_UPDATE));
                    }
                }
            }
        });

        mFinishTask = findViewById(R.id.tv_finish_task);
        mFinishTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CourseTask task = (CourseTask) v.getTag();
                if (task != null && !task.isFinish()) {
                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.task_container);
                    if (fragment instanceof TaskFinishListener) {
                        ((TaskFinishListener) fragment).doFinish();
                    }
                }
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    fullScreen();
                } else {
                    stopPlay();
                    finish();
                }
            }
        });

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        ActivityUtil.setStatusBarFitsByColor(this, R.color.transparent);
        mPresenter = new CourseProjectPresenter(mCourseProjectId, this);
        mPresenter.subscribe();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                fullScreen();
            } else {
                LessonVideoPlayerFragment fragment = (LessonVideoPlayerFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_VIDEO_TAG);
                if (fragment != null) {
                    fragment.stop();
                }
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setUpIndicatorWidth(TabLayout tabLayout) {
        try {
            int screenWidth = AppUtils.getDisplayScreenWidth(this);
            int tabMargin;
            if (tabLayout.getChildAt(0) instanceof ViewGroup) {
                ViewGroup slidingTabStrip = (ViewGroup) tabLayout.getChildAt(0);
                for (int i = 0; i < slidingTabStrip.getChildCount(); i++) {
                    ViewGroup tab = (ViewGroup) slidingTabStrip.getChildAt(i);
                    for (int j = 0; j < tab.getChildCount(); j++) {
                        if (tab.getChildAt(j) instanceof TextView) {
                            TextView textView = (TextView) tab.getChildAt(j);
                            textView.measure(0, 0);
                            tabMargin = (screenWidth - textView.getMeasuredWidth() * 3) / 4;
                            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams)
                                    tab.getLayoutParams();
                            if (i == 0) {
                                p.setMargins(tabMargin, 0, tabMargin, 0);
                            } else if (i == 2) {
                                p.setMargins(tabMargin, 0, tabMargin, 0);
                            }
                            tab.requestLayout();
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Log.d("flag--", "setUpIndicatorWidth: " + ex.getMessage());
        }
    }

    @Override
    public void initTrailTask(CourseTask trialTask) {
        setPlayLayoutVisible(true);
        mLatestTaskTitle.setText(trialTask.title);
        mImmediateLearn.setText(R.string.start_learn_trial_task);
        mImmediateLearn.setBackgroundResource(R.drawable.bg_trial_learned);
        mImmediateLearn.setTag(R.id.tv_immediate_learn, trialTask);
    }

    public void initNextTask(CourseTask nextTask, boolean isFirstTask) {
        setPlayLayoutVisible(true);
        if (nextTask.id == 0) {
            mLatestTaskTitle.setVisibility(View.GONE);
            mImmediateLearn.setText(R.string.already_finish);
            mImmediateLearn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mImmediateLearn.setTag(null);
        } else {
            mImmediateLearn.setText(isFirstTask && nextTask.result == null ? R.string.start_learn_first_task : R.string.start_learn_next_task);
            mLatestTaskTitle.setText(nextTask.title);
            mImmediateLearn.setBackgroundResource(R.drawable.bg_latest_learned);
            mImmediateLearn.setTag(nextTask);
            mFinishTask.setTag(nextTask);
        }
    }

    @Override
    public void setPlayLayoutVisible(boolean visible) {
        mPlayLayout.setVisibility(visible && !isPlaying() ? View.VISIBLE : View.GONE);
    }

    private boolean isPlaying() {
        return getSupportFragmentManager().findFragmentByTag(FRAGMENT_VIDEO_TAG) != null ||
                getSupportFragmentManager().findFragmentByTag(FRAGMENT_AUDIO_TAG) != null;
    }

    @Override
    public void showCover(String imageUrl) {
        mCourseCoverImageUrl = imageUrl;
        ImageLoader.getInstance().displayImage(imageUrl, mCourseCover, EdusohoApp.app.mOptions);
    }

    @Override
    public void showFragments(List<CourseProjectEnum> courseProjectModules, CourseProject courseProject, boolean isMember) {
        if (mViewPager.getAdapter() == null) {
            //第一次初始化
            mAdapter = new CourseProjectViewPagerAdapter(getSupportFragmentManager(), courseProjectModules, courseProject);
            mViewPager.setAdapter(mAdapter);
            mTabLayout.setupWithViewPager(mViewPager);
            setUpIndicatorWidth(mTabLayout);
        } else {
            if (isMember) {
                initJoinCourseLayout(CourseProject.LearnMode.getMode(courseProject.learnMode));
            }
        }
    }

    @Override
    public void showBottomLayout(boolean visible) {
        mBottomView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void launchImChatWithTeacher(final Teacher teacher) {
        if (teacher == null || TextUtils.isEmpty(teacher.nickname)) {
            ToastUtils.show(getBaseContext(), R.string.course_project_no_teacher);
            return;
        }
        CoreEngine.create(getBaseContext()).runNormalPlugin("ImChatActivity", getApplicationContext(), new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(ImChatActivity.FROM_NAME, teacher.nickname);
                startIntent.putExtra(ImChatActivity.FROM_ID, teacher.id);
                startIntent.putExtra(ImChatActivity.HEAD_IMAGE_URL, teacher.avatar.middle);
            }
        });
    }

    @Override
    public void showCacheButton(boolean visible) {
        mCache.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showShareButton(boolean visible) {
        mShare.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mCache.setVisibility(View.GONE);
            mBottomView.setVisibility(View.GONE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            showCacheButton(isJoin() && CourseProject.LearnMode.getMode(mPresenter.getCourseProject().learnMode) == CourseProject.LearnMode.FREEMODE);
            if (mPresenter.isJoin()) {
                mBottomView.setVisibility(View.GONE);
            } else {
                mBottomView.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 点击加入界面显示
     *
     * @param mode 是否解锁计划
     */
    @Override
    public void initJoinCourseLayout(CourseProject.LearnMode mode) {
        mTabLayout.setVisibility(View.GONE);
        mLine.setVisibility(View.GONE);
        mAdapter.initJoinCourseDataSetChanged();
        showCacheButton(mode == CourseProject.LearnMode.FREEMODE);
        showShareButton(false);
        showBottomLayout(false);
    }

    /**
     * 退出课程
     */
    @Override
    public void exitCourseLayout() {
        mTabLayout.setVisibility(View.VISIBLE);
        mLine.setVisibility(View.VISIBLE);
        mAdapter.exitCourseDataSetChanged();
        showCacheButton(false);
        showShareButton(true);
        showBottomLayout(true);
        setUpIndicatorWidth(mTabLayout);
    }

    /**
     * 进入页面显示：已加入
     *
     * @param mode 是否解锁计划
     */
    @Override
    public void initLearnLayout(CourseProject.LearnMode mode) {
        mTabLayout.setVisibility(View.GONE);
        mLine.setVisibility(View.GONE);
        showCacheButton(mode == CourseProject.LearnMode.FREEMODE);
        showShareButton(false);
        showBottomLayout(false);
    }

    @Override
    public void showAudioMode(boolean visible) {
        mAudioMode.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /**
     * 未加入底部Layout处理
     *
     * @param statusEnum 加入路径
     */
    @Override
    public void setJoinButton(JoinButtonStatusEnum statusEnum) {
        switch (statusEnum) {
            case NORMAL:
                mLearnTextView.setText(R.string.learn_course_project);
                mLearnTextView.setBackgroundResource(R.color.primary_color);
                break;
            case VIP_FREE:
                mLearnTextView.setText(R.string.learn_course_project_free_to_learn);
                mLearnTextView.setBackgroundResource(R.color.primary_color);
                break;
            case COURSE_EXPIRED:
                mLearnTextView.setText(R.string.course_closed);
                mLearnTextView.setBackgroundResource(R.color.secondary2_font_color);
                break;
            case VIP_ONLY:
                mLearnTextView.setText(R.string.only_vip_learn);
                mLearnTextView.setBackgroundResource(R.color.primary_color);
                break;
        }
    }

    @Override
    public void launchConfirmOrderActivity(int courseSetId, int courseId) {
        ConfirmOrderActivity.launch(this, courseSetId, courseId);
    }

    public void showExitDialog(int msgId, ESAlertDialog.DialogButtonClickListener positive) {
        ESAlertDialog.newInstance(null, getString(msgId), getString(R.string.course_exit_confirm), getString(R.string.course_exit_cancel))
                .setConfirmListener(positive)
                .setCancelListener(new ESAlertDialog.DialogButtonClickListener() {
                    @Override
                    public void onClick(DialogFragment dialog) {
                        dialog.dismiss();
                    }
                })
                .show(getSupportFragmentManager(), "ESAlertDialog");
    }

    public void showNotSupportTaskDialog(final CourseTask task, int msgId) {
        ESAlertDialog.newInstance(null, getString(msgId), getString(R.string.task_finish_confirm), getString(R.string.task_finish_cancel))
                .setConfirmListener(new ESAlertDialog.DialogButtonClickListener() {
                    @Override
                    public void onClick(DialogFragment dialog) {
                        TaskFinishHelper.Builder builder = new TaskFinishHelper.Builder()
                                .setCourseId(mCourseProjectId)
                                .setCourseTask(task)
                                .setEnableFinish(mPresenter.getCourseProject().enableFinish);
                        new TaskFinishHelper(builder, CourseProjectActivity.this)
                                .setActionListener(new TaskFinishActionHelper() {
                                    @Override
                                    public void onFinish(TaskEvent taskEvent) {
                                        EventBus.getDefault().post(new MessageEvent<>(task, MessageEvent.FINISH_TASK_SUCCESS));
                                        if (mPresenter.getCourseProject().enableFinish == 0) {
                                            TaskFinishDialog.newInstance(taskEvent, task, mPresenter.getCourseProject())
                                                    .show(CourseProjectActivity.this.getSupportFragmentManager(), "mTaskFinishDialog");
                                        }
                                    }
                                })
                                .stickyFinish();
                        dialog.dismiss();
                    }
                })
                .setCancelListener(new ESAlertDialog.DialogButtonClickListener() {
                    @Override
                    public void onClick(DialogFragment dialog) {
                        dialog.dismiss();
                    }
                })
                .show(getSupportFragmentManager(), "ESAlertDialog");
    }

    public boolean isJoin() {
        return mPresenter.isJoin();
    }

    private void learnTrialTask(final CourseTask task, CourseProject courseProject) {
        setPlayLayoutVisible(false);
        mFinishTask.setVisibility(View.GONE);
        clearTaskFragment();
        TaskTypeEnum taskType = TaskTypeEnum.fromString(task.type);
        Bundle bundle = new Bundle();
        switch (taskType) {
            case VIDEO:
                mPresenter.playVideo(task);
                break;
            case AUDIO:
                loadPlayAudioFragment(task);
                break;
            case TEXT:
            case DOC:
                bundle.putInt(Const.LESSON_ID, task.id);
                bundle.putInt(Const.COURSE_ID, mCourseProjectId);
                bundle.putSerializable(LessonActivity.COURSE_TASK, task);
                bundle.putSerializable(LessonActivity.COURSE, courseProject);
                bundle.putBoolean(LessonActivity.MEMBER_STATE, false);
                CoreEngine.create(getApplicationContext()).runNormalPluginWithBundleForResult(
                        "LessonActivity", this, bundle, LessonActivity.REQUEST_LEARN);
                break;
            case PPT:
                ArrayList<String> pptUrls = SqliteUtil.getUtil(this).getPPTUrls(task.id);
                if (pptUrls != null && pptUrls.size() > 0) {
                    bundle.putSerializable(PPTLessonFragment.TASK_TITLE, task.title);
                    bundle.putStringArrayList(PPTLessonFragment.PPT_URLS, pptUrls);
                } else {
                    bundle.putBoolean(PPTLessonFragment.IS_MEMBER, isJoin());
                    bundle.putSerializable(PPTLessonFragment.COURSE_TASK, task);
                    bundle.putSerializable(PPTLessonFragment.COURSE_PROJECT, mPresenter.getCourseProject());
                }
                FragmentPageActivity.launch(this, PPTLessonFragment.class.getName(), bundle);
                break;
            case FLASH:
                showToast(R.string.task_not_support);
                break;
        }
    }

    @Override
    public void learnTask(final CourseTask task, CourseProject courseProject, CourseMember courseMember) {
        mFinishTask.setTag(task);
        setPlayLayoutVisible(false);
        mFinishTask.setVisibility(View.GONE);
        clearTaskFragment();
        TaskTypeEnum taskType = TaskTypeEnum.fromString(task.type);
        Bundle bundle = new Bundle();
        switch (taskType) {
            case LIVE:
            case VIDEO:
                mPresenter.playVideo(task);
                if (task.result != null) {
                    setTaskFinishButtonBackground(TaskResultEnum.FINISH.toString().equals(task.result.status));
                } else {
                    setTaskFinishButtonBackground(false);
                }
                break;
            case AUDIO:
                loadPlayAudioFragment(task);
                if (task.result != null) {
                    setTaskFinishButtonBackground(TaskResultEnum.FINISH.toString().equals(task.result.status));
                } else {
                    setTaskFinishButtonBackground(false);
                }
                break;
            case TEXT:
            case DOC:
                bundle.putInt(Const.LESSON_ID, task.id);
                bundle.putInt(Const.COURSE_ID, mCourseProjectId);
                bundle.putSerializable(LessonActivity.COURSE_TASK, task);
                bundle.putSerializable(LessonActivity.COURSE, courseProject);
                bundle.putBoolean(LessonActivity.MEMBER_STATE, true);
                CoreEngine.create(getApplicationContext()).runNormalPluginWithBundleForResult(
                        "LessonActivity", this, bundle, LessonActivity.REQUEST_LEARN);
                break;
            case PPT:
                ArrayList<String> pptUrls = SqliteUtil.getUtil(this).getPPTUrls(task.id);
                if (pptUrls != null && pptUrls.size() > 0) {
                    bundle.putSerializable(PPTLessonFragment.TASK_TITLE, task.title);
                    bundle.putStringArrayList(PPTLessonFragment.PPT_URLS, pptUrls);
                }
                bundle.putBoolean(PPTLessonFragment.IS_MEMBER, isJoin());
                bundle.putSerializable(PPTLessonFragment.COURSE_TASK, task);
                bundle.putSerializable(PPTLessonFragment.COURSE_PROJECT, mPresenter.getCourseProject());
                FragmentPageActivity.launch(this, PPTLessonFragment.class.getName(), bundle);
                break;
            case TESTPAPER:
                bundle.putInt(Const.LESSON_ID, task.id);
                bundle.putInt(Const.COURSE_ID, mCourseProjectId);
                bundle.putSerializable(LessonActivity.COURSE_TASK, task);
                bundle.putSerializable(LessonActivity.COURSE, courseProject);
                bundle.putBoolean(LessonActivity.MEMBER_STATE, true);
                CoreEngine.create(getApplicationContext()).runNormalPluginWithBundleForResult(
                        "LessonActivity", this, bundle, LessonActivity.REQUEST_LEARN);
                break;
            case HOMEWORK:
                startActivity(new Intent().setClassName(getPackageName(), HOMEWORK_SUMMARY_ACTIVITY)
                        .putExtra(Const.LESSON_ID, task.id)
                        .putExtra("media_id", task.activity.mediaId)
                        .putExtra("courseproject", courseProject)
                        .putExtra("coursetask", task));
                break;
            case EXERCISE:
                startActivity(new Intent().setClassName(getPackageName(), EXERCISE_SUMMARY_ACTIVITY)
                        .putExtra(Const.LESSON_ID, task.id)
                        .putExtra("media_id", task.activity.mediaId)
                        .putExtra("courseproject", courseProject)
                        .putExtra("coursetask", task));
                break;
            case FLASH:
            case DOWNLOAD:
            case DISCUSS:
                if (task.result == null || TaskResultEnum.START.toString().equals(task.result.status)) {
                    showNotSupportTaskDialog(task, R.string.finish_not_support_task);
                } else {
                    showToast(R.string.task_not_support);
                }
                break;
            case QUESTIONNAIRE:
                if (task.result.status.equals("finish")) {
                    showToast("已完成教学评价");
                    return;
                }
                EvaluationSurveyActivity.launch(CourseProjectActivity.this, Integer.toString(task.activity.mediaId), mCourseProjectId, task.id);
                break;
        }
    }

    public void learnTask(CourseTask courseTask) {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof CourseTasksFragment) {
                mPresenter.learnTask(courseTask.id);
                ((CourseTasksFragment) fragment).finishTask(courseTask);
            }
        }
    }

    @Override
    public void playVideo(CourseTask task, LessonItem lessonItem) {
        if (lessonItem.mediaSource.equals("self")) {
            loadVideoFragment(task, lessonItem);
        } else if (lessonItem.mediaSource.equals("youku")) {
            loadVideoFragment(task, lessonItem.mediaUri);
        } else {
            if (task.result == null || TaskResultEnum.START.toString().equals(task.result.status)) {
                showNotSupportTaskDialog(task, R.string.finish_not_support_task);
            } else {
                showToast(R.string.task_not_support);
            }
        }
    }

    @Override
    public void launchLiveTask(LiveTicket liveTicket, CourseTask task) {
        LiveTaskLauncher.build()
                .init(this)
                .setCourseTask(task)
                .setLiveTick(liveTicket)
                .build()
                .launch();
    }

    /**
     * 播放youku视频等
     */
    private void loadVideoFragment(CourseTask task, String mediaUri) {
        Bundle bundle = new Bundle();
        bundle.putString(WebVideoLessonFragment.URL, mediaUri);
        bundle.putBoolean(WebVideoLessonFragment.IS_MEMBER, isJoin());
        bundle.putSerializable(WebVideoLessonFragment.COURSE_TASK, task);
        bundle.putSerializable(WebVideoLessonFragment.COURSE_PROJECT, mPresenter.getCourseProject());
        FragmentPageActivity.launch(this, WebVideoLessonFragment.class.getName(), bundle);
    }

    /**
     * 云视频
     */
    private void loadVideoFragment(final CourseTask task, final LessonItem lessonItem) {
        if (isJoin() && lessonItem.remainTime != null && Integer.parseInt(lessonItem.remainTime) <= 0) {
            ToastUtils.show(this, getResources().getString(R.string.lesson_had_reached_hint));
            return;
        }
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        final LessonVideoPlayerFragment[] videoFragment = new LessonVideoPlayerFragment[1];
        if (!isAudioMode() && getCachedLesson() != null) {
            videoFragment[0] = LessonVideoPlayerFragment.newInstance(task, lessonItem, mPresenter.getCourseProject(), isJoin(), -1, false);
            transaction.replace(R.id.task_container, videoFragment[0], FRAGMENT_VIDEO_TAG);
            transaction.commitAllowingStateLoss();
            return;
        }
        if (!AppUtil.isWiFiConnect(this) && EdusohoApp.app.config.offlineType != 1) {
            if ("1".equals(mPresenter.getCourseProject().isAudioOn) && !isAudioMode()) {
                ESPlayerModeDialog
                        .newInstance()
                        .setVideoClickListener(new ESPlayerModeDialog.DialogButtonClickListener() {
                            @Override
                            public void onClick(DialogFragment dialog) {
                                dialog.dismiss();
                                mAudioMode.setTextColor(getResources().getColor(R.color.disabled2_hint_color));
                                videoFragment[0] = LessonVideoPlayerFragment.newInstance(task, lessonItem, mPresenter.getCourseProject(), isJoin(), 1, isAudioMode());
                                transaction.replace(R.id.task_container, videoFragment[0], FRAGMENT_VIDEO_TAG);
                                transaction.commitAllowingStateLoss();
                            }
                        })
                        .setAudioClickListener(new ESPlayerModeDialog.DialogButtonClickListener() {
                            @Override
                            public void onClick(DialogFragment dialog) {
                                dialog.dismiss();
                                mAudioMode.setTextColor(getResources().getColor(R.color.primary_color));
                                videoFragment[0] = LessonVideoPlayerFragment.newInstance(task, lessonItem, mPresenter.getCourseProject(), isJoin(), 1, isAudioMode());
                                transaction.replace(R.id.task_container, videoFragment[0], FRAGMENT_VIDEO_TAG);
                                transaction.commitAllowingStateLoss();
                            }
                        })
                        .setCancelClickListener(new ESPlayerModeDialog.DialogButtonClickListener() {
                            @Override
                            public void onClick(DialogFragment dialog) {
                                dialog.dismiss();
                            }
                        })
                        .show(getSupportFragmentManager(), "ESPlayerModeDialog");
            } else {
                ESAlertDialog.newInstance(null, getString(R.string.play_with_4g_info), getString(R.string.goon), getString(R.string.cancel))
                        .setConfirmListener(new ESAlertDialog.DialogButtonClickListener() {
                            @Override
                            public void onClick(DialogFragment dialog) {
                                dialog.dismiss();
                                videoFragment[0] = LessonVideoPlayerFragment.newInstance(task, lessonItem, mPresenter.getCourseProject(), isJoin(), 1, isAudioMode());
                                transaction.replace(R.id.task_container, videoFragment[0], FRAGMENT_VIDEO_TAG);
                                transaction.commitAllowingStateLoss();
                            }
                        })
                        .setCancelListener(new ESAlertDialog.DialogButtonClickListener() {
                            @Override
                            public void onClick(DialogFragment dialog) {
                                dialog.dismiss();
                            }
                        })
                        .show(getSupportFragmentManager(), "ESAlertDialog");
            }
        } else if (!AppUtil.isWiFiConnect(this) && EdusohoApp.app.config.offlineType == 1) {
            Log.e("测试: ", "11111");
            videoFragment[0] = LessonVideoPlayerFragment.newInstance(task, lessonItem, mPresenter.getCourseProject(), isJoin(), 1, isAudioMode());
        } else {
            Log.e("测试: ", "22222");
            videoFragment[0] = LessonVideoPlayerFragment.newInstance(task, lessonItem, mPresenter.getCourseProject(), isJoin(), 0, isAudioMode());
        }
        if (videoFragment[0] != null) {
            Log.e("测试: ", "333333");
            transaction.replace(R.id.task_container, videoFragment[0], FRAGMENT_VIDEO_TAG);
//            transaction.replace(R.id.task_container,VideoPlayFragment.newInstance(), FRAGMENT_VIDEO_TAG);
            transaction.commitAllowingStateLoss();
        }
    }

    private void loadPlayAudioFragment(final CourseTask task) {
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        final LessonAudioPlayerFragment[] audioFragment = new LessonAudioPlayerFragment[1];
        if (!AppUtil.isWiFiConnect(this) && EdusohoApp.app.config.offlineType != 1) {
            ESAlertDialog.newInstance(null, getString(R.string.play_with_4g_info), getString(R.string.goon), getString(R.string.cancel))
                    .setConfirmListener(new ESAlertDialog.DialogButtonClickListener() {
                        @Override
                        public void onClick(DialogFragment dialog) {
                            dialog.dismiss();
                            audioFragment[0] = LessonAudioPlayerFragment.newInstance(mCourseCoverImageUrl, task, mPresenter.getCourseProject(), 1, isAudioMode());
                            transaction.replace(R.id.task_container, audioFragment[0], FRAGMENT_AUDIO_TAG);
                            transaction.commitAllowingStateLoss();
                        }
                    })
                    .setCancelListener(new ESAlertDialog.DialogButtonClickListener() {
                        @Override
                        public void onClick(DialogFragment dialog) {
                            dialog.dismiss();
                        }
                    })
                    .show(getSupportFragmentManager(), "ESAlertDialog");
        } else if (!AppUtil.isWiFiConnect(this) && EdusohoApp.app.config.offlineType == 1) {
            audioFragment[0] = LessonAudioPlayerFragment.newInstance(mCourseCoverImageUrl, task, mPresenter.getCourseProject(), 1, isAudioMode());
        } else {
            audioFragment[0] = LessonAudioPlayerFragment.newInstance(mCourseCoverImageUrl, task, mPresenter.getCourseProject(), 0, isAudioMode());
        }
        if (audioFragment[0] != null) {
            transaction.replace(R.id.task_container, audioFragment[0], FRAGMENT_AUDIO_TAG);
            transaction.commitAllowingStateLoss();
        }
    }

    public void stopPlay() {
        LessonVideoPlayerFragment videoFragment = (LessonVideoPlayerFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_VIDEO_TAG);
        if (videoFragment != null) {
            videoFragment.stop();
        }

        LessonAudioPlayerFragment audioFragment = (LessonAudioPlayerFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_AUDIO_TAG);
        if (audioFragment != null) {
            audioFragment.pause();
            audioFragment.destoryService();
        }
    }

    public void clearTaskFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.task_container);
        if (fragment == null) {
            return;
        }
        if (fragment instanceof LessonAudioPlayerFragment) {
            ((LessonAudioPlayerFragment) fragment).pause();
            ((LessonAudioPlayerFragment) fragment).destoryService();
        }

        transaction.remove(fragment).commitAllowingStateLoss();
    }

    private void showLiveTaskFinishDialog(CourseProject courseProject, final CourseTask courseTask) {
        TaskFinishHelper.Builder builder = new TaskFinishHelper.Builder()
                .setCourseId(courseProject.id)
                .setCourseTask(courseTask)
                .setEnableFinish(courseProject.enableFinish);
        new TaskFinishHelper(builder, CourseProjectActivity.this)
                .setActionListener(new TaskFinishActionHelper() {
                    @Override
                    public void onFinish(TaskEvent taskEvent) {
                        EventBus.getDefault().post(new MessageEvent<>(courseTask, MessageEvent.FINISH_TASK_SUCCESS));
                        if (mPresenter.getCourseProject().enableFinish == 0) {
                            TaskFinishDialog.newInstance(taskEvent, courseTask, mPresenter.getCourseProject())
                                    .show(CourseProjectActivity.this.getSupportFragmentManager(), "TaskFinishDialog");
                        }
                    }
                })
                .stickyFinish();
    }

    @Override
    public void setTaskFinishButtonBackground(boolean learned) {
        mFinishTask.setVisibility(View.VISIBLE);
        if ("1".equals(mPresenter.getCourseProject().isAudioOn)) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mFinishTask.getLayoutParams();
            lp.setMargins(0, 0, AppUtils.dp2px(this, 112 + 20), 0);
        } else {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mFinishTask.getLayoutParams();
            lp.setMargins(0, 0, AppUtils.dp2px(this, 56 + 20), 0);
        }
        if (learned) {
            mFinishTask.setCompoundDrawablesWithIntrinsicBounds(R.drawable.task_finish_left_icon, 0, 0, 0);
            mFinishTask.setBackground(getResources().getDrawable(R.drawable.task_finish_button_bg));
        } else {
            mFinishTask.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mFinishTask.setBackground(getResources().getDrawable(R.drawable.task_unfinish_button_bg));
        }
    }

    @Override
    public void clearCoursesCache(int... courseIds) {
        AppSettingProvider appSettingProvider = FactoryManager.getInstance().create(AppSettingProvider.class);
        School school = appSettingProvider.getCurrentSchool();
        User user = appSettingProvider.getCurrentUser();
        new CourseCacheHelper(getApplicationContext(), school.getDomain(), user.id).clearLocalCacheByCourseId(courseIds);
    }

    @Override
    public void launchLoginActivity() {
        LoginActivity.startLogin(this);
    }

    @Override
    public void launchVipListWeb() {
        final String url = String.format(
                Const.MOBILE_APP_URL,
                EdusohoApp.app.schoolHost,
                Const.VIP_LIST
        );
        CoreEngine.create(getApplicationContext()).runNormalPlugin("WebViewActivity"
                , getApplicationContext(), new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(Const.WEB_URL, url);
                    }
                });
    }

    private class CourseProjectViewPagerAdapter extends FragmentStatePagerAdapter {

        private List<CourseProjectEnum> mCourseProjectModules;
        private CourseProject mCourseProject;

        public CourseProjectViewPagerAdapter(FragmentManager fm, List<CourseProjectEnum> courseProjects, CourseProject courseProject) {
            super(fm);
            mCourseProjectModules = courseProjects;
            mCourseProject = courseProject;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = Fragment.instantiate(CourseProjectActivity.this, mCourseProjectModules.get(position).getModuleName());
            Bundle bundle = new Bundle();
            bundle.putSerializable(((CourseProjectFragmentListener) fragment).getBundleKey(), mCourseProject);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return mCourseProjectModules.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mCourseProjectModules.get(position).getModuleTitle();
        }

        public void initJoinCourseDataSetChanged() {
            mCourseProjectModules.remove(2);
            mCourseProjectModules.remove(0);
            notifyDataSetChanged();
        }

        public void exitCourseDataSetChanged() {
            mCourseProjectModules.add(0, CourseProjectEnum.INFO);
            mCourseProjectModules.add(2, CourseProjectEnum.RATE);
            notifyDataSetChanged();
        }
    }

    public enum JoinButtonStatusEnum {
        NORMAL, VIP_FREE, COURSE_EXPIRED, VIP_ONLY
    }

    @Override
    public void setShowError(CourseProjectPresenter.ShowActionHelper helper) {
        mShowDialogHelper = helper;
    }

    private boolean mIsFullScreen;

    private void fullScreen() {
        ViewGroup.LayoutParams params = mTaskPlayContainer.getLayoutParams();
        if (!mIsFullScreen) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mIsFullScreen = true;
            params.height = AppUtil.getWidthPx(this);
            params.width = LinearLayout.LayoutParams.MATCH_PARENT;
            mTaskPlayContainer.setLayoutParams(params);
            mFragmentsLayout.setVisibility(View.GONE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mCache.setVisibility(View.GONE);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mIsFullScreen = false;
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = AppUtil.dp2px(this, 222);
            mTaskPlayContainer.setLayoutParams(params);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mFragmentsLayout.setVisibility(View.VISIBLE);
            mCache.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onReceiveMessage(MessageEvent messageEvent) {
        if (messageEvent.getType() == MessageEvent.LEARN_TASK) {
            CourseTask task = (CourseTask) messageEvent.getMessageBody();
            switch (messageEvent.getType()) {
                case MessageEvent.LEARN_TASK:
                    if ((mPresenter.getCourseMember() == null || mPresenter.getCourseMember().user == null) && (task.isFree == 1)) {
                        learnTrialTask(task, mPresenter.getCourseProject());
                    } else if ((mPresenter.getCourseMember() == null || mPresenter.getCourseMember().user == null)
                            && task.isFree == 0
                            && mPresenter.getCourseProject().tryLookable == 1
                            && TaskTypeEnum.fromString(task.type) == VIDEO
                            && "cloud".equals(task.activity.mediaStorage)) {
                        learnTrialTask(task, mPresenter.getCourseProject());
                    } else if (mShowDialogHelper != null) {
                        mShowDialogHelper.doAction();
                    } else {
                        mPresenter.learnTask(task);
                    }
                    break;
            }
        } else if (messageEvent.getType() == MessageEvent.SHOW_NEXT_TASK) {
            SparseArray<Object> nextTaskInfo = (SparseArray<Object>) messageEvent.getMessageBody();
            initNextTask((CourseTask) nextTaskInfo.get(0), (boolean) nextTaskInfo.get(1));
        } else if (messageEvent.getType() == MessageEvent.FULL_SCREEN) {
            fullScreen();
        } else if (messageEvent.getType() == MessageEvent.SHOW_VIP_BUTTON) {
            setJoinButton(JoinButtonStatusEnum.VIP_FREE);

        }
    }

    @Override
    public void showToast(int resId, String param) {
        Toast.makeText(this, String.format(getString(resId, param)), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() {
        mLoading.showLoading();
    }

    @Override
    public void showError() {
        mLoading.showError();
    }

    @Override
    public void showComplete() {
        mLoading.complete();
    }

    private boolean isAudioMode() {
        return "1".equals(mPresenter.getCourseProject().isAudioOn) &&
                mAudioMode.getCurrentTextColor() == getResources().getColor(R.color.primary_color);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onReceiveStickyNessage(MessageEvent messageEvent) {
        if (messageEvent.getType() == MessageEvent.LOGIN) {
            mPresenter.subscribe();
        } else if (messageEvent.getType() == MessageEvent.FINISH_TASK_SUCCESS) {
            if (messageEvent.getMessageBody() != null && messageEvent.getMessageBody() instanceof CourseTask) {
                TaskTypeEnum type = TaskTypeEnum.fromString(((CourseTask) messageEvent.getMessageBody()).type);
                if (type == AUDIO || type == VIDEO) {
                    setTaskFinishButtonBackground(true);
                }
            }
        } else if (messageEvent.getType() == MessageEvent.LEARN_NEXT_TASK) {
            // 事件通知比onCreate生命周期还要快，
            // onCreate中removeAllStickyEvents来不及删除就已经执行Subscribe了
            // 导致重复执行LEARN_NEXT_TASK，所以手动removeAllStickyEvents
            EventBus.getDefault().removeAllStickyEvents();
            EventBus.getDefault().post(new MessageEvent<>(MessageEvent.FINISH_TASK_SUCCESS));
            EventBus.getDefault().post(new MessageEvent<>(messageEvent.getMessageBody(), MessageEvent.COURSE_TASK_ITEM_UPDATE));
            mPresenter.learnTask(((CourseTask) messageEvent.getMessageBody()).id);
        } else if (messageEvent.getType() == MessageEvent.PAY_SUCCESS) {
            mShowDialogHelper = null;
            mPresenter.syncLoginUserInfo();
        }
    }

    @Override
    public void setCurrentTaskId(int taskId) {
        this.mCurrentTaskId = taskId;
    }

    private LessonItem getCachedLesson() {
        AppSettingProvider appSettingProvider = FactoryManager.getInstance().create(AppSettingProvider.class);
        User user = appSettingProvider.getCurrentUser();
        School school = appSettingProvider.getCurrentSchool();
        if (user == null || school == null) {
            return null;
        }
        M3U8DbModel m3U8DbModel = M3U8Util.queryM3U8Model(
                this, user.id, mCurrentTaskId, school.getDomain(), M3U8Util.FINISH);
        if (m3U8DbModel == null) {
            return null;
        }
        SqliteUtil sqliteUtil = SqliteUtil.getUtil(this);
        return sqliteUtil.queryForObj(
                new TypeToken<LessonItem>() {
                },
                "where type=? and key=?",
                Const.CACHE_LESSON_TYPE,
                "lesson-" + mCurrentTaskId
        );
    }

    public void performAudioClick() {
        if (mAudioMode != null) {
            mAudioMode.performClick();
        }
    }

    private void share() {
        CourseProject courseProject = mPresenter.getCourseProject();
        if (courseProject == null) {
            return;
        }
        ShareHelper
                .builder()
                .init(this)
                .setTitle(courseProject.getTitle())
                .setText(courseProject.summary)
                .setUrl(EdusohoApp.app.host + "/course/" + courseProject.id)
                .setImageUrl(courseProject.courseSet.cover.middle)
                .build()
                .share();
    }

    public interface TaskFinishListener {
        void doFinish();
    }
}
