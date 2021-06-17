package com.edusoho.kuozhi.clean.module.main.study.project;

import com.edusoho.kuozhi.clean.api.MyStudyApi;
import com.edusoho.kuozhi.clean.bean.mystudy.OfflineActivitiesResult;
import com.edusoho.kuozhi.clean.bean.mystudy.OfflineActivityCategory;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.module.main.study.offlineactivity.OfflineListContract;
import com.edusoho.kuozhi.clean.module.main.study.offlineactivity.OfflineListPresenter;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.google.gson.JsonObject;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by RexXiang on 2018/3/21.
 */

public class ProjectPlanListPresenter extends OfflineListPresenter {

    public ProjectPlanListPresenter(OfflineListContract.View view) {
        super(view);
    }

    @Override
    public void getList(String status) {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(MyStudyApi.class)
                .getProjectList(status)
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
                        if (offlineActivitiesResult.getData().size() >= 0) {
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
                .getProjectCategory()
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
                .joinProject(activityId)
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
}
