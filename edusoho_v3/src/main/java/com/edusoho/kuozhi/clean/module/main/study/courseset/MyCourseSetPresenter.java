package com.edusoho.kuozhi.clean.module.main.study.courseset;

import com.edusoho.kuozhi.clean.api.MyStudyApi;
import com.edusoho.kuozhi.clean.bean.DataPageResult;
import com.edusoho.kuozhi.clean.bean.StudyCourse;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.module.base.BaseFragment;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.navi.NaviLifecycle;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by RexXiang on 2018/1/18.
 */

public class MyCourseSetPresenter implements MyCourseSetContract.Presenter {

    private MyCourseSetContract.View mView;

    private final LifecycleProvider<FragmentEvent> mFragmentLifeProvider;


    public MyCourseSetPresenter(MyCourseSetContract.View view) {
        mView = view;
        mFragmentLifeProvider = NaviLifecycle.createFragmentLifecycleProvider((BaseFragment) view);

    }

    @Override
    public void getCourseSet(final int status) {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(MyStudyApi.class)
                .getMyStudyCourse(0, 1000, status)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mFragmentLifeProvider.<DataPageResult<StudyCourse>>bindToLifecycle())
                .subscribe(new SubscriberProcessor<DataPageResult<StudyCourse>>() {
                    @Override
                    public void onError(String message) {
                        super.onError(message);
                    }

                    @Override
                    public void onCompleted() {
                        mView.refreshCompleted();
                    }

                    @Override
                    public void onNext(DataPageResult<StudyCourse> studyCourseDataPageResult) {
                        mView.refreshView(studyCourseDataPageResult.data, status);
                    }
                });
    }

    @Override
    public void subscribe() {
        mView.clearData();
        getCourseSet(0);
    }


    @Override
    public void unsubscribe() {

    }
}
