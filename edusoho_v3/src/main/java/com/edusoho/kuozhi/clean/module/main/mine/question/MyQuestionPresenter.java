package com.edusoho.kuozhi.clean.module.main.mine.question;

import com.edusoho.kuozhi.clean.api.UserApi;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.module.base.BaseFragment;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.bal.thread.MyThreadEntity;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.navi.NaviLifecycle;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DF on 2017/5/11.
 */


class MyQuestionPresenter implements MyQuestionContract.Presenter {

    private       MyQuestionContract.View          mView;
    private final LifecycleProvider<FragmentEvent> mFragmentLifeProvider;

    MyQuestionPresenter(MyQuestionContract.View view) {
        this.mView = view;
        mFragmentLifeProvider = NaviLifecycle.createFragmentLifecycleProvider((BaseFragment) view);
    }

    @Override
    public void requestAskData() {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .baseOnApi()
                .createApi(UserApi.class)
                .getMyAskThread(0, 1000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mFragmentLifeProvider.<MyThreadEntity[]>bindToLifecycle())
                .subscribe(new SubscriberProcessor<MyThreadEntity[]>() {
                    @Override
                    public void onError(String e) {
                        mView.hideSwp();
                    }

                    @Override
                    public void onNext(MyThreadEntity[] myThreadEntities) {
                        mView.hideSwp();
                        if (myThreadEntities != null) {
                            mView.showAskComplete(myThreadEntities);
                        }
                    }
                });

    }

    @Override
    public void requestAnswerData() {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .baseOnApi()
                .createApi(UserApi.class)
                .getMyAnswerThread(0, 1000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mFragmentLifeProvider.<MyThreadEntity[]>bindToLifecycle())
                .subscribe(new SubscriberProcessor<MyThreadEntity[]>() {
                    @Override
                    public void onError(String e) {
                        mView.hideSwp();
                    }

                    @Override
                    public void onNext(MyThreadEntity[] myThreadEntities) {
                        mView.hideSwp();
                        if (myThreadEntities != null) {
                            mView.showAnswerComplete(myThreadEntities);
                        }
                    }
                });
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {

    }

}
