package com.edusoho.kuozhi.v3.ui.fragment.video;


import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.api.CourseApi;
import com.edusoho.kuozhi.clean.api.LessonApi;
import com.edusoho.kuozhi.clean.bean.CourseItem;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.bean.TaskEvent;
import com.edusoho.kuozhi.clean.bean.seting.CloudVideoSetting;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.module.classroom.BaseStudyDetailActivity;
import com.edusoho.kuozhi.clean.module.course.CourseProjectActivity;
import com.edusoho.kuozhi.clean.module.course.dialog.TaskFinishDialog;
import com.edusoho.kuozhi.clean.module.course.task.catalog.CourseItemEnum;
import com.edusoho.kuozhi.clean.module.course.task.catalog.TaskTypeEnum;
import com.edusoho.kuozhi.clean.receiver.NetworkChangeBroadcastReceiver;
import com.edusoho.kuozhi.clean.utils.AppUtils;
import com.edusoho.kuozhi.clean.utils.StringUtils;
import com.edusoho.kuozhi.clean.utils.TimeUtils;
import com.edusoho.kuozhi.clean.utils.ToastUtils;
import com.edusoho.kuozhi.clean.utils.biz.CloudHelper;
import com.edusoho.kuozhi.clean.utils.biz.SettingHelper;
import com.edusoho.kuozhi.clean.utils.biz.SharedPreferencesHelper;
import com.edusoho.kuozhi.clean.utils.biz.TaskFinishActionHelper;
import com.edusoho.kuozhi.clean.utils.biz.TaskFinishHelper;
import com.edusoho.kuozhi.clean.utils.biz.TaskLearningRecordHelper;
import com.edusoho.kuozhi.clean.widget.ESAlertDialog;
import com.edusoho.kuozhi.clean.widget.ESPlayerModeDialog;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.entity.lesson.LessonItem;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.provider.AppSettingProvider;
import com.edusoho.kuozhi.v3.listener.ResponseCallbackListener;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailModel;
import com.edusoho.kuozhi.v3.model.bal.m3u8.M3U8DbModel;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.ImageUtil;
import com.edusoho.kuozhi.v3.util.M3U8Util;
import com.edusoho.kuozhi.v3.util.MediaUtil;
import com.edusoho.kuozhi.v3.util.server.CacheServerFactory;
import com.edusoho.kuozhi.v3.util.sql.SqliteUtil;
import com.edusoho.videoplayer.media.listener.SimpleVideoControllerListener;
import com.edusoho.videoplayer.ui.VideoPlayerFragment;
import com.edusoho.videoplayer.util.VLCOptions;
import com.edusoho.videoplayer.view.VideoControllerView;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.tencent.stat.StatService;
import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.android.FragmentEvent;

import org.greenrobot.eventbus.EventBus;
import org.videolan.libvlc.util.AndroidUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;
import static com.edusoho.kuozhi.clean.bean.TaskFinishType.END;

/**
 * Created by suju on 16/12/16.
 */

