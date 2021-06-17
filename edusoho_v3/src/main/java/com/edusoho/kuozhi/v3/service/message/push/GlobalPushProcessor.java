package com.edusoho.kuozhi.v3.service.message.push;

import android.content.Context;
import android.content.Intent;

import com.edusoho.kuozhi.clean.utils.GsonUtils;
import com.edusoho.kuozhi.imserver.entity.message.MessageBody;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.ui.BulletinActivity;
import com.edusoho.kuozhi.v3.ui.fragment.NewsFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

/**
 * Created by suju on 16/11/10.
 */
public class GlobalPushProcessor implements IPushProcessor {

    private Context     mContext;
    private MessageBody mMessageBody;

    public GlobalPushProcessor(Context context, MessageBody messageBody) {
        this.mContext = context;
        this.mMessageBody = messageBody;
    }

    @Override
    public void processor() {
//        School school = FactoryManager.getInstance().<AppSettingProvider>create(AppSettingProvider.class).getCurrentSchool();
//        if (school == null) {
//            return;
//        }
//        IDbManager dbManager = new ESDbManager(mContext, school.getDomain());
//        NotifyDbHelper notifyDbHelper = new NotifyDbHelper(mContext, dbManager);
//        if (notifyDbHelper.hasNotifyByMsgNo(mMessageBody.getMsgNo())) {
//            return;
//        }
//        Map<String, String> bodyMap = GsonUtils.parseJson(mMessageBody.getBody(), new TypeToken<Map<String, String>>() {
//        });
//        Notify notify = new Notify();
//        notify.setCreatedTime(mMessageBody.getCreatedTime());
//        notify.setType(bodyMap.get("type"));
//        notify.setContent(mMessageBody.getBody());
//        notify.setTitle(NotificationHelper.getName(bodyMap.get("type")));
//        notifyDbHelper.createNotify(notify);
//        CourseNotificationPushHelper helper = new CourseNotificationPushHelper.Builder().init(mContext).setMessageBody(mMessageBody).build();
//        helper.updateConvEntity(notify);
        MessageEngine.getInstance().sendMsgToTaget(Const.REFRESH_LIST, null, NewsFragment.class);
    }

    @Override
    public Intent getNotifyIntent() {
        Intent notifyIntent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
        notifyIntent.removeCategory(Intent.CATEGORY_LAUNCHER);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notifyIntent.putExtra(Const.INTENT_TARGET, BulletinActivity.class);
        return notifyIntent;
    }

    @Override
    public String[] getNotificationContent(String body) {
        LinkedTreeMap<String, String> linkedHashMap = GsonUtils.parseJson(body, new TypeToken<LinkedTreeMap<String, String>>() {
        });
        return new String[]{"网校公告", AppUtil.coverCourseAbout(linkedHashMap.get("title"))};
    }
}
