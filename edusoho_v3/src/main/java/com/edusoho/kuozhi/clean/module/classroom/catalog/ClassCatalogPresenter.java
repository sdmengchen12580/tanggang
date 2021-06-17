package com.edusoho.kuozhi.clean.module.classroom.catalog;

import android.text.TextUtils;
import android.view.View;

import com.edusoho.kuozhi.clean.api.ClassroomApi;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.module.base.BaseFragment;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.google.gson.JsonObject;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.navi.NaviLifecycle;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DF on 2017/5/22.
 */

public class ClassCatalogPresenter implements ClassCatalogContract.Presenter {

    private ClassCatalogContract.View        mView;
    private int                              mClassroomId;
    private LifecycleProvider<FragmentEvent> mFragmentLifeProvider;

    public ClassCatalogPresenter(ClassCatalogContract.View view, int classroomId) {
        mFragmentLifeProvider = NaviLifecycle.createFragmentLifecycleProvider((BaseFragment) view);
        this.mView = view;
        this.mClassroomId = classroomId;
    }

    @Override
    public void subscribe() {
        if (!TextUtils.isEmpty(EdusohoApp.app.token)) {
            getClassStatus();
        } else {
            getClassroomCatalog();
        }
    }

    private void getClassroomCatalog() {
        HttpUtils.getInstance()
                .createApi(ClassroomApi.class)
                .getCourseProjects(mClassroomId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mFragmentLifeProvider.<List<CourseProject>>bindToLifecycle())
                .subscribe(new SubscriberProcessor<List<CourseProject>>() {
                    @Override
                    public void onError(String e) {
                        mView.setLoadStatus(View.GONE);
                    }

                    @Override
                    public void onNext(List<CourseProject> classroomItems) {
                        mView.setLoadStatus(View.GONE);
                        if (classroomItems != null && !classroomItems.isEmpty()) {
                            mView.showComplete(classroomItems);
                        } else {
                            mView.setLessonEmptyViewVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void getClassStatus() {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(ClassroomApi.class)
                .getClassroomStatus(mClassroomId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mFragmentLifeProvider.<JsonObject>bindToLifecycle())
                .subscribe(new SubscriberProcessor<JsonObject>() {
                    @Override
                    public void onError(String message) {
                        mView.setLoadStatus(View.GONE);
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        if (jsonObject != null && jsonObject.getAsJsonObject("access") != null) {
                            String code = jsonObject.getAsJsonObject("access").get("code").getAsString();
                            mView.setClassStatus(code);
                        }
                        getClassroomCatalog();
                    }

                });

    }

    @Override
    public void unsubscribe() {

    }
}
