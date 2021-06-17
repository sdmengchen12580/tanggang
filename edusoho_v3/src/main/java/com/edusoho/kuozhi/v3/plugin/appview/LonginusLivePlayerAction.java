package com.edusoho.kuozhi.v3.plugin.appview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.edusoho.eslive.athena.entity.LiveTicket;
import com.edusoho.eslive.longinus.ui.LessonLivePlayerActivity;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.ui.fragment.video.LessonVideoPlayerFragment;
import com.edusoho.kuozhi.v3.util.Const;

/**
 * Created by 菊 on 2016/4/11.
 */
public class LonginusLivePlayerAction {

    private Activity mActivity;

    public LonginusLivePlayerAction(Activity activity) {
        this.mActivity = activity;
    }

    public void invoke(Bundle bundle) {
        Intent intent = new Intent(LessonLivePlayerActivity.ACTION);
        if (bundle.getBoolean("replayState", false)) {
            startReplyActivity(mActivity, bundle);
        } else {
            intent.putExtras(bundle);
            mActivity.startActivity(intent);
        }
    }

    public void invoke(LiveTicket liveTicket, int lessonId, String title) {
        if (!TextUtils.isEmpty(liveTicket.getUrl())) {
            Bundle bundle = new Bundle();
            bundle.putString(LessonVideoPlayerFragment.PLAY_URI, liveTicket.getUrl());
            bundle.putString(Const.ACTIONBAR_TITLE, title);
            CoreEngine.create(mActivity).runNormalPluginWithBundle("VideoPlayerActivity", mActivity, bundle);
        } else {
            Intent intent = new Intent(LessonLivePlayerActivity.ACTION);
            intent.putExtra("live_ticket", liveTicket);
            intent.putExtra("task_id", lessonId);
            intent.putExtra("title", title);
            mActivity.startActivity(intent);
        }
    }

    private void startReplyActivity(Context context, Bundle bundle) {
        bundle.putString(LessonVideoPlayerFragment.PLAY_URI, bundle.getString("playUrl"));
        bundle.putString(Const.ACTIONBAR_TITLE, bundle.getString("title"));
        CoreEngine.create(context).runNormalPluginWithBundle("VideoPlayerActivity", context, bundle);
    }
}
