package com.edusoho.kuozhi.clean.module.main.mine.project;


import com.edusoho.kuozhi.clean.api.UserApi;
import com.edusoho.kuozhi.clean.bean.mystudy.TrainingRecordItem;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.module.base.BaseActivity;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.navi.NaviLifecycle;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ProjectPlanRecordPresenter implements ProjectPlanRecordContract.Presenter {
    private ProjectPlanRecordContract.View   mView;
    private LifecycleProvider<ActivityEvent> mLifecycleProvider;

    public ProjectPlanRecordPresenter(ProjectPlanRecordContract.View view) {
        this.mView = view;
        this.mLifecycleProvider = NaviLifecycle.createActivityLifecycleProvider((BaseActivity) mView);
    }

    @Override
    public void subscribe() {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(UserApi.class)
                .getMyTrainingRecords()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mLifecycleProvider.<List<TrainingRecordItem>>bindToLifecycle())
                .subscribe(new SubscriberProcessor<List<TrainingRecordItem>>() {
                    @Override
                    public void onNext(List<TrainingRecordItem> trainingRecordItems) {
                        mView.showTrainingRecords(trainingRecordItems);
                    }
                });
    }

    @Override
    public void unsubscribe() {

    }


}
