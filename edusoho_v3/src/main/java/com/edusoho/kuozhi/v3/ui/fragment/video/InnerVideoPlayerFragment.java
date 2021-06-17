package com.edusoho.kuozhi.v3.ui.fragment.video;


import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.seting.CloudVideoSetting;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.utils.AppUtils;
import com.edusoho.kuozhi.clean.utils.TimeUtils;
import com.edusoho.kuozhi.clean.utils.biz.SettingHelper;
import com.edusoho.kuozhi.clean.utils.biz.SharedPreferencesHelper;
import com.edusoho.kuozhi.clean.utils.biz.TaskLearningRecordHelper;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.util.MediaUtil;
import com.edusoho.videoplayer.ui.VideoPlayerFragment;
import com.edusoho.videoplayer.util.VLCOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.android.FragmentEvent;

import org.videolan.libvlc.util.AndroidUtil;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;


/**
 * Created by suju on 16/12/16.
 */

public class InnerVideoPlayerFragment extends VideoPlayerFragment {

    private final BehaviorSubject<FragmentEvent> lifecycleSubject = BehaviorSubject.create();
    private long     mSeekPosition;
    private View     mMemoryPlay;
    private TextView mMemoryTime;
    private View     mMemoryClose;
    private View     mMemoryPanel;
    private int      mTaskId;
    private String   mErrorType;
    private boolean  mIsKeepPlaying = true;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        int mediaCoder = MediaUtil.getMediaSupportType(getContext());
        if (mediaCoder == VLCOptions.NONE_RATE && AndroidUtil.isKitKatOrLater()) {
            mediaCoder = VLCOptions.SUPPORT_RATE;
            MediaUtil.saveMediaSupportType(getContext(), mediaCoder);
        }
        Bundle bundle = getArguments();
        bundle.putInt(PLAY_MEDIA_CODER, mediaCoder);
        mTaskId = bundle.getInt("lessonId");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFragmentSize(getContext().getResources().getDimensionPixelOffset(com.edusoho.videoplayer.R.dimen.video_height));
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp instanceof FrameLayout.LayoutParams) {
            ((FrameLayout.LayoutParams) lp).gravity = Gravity.CENTER;
            view.setLayoutParams(lp);
        }

        ((View) view.getParent()).setBackgroundColor(Color.BLACK);
        if (getActivity() != null && getActivity() instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(false);
            }
        }

        addMemoryPlayView((ViewGroup) view);
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

    @Override
    public void onStop() {
        super.onStop();
        TaskLearningRecordHelper.put(new TaskLearningRecordHelper.TaskLearningRecord(
                EdusohoApp.app.loginUser == null ? 0 : EdusohoApp.app.loginUser.id
                , mTaskId), (int) mSeekPosition, getActivity()
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lifecycleSubject.onNext(FragmentEvent.DESTROY);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem moreItem = menu.findItem(R.id.menu_more);
        if (moreItem != null) {
            moreItem.setVisible(false);
        }
    }

    private void initFragmentSize(int height) {
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        setVideoSize(width, height);
    }

    @Override
    public void onPrepare() {
        super.onPrepare();
        if (mSeekPosition != 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setSeekPosition(mSeekPosition);
                    mSeekPosition = 0;
                }
            }, 500);
        }
        if (!TextUtils.isEmpty(mErrorType)) {
            playVideo(getArguments().getString(LessonVideoPlayerFragment.PLAY_URI));
            mErrorType = null;
        }
    }


    @Override
    public void onPlaying() {
        super.onPlaying();
        if (mIsKeepPlaying) {
            final int seek = TaskLearningRecordHelper.get(new TaskLearningRecordHelper.TaskLearningRecord(
                    EdusohoApp.app.loginUser == null ? 0 : EdusohoApp.app.loginUser.id
                    , mTaskId), getActivity());
            if (seek != 0) {
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
                            mMemoryPanel.setVisibility(View.GONE);
                        }
                    });
                }
            }
            mIsKeepPlaying = false;
        }
    }

    @Override
    public void onReceive(String type, String mesasge) {
        super.onReceive(type, mesasge);
        this.mErrorType = type;
    }

    private void addMemoryPlayView(ViewGroup parent) {
        mMemoryPanel = LayoutInflater.from(getActivity()).inflate(R.layout.view_memory_play, null);
        mMemoryPlay = mMemoryPanel.findViewById(R.id.tv_keep_play);
        mMemoryTime = (TextView) mMemoryPanel.findViewById(R.id.tv_memory_time);
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
        ImageView ivWaterMark = new ImageView(getActivity());
        ivWaterMark.setAlpha(0.6f);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(120, 60);
        lp.gravity = Gravity.BOTTOM;
        ivWaterMark.setLayoutParams(lp);
        ImageLoader.getInstance().displayImage(logoUrl, ivWaterMark, EdusohoApp.app.mOptions);
        parent.addView(ivWaterMark);
    }

    private void addAccountNameWaterMark(final ViewGroup parent, final long showTime) {
        final TextView tvAccountName = new TextView(getActivity());
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
                        int videoScreenWidth = parent.getWidth();
                        int videoScreenHeight = parent.getHeight();
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
                });
    }

    @Override
    protected void changeScreenLayout(int orientation) {
        if (orientation == getResources().getConfiguration().orientation) {
            return;
        }
        int screenOrientation = orientation == Configuration.ORIENTATION_LANDSCAPE ?
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        getActivity().setRequestedOrientation(screenOrientation);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (getActivity() instanceof AppCompatActivity) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            }
        } else {
            if (getActivity() instanceof AppCompatActivity) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().show();
            }
        }


        View playView = getView();
        ViewParent viewParent = playView.getParent();
        if (viewParent == null) {
            return;
        }
        ViewGroup parent = (ViewGroup) viewParent;

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        ViewGroup.LayoutParams lp = parent.getLayoutParams();
        int height = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ?
                wm.getDefaultDisplay().getHeight() : getContext().getResources().getDimensionPixelOffset(com.edusoho.videoplayer.R.dimen.video_height);
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;

        initFragmentSize(height);
        parent.setLayoutParams(lp);
    }

    @Override
    protected void savePosition(long seekTime) {
        super.savePosition(seekTime);
        mSeekPosition = seekTime;
    }
}
