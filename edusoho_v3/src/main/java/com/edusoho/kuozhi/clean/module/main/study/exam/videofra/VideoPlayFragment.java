package com.edusoho.kuozhi.clean.module.main.study.exam.videofra;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;

import com.edusoho.kuozhi.clean.api.LessonApi;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.utils.biz.CloudHelper;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.entity.lesson.LessonItem;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.provider.AppSettingProvider;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.bal.m3u8.M3U8DbModel;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.M3U8Util;
import com.edusoho.kuozhi.v3.util.MediaUtil;
import com.edusoho.kuozhi.v3.util.server.CacheServerFactory;
import com.edusoho.kuozhi.v3.util.sql.SqliteUtil;
import com.edusoho.videoplayer.media.listener.SimpleVideoControllerListener;
import com.edusoho.videoplayer.ui.VideoPlayerFragment;
import com.edusoho.videoplayer.util.VLCOptions;
import com.edusoho.videoplayer.view.VideoControllerView;
import com.google.gson.reflect.TypeToken;
import com.tencent.stat.StatService;
import com.trello.rxlifecycle.android.FragmentEvent;

import org.greenrobot.eventbus.EventBus;
import org.videolan.libvlc.util.AndroidUtil;

import rx.Observable;
import rx.subjects.BehaviorSubject;

//fragment_video_play
public class VideoPlayFragment extends VideoPlayerFragment implements View.OnFocusChangeListener {

    private final BehaviorSubject<FragmentEvent> lifecycleSubject = BehaviorSubject.create();

    private boolean mIsPlay;
    private long mSeekPosition;
    private long mPlayStartTime;
    private String mPlayM3u8Url;
    private boolean isSwitchPlayerModeClick;
    private boolean isSwitchVideoResource;
    private String url;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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
        url = getArguments().getString("url");
        mPlayStartTime = System.currentTimeMillis();
        setAudioOn(false);
        loadVideo();
    }

//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//    }

    @Override
    public void onResume() {
        super.onResume();
        CacheServerFactory.getInstance().resume();
        CloudHelper.getInstance().record(getPlayerCurrentPosition());
    }

    @Override
    public void onPause() {
        super.onPause();
        CacheServerFactory.getInstance().pause();
        CloudHelper.getInstance().stop(getPlayerCurrentPosition());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lifecycleSubject.onNext(FragmentEvent.DESTROY);
        CacheServerFactory.getInstance().stop();
        CloudHelper.getInstance().uploadWatchRecords();
    }

    @Override
    public void onReceive(String type, String mesasge) {
        super.onReceive(type, mesasge);
        if ("MediaCodecError".equals(type)) {
            loadVideo();
        }
    }

    public static VideoPlayFragment newInstance(String url) {
        Bundle args = new Bundle();
        args.putString("url", url);
        VideoPlayFragment fragment = new VideoPlayFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void loadVideo() {
        playUri();
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

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            play();
        } else {
            pause();
        }
    }

    public void onPrepare() {
        super.onPrepare();
        if (!CloudHelper.getInstance().isRecordView()) {
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
        }
    }

    @Override
    public void onFinish() {
        super.onFinish();
        mIsPlay = false;
        CloudHelper.getInstance().stop(getPlayerCurrentPosition());
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
        super.play();
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

    private void playUri() {
        Log.e("测试: ", "播放url=" + url);
        mPlayM3u8Url = filterJsonFormat(url);
        Log.e("测试: ", "播放mPlayM3u8Url=" + mPlayM3u8Url);
        playVideo(mPlayM3u8Url);
        //获取视频
//        getLesson(mLessonItem.id)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .compose(RxLifecycle.<LessonItem, FragmentEvent>bindUntilEvent(lifecycleSubject, FragmentEvent.DESTROY))
//                .subscribe(new SubscriberProcessor<LessonItem>() {
//                    @Override
//                    public void onNext(LessonItem lessonItem) {
//                        mLessonItem = lessonItem;
//                        mPlayUri.clear();
//                        mPlayUri.put(mLessonItem.mediaUri, false);
//                        mPlayUri.put(mLessonItem.audioUri, false);
//                        String newUri = mPlayerMode == LessonVideoPlayerFragment.PlayerMode.AUDIO ? mLessonItem.audioUri : mLessonItem.mediaUri;
//                        mPlayM3u8Url = filterJsonFormat(newUri);
//                        playVideo(mPlayM3u8Url);
//                        updatePlayUri(newUri);
//                    }
//                });
    }

    private Observable<LessonItem> getLesson(int taskId) {
        return HttpUtils.getInstance()
                .baseOnApi()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(LessonApi.class)
                .getLesson(taskId);
    }
}
