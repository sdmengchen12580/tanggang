package com.edusoho.kuozhi.clean.utils.biz;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.edusoho.eslive.athena.entity.LiveTicket;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.kuozhi.clean.bean.innerbean.Cover;
import com.edusoho.kuozhi.clean.utils.Constants;
import com.edusoho.kuozhi.clean.utils.StringUtils;
import com.edusoho.kuozhi.clean.utils.ToastUtils;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.plugin.appview.AthenaLivePlayerAction;
import com.edusoho.kuozhi.v3.plugin.appview.GenseeLivePlayerAction;
import com.edusoho.kuozhi.v3.plugin.appview.LonginusLivePlayerAction;
import com.edusoho.kuozhi.v3.plugin.appview.TalkFunLivePlayerAction;
import com.edusoho.kuozhi.v3.util.Const;

public class LiveTaskLauncher {
    private Context    mContext;
    private LiveTicket mLiveTick;
    private CourseTask mCourseTask;

    private LiveTaskLauncher(Builder builder) {
        this.mContext = builder.mContext;
        this.mLiveTick = builder.mLiveTick;
        this.mCourseTask = builder.mCourseTask;
    }

    public static Builder build() {
        return new Builder();
    }

    public void launch() {
        if (mLiveTick.getExtra() != null && !StringUtils.isEmpty(mLiveTick.getExtra().getProvider())) {
            switch (mLiveTick.getExtra().getProvider()) {
                case Constants.LiveProvider.LONGINUS:
                    new LonginusLivePlayerAction((Activity) mContext).invoke(mLiveTick, mCourseTask.id, mCourseTask.title);
                    break;
                case Constants.LiveProvider.ATHENA:
                    new AthenaLivePlayerAction((Activity) mContext).invoke(mLiveTick, mCourseTask);
                    break;
                case Constants.LiveProvider.GENSEE:
                    Bundle genseeBundle = new Bundle();
                    genseeBundle.putBoolean("replayState", mLiveTick.isReplayStatus());
                    genseeBundle.putString("domain", mLiveTick.getExtra().getDomain());
                    genseeBundle.putString("roomNumber", mLiveTick.getExtra().getRoomNumber());
                    genseeBundle.putString("loginAccount", mLiveTick.getExtra().getLoginAccount());
                    genseeBundle.putString("loginPwd", mLiveTick.getExtra().getLoginPwd());
                    genseeBundle.putString("joinPwd", mLiveTick.getExtra().getJoinPwd());
                    genseeBundle.putString("vodPwd", mLiveTick.getExtra().getVodPwd());
                    genseeBundle.putString("nickName", mLiveTick.getExtra().getNickName());
                    genseeBundle.putString("serviceType", mLiveTick.getExtra().getServiceType());
                    genseeBundle.putString("k", mLiveTick.getExtra().getK());
                    new GenseeLivePlayerAction((Activity) mContext).invoke(genseeBundle);
                    break;
                case Constants.LiveProvider.TALKFUN:
                    Bundle talkfunBundle = new Bundle();
                    talkfunBundle.putBoolean("replayState", mLiveTick.isReplayStatus());
                    talkfunBundle.putString("token", mLiveTick.getExtra().getAccessToken());
                    talkfunBundle.putString("id", mCourseTask.id + "");
                    new TalkFunLivePlayerAction((Activity) mContext).invoke(talkfunBundle);
                    break;
                default:
                    ToastUtils.show(mContext, R.string.live_task_unsupport);
            }
        } else if (mLiveTick.getExtra() == null && !StringUtils.isEmpty(mLiveTick.getUrl())) {
            CoreEngine.create(mContext).runNormalPlugin("WebViewActivity", mContext, new PluginRunCallback() {
                @Override
                public void setIntentDate(Intent startIntent) {
                    startIntent.putExtra(Const.WEB_URL, mLiveTick.getUrl());
                }
            });
        }
    }

    public static class Builder {
        private Context    mContext;
        private LiveTicket mLiveTick;
        private CourseTask mCourseTask;

        public Builder init(Context context) {
            this.mContext = context;
            return this;
        }

        public Builder setLiveTick(LiveTicket liveTick) {
            this.mLiveTick = liveTick;
            return this;
        }

        public Builder setCourseTask(CourseTask courseTask) {
            this.mCourseTask = courseTask;
            return this;
        }

        public Builder setCover(Cover cover) {
            return this;
        }

        public LiveTaskLauncher build() {
            return new LiveTaskLauncher(this);
        }
    }
}
