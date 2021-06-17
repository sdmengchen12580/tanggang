package com.edusoho.kuozhi.v3.ui.fragment.lesson;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.api.CourseApi;
import com.edusoho.kuozhi.clean.bean.CourseItem;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.bean.TaskEvent;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.module.course.CourseProjectActivity;
import com.edusoho.kuozhi.clean.module.course.dialog.TaskFinishDialog;
import com.edusoho.kuozhi.clean.module.course.task.catalog.CourseItemEnum;
import com.edusoho.kuozhi.clean.module.course.task.catalog.TaskTypeEnum;
import com.edusoho.kuozhi.clean.receiver.NetworkChangeBroadcastReceiver;
import com.edusoho.kuozhi.clean.utils.AppUtils;
import com.edusoho.kuozhi.clean.utils.TimeUtils;
import com.edusoho.kuozhi.clean.utils.ToastUtils;
import com.edusoho.kuozhi.clean.utils.biz.TaskFinishActionHelper;
import com.edusoho.kuozhi.clean.utils.biz.TaskFinishHelper;
import com.edusoho.kuozhi.clean.utils.biz.TaskLearningRecordHelper;
import com.edusoho.kuozhi.clean.widget.ESAlertDialog;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.entity.lesson.LessonItem;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.provider.LessonProvider;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.ImageUtil;
import com.edusoho.videoplayer.ui.AudioPlayerFragment;
import com.edusoho.videoplayer.util.ControllerOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.greenrobot.eventbus.EventBus;
import org.videolan.libvlc.MediaPlayer;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by suju on 16/12/18.
 */

