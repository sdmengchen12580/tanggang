package com.edusoho.kuozhi.v3.service.message;

import android.content.Context;

import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.imserver.entity.message.MessageBody;
import com.edusoho.kuozhi.imserver.listener.IMMessageReceiver;
import com.edusoho.kuozhi.v3.util.PushUtil;

/**
 * Created by 菊 on 2016/4/25.
 */
public class CommandFactory {

    public static AbstractCommand create(Context context, String cmd, IMMessageReceiver receiver, MessageEntity messageEntity) {
        MessageBody mMessageBody = new MessageBody(messageEntity);
        String toType = mMessageBody.getDestination().getType();
        String bodyType = mMessageBody.getType();

        switch (bodyType) {
            case PushUtil.ChatMsgType.AUDIO:
            case PushUtil.ChatMsgType.IMAGE:
            case PushUtil.ChatMsgType.TEXT:
            case PushUtil.ChatMsgType.MULTI:
                if (PushUtil.ChatUserType.CLASSROOM.equals(toType)) {
                    return new DiscussMsgCommand(context, cmd, receiver, mMessageBody);
                } else if (PushUtil.ChatUserType.USER.equals(toType)) {
                    return new MessageCommand(context, cmd, receiver, mMessageBody);
                } else if (PushUtil.ChatUserType.COURSE.equals(toType)) {
                    return new DiscussMsgCommand(context, cmd, receiver, mMessageBody);
                }
                break;
            case PushUtil.ChatMsgType.PUSH:
                return new PushMsgCommand(context, receiver, messageEntity);
        }
        return new EmptyCommand(context, receiver, mMessageBody);
    }

    private static class EmptyCommand extends AbstractCommand {
        public EmptyCommand(Context context, IMMessageReceiver receiver, MessageBody messageBody) {
            super(context, "", receiver, messageBody);
        }

        @Override
        public void invoke() {
        }
    }
}
