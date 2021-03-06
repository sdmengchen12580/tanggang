
package com.edusoho.kuozhi.v3.view.video;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.MediaController;

public class
CustomVideoView extends SurfaceView implements
        MediaController.MediaPlayerControl {

    private boolean pause;
    private boolean seekBackward;
    private boolean seekForward;
    private Uri videoUri;
    private MediaPlayer mediaPlayer;
    private Context context;
    private MediaPlayer.OnPreparedListener onPreparedListener;
    public int videoWidth;
    public int videoHeight;
    private MediaController mediaController;
    protected SurfaceHolder surfaceHolder;

    private SurfaceHolder.Callback surfaceHolderCallback = new SurfaceHolder.Callback() {
        public void surfaceChanged(SurfaceHolder holder, int format, int w,
                                   int h) {
        }

        public void surfaceCreated(SurfaceHolder holder) {
            surfaceHolder = holder;
            if (mediaPlayer != null) {
                mediaPlayer.setDisplay(surfaceHolder);
                resume();
            } else {
                openVideo();
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            surfaceHolder = null;
            if (mediaController != null) {
                mediaController.hide();
            }
            release(true);
        }
    };

    private void release(boolean cleartargetstate) {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void resume() {
        if (surfaceHolder == null) {
            return;
        }
        if (mediaPlayer != null) {
            return;
        }
        openVideo();
    }

    public CustomVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        this.initVideoView();
    }
    public CustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.initVideoView();
    }
    public CustomVideoView(Context context) {
        super(context);
        this.context = context;
        this.initVideoView();
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return this.pause;
    }
    @Override
    public boolean canSeekBackward() {
        return this.seekBackward;
    }
    @Override
    public boolean canSeekForward() {
        return this.seekForward;
    }
    @Override
    public int getBufferPercentage() {
        return 0;
    }
    @Override
    public int getCurrentPosition() {
        return mediaPlayer!=null?mediaPlayer.getCurrentPosition():0;
    }
    @Override
    public int getDuration() {
        return mediaPlayer!=null?mediaPlayer.getDuration():0;
    }
    @Override
    public boolean isPlaying() {
        return false;
    }
    @Override
    public void pause() {
    }
    @Override
    public void seekTo(int mSec) {
    }
    @Override
    public void start() {
    }

    public void setVideoURI(Uri uri) {
        this.videoUri = uri;
        openVideo();
        requestLayout();
        invalidate();
    }
    private void openVideo() {
        this.mediaPlayer = new MediaPlayer();
        try {
            this.mediaPlayer.setDataSource(this.context, this.videoUri);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.mediaPlayer.prepareAsync();
        this.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        this.mediaPlayer.setOnPreparedListener(onPreparedListener);
        this.mediaPlayer.setOnErrorListener(null);
        attachMediaController();
    }
    private void attachMediaController() {
        if (mediaPlayer != null && mediaController != null) {
            mediaController.setMediaPlayer(this);
            View anchorView = this.getParent() instanceof View ? (View) this
                    .getParent() : this;
            mediaController.setAnchorView(anchorView);
            mediaController.setEnabled(true);
        }
    }
    public void setMediaController(MediaController controller) {
        if (mediaController != null) {
            mediaController.hide();
        }
        mediaController = controller;
        attachMediaController();
    }

    public void setOnPreparedListener(MediaPlayer.OnPreparedListener onPreparedListener) {
        this.onPreparedListener = onPreparedListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(videoWidth, widthMeasureSpec);
        int height = getDefaultSize(videoHeight, heightMeasureSpec);

        if (videoWidth > 0 && videoHeight > 0) {
            //??????
            if (height > width) {
                height = width * videoHeight / videoWidth;
            } else if (width > height && width > videoWidth) {
                width = videoWidth;
            }
        }

        setMeasuredDimension(width, height);
    }
    private void initVideoView() {
        videoWidth = 0;
        videoHeight = 0;
        getHolder().addCallback(surfaceHolderCallback);
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
    }
}