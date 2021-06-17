package com.edusoho.kuozhi.v3.plugin.appview;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;

import com.edusoho.eslive.athena.ui.AthenaLivePlayerActivity;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.eslive.athena.entity.LiveTicket;
import com.edusoho.kuozhi.clean.widget.ESAlertDialog;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.ui.fragment.video.LessonVideoPlayerFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;

public class AthenaLivePlayerAction {
    private Activity mActivity;

    public AthenaLivePlayerAction(Activity activity) {
        this.mActivity = activity;
    }

    public void invoke(Bundle bundle) {
        Intent intent = new Intent(AthenaLivePlayerActivity.ACTION);
        if (checkLiveAppIsExist(intent)) {
            CommonUtil.shortToast(mActivity.getApplicationContext(), "客户端暂时不支持播放此直播类型");
            return;
        }

        if (bundle.getBoolean("replayState", false)) {
            startReplyActivity(mActivity.getBaseContext(), bundle);
            return;
        }
        intent.putExtras(bundle);
        mActivity.startActivity(intent);
    }

    public void invoke(final LiveTicket liveTicket, final CourseTask courseTask) {
        if (!AppUtil.isNetConnect(mActivity)) {
            ESAlertDialog.newInstance(null, mActivity.getString(R.string.network_does_not_work), mActivity.getString(R.string.confirm), null)
                    .setConfirmListener(new ESAlertDialog.DialogButtonClickListener() {
                        @Override
                        public void onClick(DialogFragment dialog) {
                            dialog.dismiss();
                        }
                    })
                    .show(((AppCompatActivity) mActivity).getSupportFragmentManager(), "ESAlertDialog");
        } else if (!AppUtil.isWiFiConnect(mActivity) && EdusohoApp.app.config.offlineType != 1) {
            ESAlertDialog.newInstance(null, mActivity.getString(R.string.play_with_4g_info), mActivity.getString(R.string.goon), mActivity.getString(R.string.exit))
                    .setConfirmListener(new ESAlertDialog.DialogButtonClickListener() {
                        @Override
                        public void onClick(DialogFragment dialog) {
                            dialog.dismiss();
                            startLive(liveTicket, courseTask, true);
                        }
                    })
                    .setCancelListener(new ESAlertDialog.DialogButtonClickListener() {
                        @Override
                        public void onClick(DialogFragment dialog) {
                            dialog.dismiss();
                        }
                    })
                    .show(((AppCompatActivity) mActivity).getSupportFragmentManager(), "ESAlertDialog");
        } else if (!AppUtil.isWiFiConnect(mActivity) && EdusohoApp.app.config.offlineType == 1) {
            startLive(liveTicket, courseTask, true);
        } else {
            startLive(liveTicket, courseTask, false);
        }
    }

    public void startLive(final LiveTicket liveTicket, final CourseTask courseTask, boolean isMobileNetWork) {
        Intent intent = new Intent(AthenaLivePlayerActivity.ACTION);
        intent.putExtra("live_ticket", liveTicket);
        intent.putExtra("course_title", courseTask.title);
        intent.putExtra("is_mobile_network", isMobileNetWork);
        mActivity.startActivity(intent);
    }

    private void startReplyActivity(Context context, Bundle bundle) {
        bundle.putString(LessonVideoPlayerFragment.PLAY_URI, bundle.getString("playUrl"));
        bundle.putString(Const.ACTIONBAR_TITLE, bundle.getString("title"));
        CoreEngine.create(context).runNormalPluginWithBundle("VideoPlayerActivity", context, bundle);
    }

    private boolean checkLiveAppIsExist(Intent intent) {
        return mActivity.getBaseContext().getPackageManager().resolveActivity(intent, 0) == null;
    }
}
