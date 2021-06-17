package com.edusoho.kuozhi.v3.ui;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.utils.ToastUtils;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.ui.fragment.video.LessonVideoPlayerFragment;
import com.edusoho.kuozhi.v3.util.Const;
import com.gyf.barlibrary.ImmersionBar;


/**
 * Created by suju on 17/1/16.
 */

public class VideoPlayerActivity extends AppCompatActivity {

    private Toolbar  mToolbar;
    private View     mBack;
    private TextView mTitleView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoplayer_layout);
        ImmersionBar.with(this).init();

        if (getIntent() == null) {
            ToastUtils.show(getBaseContext(), R.string.video_no_meidaurl);
            return;
        }
        String mediaUrl = getIntent().getStringExtra(LessonVideoPlayerFragment.PLAY_URI);
        if (TextUtils.isEmpty(mediaUrl)) {
            ToastUtils.show(getBaseContext(), R.string.video_no_meidaurl);
            return;
        }

        mToolbar = findViewById(R.id.toolbar);
        mBack = findViewById(R.id.iv_back);
        setSupportActionBar(mToolbar);
        mTitleView = findViewById(R.id.tv_toolbar_title);
        mTitleView.setText(getIntent().getStringExtra(Const.ACTIONBAR_TITLE));
        loadVideoPlayer(mediaUrl);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    onBackPressed();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy();
    }

    private void loadVideoPlayer(String mediaUrl) {
        Bundle bundle = new Bundle();
        bundle.putString(LessonVideoPlayerFragment.PLAY_URI, mediaUrl);
        try {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment = CoreEngine.create(getBaseContext()).runPluginWithFragmentByBundle(
                    "VideoLessonFragment", this, bundle);
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
