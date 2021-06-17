package com.edusoho.kuozhi.clean.module.main.study.exam;

import com.edusoho.kuozhi.clean.api.MyStudyApi;
import com.edusoho.kuozhi.clean.bean.mystudy.ExamAnswer;
import com.edusoho.kuozhi.clean.bean.mystudy.ExamModel;
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
 * Created by RexXiang on 2018/2/6.
 */

public class ExamPresenter implements ExamContract.Presenter {

    private ExamContract.View mView;
    private String mExamId;
    private LifecycleProvider<ActivityEvent> mActivityLifeProvider;

    public ExamPresenter(ExamContract.View view, String examId) {
        mView = view;
        mExamId = examId;
        mActivityLifeProvider = NaviLifecycle.createActivityLifecycleProvider((BaseActivity) view);
    }

    @Override
    public void getExam() {
        mView.showProcessDialog(true);
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(MyStudyApi.class)
                .getExam(mExamId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivityLifeProvider.<ExamModel>bindToLifecycle())
                .subscribe(new SubscriberProcessor<ExamModel>() {

                    @Override
                    public void onError(String message) {
                        mView.showProcessDialog(false);
                        mView.showToast(message);
                        mView.sendBroad();
                    }

                    @Override
                    public void onNext(ExamModel examModel) {
                        mView.showProcessDialog(false);
                        mView.refreshView(examModel);
                    }
                });
    }

    @Override
    public void submitExam(String examResultId, ExamAnswer answer) {
        mView.showProcessDialog(true);
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(MyStudyApi.class)
                .submitExam(examResultId, answer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivityLifeProvider.<JsonObject>bindToLifecycle())
                .subscribe(new SubscriberProcessor<JsonObject>() {

                    @Override
                    public void onError(String message) {
                        mView.showProcessDialog(false);
                        mView.showToast(message);
                    }

                    @Override
                    public void onNext(JsonObject examModel) {
                        mView.showProcessDialog(false);
                        mView.timeFinishSubmit(examModel);
                    }
                });
    }

    @Override
    public void subscribe() {
        getExam();
    }

    @Override
    public void unsubscribe() {

    }
}
