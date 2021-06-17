package com.edusoho.kuozhi.clean.module.main.study.postcourse;

import com.edusoho.kuozhi.clean.api.MyStudyApi;
import com.edusoho.kuozhi.clean.api.UserApi;
import com.edusoho.kuozhi.clean.bean.mystudy.PostCourseModel;
import com.edusoho.kuozhi.clean.bean.mystudy.PostCoursesProgress;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.module.base.BaseFragment;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.navi.NaviLifecycle;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by RexXiang on 2018/1/18.
 */

public class MyPostCoursePresenter implements MyPostCourseContract.Presenter {

    private MyPostCourseContract.View mView;
    private List<PostCourseModel> mPostCourses = new ArrayList<PostCourseModel>();
    private String mPostName;

    private final LifecycleProvider<FragmentEvent> mFragmentLifeProvider;

    public MyPostCoursePresenter(MyPostCourseContract.View view) {
        mView = view;
        mFragmentLifeProvider = NaviLifecycle.createFragmentLifecycleProvider((BaseFragment) view);
    }

    @Override
    public void getMyPostCourse() {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(MyStudyApi.class)
                .getPostcourse()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mFragmentLifeProvider.<List<PostCourseModel>>bindToLifecycle())
                .subscribe(new SubscriberProcessor<List<PostCourseModel>>() {
                    @Override
                    public void onError(String message) {
                        super.onError(message);
                    }

                    @Override
                    public void onNext(List<PostCourseModel> postCourseModels) {
                        mPostCourses = postCourseModels;
                        getUserPostName();
                    }
                });
    }



    private void getUserPostName() {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(UserApi.class)
                .getUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mFragmentLifeProvider.<User>bindToLifecycle())
                .subscribe(new SubscriberProcessor<User>() {
                    @Override
                    public void onError(String message) {
                        super.onError(message);
                    }

                    @Override
                    public void onNext(User user) {
                        mPostName = user.postName;
                        getPostCourseProgress();
                    }
                });

    }

    private void getPostCourseProgress() {
        HttpUtils.getInstance().addTokenHeader(EdusohoApp.app.token)
                .createApi(MyStudyApi.class)
                .getPostCoursesProgress()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mFragmentLifeProvider.<PostCoursesProgress>bindToLifecycle())
                .subscribe(new SubscriberProcessor<PostCoursesProgress>() {
                    @Override
                    public void onError(String message) {
                        super.onError(message);
                    }

                    @Override
                    public void onCompleted() {
                        mView.refreshCompleted();
                    }

                    @Override
                    public void onNext(PostCoursesProgress postCoursesProgress) {
                        mView.refreshView(mPostCourses, mPostName, postCoursesProgress);
                    }
                });
    }

    @Override
    public void subscribe() {
        mView.clearData();
        getMyPostCourse();
    }

    @Override
    public void unsubscribe() {

    }
}
