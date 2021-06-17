package com.edusoho.kuozhi.clean.module.courseset.plan;

import com.edusoho.kuozhi.clean.api.CourseSetApi;
import com.edusoho.kuozhi.clean.api.PluginsApi;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.VipInfo;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.module.base.BaseFragment;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.navi.NaviLifecycle;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DF on 2017/4/1.
 */

class CourseProjectsPresenter implements CourseProjectsContract.Presenter {

    private       int                              mCourseSetId;
    private       CourseProjectsContract.View      mView;
    private       List<CourseProject>              mCourseProjects;
    private final LifecycleProvider<FragmentEvent> mFragmentLifeProvider;

    CourseProjectsPresenter(CourseProjectsContract.View view, int id) {
        mFragmentLifeProvider = NaviLifecycle.createFragmentLifecycleProvider((BaseFragment) view);
        this.mView = view;
        this.mCourseSetId = id;
    }

    @Override
    public void subscribe() {
        HttpUtils.getInstance()
                .createApi(CourseSetApi.class)
                .getCourseProjects(mCourseSetId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mFragmentLifeProvider.<List<CourseProject>>bindToLifecycle())
                .subscribe(new SubscriberProcessor<List<CourseProject>>() {
                    @Override
                    public void onError(String e) {
                        mView.setLoadViewVisible(false);
                    }

                    @Override
                    public void onNext(List<CourseProject> courseProjects) {
                        mCourseProjects = courseProjects;
                        acquireVipInfo();
                    }
                });
    }

    private void acquireVipInfo() {
        HttpUtils.getInstance()
                .createApi(PluginsApi.class)
                .getVipInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mFragmentLifeProvider.<List<VipInfo>>bindToLifecycle())
                .subscribe(new SubscriberProcessor<List<VipInfo>>() {
                    @Override
                    public void onError(String message) {
                        mView.setLoadViewVisible(false);
                        if (mCourseProjects != null && mCourseProjects.size() != 0) {
                            mView.showComPanies(mCourseProjects, null);
                        }
                    }

                    @Override
                    public void onNext(List<VipInfo> vipInfos) {
                        mView.setLoadViewVisible(false);
                        if (mCourseProjects != null && mCourseProjects.size() != 0 && vipInfos != null) {
                            mView.showComPanies(mCourseProjects, vipInfos);
                        }
                    }
                });
    }

    @Override
    public void unsubscribe() {

    }

}