public class LessonVideoPlayerFragment extends VideoPlayerFragment implements View.OnFocusChangeListener, CourseProjectActivity.TaskFinishListener,
        NetworkChangeBroadcastReceiver.MobileConnectListener {

    private static final String COURSE_PROJECT = "course_project";
    private static final String COURSE_TASK = "course_task";
    private static final String LESSON_ITEM = "lesson_item";
    private static final String IS_MEMBER = "is_member";
    private static final String NETWORK_TYPE = "network_type";
    private static final String IS_AUDIO_ON = "is_audio_on";
    private static final int LIMIT_PLAY_TIME = 2 * 60 * 1000;

    private final BehaviorSubject<FragmentEvent> lifecycleSubject = BehaviorSubject.create();

    private NetworkChangeBroadcastReceiver mNetworkChangeBroadcastReceiver;
    private IntentFilter mIntentFilter;
    private int mNetworkType;
    private int mPlayTime;
    private int mTotalTime;
    private boolean mIsContinue;
    private boolean mIsPlay;
    private Timer mTimer;
    private BaseStudyDetailActivity mMenuCallback;
    private CourseTask mCourseTask;
    private CourseProject mCourseProject;
    private TaskFinishHelper mTaskFinishHelper;
    private LessonItem mLessonItem;
    private boolean mIsMember;
    private boolean mShowDialog;
    private long mSeekPosition;
    private TextView tvAccountName;
    private ImageView ivWaterMark;
    private View mMemoryPlay;
    private TextView mMemoryTime;
    private View mMemoryClose;
    private View mMemoryPanel;
    private ImageView mCoverImageView;
    private boolean mIsKeepPlaying;
    private long mPlayStartTime;
    private String mPlayM3u8Url;
    private PlayerMode mPlayerMode;
    private View mAudioCover;
    private boolean isSwitchPlayerModeClick;
    private boolean isSwitchVideoResource;
    private ObjectAnimator mAudioCoverAnim;
    private float mAudioCoverAnimOffset;

    private Map<String, Boolean> mPlayUri = new HashMap<>(2);

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof BaseStudyDetailActivity) {
            mMenuCallback = (BaseStudyDetailActivity) activity;
        }
        int mediaCoder = MediaUtil.getMediaSupportType(getContext());
        if (mediaCoder == VLCOptions.NONE_RATE && AndroidUtil.isKitKatOrLater()) {
            mediaCoder = VLCOptions.SUPPORT_RATE;
            MediaUtil.saveMediaSupportType(getContext(), mediaCoder);
        }
        Log.e("测试", "onAttach的mediaCoder=" + mediaCoder);
        getArguments().putInt(PLAY_MEDIA_CODER, mediaCoder);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mCourseTask = (CourseTask) bundle.getSerializable(COURSE_TASK);
        mCourseProject = (CourseProject) bundle.getSerializable(COURSE_PROJECT);
        mLessonItem = (LessonItem) bundle.getSerializable(LESSON_ITEM);
        mIsMember = bundle.getBoolean(IS_MEMBER);
        mNetworkType = bundle.getInt(NETWORK_TYPE);
        mPlayerMode = bundle.getBoolean(IS_AUDIO_ON) ? PlayerMode.AUDIO : PlayerMode.VIDEO;
        if (mLessonItem != null && !StringUtils.isEmpty(mLessonItem.mediaUri) && !StringUtils.isEmpty(mLessonItem.audioUri)) {
            mPlayUri.put(mLessonItem.mediaUri, false);
            mPlayUri.put(mLessonItem.audioUri, false);
        }
        if (mIsMember && mLessonItem != null && mLessonItem.remainTime != null && Integer.parseInt(mLessonItem.remainTime) > 0) {
            startReturnData();
            startTiming();
        }
        startCacheServer();
        mIsKeepPlaying = true;
        mPlayStartTime = System.currentTimeMillis();
        setAudioOn(mPlayerMode == PlayerMode.AUDIO);
        mAudioCover = getCourseCoverOnAudio();
        setAudioCover(mAudioCover);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TaskFinishHelper.Builder builder = new TaskFinishHelper.Builder()
                .setCourseId(mCourseProject.id)
                .setCourseTask(mCourseTask)
                .setEnableFinish(mCourseProject.enableFinish);

        mTaskFinishHelper = new TaskFinishHelper(builder, getActivity())
                .setActionListener(new TaskFinishActionHelper() {
                    @Override
                    public void onFinish(TaskEvent taskEvent) {
                        EventBus.getDefault().postSticky(new MessageEvent<>(mCourseTask, MessageEvent.FINISH_TASK_SUCCESS));
                        if (mCourseProject.enableFinish == 0 || mShowDialog) {
                            TaskFinishDialog.newInstance(taskEvent, mCourseTask, mCourseProject).show(getActivity()
                                    .getSupportFragmentManager(), "mTaskFinishDialog");
                        }
                    }
                });
        mTaskFinishHelper.onInvoke();
        if (mPlayerMode == PlayerMode.AUDIO) {
            loadAudio();
        } else {
            loadVideo();
            CloudVideoSetting cloudVideoSetting = SettingHelper.getSetting(CloudVideoSetting.class, getActivity(), SharedPreferencesHelper.SchoolSetting.Cloud_VIDEO_SETTING);
            if (cloudVideoSetting != null && cloudVideoSetting.getFingerPrintSetting() != null && cloudVideoSetting.getWatermarkSetting() != null) {
                if (cloudVideoSetting.getWatermarkSetting().getVideoWatermark() != 0) {
                    addLogoWaterMark((ViewGroup) view, cloudVideoSetting.getWatermarkSetting().getVideoWatermarkImage());
                }
                if (cloudVideoSetting.getFingerPrintSetting().getVideoFingerprint() != 0) {
                    addAccountNameWaterMark((ViewGroup) view, (long) (cloudVideoSetting.getFingerPrintSetting().getVideoFingerprintTime() * 1000));
                }
            }
        }
        addMemoryPlayView((ViewGroup) view);
    }

    @Override
    public void onResume() {
        super.onResume();
        CacheServerFactory.getInstance().resume();
        CloudHelper.getInstance().record(getPlayerCurrentPosition());
        registerNetworkChangeBroadcastReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMenuCallback != null && mMenuCallback.getMenu() != null) {
            mMenuCallback.getMenu().dismiss();
        }
        CacheServerFactory.getInstance().pause();
        CloudHelper.getInstance().stop(getPlayerCurrentPosition());
        if (mNetworkChangeBroadcastReceiver != null) {
            getActivity().unregisterReceiver(mNetworkChangeBroadcastReceiver);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        TaskLearningRecordHelper.put(new TaskLearningRecordHelper.TaskLearningRecord(
                EdusohoApp.app.loginUser == null ? 0 : EdusohoApp.app.loginUser.id
                , mCourseTask.id), (int) mSeekPosition, getActivity()
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lifecycleSubject.onNext(FragmentEvent.DESTROY);
        if (mMenuCallback != null && mMenuCallback.getMenu() != null) {
            mMenuCallback.getMenu().setVisibility(false);
        }
        CacheServerFactory.getInstance().stop();
        if (mTimer != null) {
            if (mIsContinue) {
                CourseDetailModel.sendTime(mCourseTask.id, mPlayTime, null);
            }
            mTimer.cancel();
            mIsContinue = false;
        }
        if (mAudioCoverAnim != null) {
            mAudioCoverAnim.cancel();
            mAudioCoverAnim = null;
        }
        CloudHelper.getInstance().uploadWatchRecords();
    }

    @Override
    public void onReceive(String type, String mesasge) {
        super.onReceive(type, mesasge);
        if ("MediaCodecError".equals(type)) {
            mLessonItem.mediaUri = null;
            loadVideo();
        }
    }

    public static LessonVideoPlayerFragment newInstance(CourseTask courseTask, LessonItem lessonItem, CourseProject courseProject,
                                                        boolean isMember, int isMobileNetwork, boolean isAudioOn) {
        Bundle args = new Bundle();
        args.putSerializable(COURSE_TASK, courseTask);
        args.putSerializable(COURSE_PROJECT, courseProject);
        args.putSerializable(LESSON_ITEM, lessonItem);
        args.putBoolean(IS_MEMBER, isMember);
        args.putInt(NETWORK_TYPE, isMobileNetwork);
        args.putBoolean(IS_AUDIO_ON, isAudioOn);
        LessonVideoPlayerFragment fragment = new LessonVideoPlayerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void loadVideo() {
        LessonItem cachedLesson = getCachedLesson();
        if (cachedLesson != null) {
            cachedLesson.mediaUri = String.format("http://%s:8800/playlist/%d.m3u8", "localhost", mCourseTask.id);
            playVideo(cachedLesson.mediaUri);
            return;
        }
        if (!TextUtils.isEmpty(mLessonItem.mediaUri)) {
            playUri();
        } else {
            ToastUtils.show(getActivity(), getString(R.string.video_file_not_exist));
        }
    }

    private void loadAudio() {
        if (!TextUtils.isEmpty(mLessonItem.audioUri)) {
            playUri();
            updateAudioCoverViewStatus(true);
        } else {
            ToastUtils.show(getActivity(), getString(R.string.audio_file_not_exist));
        }
    }

    public void play(PlayerMode mode) {
        isSwitchPlayerModeClick = true;
        mPlayerMode = mode;
        mSeekPosition = getCurrentPosition();
        setAudioOn(mPlayerMode == PlayerMode.AUDIO);
        if (mode == PlayerMode.AUDIO) {
            if (mAudioCover != null) {
                mAudioCover.setVisibility(View.VISIBLE);
            }
            if (tvAccountName != null) {
                tvAccountName.setVisibility(View.INVISIBLE);
            }
            if (ivWaterMark != null) {
                ivWaterMark.setVisibility(View.INVISIBLE);
            }
            loadAudio();
        } else {
            if (mAudioCover != null) {
                mAudioCover.setVisibility(View.GONE);
            }
            if (tvAccountName != null) {
                tvAccountName.setVisibility(View.VISIBLE);
            }
            if (ivWaterMark != null) {
                ivWaterMark.setVisibility(View.VISIBLE);
            }
            loadVideo();
        }
    }

    private String filterJsonFormat(String url) {
        if (url.contains("?")) {
            String[] urls = url.split("\\?");
            if (urls.length > 1) {
                return urls[0];
            }
        }
        return url;
    }

    private LessonItem getCachedLesson() {
        User user = getAppSettingProvider().getCurrentUser();
        School school = getAppSettingProvider().getCurrentSchool();
        if (user == null || school == null) {
            return null;
        }
        M3U8DbModel m3U8DbModel = M3U8Util.queryM3U8Model(
                getContext(), user.id, mCourseTask.id, school.getDomain(), M3U8Util.FINISH);
        if (m3U8DbModel == null) {
            return null;
        }
        SqliteUtil sqliteUtil = SqliteUtil.getUtil(getContext());
        return sqliteUtil.queryForObj(
                new TypeToken<LessonItem>() {
                },
                "where type=? and key=?",
                Const.CACHE_LESSON_TYPE,
                "lesson-" + mCourseTask.id
        );
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            play();
        } else {
            pause();
        }
    }

    @Override
    public void doFinish() {
        mShowDialog = true;
        mTaskFinishHelper.finish();
    }

    @Override
    public void onPrepare() {
        super.onPrepare();
        if (!CloudHelper.getInstance().isRecordView() && "cloud".equals(mLessonItem.mediaStorage)) {
            CloudHelper.getInstance().init(getActivity(), mPlayM3u8Url, mPlayStartTime);
        }
        mIsPlay = true;
        CloudHelper.getInstance().record(getPlayerCurrentPosition());
    }

    @Override
    public void onPlaying() {
        super.onPlaying();
        if (isSwitchVideoResource) {
            setSeekPosition(mSeekPosition);
            isSwitchVideoResource = false;
        }
        if (isSwitchPlayerModeClick) {
            setSeekPosition(mSeekPosition);
            isSwitchPlayerModeClick = false;
        } else if (mIsKeepPlaying) {
            final int seek = TaskLearningRecordHelper.get(new TaskLearningRecordHelper.TaskLearningRecord(
                    EdusohoApp.app.loginUser == null ? 0 : EdusohoApp.app.loginUser.id
                    , mCourseTask.id), getActivity());
            if (seek / 1000 != 0 && seek / 1000 != mCourseTask.length) {
                if (mMemoryPanel != null) {
                    mMemoryPanel.setVisibility(View.VISIBLE);
                    mMemoryPanel.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mMemoryPanel.setVisibility(View.GONE);
                        }
                    }, 5000);
                }
                if (mMemoryTime != null) {
                    mMemoryTime.setText(String.format(getResources().getString(R.string.memory_play_video_last_time) + TimeUtils.getSecond2Min(seek / 1000)));
                }
                if (mMemoryPlay != null) {
                    mMemoryPlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setSeekPosition(seek);
                            CloudHelper.getInstance().record(seek / 1000);
                            mMemoryPanel.setVisibility(View.GONE);
                        }
                    });
                }
            }
            mIsKeepPlaying = false;
        }
    }

    @Override
    public void onFinish() {
        super.onFinish();
        if (mCourseTask.activity != null && !mCourseTask.isFinish() && END.toString().equals(mCourseTask.activity.finishType)) {
            mTaskFinishHelper.stickyFinish();
        }
        mIsPlay = false;
        CloudHelper.getInstance().stop(getPlayerCurrentPosition());
        if (mPlayerMode == PlayerMode.AUDIO) {
            HttpUtils.getInstance()
                    .addTokenHeader(EdusohoApp.app.token)
                    .createApi(CourseApi.class)
                    .getCourseItems(mCourseProject.id, 1)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SubscriberProcessor<List<CourseItem>>() {
                        @Override
                        public void onNext(List<CourseItem> courseItems) {
                            int currentIndex = -1;
                            for (int i = 0; i < courseItems.size(); i++) {
                                if (courseItems.get(i).task != null && courseItems.get(i).task.id == mCourseTask.id && i != courseItems.size() - 1) {
                                    currentIndex = i;
                                    break;
                                }
                            }
                            if (currentIndex != -1) {
                                for (int i = currentIndex + 1; i < courseItems.size(); i++) {
                                    if (CourseItemEnum.CHAPTER.toString().equals(courseItems.get(i).type)
                                            || CourseItemEnum.UNIT.toString().equals(courseItems.get(i).type)) {
                                        continue;
                                    }
                                    if (TaskTypeEnum.VIDEO.toString().equals(courseItems.get(i).task.type) ||
                                            TaskTypeEnum.AUDIO.toString().equals(courseItems.get(i).task.type)) {
                                        ((CourseProjectActivity) getActivity()).learnTask(courseItems.get(i).task);
                                    }
                                    break;
                                }
                            }
                        }
                    });
        }
    }

    @Override
    protected VideoControllerView.ControllerListener getDefaultControllerListener() {

        return new SimpleVideoControllerListener() {

            @Override
            public void onSeek(int position) {
                super.onSeek(position);
                CloudHelper.getInstance().stop(getPlayerCurrentPosition());
                CloudHelper.getInstance().record(position / 1000);
            }

            @Override
            public void onPlayStatusChange(boolean isPlay) {
                mIsPlay = isPlay;
                if (mIsPlay) {
                    CloudHelper.getInstance().record(getPlayerCurrentPosition());
                } else {
                    CloudHelper.getInstance().stop(getPlayerCurrentPosition());
                }
                if (mPlayerMode == PlayerMode.AUDIO) {
                    updateAudioCoverViewStatus(mIsPlay);
                }
            }

            @Override
            public void onChangeScreen(int orientation) {
                super.onChangeScreen(orientation);
                changeScreenLayout(orientation);
            }

            @Override
            public void onChangeOverlay(boolean isShow) {
                super.onChangeOverlay(isShow);
                changeHeaderViewStatus(isShow);
            }

            @Override
            public void onChangeRate(float rate) {
                super.onChangeRate(rate);
            }

            @Override
            public void onChangePlaySource(String url) {
                super.onChangePlaySource(url);
                mSeekPosition = getCurrentPosition();
                isSwitchVideoResource = true;
            }
        };
    }

    @Override
    protected void changeScreenLayout(final int orientation) {
        if (orientation == getResources().getConfiguration().orientation) {
            return;
        }
        View playView = getView();
        ViewParent viewParent = playView.getParent();
        if (viewParent == null) {
            return;
        }
        EventBus.getDefault().post(new MessageEvent<>(MessageEvent.FULL_SCREEN));

    }

    @Override
    protected void changeHeaderViewStatus(boolean isShow) {
        String changeBarEvent = isShow ?
                Const.COURSE_SHOW_BAR : Const.COURSE_HIDE_BAR;
        MessageEngine.getInstance().sendMsg(changeBarEvent, null);
    }

    @Override
    protected void savePosition(long seekTime) {
        mSeekPosition = seekTime;
    }

    @Override
    public void play() {
        if (mLessonItem.remainTime != null && mTotalTime >= Integer.parseInt(mLessonItem.remainTime) && getActivity() != null) {
            CommonUtil.shortCenterToast(getActivity(), getResources().getString(R.string.lesson_had_reached_hint));
            return;
        }
        super.play();
    }

    private void startTiming() {
        mIsContinue = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mIsContinue) {
                    try {
                        Thread.sleep(1000);
                        if (mIsPlay) {
                            mPlayTime++;
                            mTotalTime++;
                            if (mTotalTime >= Integer.parseInt(mLessonItem.remainTime)) {
                                mIsContinue = false;
                                mTimer.cancel();
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mTimer.cancel();
                                            CourseDetailModel.sendTime(mCourseTask.id, mPlayTime, null);
                                            if (getActivity() == null || getActivity().isFinishing() || !isAdded()) {
                                                return;
                                            }
                                            CommonUtil.shortCenterToast(getActivity(), getResources().getString(R.string.lesson_had_reached_hint));
                                            if (getActivity() instanceof CourseProjectActivity) {
                                                if (getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE) {
                                                    changeScreenLayout(ORIENTATION_PORTRAIT);
                                                }
                                                ((CourseProjectActivity) getActivity()).clearTaskFragment();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    } catch (InterruptedException e) {
                        StatService.reportException(getActivity(), e);
                    }
                }
            }
        }).start();
    }

    private void startReturnData() {
        mTimer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                CourseDetailModel.sendTime(mCourseTask.id, mPlayTime, new ResponseCallbackListener<String>() {
                    @Override
                    public void onSuccess(String data) {
                        mPlayTime = 0;
                    }

                    @Override
                    public void onFailure(String code, String message) {
                    }
                });
            }
        };
        mTimer.schedule(timerTask, LIMIT_PLAY_TIME, LIMIT_PLAY_TIME);
    }

    private void startCacheServer() {
        User user = getAppSettingProvider().getCurrentUser();
        School school = getAppSettingProvider().getCurrentSchool();
        if (user == null || school == null) {
            return;
        }
        CacheServerFactory.getInstance().start(getActivity().getApplicationContext(), school.host, user.id);
    }


    protected AppSettingProvider getAppSettingProvider() {
        return FactoryManager.getInstance().create(AppSettingProvider.class);
    }

    private int getPlayerCurrentPosition() {
        try {
            return (int) (getCurrentPosition() / 1000);
        } catch (Exception ex) {
            StatService.reportException(getActivity(), ex);
        }
        return 0;
    }

    private View getCourseCoverOnAudio() {
        final View audioBackground = LayoutInflater.from(getContext()).inflate(R.layout.view_audio_container_layout, null);
        mCoverImageView = audioBackground.findViewById(R.id.rl_audio_cover);
        ImageLoader.getInstance().displayImage(mCourseProject.courseSet.cover.middle, mCoverImageView);
        ImageLoader.getInstance().loadImage(mCourseProject.courseSet.cover.middle, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                if (getActivity() == null || getActivity().isFinishing() || isDetached()) {
                    return;
                }
                Bitmap maskBg = ImageUtil.maskImage(getContext(), loadedImage);
                audioBackground.setBackground(new BitmapDrawable(maskBg));
            }
        });
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        int parentWidth = wm.getDefaultDisplay().getWidth();
        ViewGroup.LayoutParams lp = mCoverImageView.getLayoutParams();
        lp.width = parentWidth / 3;
        lp.height = parentWidth / 3;
        mCoverImageView.setLayoutParams(lp);
        return audioBackground;
    }

    private void updateAudioCoverViewStatus(boolean isPlay) {
        if (mAudioCoverAnim == null) {
            mAudioCoverAnim = ObjectAnimator.ofFloat(mCoverImageView, "rotation", 0f, 359f);
            mAudioCoverAnim.setDuration(10000);
            mAudioCoverAnim.setInterpolator(new LinearInterpolator());
            mAudioCoverAnim.setRepeatCount(-1);
        }
        if (isPlay) {
            if (mAudioCoverAnim.isRunning()) {
                return;
            }
            mAudioCoverAnim.setFloatValues(mAudioCoverAnimOffset, mAudioCoverAnimOffset + 359f);
            mAudioCoverAnim.start();
        } else {
            mAudioCoverAnimOffset = (float) mAudioCoverAnim.getAnimatedValue();
            mAudioCoverAnim.cancel();
        }
    }

    private void addMemoryPlayView(ViewGroup parent) {
        mMemoryPanel = LayoutInflater.from(getActivity()).inflate(R.layout.view_memory_play, null);
        mMemoryPlay = mMemoryPanel.findViewById(R.id.tv_keep_play);
        mMemoryTime = mMemoryPanel.findViewById(R.id.tv_memory_time);
        mMemoryClose = mMemoryPanel.findViewById(R.id.iv_close);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.BOTTOM;
        lp.setMargins(0, 0, 0, AppUtils.dp2px(getActivity(), 48));
        mMemoryPanel.setLayoutParams(lp);
        parent.addView(mMemoryPanel);
        mMemoryClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMemoryPanel.setVisibility(View.GONE);
            }
        });
    }

    private void addLogoWaterMark(ViewGroup parent, String logoUrl) {
        ivWaterMark = new ImageView(getActivity());
        ivWaterMark.setAlpha(0.6f);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(120, 60);
        lp.gravity = Gravity.BOTTOM;
        ivWaterMark.setLayoutParams(lp);
        ImageLoader.getInstance().displayImage(logoUrl, ivWaterMark, EdusohoApp.app.mOptions);
        parent.addView(ivWaterMark);
    }

    private void addAccountNameWaterMark(final ViewGroup parent, final long showTime) {
        tvAccountName = new TextView(getActivity());
        String waterMark = EdusohoApp.app.domain + " " + (EdusohoApp.app.loginUser != null ? EdusohoApp.app.loginUser.nickname : "");
        final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.BOTTOM | Gravity.LEFT;
        tvAccountName.setText(waterMark);
        tvAccountName.setAlpha(0.5f);
        tvAccountName.setTextColor(getActivity().getResources().getColor(R.color.disabled2_hint_color));
        tvAccountName.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimension(R.dimen.font_size_xx_s));
        tvAccountName.setLayoutParams(lp);
        tvAccountName.setBackgroundColor(getActivity().getResources().getColor(R.color.transparent));
        tvAccountName.setVisibility(View.GONE);
        parent.addView(tvAccountName);
        showAccountNameWaterMark(parent, tvAccountName, showTime);
    }

    private void showAccountNameWaterMark(final ViewGroup parent, final TextView tvAccountName, final long showTime) {
        Observable.just(true)
                .subscribeOn(Schedulers.io())
                .delay((long) (Math.random() * 10 + 10), TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycle.<Boolean, FragmentEvent>bindUntilEvent(lifecycleSubject, FragmentEvent.DESTROY))
                .subscribe(new SubscriberProcessor<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (mPlayerMode == PlayerMode.VIDEO) {
                            int videoScreenWidth = 0;
                            int videoScreenHeight = 0;
                            if (parent.getParent() != null && parent.getParent() instanceof FrameLayout) {
                                videoScreenWidth = ((FrameLayout) parent.getParent()).getWidth();
                                videoScreenHeight = ((FrameLayout) parent.getParent()).getHeight();
                            }
                            float x = (float) (Math.random() * (videoScreenWidth - tvAccountName.getWidth()));
                            float y = (float) (Math.random() * (videoScreenHeight - tvAccountName.getHeight()));
                            tvAccountName.setX(x);
                            tvAccountName.setY(y);
                            tvAccountName.setVisibility(View.VISIBLE);
                            tvAccountName.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    tvAccountName.setVisibility(View.GONE);
                                    showAccountNameWaterMark(parent, tvAccountName, showTime);
                                }
                            }, showTime);
                        }
                    }
                });
    }

    private void registerNetworkChangeBroadcastReceiver() {
        if (mNetworkType == 0 && EdusohoApp.app.config.offlineType != 1) {
            if (mNetworkChangeBroadcastReceiver == null) {
                mNetworkChangeBroadcastReceiver = new NetworkChangeBroadcastReceiver(this);
                mIntentFilter = new IntentFilter();
                mIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            }
            getActivity().registerReceiver(mNetworkChangeBroadcastReceiver, mIntentFilter);
        }
    }

    @Override
    public void invokeMobileNetwork() {
        if (getCachedLesson() != null) {
            return;
        }
        if (mPlayerMode == PlayerMode.VIDEO && "1".equals(mCourseProject.isAudioOn)) {
            ESPlayerModeDialog
                    .newInstance()
                    .setAudioClickListener(new ESPlayerModeDialog.DialogButtonClickListener() {
                        @Override
                        public void onClick(DialogFragment dialog) {
                            if (getActivity() instanceof CourseProjectActivity) {
                                ((CourseProjectActivity) getActivity()).performAudioClick();
                            }
                            dialog.dismiss();
                        }
                    })
                    .setCancelClickListener(new ESPlayerModeDialog.DialogButtonClickListener() {
                        @Override
                        public void onClick(DialogFragment dialog) {
                            dialog.dismiss();
                            if (getActivity() != null && getActivity() instanceof CourseProjectActivity && !getActivity().isFinishing()) {
                                ((CourseProjectActivity) getActivity()).clearTaskFragment();
                            }
                        }
                    })
                    .show(getFragmentManager(), "ESPlayerModeDialog");
        } else {
            ESAlertDialog
                    .newInstance(null, getString(R.string.play_with_4g_info), getString(R.string.goon), getString(R.string.exit))
                    .setConfirmListener(new ESAlertDialog.DialogButtonClickListener() {
                        @Override
                        public void onClick(DialogFragment dialog) {
                            dialog.dismiss();
                        }
                    })
                    .setCancelListener(new ESAlertDialog.DialogButtonClickListener() {
                        @Override
                        public void onClick(DialogFragment dialog) {
                            if (getActivity() != null && getActivity() instanceof CourseProjectActivity && !getActivity().isFinishing()) {
                                ((CourseProjectActivity) getActivity()).clearTaskFragment();
                            }
                        }
                    })
                    .show(getActivity().getSupportFragmentManager(), "ESAlertDialog");
        }
    }

    private void playUri() {
        String uri = mPlayerMode == PlayerMode.AUDIO ? mLessonItem.audioUri : mLessonItem.mediaUri;
        if (isNeedRequestLesson()) {
            getLesson(mLessonItem.id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(RxLifecycle.<LessonItem, FragmentEvent>bindUntilEvent(lifecycleSubject, FragmentEvent.DESTROY))
                    .subscribe(new SubscriberProcessor<LessonItem>() {
                        @Override
                        public void onNext(LessonItem lessonItem) {
                            mLessonItem = lessonItem;
                            mPlayUri.clear();
                            mPlayUri.put(mLessonItem.mediaUri, false);
                            mPlayUri.put(mLessonItem.audioUri, false);
                            String newUri = mPlayerMode == PlayerMode.AUDIO ? mLessonItem.audioUri : mLessonItem.mediaUri;
                            mPlayM3u8Url = filterJsonFormat(newUri);
                            playVideo(mPlayM3u8Url);
                            updatePlayUri(newUri);
                        }
                    });

        } else {
            mPlayM3u8Url = filterJsonFormat(uri);
            playVideo(mPlayM3u8Url);
            Log.e("测试: ", "播放链接=" + mPlayM3u8Url);
            updatePlayUri(uri);
        }
    }

    private void updatePlayUri(String key) {
        for (Map.Entry<String, Boolean> item : mPlayUri.entrySet()) {
            if (item.getKey().equals(key)) {
                item.setValue(true);
            }
        }
    }

    private boolean isNeedRequestLesson() {
        for (Map.Entry<String, Boolean> item : mPlayUri.entrySet()) {
            if (!item.getValue()) {
                return false;
            }
        }
        return true;
    }

    private Observable<LessonItem> getLesson(int taskId) {
        return HttpUtils.getInstance()
                .baseOnApi()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(LessonApi.class)
                .getLesson(taskId);
    }

    public enum PlayerMode {
        VIDEO, AUDIO
    }
}
