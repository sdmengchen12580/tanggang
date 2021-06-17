package com.edusoho.kuozhi.clean.module.main.study.project;

import com.edusoho.kuozhi.clean.api.MyStudyApi;
import com.edusoho.kuozhi.clean.bean.mystudy.ProjectPlan;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.module.base.BaseActivity;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.navi.NaviLifecycle;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by RexXiang on 2018/3/20.
 */

public class ProjectPlanDetailPresenter implements ProjectPlanDetailContract.Presenter {

    private ProjectPlanDetailContract.View   mView;
    private LifecycleProvider<ActivityEvent> mActivityLifeProvider;
    private String                           mProjectId;

    public ProjectPlanDetailPresenter(ProjectPlanDetailContract.View view, String projectId) {
        mView = view;
        mProjectId = projectId;
        mActivityLifeProvider = NaviLifecycle.createActivityLifecycleProvider((BaseActivity) view);
    }

    @Override
    public void getProjectPlanDetail() {
        mView.showProcessDialog(true);
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(MyStudyApi.class)
                .getProjectPlan(mProjectId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivityLifeProvider.<ProjectPlan>bindToLifecycle())
                .subscribe(new SubscriberProcessor<ProjectPlan>() {
                    @Override
                    public void onError(String message) {
                        mView.showProcessDialog(false);
                        super.onError(message);
                    }

                    @Override
                    public void onNext(ProjectPlan projectPlan) {
                        mView.showProcessDialog(false);
                        mView.refreshView(projectPlan);
                    }
                });

    }

    @Override
    public void subscribe() {
        mView.clearData();
        getProjectPlanDetail();
    }

    @Override
    public void unsubscribe() {

    }

}
