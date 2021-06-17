package com.edusoho.kuozhi.clean.module.main.study.project;

import com.edusoho.kuozhi.clean.api.MyStudyApi;
import com.edusoho.kuozhi.clean.bean.mystudy.ProjectCourseItem;
import com.edusoho.kuozhi.clean.bean.mystudy.ProjectExamItem;
import com.edusoho.kuozhi.clean.bean.mystudy.ProjectOfflineCourseItem;
import com.edusoho.kuozhi.clean.bean.mystudy.ProjectOfflineExamItem;
import com.edusoho.kuozhi.clean.bean.mystudy.ProjectPlan;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.module.base.BaseActivity;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.google.gson.JsonObject;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.navi.NaviLifecycle;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by RexXiang on 2018/3/22.
 */

public class ProjectPlanEnrollDetailPresenter implements ProjectPlanEnrollDetailContract.Presenter {

    private ProjectPlanEnrollDetailContract.View mView;
    private LifecycleProvider<ActivityEvent> mActivityLifeProvider;
    private String mProjectId;

    public ProjectPlanEnrollDetailPresenter(ProjectPlanEnrollDetailContract.View view, String projectId) {
        mView = view;
        mProjectId = projectId;
        mActivityLifeProvider = NaviLifecycle.createActivityLifecycleProvider((BaseActivity) view);
    }

    @Override
    public void getProject() {
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
    public void getItemDetail(String targetId, final String targetType, final int index) {
        mView.showProcessDialog(true);
        switch (targetType) {
            case "course": {
                HttpUtils.getInstance()
                        .addTokenHeader(EdusohoApp.app.token)
                        .createApi(MyStudyApi.class)
                        .getProjectCourseItem(mProjectId, targetId, targetType)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(mActivityLifeProvider.<List<ProjectCourseItem>>bindToLifecycle())
                        .subscribe(new SubscriberProcessor<List<ProjectCourseItem>>(){
                            @Override
                            public void onError(String message) {
                                super.onError(message);
                            }

                            @Override
                            public void onNext(List<ProjectCourseItem> projectCourseItems) {
                                mView.expandChildView(projectCourseItems, targetType, index);
                            }
                        });
                break;
            }
            case "offline_course": {
                HttpUtils.getInstance()
                        .addTokenHeader(EdusohoApp.app.token)
                        .createApi(MyStudyApi.class)
                        .getProjectOfflineCourseItem(mProjectId, targetId, targetType)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(mActivityLifeProvider.<List<ProjectOfflineCourseItem>>bindToLifecycle())
                        .subscribe(new SubscriberProcessor<List<ProjectOfflineCourseItem>>(){

                            @Override
                            public void onError(String message) {
                                super.onError(message);
                            }

                            @Override
                            public void onNext(List<ProjectOfflineCourseItem> projectOfflineCourseItems) {
                                mView.expandChildView(projectOfflineCourseItems, targetType, index);
                            }
                        });
                break;
            }
            case "exam": {
                HttpUtils.getInstance()
                        .addTokenHeader(EdusohoApp.app.token)
                        .createApi(MyStudyApi.class)
                        .getProjectExamItem(mProjectId, targetId, targetType)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(mActivityLifeProvider.<ProjectExamItem>bindToLifecycle())
                        .subscribe(new SubscriberProcessor<ProjectExamItem>(){
                            @Override
                            public void onError(String message) {
                                super.onError(message);
                            }

                            @Override
                            public void onNext(ProjectExamItem projectExamItem) {
                                mView.expandChildView(projectExamItem, targetType, index);
                            }
                        });
                break;
            }
            default: {
                HttpUtils.getInstance()
                        .addTokenHeader(EdusohoApp.app.token)
                        .createApi(MyStudyApi.class)
                        .getProjectOfflineExamItem(mProjectId, targetId, targetType)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(mActivityLifeProvider.<ProjectOfflineExamItem>bindToLifecycle())
                        .subscribe(new SubscriberProcessor<ProjectOfflineExamItem>(){
                            @Override
                            public void onError(String message) {
                                super.onError(message);
                            }

                            @Override
                            public void onNext(ProjectOfflineExamItem projectOfflineExamItem) {
                                mView.expandChildView(projectOfflineExamItem, targetType, index);
                            }
                        });
            }
        }
    }

    @Override
    public void joinProject() {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(MyStudyApi.class)
                .joinProject(mProjectId)
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
        getProject();
    }

    @Override
    public void unsubscribe() {

    }
}