public class LessonAudioPlayerFragment extends AudioPlayerFragment implements CourseProjectActivity.TaskFinishListener
        , NetworkChangeBroadcastReceiver.MobileConnectListener {

    public static final  String COVER          = "cover";
    private static final String COURSE_PROJECT = "course_project";
    private static final String COURSE_TASK    = "course_task";
    private static final String NETWORK_TYPE   = "network_type";
    private static final String IS_AUDIO_ON    = "is_audio_on";

    private   NetworkChangeBroadcastReceiver mNetworkChangeBroadcastReceiver;
    private   IntentFilter                   mIntentFilter;
    protected String                         mCoverUrl;
    protected float                          mAudioCoverAnimOffset;
    protected ObjectAnimator                 mAudioCoverAnim;
    private   ImageView                      mCoverImageView;
    private   CourseTask                     mCourseTask;
    private   CourseProject                  mCourseProject;
    private   TaskFinishHelper               mTaskFinishHelper;
    private   boolean                        mShowFinishDialog;
    private   long                           mSeekPosition;
    private   View                           mMemoryPlay;
    private   TextView                       mMemoryTime;
    private   View                           mMemoryClose;
    private   View                           mMemoryPanel;
    private   int                            mNetworkType;
    private   boolean                        isAudioOn;
    private   boolean                        mIsKeepPlaying;

    public static LessonAudioPlayerFragment newInstance(String cover, CourseTask courseTask, CourseProject courseProject, int isMobileNetwork, boolean isAudioOn) {
        Bundle args = new Bundle();
        args.putString(COVER, cover);
        args.putSerializable(COURSE_TASK, courseTask);
        args.putSerializable(COURSE_PROJECT, courseProject);
        args.putInt(NETWORK_TYPE, isMobileNetwork);
        args.putBoolean(IS_AUDIO_ON, isAudioOn);
        LessonAudioPlayerFragment fragment = new LessonAudioPlayerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mCoverUrl = bundle.getString(COVER);
        mCourseTask = (CourseTask) bundle.getSerializable(COURSE_TASK);
        mCourseProject = (CourseProject) bundle.getSerializable(COURSE_PROJECT);
        mNetworkType = bundle.getInt(NETWORK_TYPE);
        isAudioOn = bundle.getBoolean(IS_AUDIO_ON);
        if (mCourseTask == null) {
            ToastUtils.show(getActivity(), "CourseTask is null");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ControllerOptions options = new ControllerOptions.Builder()
                .addOption(ControllerOptions.RATE, false)
                .addOption(ControllerOptions.SCREEN, false)
                .build();
        mVideoControllerView.setControllerOptions(options);
        initPlayContainer();

        TaskFinishHelper.Builder builder = new TaskFinishHelper.Builder()
                .setCourseId(mCourseProject.id)
                .setCourseTask(mCourseTask)
                .setEnableFinish(mCourseProject.enableFinish);

        mTaskFinishHelper = new TaskFinishHelper(builder, getActivity())
                .setActionListener(new TaskFinishActionHelper() {
                    @Override
                    public void onFinish(TaskEvent taskEvent) {
                        EventBus.getDefault().postSticky(new MessageEvent<>(mCourseTask, MessageEvent.FINISH_TASK_SUCCESS));
                        if (mCourseProject.enableFinish == 0 || mShowFinishDialog) {
                            TaskFinishDialog.newInstance(taskEvent, mCourseTask, mCourseProject).show(getActivity()
                                    .getSupportFragmentManager(), "mTaskFinishDialog");
                        }
                    }
                });

        mTaskFinishHelper.onInvoke();
        loadPlayUrl();
        addMemoryPlayView((ViewGroup) view);
        mIsKeepPlaying = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        registerNetworkChangeBroadcastReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mService != null) {
            mSeekPosition = mService.getTime() > mSeekPosition ? mService.getTime() : mSeekPosition;
        }
        if (mNetworkChangeBroadcastReceiver != null) {
            getActivity().unregisterReceiver(mNetworkChangeBroadcastReceiver);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mSeekPosition != 0) {
            TaskLearningRecordHelper.put(new TaskLearningRecordHelper.TaskLearningRecord(
                    EdusohoApp.app.loginUser == null ? 0 : EdusohoApp.app.loginUser.id
                    , mCourseTask.id), (int) mSeekPosition, getActivity()
            );
        }
    }

    @Override
    public void doFinish() {
        mShowFinishDialog = true;
        mTaskFinishHelper.finish();
    }

    public void pause() {
        if (mService.isPlaying()) {
            mService.pause();
            if (mService != null) {
                mSeekPosition = mService.getTime();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAudioCoverAnim != null) {
            mAudioCoverAnim.cancel();
            mAudioCoverAnim = null;
        }
    }

    public void setAudioOn(boolean isAudioOn) {
        this.isAudioOn = isAudioOn;
    }

    @Override
    public void onMediaPlayerEvent(MediaPlayer.Event event) {
        super.onMediaPlayerEvent(event);
        switch (event.type) {
            case MediaPlayer.Event.EndReached:
                if (!mCourseTask.isFinish()) {
                    mTaskFinishHelper.stickyFinish();
                }
                if (isAudioOn) {
                    playNext();
                }
                break;
            case MediaPlayer.Event.Playing:
                if (mIsKeepPlaying) {
                    keepPlay();
                    mIsKeepPlaying = false;
                }
                break;
            case MediaPlayer.Event.Paused:
                break;
            case MediaPlayer.Event.PausableChanged:
                break;
            case MediaPlayer.Event.Stopped:
                break;
        }
    }

    private void keepPlay() {
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
                mMemoryTime.setText(String.format(getResources().getString(R.string.memory_play_audio_last_time) + TimeUtils.getSecond2Min(seek / 1000)));
            }
            if (mMemoryPlay != null) {
                mMemoryPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mService.seek(seek);
                        mMemoryPanel.setVisibility(View.GONE);
                    }
                });
            }
        }
    }

    protected void setCoverViewState(boolean isShow) {
        mCoverImageView.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    private void loadPlayUrl() {
        new LessonProvider(getContext()).getLesson(mCourseTask.id)
                .success(new NormalCallback<LessonItem>() {
                    @Override
                    public void success(LessonItem lessonItem) {
                        if (getActivity() == null || getActivity().isFinishing() || !isAdded() || isDetached()) {
                            return;
                        }
                        changeToolBarState(false);
                        setCoverViewState(true);
                        if (lessonItem == null || TextUtils.isEmpty(lessonItem.mediaUri)) {
                            return;
                        }
                        playAudio(lessonItem.mediaUri);
                    }
                }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError obj) {
            }
        });
    }

    protected void initPlayContainer() {
        final View containerView = LayoutInflater.from(getContext()).inflate(R.layout.view_audio_container_layout, null);
        setContainerView(containerView);
        mCoverImageView = containerView.findViewById(R.id.rl_audio_cover);
        ImageLoader.getInstance().displayImage(mCoverUrl, mCoverImageView);
        ImageLoader.getInstance().loadImage(mCoverUrl, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                if (getActivity() == null || getActivity().isFinishing() || isDetached()) {
                    return;
                }
                Bitmap maskBg = ImageUtil.maskImage(getContext(), loadedImage);
                containerView.setBackground(new BitmapDrawable(maskBg));
            }
        });
        initCoverSize();
    }

    private void initCoverSize() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        int parentWidth = wm.getDefaultDisplay().getWidth();
        ViewGroup.LayoutParams lp = mCoverImageView.getLayoutParams();
        lp.width = parentWidth / 3;
        lp.height = parentWidth / 3;
        mCoverImageView.setLayoutParams(lp);
    }

    @Override
    public void onPlayStatusChange(boolean isPlay) {
        super.onPlayStatusChange(isPlay);
        updateAudioCoverViewStatus(isPlay);
    }

    @Override
    protected void stopPlayback() {
        super.stopPlayback();
        updateAudioCoverViewStatus(false);
    }

    @Override
    public void onChangeOverlay(boolean isShow) {
        super.onChangeOverlay(isShow);
        changeToolBarState(isShow);
    }

    private void changeToolBarState(boolean isShow) {
        String changeBarEvent = isShow ?
                Const.COURSE_SHOW_BAR : Const.COURSE_HIDE_BAR;
        MessageEngine.getInstance().sendMsg(changeBarEvent, null);
    }

    @Override
    protected void updateMediaPlayStatus(boolean isPlay) {
        super.updateMediaPlayStatus(isPlay);
        updateAudioCoverViewStatus(isPlay);
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

    public void playNext() {
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
