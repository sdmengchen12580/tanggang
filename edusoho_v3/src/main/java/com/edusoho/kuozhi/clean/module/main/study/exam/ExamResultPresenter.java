package com.edusoho.kuozhi.clean.module.main.study.exam;

import com.edusoho.kuozhi.clean.api.MyStudyApi;
import com.edusoho.kuozhi.clean.bean.mystudy.ExamResultModel;
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
 * Created by RexXiang on 2018/2/9.
 */

public class ExamResultPresenter implements ExamResultContract.Presenter{

    private LifecycleProvider<ActivityEvent> mActivityLifeProvider;
    private String examResultId;
    private ExamResultContract.View mView;

    public ExamResultPresenter(ExamResultContract.View view, String resultId) {
        mView = view;
        examResultId = resultId;
        mActivityLifeProvider = NaviLifecycle.createActivityLifecycleProvider((BaseActivity) view);
    }


    @Override
    public void getExamResult() {
        mView.showProcessDialog(true);
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(MyStudyApi.class)
                .getExamResult(examResultId, "all")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivityLifeProvider.<ExamResultModel>bindToLifecycle())
                .subscribe(new SubscriberProcessor<ExamResultModel>() {

                    @Override
                    public void onError(String message) {
                        mView.showProcessDialog(false);
                        super.onError(message);
                    }

                    @Override
                    public void onNext(ExamResultModel examResultModel) {
                        mView.showProcessDialog(false);
                        mView.refreshView(examResultModel);
                    }
                });
    }

    @Override
    public void subscribe() {
        getExamResult();
    }

    @Override
    public void unsubscribe() {

    }
}
