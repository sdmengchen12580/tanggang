package com.edusoho.kuozhi.clean.utils.biz;


import android.content.Context;

import com.edusoho.kuozhi.imserver.IMClient;
import com.edusoho.kuozhi.imserver.entity.ConvEntity;
import com.edusoho.kuozhi.imserver.entity.message.MessageBody;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.model.bal.push.Notify;
import com.edusoho.kuozhi.v3.ui.fragment.NewsFragment;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.PushUtil;

/**
 * 通知中心-课程通知处理
 */
public class CourseNotificationPushHelper {

    private Context     mContext;
    private MessageBody mMessageBody;

    private CourseNotificationPushHelper(Builder builder) {
        this.mContext = builder.mContext;
        this.mMessageBody = builder.mMessageBody;
    }

    public void updateConvEntity(Notify notify) {
        ConvEntity convEntity = IMClient.getClient().getConvManager().getConvByConvNo(PushUtil.ChatUserType.NOTIFY);
        if (convEntity == null) {
            convEntity = createConvEntityFromNofity(notify);
        }
        if (convEntity.getCreatedTime() == 0) {
            convEntity.setCreatedTime(notify.getCreatedTime());
        }
        convEntity.setLaterMsg(mMessageBody.toJson());
        convEntity.setUpdatedTime(notify.getCreatedTime());
        convEntity.setUnRead(convEntity.getUnRead() + 1);
        convEntity.setUid(IMClient.getClient().getClientId());
        IMClient.getClient().getConvManager().updateConvByConvNo(convEntity);
        MessageEngine.getInstance().sendMsgToTaget(Const.REFRESH_LIST, null, NewsFragment.class);
    }

    private ConvEntity createConvEntityFromNofity(Notify notify) {
        ConvEntity convEntity = new ConvEntity();
        convEntity.setType(PushUtil.ChatUserType.NOTIFY);
        convEntity.setConvNo(PushUtil.ChatUserType.NOTIFY);
        convEntity.setUpdatedTime(notify.getCreatedTime());
        convEntity.setAvatar("drawable://" + mContext.getResources().getIdentifier("icon", "drawable", mContext.getPackageName()));
        convEntity.setTargetId(0);
        convEntity.setTargetName("通知中心");
        convEntity.setUnRead(0);
        convEntity.setLaterMsg(mMessageBody.toJson());
        convEntity.setUid(IMClient.getClient().getClientId());
        IMClient.getClient().getConvManager().createConv(convEntity);

        return convEntity;
    }

    public static class Builder {
        private Context     mContext;
        private MessageBody mMessageBody;

        public Builder init(Context context) {
            this.mContext = context;
            return this;
        }

        public Builder setMessageBody(MessageBody messageBody) {
            this.mMessageBody = messageBody;
            return this;
        }

        public CourseNotificationPushHelper build() {
            return new CourseNotificationPushHelper(this);
        }
    }
}
