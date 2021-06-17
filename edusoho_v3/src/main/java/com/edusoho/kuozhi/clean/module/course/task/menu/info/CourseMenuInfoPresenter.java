package com.edusoho.kuozhi.clean.module.course.task.menu.info;

import com.edusoho.kuozhi.clean.api.CourseSetApi;
import com.edusoho.kuozhi.clean.api.PluginsApi;
import com.edusoho.kuozhi.clean.bean.CourseLearningProgress;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.VipInfo;
import com.edusoho.kuozhi.clean.bean.innerbean.Teacher;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.module.base.BaseFragment;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.navi.NaviLifecycle;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by JesseHuang on 2017/4/24.
 */

public class CourseMenuInfoPresenter implements CourseMenuInfoContract.Presenter {

    private       CourseMenuInfoContract.View      mView;
    private       CourseLearningProgress           mCourseLearningProgress;
    private       CourseProject                    mCourseProject;
    private final LifecycleProvider<FragmentEvent> mFragmentLifeProvider;

    public CourseMenuInfoPresenter(CourseMenuInfoContract.View view
            , CourseProject courseProject
            , CourseLearningProgress courseLearningProgress) {
        mFragmentLifeProvider = NaviLifecycle.createFragmentLifecycleProvider((BaseFragment) view);
        this.mView = view;
        this.mCourseProject = courseProject;
        this.mCourseLearningProgress = courseLearningProgress;
    }

    @Override
    public void subscribe() {
        showServices(mCourseProject.services);
        if (mCourseProject.teachers != null && mCourseProject.teachers.length > 0) {
            showTeacher(mCourseProject.teachers[0]);
        }
        showRelativeCourseProjects(mCourseProject.courseSet.id, mCourseProject.id);
    }

    private void showRelativeCourseProjects(int courseSetId, final int currentCourseProjectId) {
        Observable
                .combineLatest(getRelativeCourseProjects(courseSetId, currentCourseProjectId), getVipInfos(), new Func2<List<CourseProject>, List<VipInfo>, Object>() {
                    @Override
                    public Object call(List<CourseProject> courseProjects, List<VipInfo> vipInfos) {
                        mView.showRelativeCourseProjects(courseProjects, vipInfos);
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mFragmentLifeProvider.bindToLifecycle())
                .subscribe(new SubscriberProcessor<>());
    }

    private Observable<List<CourseProject>> getRelativeCourseProjects(int courseSetId, final int currentCourseProjectId) {
        return HttpUtils.getInstance().createApi(CourseSetApi.class)
                .getCourseProjects(courseSetId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mFragmentLifeProvider.<List<CourseProject>>bindToLifecycle())
                .flatMap(new Func1<List<CourseProject>, Observable<CourseProject>>() {
                    @Override
                    public Observable<CourseProject> call(List<CourseProject> courseProjects) {
                        return Observable.from(courseProjects);
                    }
                })
                .filter(new Func1<CourseProject, Boolean>() {
                    @Override
                    public Boolean call(CourseProject courseProject) {
                        return courseProject.id != currentCourseProjectId;
                    }
                })
                .toList();
    }

    private Observable<List<VipInfo>> getVipInfos() {
        return HttpUtils
                .getInstance()
                .createApi(PluginsApi.class)
                .getVipInfo();
    }

    private void showServices(CourseProject.Service[] services) {
        mView.showServices(services);
    }

    private void showTeacher(Teacher teacher) {
        mView.showTeacher(teacher);
    }

    @Override
    public void unsubscribe() {

    }
}
