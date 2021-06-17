package com.edusoho.kuozhi.clean.push;


import android.content.Context;
import android.content.Intent;

import com.edusoho.kuozhi.clean.utils.GsonUtils;
import com.edusoho.kuozhi.clean.utils.StringUtils;
import com.edusoho.kuozhi.clean.utils.biz.NotificationHelper;
import com.edusoho.kuozhi.imserver.entity.message.MessageBody;
import com.edusoho.kuozhi.v3.service.message.push.IPushProcessor;
import com.edusoho.kuozhi.v3.ui.BulletinActivity;
import com.edusoho.kuozhi.v3.util.Const;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.util.LinkedHashMap;

public class SchoolNotificationPushProcessor implements IPushProcessor {
    private Context     mContext;
    private MessageBody mMessageBody;

    public SchoolNotificationPushProcessor(Context context, MessageBody messageBody) {
        this.mContext = context;
        this.mMessageBody = messageBody;
    }

    @Override
    public Intent getNotifyIntent() {
        Intent notifyIntent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
        notifyIntent.removeCategory(Intent.CATEGORY_LAUNCHER);
        notifyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        LinkedHashMap<String, String> body = GsonUtils.parseJson(mMessageBody.getBody(), new TypeToken<LinkedHashMap<String, String>>() {
        });

        switch (body.get("type")) {
            case NotificationHelper.BATCH_NOTIFICATION_PUBLISH:
                notifyIntent.putExtra(Const.INTENT_TARGET, BulletinActivity.class);
                break;
        }

        return notifyIntent;
    }

    @Override
    public String[] getNotificationContent(String json) {
        LinkedTreeMap<String, String> body = GsonUtils.parseJson(json, new TypeToken<LinkedTreeMap<String, String>>() {
        });
        String title = StringUtils.isCheckNull(body.get("title"));
        String message = StringUtils.isCheckNull(body.get("message"));
        return new String[]{title, message};
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
    }
}
