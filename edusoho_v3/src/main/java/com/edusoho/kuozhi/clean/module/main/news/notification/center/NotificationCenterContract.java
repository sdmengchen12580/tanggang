package com.edusoho.kuozhi.clean.module.main.news.notification.center;


import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;
import com.edusoho.kuozhi.imserver.entity.ConvEntity;

import java.util.List;

public interface NotificationCenterContract {

    interface View extends BaseView<Presenter> {
        void initNotificationList(List<ConvEntity> list);
    }

    interface Presenter extends BasePresenter {

    }
}
