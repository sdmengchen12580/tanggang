package com.edusoho.kuozhi.clean.module.main.study.offlineactivity;

import com.edusoho.kuozhi.clean.api.MyStudyApi;
import com.edusoho.kuozhi.clean.bean.mystudy.OfflineActivitiesResult;
import com.edusoho.kuozhi.clean.bean.mystudy.OfflineActivityCategory;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.module.base.BaseActivity;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.navi.NaviLifecycle;

import com.google.gson.JsonObject;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by RexXiang on 2018/1/25.
 */

public class OfflineListPresenter implements OfflineListContract.Presenter {

    protected OfflineListContract.View mView;
    protected LifecycleProvider<ActivityEvent> mActivityLifeProvider;
    protected List<OfflineActivitiesResult.DataBean> mList;

    public OfflineListPresenter(OfflineListContract.View view) {
        mView = view;
        mActivityLifeProvider = NaviLifecycle.createActivityLifecycleProvider((BaseActivity) view);
    }

    @Override
    public void getList(String status) {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(MyStudyApi.class)
                .getOfflineActivitiesResult(status)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivityLifeProvider.<OfflineActivitiesResult>bindToLifecycle())
                .subscribe(new SubscriberProcessor<OfflineActivitiesResult>() {
                    @Override
                    public void onError(String message) {
                        super.onError(message);
                    }

                    @Override
                    public void onNext(OfflineActivitiesResult offlineActivitiesResult) {
                        if (offlineActivitiesResult.getData().size() > 0) {
                            getCategoryList();
                            mList = offlineActivitiesResult.getData();
                        }
                    }

                });
    }

    @Override
    public void getCategoryList() {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(MyStudyApi.class)
                .getOfflineActivitiesCategory()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivityLifeProvider.<List<OfflineActivityCategory>>bindToLifecycle())
                .subscribe(new SubscriberProcessor<List<OfflineActivityCategory>>() {
                    @Override
                    public void onError(String message) {
                        super.onError(message);
                    }

                    @Override
                    public void onNext(List<OfflineActivityCategory> offlineActivityCategories) {
                        mView.refreshView(mList, offlineActivityCategories);
                    }

                    @Override
                    public void onCompleted() {
                        mView.refreshCompleted();
                    }
                });
    }

    @Override
    public void joinActivity(String activityId) {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(MyStudyApi.class)
                .joinOfflineActivity(activityId)
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
        mView.clearData();
        getList("ongoing");
    }

    @Override
    public void unsubscribe() {

    }
}
