package com.edusoho.kuozhi.clean.module.main.news.notification.course;


import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;
import com.edusoho.kuozhi.v3.model.bal.push.Notify;

import java.util.List;

public interface CourseNotificationContract {

    interface View extends BaseView<Presenter> {

        void addNotificationList(List<Notify> list);
    }

    interface Presenter extends BasePresenter {

        void showNotifications(int offset, int limit);
    }
}
