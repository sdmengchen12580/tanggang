package com.edusoho.kuozhi.clean.module.main.study.exam;

import android.util.Log;

import com.edusoho.kuozhi.clean.api.MyStudyApi;
import com.edusoho.kuozhi.clean.bean.mystudy.ExamInfoModel;
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
 * Created by RexXiang on 2018/2/6.
 */

public class ExamInfoPresenter implements ExamInfoContract.Presenter {

    private ExamInfoContract.View mView;
    private String mExamId;
    private LifecycleProvider<ActivityEvent> mActivityLifeProvider;

    public ExamInfoPresenter(ExamInfoContract.View view, String examId) {
        mView = view;
        mExamId = examId;
        mActivityLifeProvider = NaviLifecycle.createActivityLifecycleProvider((BaseActivity) view);
    }

    @Override
    public void getExamInfo() {
        Log.e("测试代码: ", "token=" +EdusohoApp.app.token);
        mView.showProcessDialog(true);
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(MyStudyApi.class)
                .getExamInfo(mExamId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivityLifeProvider.<ExamInfoModel>bindToLifecycle())
                .subscribe(new SubscriberProcessor<ExamInfoModel>() {

                    @Override
                    public void onError(String message) {
                        super.onError(message);
                        mView.showProcessDialog(false);
                    }

                    @Override
                    public void onNext(ExamInfoModel examInfoModel) {
                        mView.showProcessDialog(false);
                        mView.refreshView(examInfoModel);
                    }
                });
    }

    @Override
    public void subscribe() {
        getExamInfo();
    }

    @Override
    public void unsubscribe() {

    }
}
