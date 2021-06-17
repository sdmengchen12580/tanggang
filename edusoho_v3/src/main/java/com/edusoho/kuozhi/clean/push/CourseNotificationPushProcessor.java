package com.edusoho.kuozhi.clean.push;


import android.content.Context;
import android.content.Intent;
import android.text.Html;

import com.edusoho.kuozhi.clean.module.main.news.notification.course.CourseNotificationActivity;
import com.edusoho.kuozhi.clean.utils.GsonUtils;
import com.edusoho.kuozhi.clean.utils.StringUtils;
import com.edusoho.kuozhi.clean.utils.biz.CourseNotificationPushHelper;
import com.edusoho.kuozhi.clean.utils.biz.NotificationHelper;
import com.edusoho.kuozhi.imserver.IMClient;
import com.edusoho.kuozhi.imserver.entity.message.MessageBody;
import com.edusoho.kuozhi.imserver.helper.IDbManager;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.provider.AppSettingProvider;
import com.edusoho.kuozhi.v3.model.bal.push.Notify;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.service.message.push.ESDbManager;
import com.edusoho.kuozhi.v3.service.message.push.IPushProcessor;
import com.edusoho.kuozhi.v3.service.message.push.NotifyDbHelper;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

/**
 * 放入课程中心-课程通知的处理
 */
public class CourseNotificationPushProcessor implements IPushProcessor {
    private Context     mContext;
    private MessageBody mMessageBody;

    public CourseNotificationPushProcessor(Context context, MessageBody messageBody) {
        this.mContext = context;
        this.mMessageBody = messageBody;
    }

    @Override
    public Intent getNotifyIntent() {
        Intent notifyIntent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
        notifyIntent.removeCategory(Intent.CATEGORY_LAUNCHER);
        notifyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notifyIntent.putExtra(Const.INTENT_TARGET, CourseNotificationActivity.class);
        return notifyIntent;
    }

    @Override
    public String[] getNotificationContent(String json) {
        LinkedTreeMap<String, String> body = GsonUtils.parseJson(json, new TypeToken<LinkedTreeMap<String, String>>() {
        });
        String type = StringUtils.isCheckNull(NotificationHelper.getName(body.get("type")));
        String title = Html.fromHtml(StringUtils.isCheckNull(body.get("title"))).toString();
        String message = Html.fromHtml(StringUtils.isCheckNull(body.get("message"))).toString();
        switch (type) {
            case NotificationHelper.QUESTION_CREATED:
                title = AppUtil.coverCourseAbout(title);
                message = String.format("「课程问答」:%s", StringUtils.isCheckNull(Html.fromHtml(body.get("questionTitle")).toString()));
                break;
            case NotificationHelper.QUESTION_ANSWERED:
                title = AppUtil.coverCourseAbout(StringUtils.isCheckNull(Html.fromHtml(body.get("questionTitle")).toString()));
                message = String.format("%s", "您收到一条问答回复");
                break;
            case NotificationHelper.COURSE_THREAD_UPDATE:
            case NotificationHelper.COURSE_THREAD_DELETE:
            case NotificationHelper.COURSE_THREAD_STICK:
            case NotificationHelper.COURSE_THREAD_UNSTICK:
            case NotificationHelper.COURSE_THREAD_ELITE:
            case NotificationHelper.COURSE_THREAD_UNELITE:
            case NotificationHelper.COURSE_THREAD_POST_UPDATE:
            case NotificationHelper.COURSE_THREAD_POST_DELETE:
            case NotificationHelper.COURSE_THREAD_POST_AT:
            case NotificationHelper.CLASSROOM_DEADLINE:
            case NotificationHelper.CLASSROOM_JOIN:
            case NotificationHelper.CLASSROOM_QUIT:
            case NotificationHelper.CLASSROOM_ANNOUNCEMENT_CREATE:
            case NotificationHelper.COURSE_DEADLINE:
            case NotificationHelper.COURSE_JOIN:
            case NotificationHelper.COURSE_QUIT:
            case NotificationHelper.COURSE_LIVE_START:
            case NotificationHelper.COURSE_LIVE_START1:
            case NotificationHelper.COURSE_ANNOUNCEMENT_CREATE:
                break;
        }
        return new String[]{title, message};
    }

    @Override
    public void processor() {
        School school = FactoryManager.getInstance().<AppSettingProvider>create(AppSettingProvider.class).getCurrentSchool();
        if (school == null) {
            return;
        }
        IDbManager dbManager = new ESDbManager(mContext, school.getDomain());
        NotifyDbHelper notifyDbHelper = new NotifyDbHelper(mContext, dbManager);
        if (notifyDbHelper.hasNotifyByMsgNo(mMessageBody.getMsgNo())) {
            return;
        }
        Map<String, String> bodyMap = GsonUtils.parseJson(mMessageBody.getBody(), new TypeToken<Map<String, String>>() {
        });
        Notify notify = new Notify();
        notify.setCreatedTime(mMessageBody.getCreatedTime());
        notify.setType(bodyMap.get("type"));
        notify.setContent(mMessageBody.getBody());
        notify.setTitle(NotificationHelper.getName(bodyMap.get("type")));
        notify.setOwnerId(IMClient.getClient().getClientId());
        notifyDbHelper.createNotify(notify);
        CourseNotificationPushHelper helper = new CourseNotificationPushHelper.Builder().init(mContext).setMessageBody(mMessageBody).build();
        helper.updateConvEntity(notify);
    }
}
