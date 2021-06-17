package com.edusoho.kuozhi.clean.module.main.news.notification.course;


import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.module.base.BaseActivity;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.provider.AppSettingProvider;
import com.edusoho.kuozhi.v3.model.bal.push.Notify;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.service.message.push.ESDbManager;
import com.edusoho.kuozhi.v3.service.message.push.NotifyDbHelper;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.navi.NaviLifecycle;

import java.util.List;

import rx.Observable;

public class CourseNotificationPresenter implements CourseNotificationContract.Presenter {

    private CourseNotificationContract.View  mView;
    private LifecycleProvider<ActivityEvent> mActivityLifeProvider;

    public CourseNotificationPresenter(CourseNotificationContract.View view) {
        this.mView = view;
        mActivityLifeProvider = NaviLifecycle.createActivityLifecycleProvider((BaseActivity) view);
    }

    @Override
    public void subscribe() {
        getNotifications(0, 10).subscribe(new SubscriberProcessor<List<Notify>>() {
            @Override
            public void onNext(List<Notify> notifies) {
                mView.addNotificationList(notifies);
            }

            @Override
            public void onError(String message) {
                mView.showToast(message);
                mView.addNotificationList(null);
            }
        });
    }

    @Override
    public void unsubscribe() {

    }

    @Override
    public void showNotifications(int offset, int limit) {
        getNotifications(offset, limit)
                .subscribe(new SubscriberProcessor<List<Notify>>() {
                    @Override
                    public void onNext(List<Notify> notifies) {
                        mView.addNotificationList(notifies);
                    }
                });
    }

    private Observable<List<Notify>> getNotifications(int offset, int limit) {
        School school = getAppSettingProvider().getCurrentSchool();
        NotifyDbHelper notifyDbHelper = new NotifyDbHelper(EdusohoApp.app.mContext, new ESDbManager(EdusohoApp.app.mContext, school.getDomain()));
        List<Notify> notifyList = notifyDbHelper.getNofityListByUserId(EdusohoApp.app.loginUser.id, offset, limit);
        return Observable.just(notifyList);


//        return HttpUtils.getInstance()
//                .addTokenHeader(EdusohoApp.app.token)
//                .createApi(CommonApi.class)
//                .getNotifications(type, 0, offset, limit)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .compose(mActivityLifeProvider.<DataPageResult<Notification>>bindToLifecycle());
    }

    public AppSettingProvider getAppSettingProvider() {
        return FactoryManager.getInstance().create(AppSettingProvider.class);
    }
}
