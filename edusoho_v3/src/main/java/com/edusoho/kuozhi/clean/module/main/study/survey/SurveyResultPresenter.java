package com.edusoho.kuozhi.clean.module.main.study.survey;

import com.edusoho.kuozhi.clean.api.MyStudyApi;
import com.edusoho.kuozhi.clean.bean.mystudy.SurveyModel;
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

public class SurveyResultPresenter implements SurveyResultContract.Presenter {


    private SurveyResultContract.View mView;
    private LifecycleProvider<ActivityEvent> mActivityLifeProvider;
    private String surveyId;


    public SurveyResultPresenter(SurveyResultContract.View view, String id) {
        mView = view;
        surveyId = id;
        mActivityLifeProvider = NaviLifecycle.createActivityLifecycleProvider((BaseActivity) view);
    }

    @Override
    public void getSurveyResult() {
        mView.showProcessDialog(true);
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(MyStudyApi.class)
                .getSurveyResult(surveyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivityLifeProvider.<SurveyModel>bindToLifecycle())
                .subscribe(new SubscriberProcessor<SurveyModel>() {
                    @Override
                    public void onError(String message) {
                        mView.showToast(message);
                        mView.showProcessDialog(false);
                    }

                    @Override
                    public void onNext(SurveyModel surveyResultModel) {
                        mView.showProcessDialog(false);
                        mView.refreshView(surveyResultModel);
                    }
                });
    }

    @Override
    public void subscribe() {
        getSurveyResult();
    }

    @Override
    public void unsubscribe() {

    }
}

