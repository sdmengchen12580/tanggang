package com.edusoho.kuozhi.clean.module.main.mine.study;

import com.edusoho.kuozhi.clean.api.UserApi;
import com.edusoho.kuozhi.clean.bean.Classroom;
import com.edusoho.kuozhi.clean.bean.DataPageResult;
import com.edusoho.kuozhi.clean.bean.StudyCourse;
import com.edusoho.kuozhi.clean.bean.innerbean.Study;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.module.base.BaseFragment;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.navi.NaviLifecycle;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DF on 2017/5/11.
 */

public class MyStudyPresenter implements MyStudyContract.Presenter {

    private MyStudyContract.View mView;

    private final LifecycleProvider<FragmentEvent> mFragmentLifeProvider;

    public MyStudyPresenter(MyStudyContract.View view) {
        this.mView = view;
        mFragmentLifeProvider = NaviLifecycle.createFragmentLifecycleProvider((BaseFragment) view);
    }

    @Override
    public void getMyStudyCourse() {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(UserApi.class)
                .getMyStudyCourse(0, 1000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mFragmentLifeProvider.<DataPageResult<StudyCourse>>bindToLifecycle())
                .subscribe(new SubscriberProcessor<DataPageResult<StudyCourse>>() {
                    @Override
                    public void onError(String e) {
                        mView.hideSwp();
                    }

                    @Override
                    public void onNext(DataPageResult<StudyCourse> studyCourseDataPageResult) {
                        mView.hideSwp();
                        if (studyCourseDataPageResult != null && studyCourseDataPageResult.data != null) {
                            mView.showStudyCourseComplete(studyCourseDataPageResult.data);
                        }
                    }
                });
    }

    @Override
    public void getMyStudyLiveCourseSet() {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(UserApi.class)
                .getMyStudyLiveCourseSet(0, 1000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mFragmentLifeProvider.<List<Study>>bindToLifecycle())
                .subscribe(new SubscriberProcessor<List<Study>>() {
                    @Override
                    public void onError(String e) {
                        mView.hideSwp();
                    }

                    @Override
                    public void onNext(List<Study> studies) {
                        mView.hideSwp();
                        if (studies != null) {
                            mView.showLiveCourseComplete(studies);
                        }
                    }
                });
    }

    @Override
    public void getMyStudyClassRoom() {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(UserApi.class)
                .getMyClassrooms(0, 1000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mFragmentLifeProvider.<List<Classroom>>bindToLifecycle())
                .subscribe(new SubscriberProcessor<List<Classroom>>() {
                    @Override
                    public void onError(String e) {
                        mView.hideSwp();
                    }

                    @Override
                    public void onNext(List<Classroom> classrooms) {
                        mView.hideSwp();
                        if (classrooms != null) {
                            mView.showClassRoomComplete(classrooms);
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
