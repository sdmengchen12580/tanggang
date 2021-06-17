package com.edusoho.kuozhi.clean.module.main.study.offlineactivity;

import com.edusoho.kuozhi.clean.api.MyStudyApi;
import com.edusoho.kuozhi.clean.bean.mystudy.ActivityMembers;
import com.edusoho.kuozhi.clean.bean.mystudy.OfflineActivitiesResult;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.module.base.BaseActivity;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.google.gson.JsonObject;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.navi.NaviLifecycle;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by RexXiang on 2018/1/29.
 */

public class OfflineActivityDetailPresenter implements OfflineActivityDetailContract.Presenter {

    private OfflineActivityDetailContract.View mView;
    private LifecycleProvider<ActivityEvent> mActivityLifeProvider;
    private String mActivityId;
    private OfflineActivitiesResult.DataBean mDataBean;


    public OfflineActivityDetailPresenter(OfflineActivityDetailContract.View view, String activityId) {
        mView = view;
        mActivityId = activityId;
        mActivityLifeProvider = NaviLifecycle.createActivityLifecycleProvider((BaseActivity) view);
    }

    @Override
    public void getOfflineActivityDetail(String activityId) {
        mView.showProcessDialog(true);
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(MyStudyApi.class)
                .getActivityDetail(activityId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivityLifeProvider.<OfflineActivitiesResult.DataBean>bindToLifecycle())
                .subscribe(new SubscriberProcessor<OfflineActivitiesResult.DataBean>() {
                    @Override
                    public void onError(String message) {
                        mView.showProcessDialog(false);
                        mView.showToast(message);
                    }

                    @Override
                    public void onNext(OfflineActivitiesResult.DataBean dataBean) {
                        mDataBean = dataBean;
                        getOfflineActivityMembers(mActivityId);
                    }

                });
    }

    @Override
    public void getOfflineActivityMembers(final String activityId) {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(MyStudyApi.class)
                .getActivityMembers(activityId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivityLifeProvider.<ActivityMembers>bindToLifecycle())
                .subscribe(new SubscriberProcessor<ActivityMembers>() {
                    @Override
                    public void onError(String message) {
                        mView.showProcessDialog(false);
                        mView.showToast(String.format("%s, error api:offline_activity/%s/members", message, activityId));
                    }

                    @Override
                    public void onNext(ActivityMembers activityMembers) {
                        mView.showProcessDialog(false);
                        mView.refreshView(mDataBean, activityMembers);
                    }

                });
    }

    @Override
    public void joinActivity(String id) {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(MyStudyApi.class)
                .joinOfflineActivity(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivityLifeProvider.<JsonObject>bindToLifecycle())
                .subscribe(new SubscriberProcessor<JsonObject>() {
                    @Override
                    public void onError(String message) {
                        super.onError(message);
                        mView.showProcessDialog(false);
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        mView.showProcessDialog(false);
                        if (jsonObject != null && jsonObject.get("result").getAsBoolean()) {
                            mView.showToast("报名成功");
                            subscribe();
                        }
                    }
                });
    }

    @Override
    public void subscribe() {
        getOfflineActivityDetail(mActivityId);
    }

    @Override
    public void unsubscribe() {

    }
}
