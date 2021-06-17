package com.edusoho.kuozhi.v3.service.message;

import android.content.Context;

import com.edusoho.kuozhi.clean.push.VerifiedNotificationPushProcessor;
import com.edusoho.kuozhi.clean.utils.GsonUtils;
import com.edusoho.kuozhi.clean.utils.biz.NotificationHelper;
import com.edusoho.kuozhi.imserver.IMClient;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.imserver.entity.message.MessageBody;
import com.edusoho.kuozhi.imserver.listener.IMMessageReceiver;
import com.edusoho.kuozhi.v3.model.provider.IMProvider;
import com.edusoho.kuozhi.v3.service.message.push.IPushProcessor;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

/**
 * Created by 菊 on 2016/4/25.
 */
public class MessageCommand extends AbstractCommand {

    public MessageCommand(Context context, String cmd, IMMessageReceiver receiver, MessageBody messageBody) {
        super(context, cmd, receiver, messageBody);
    }

    @Override
    public void invoke() {
        LinkedTreeMap<String, String> body = GsonUtils.parseJson(mMessageBody.getBody(), new TypeToken<LinkedTreeMap<String, String>>() {
        });
        if (body == null) {
            if (!isInBlackList(mMessageBody.getConvNo())
                    && !IMClient.getClient().isHandleMessageInFront(Destination.USER, mMessageBody.getConvNo())) {
                showNotification(getNotificationContent(), getNotifyIntent());
            }

            String type = mMessageBody.getSource().getType();
            int targetId = mMessageBody.getSource().getId();
            new IMProvider(mContext).updateConvEntityByMessage(mMessageBody.getConvNo(), type, targetId);
        } else if (body.get("type") != null) {
            IPushProcessor pushProcessor = null;
            switch (body.get("type")) {
                case NotificationHelper.USER_FOLLOW:
                case NotificationHelper.USER_UNFOLLOW:
                    pushProcessor = new VerifiedNotificationPushProcessor(mContext, mMessageBody);
                    break;
            }
            if (pushProcessor != null) {
                String[] content = pushProcessor.getNotificationContent(mMessageBody.getBody());
                showNotification(content, pushProcessor.getNotifyIntent());
                pushProcessor.processor();
            }
        }
    }
}
