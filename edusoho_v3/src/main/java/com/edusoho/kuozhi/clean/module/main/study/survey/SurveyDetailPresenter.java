package com.edusoho.kuozhi.clean.module.main.study.survey;

import com.edusoho.kuozhi.clean.api.CourseApi;
import com.edusoho.kuozhi.clean.api.MyStudyApi;
import com.edusoho.kuozhi.clean.bean.TaskEvent;
import com.edusoho.kuozhi.clean.bean.mystudy.EvaluationAnswer;
import com.edusoho.kuozhi.clean.bean.mystudy.SurveyAnswer;
import com.edusoho.kuozhi.clean.bean.mystudy.SurveyModel;
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
 * Created by RexXiang on 2018/1/31.
 */

public class SurveyDetailPresenter implements SurveyDetailContract.Presenter {


    private SurveyDetailContract.View        mView;
    private LifecycleProvider<ActivityEvent> mActivityLifeProvider;
    private String                           surveyId;

    public SurveyDetailPresenter(SurveyDetailContract.View view, String id) {
        mView = view;
        surveyId = id;
        mActivityLifeProvider = NaviLifecycle.createActivityLifecycleProvider((BaseActivity) view);
    }

    @Override
    public void doSurvey(String surveyId) {
        mView.showProcessDialog(true);
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(MyStudyApi.class)
                .doSurvey(surveyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivityLifeProvider.<SurveyModel>bindToLifecycle())
                .subscribe(new SubscriberProcessor<SurveyModel>() {
                    @Override
                    public void onError(String message) {
                        mView.showToast(message);
                        mView.showProcessDialog(false);
                        mView.close();
                    }

                    @Override
                    public void onNext(SurveyModel surveyModel) {
                        mView.showProcessDialog(false);
                        mView.refreshView(surveyModel);
                    }
                });
    }

    @Override
    public void submitSurvey(String surveyResultId, SurveyAnswer answer) {
        mView.showProcessDialog(true);
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(MyStudyApi.class)
                .submitSurvey(surveyResultId, answer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivityLifeProvider.<JsonObject>bindToLifecycle())
                .subscribe(new SubscriberProcessor<JsonObject>() {
                    @Override
                    public void onError(String message) {
                        mView.showProcessDialog(false);
                        mView.showToast("提交失败");
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        mView.showProcessDialog(false);
                        if (jsonObject != null && jsonObject.get("submitted").getAsBoolean()) {
                            mView.showToast("提交成功");
                            mView.sendBroad();
                        }
                    }
                });
    }

    @Override
    public void submitEvaluation(String surveyResultId, EvaluationAnswer answer, final int courseId, final int taskId) {
        mView.showProcessDialog(true);
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(MyStudyApi.class)
                .submitEvaluation(surveyResultId, answer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivityLifeProvider.<JsonObject>bindToLifecycle())
                .subscribe(new SubscriberProcessor<JsonObject>() {
                    @Override
                    public void onError(String message) {
                        mView.showProcessDialog(false);
                        mView.showToast("提交失败");
                    }

                    @Override
                    public void onCompleted() {
                        mView.sendBroad();
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        mView.showProcessDialog(false);
                        if (jsonObject != null && jsonObject.get("submitted").getAsBoolean()) {
                            mView.showToast("提交成功");
                            finishEvaluationTask(courseId, taskId);
                        }
                    }
                });
    }

    private void finishEvaluationTask(int courseId, int taskId) {
        if (courseId == 0 || taskId == 0) {
            return;
        }
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(CourseApi.class)
                .setCourseTaskFinish(courseId, taskId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SubscriberProcessor<TaskEvent>() {
                    @Override
                    public void onError(String message) {
                        super.onError(message);
                    }

                    @Override
                    public void onNext(TaskEvent taskEvent) {
                        super.onNext(taskEvent);
                    }
                });

    }

    @Override
    public void subscribe() {
        doSurvey(surveyId);
    }

    @Override
    public void unsubscribe() {

    }
}
