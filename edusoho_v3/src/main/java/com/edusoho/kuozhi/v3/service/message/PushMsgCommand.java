package com.edusoho.kuozhi.v3.service.message;

import android.content.Context;
import android.util.Log;

import com.edusoho.kuozhi.clean.push.CourseNotificationPushProcessor;
import com.edusoho.kuozhi.clean.push.SchoolNotificationPushProcessor;
import com.edusoho.kuozhi.clean.push.VerifiedNotificationPushProcessor;
import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.imserver.listener.IMMessageReceiver;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.service.message.push.ArticlePushProcessor;
import com.edusoho.kuozhi.v3.service.message.push.GlobalPushProcessor;
import com.edusoho.kuozhi.v3.service.message.push.IPushProcessor;

/**
 * Created by suju on 16/8/24.
 */
public class PushMsgCommand extends AbstractCommand {

    private static final String TAG = "PushMsgCommand";

    public PushMsgCommand(Context context, IMMessageReceiver receiver, MessageEntity messageEntity) {
        super(context, "push", receiver, messageEntity);
    }

    @Override
    public void invoke() {
        IPushProcessor pushProcessor = null;
        String fromType = mMessageBody.getSource().getType();
        Log.d("flag--", "invoke: " + fromType);
        switch (fromType) {
            case Destination.ARTICLE:
                pushProcessor = new ArticlePushProcessor(mContext, mMessageBody);
                break;
            case Destination.GLOBAL:
                pushProcessor = new GlobalPushProcessor(mContext, mMessageBody);
                break;
//            case Destination.LESSON:
//                pushProcessor = new LessonPushProcessor(mContext, mMessageBody);
//                break;
            case Destination.COURSE:
            case Destination.CLASSROOM:
            case Destination.COUPON:
            case Destination.VIP:
            case Destination.TESTPAPER:
            case Destination.LESSON:
                int toId = 0;
                try {
                    toId = EdusohoApp.app.loginUser.id;
                } catch (Exception ex) {
                }
                mMessageBody.setDestination(new Destination(toId, Destination.USER));
                pushProcessor = new CourseNotificationPushProcessor(mContext, mMessageBody);
                break;
            case Destination.BATCH_NOTIFICATION:
                pushProcessor = new SchoolNotificationPushProcessor(mContext, mMessageBody);
                break;
            case Destination.USER:
                pushProcessor = new VerifiedNotificationPushProcessor(mContext, mMessageBody);
                break;
        }
        Log.d("flag--", "invoke: " + mMessageBody.getBody());
        if (pushProcessor != null) {
            String[] content = pushProcessor.getNotificationContent(mMessageBody.getBody());
            showNotification(content, pushProcessor.getNotifyIntent());
            pushProcessor.processor();
        }
    }
}
