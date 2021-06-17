package com.edusoho.kuozhi.clean.module.main.study.survey;

import com.edusoho.kuozhi.clean.api.MyStudyApi;
import com.edusoho.kuozhi.clean.bean.mystudy.SurveyAnswersModel;
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
 * Created by RexXiang on 2018/2/1.
 */

public class SurveyAnswerListPresenter implements SurveyAnswerListContract.Presenter {

    private SurveyAnswerListContract.View mView;
    private LifecycleProvider<ActivityEvent> mActivityLifeProvider;
    private String mSurveyId;
    private String mQuestionId;

    public SurveyAnswerListPresenter(SurveyAnswerListContract.View view, String surveyId, String questionId) {
        mView = view;
        mSurveyId = surveyId;
        mQuestionId = questionId;
        mActivityLifeProvider = NaviLifecycle.createActivityLifecycleProvider((BaseActivity) view);
    }


    @Override
    public void getQuestionnaireAnswers() {
        mView.showProcessDialog(true);
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(MyStudyApi.class)
                .getQuestionnaireAnswers(mSurveyId, mQuestionId, 0, 20)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivityLifeProvider.<SurveyAnswersModel>bindToLifecycle())
                .subscribe(new SubscriberProcessor<SurveyAnswersModel>() {
                    @Override
                    public void onError(String message) {
                        mView.showProcessDialog(false);
                        mView.showToast(message);
                    }

                    @Override
                    public void onNext(SurveyAnswersModel answersModel) {
                        mView.showProcessDialog(false);
                        mView.refreshView(answersModel);
                    }
                });
    }

    @Override
    public void getMoreQuestionnaireAnswers(int limit, int offset) {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(MyStudyApi.class)
                .getQuestionnaireAnswers(mSurveyId, mQuestionId, offset, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivityLifeProvider.<SurveyAnswersModel>bindToLifecycle())
                .subscribe(new SubscriberProcessor<SurveyAnswersModel>() {
                    @Override
                    public void onError(String message) {
                        mView.showToast(message);
                    }

                    @Override
                    public void onNext(SurveyAnswersModel answersModel) {
                        mView.loadMoreData(answersModel);
                    }

                    @Override
                    public void onCompleted() {
                        mView.refreshCompleted();
                    }
                });
    }

    @Override
    public void subscribe() {
        getQuestionnaireAnswers();
    }

    @Override
    public void unsubscribe() {

    }
}
