package com.edusoho.kuozhi.clean.module.main.study.assignment;

import com.edusoho.kuozhi.clean.api.MyStudyApi;
import com.edusoho.kuozhi.clean.bean.mystudy.AssignmentModel;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.edusoho.kuozhi.clean.module.base.BaseFragment;
import com.trello.rxlifecycle.navi.NaviLifecycle;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by RexXiang on 2018/1/15.
 */

public class MyAssignmentsPresenter implements MyAssignmentsContract.Presenter {

    private MyAssignmentsContract.View mView;

    private final LifecycleProvider<FragmentEvent> mFragmentLifeProvider;

    public MyAssignmentsPresenter(MyAssignmentsContract.View view) {
        mView = view;
        mFragmentLifeProvider = NaviLifecycle.createFragmentLifecycleProvider((BaseFragment) view);
    }

    @Override
    public void getMyAssignments() {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(MyStudyApi.class)
                .getAssignment()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mFragmentLifeProvider.<List<AssignmentModel>>bindToLifecycle())
                .subscribe(new SubscriberProcessor<List<AssignmentModel>>() {
                    @Override
                    public void onError(String message) {
                        super.onError(message);
                    }

                    @Override
                    public void onCompleted() {
                        mView.refreshCompleted();
                    }

                    @Override
                    public void onNext(List<AssignmentModel> assignmentModels) {
                        mView.refreshView(assignmentModels);
                    }
                });
    }

    @Override
    public void subscribe() {
        mView.clearData();
        getMyAssignments();
    }

    @Override
    public void unsubscribe() {

    }
}
