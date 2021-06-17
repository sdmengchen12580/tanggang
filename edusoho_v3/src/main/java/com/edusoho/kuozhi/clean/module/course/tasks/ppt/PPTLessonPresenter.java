package com.edusoho.kuozhi.clean.module.course.tasks.ppt;


import com.edusoho.kuozhi.clean.api.LessonApi;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.module.base.BaseFragment;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.entity.lesson.LessonItem;
import com.google.gson.internal.LinkedTreeMap;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.navi.NaviLifecycle;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PPTLessonPresenter implements PPTLessonContract.Presenter {

    private int                    mCourseTaskId;
    private PPTLessonContract.View mView;

    private final LifecycleProvider<FragmentEvent> mFragmentLifeProvider;

    public PPTLessonPresenter(int courseTaskId, PPTLessonContract.View view) {
        mFragmentLifeProvider = NaviLifecycle.createFragmentLifecycleProvider((BaseFragment) view);
        this.mCourseTaskId = courseTaskId;
        this.mView = view;
    }

    @Override
    public void subscribe() {
        HttpUtils.getInstance()
                .baseOnApi()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(LessonApi.class)
                .getLesson(mCourseTaskId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mFragmentLifeProvider.<LessonItem>bindToLifecycle())
                .subscribe(new SubscriberProcessor<LessonItem>() {
                    @Override
                    public void onNext(LessonItem lessonItem) {
                        LinkedTreeMap<String, List<String>> content = (LinkedTreeMap<String, List<String>>) lessonItem.content;
                        mView.showPTT(content.get("resource"));
                    }
                });
    }

    @Override
    public void unsubscribe() {

    }
}
