package com.edusoho.kuozhi.clean.module.main.news.notification.center;


import com.edusoho.kuozhi.imserver.IMClient;
import com.edusoho.kuozhi.imserver.entity.ConvEntity;
import com.edusoho.kuozhi.imserver.entity.message.Destination;

import java.util.ArrayList;
import java.util.List;

public class NotificationCenterPresenter implements NotificationCenterContract.Presenter {

    private NotificationCenterContract.View mView;
    private final String[] COURSE_NOTIFICATION_TYPES       = {Destination.NOTIFY, Destination.TESTPAPER};
    private final String[] ANNOUNCEMENT_NOTIFICATION_TYPES = {Destination.GLOBAL, Destination.BATCH_NOTIFICATION};
    private final String[] FRIEND_VERIFIED_TYPES           = {Destination.USER};

    public NotificationCenterPresenter(NotificationCenterContract.View view) {
        this.mView = view;
    }

    @Override
    public void subscribe() {
        List<ConvEntity> list = new ArrayList<>();

        ConvEntity notifyEntity = IMClient.getClient().getConvManager().getNotificationCenterEntity(IMClient.getClient().getClientId(), COURSE_NOTIFICATION_TYPES);
        if (notifyEntity == null) {
            notifyEntity = new ConvEntity();
            notifyEntity.setType(Destination.NOTIFY);
            notifyEntity.setConvNo(Destination.NOTIFY);
        }
        list.add(notifyEntity);

        ConvEntity announcementEntity = IMClient.getClient().getConvManager().getAnnouncementEntity(IMClient.getClient().getClientId(), ANNOUNCEMENT_NOTIFICATION_TYPES);
        if (announcementEntity == null) {
            announcementEntity = new ConvEntity();
            announcementEntity.setType(Destination.GLOBAL);
            announcementEntity.setConvNo(Destination.GLOBAL);
        }
        list.add(announcementEntity);

        ConvEntity verifiedEntity = IMClient.getClient().getConvManager().getFriendVerifiedEntity(IMClient.getClient().getClientId(), FRIEND_VERIFIED_TYPES);
        if (verifiedEntity == null) {
            verifiedEntity = new ConvEntity();
            verifiedEntity.setType(Destination.USER);
            verifiedEntity.setConvNo(Destination.USER);
        }
        list.add(verifiedEntity);

        mView.initNotificationList(list);
    }

    @Override
    public void unsubscribe() {

    }
}
