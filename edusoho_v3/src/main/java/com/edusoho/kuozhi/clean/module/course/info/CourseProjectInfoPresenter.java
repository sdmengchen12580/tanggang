package com.edusoho.kuozhi.clean.module.course.info;

import com.edusoho.kuozhi.clean.api.CourseApi;
import com.edusoho.kuozhi.clean.api.CourseSetApi;
import com.edusoho.kuozhi.clean.api.PluginsApi;
import com.edusoho.kuozhi.clean.bean.CourseMemberRoleEnum;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.bean.DataPageResult;
import com.edusoho.kuozhi.clean.bean.Member;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.bean.innerbean.Teacher;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.module.base.BaseFragment;
import com.edusoho.kuozhi.clean.utils.StringUtils;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.bal.VipLevel;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.navi.NaviLifecycle;

import org.greenrobot.eventbus.EventBus;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by JesseHuang on 2017/3/26.
 */

public class CourseProjectInfoPresenter implements CourseProjectInfoContract.Presenter {

    private static final int NO_VIP = 0;
    private static final int FREE   = 1;
    private CourseProject                    mCourseProject;
    private CourseProjectInfoContract.View   mView;
    private LifecycleProvider<FragmentEvent> mFragmentLifeProvider;

    public CourseProjectInfoPresenter(CourseProject courseProject, CourseProjectInfoContract.View view) {
        this.mCourseProject = courseProject;
        mFragmentLifeProvider = NaviLifecycle.createFragmentLifecycleProvider((BaseFragment) view);
        this.mView = view;
    }

    @Override
    public void subscribe() {
        mView.initCourseProjectInfo(mCourseProject);
        showIntroduce();
        showPrice();
        showVip(mCourseProject.vipLevelId);
        showAudiences(mCourseProject.audiences);
        showMemberNum(mCourseProject.studentNum);
        showMembers(mCourseProject.id, CourseMemberRoleEnum.STUDENT.toString());
        showServices(mCourseProject.services);
        if (mCourseProject.teachers != null && mCourseProject.teachers.length > 0) {
            showTeacher(mCourseProject.teachers[0]);
        }
    }

    private void showPrice() {
        if (FREE == mCourseProject.isFree) {
            mView.showPrice(CourseProjectPriceEnum.FREE, mCourseProject.price2, mCourseProject.originPrice2);
        } else if (mCourseProject.price2.getPrice() == mCourseProject.originPrice2.getPrice()) {
            mView.showPrice(CourseProjectPriceEnum.ORIGINAL, mCourseProject.price2, mCourseProject.originPrice2);
        } else {
            mView.showPrice(CourseProjectPriceEnum.SALE, mCourseProject.price2, mCourseProject.originPrice2);
        }
    }

    private void showVip(int vipLevelId) {
        if (NO_VIP != vipLevelId) {
            HttpUtils.getInstance()
                    .createApi(PluginsApi.class)
                    .getVipLevel(vipLevelId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(mFragmentLifeProvider.<VipLevel>bindToLifecycle())
                    .subscribe(new SubscriberProcessor<VipLevel>() {
                        @Override
                        public void onNext(VipLevel vipLevel) {
                            mView.showVipAdvertising(vipLevel.name);
                            if (EdusohoApp.app.loginUser.vip != null
                                    && EdusohoApp.app.loginUser.vip.seq >= vipLevel.seq
                                    && !isVipExpired(EdusohoApp.app.loginUser.vip.deadline)) {
                                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.SHOW_VIP_BUTTON));
                            }
                        }
                    });
        }
    }

    private boolean isVipExpired(String deadline) {
        return System.currentTimeMillis() > Long.parseLong(deadline) * 1000l;
    }

    private void showServices(CourseProject.Service[] services) {
        mView.showServices(services);
    }

    private void showAudiences(String[] audiences) {
        mView.showAudiences(audiences);
    }

    private void showTeacher(Teacher teacher) {
        mView.showTeacher(teacher);
    }

    private void showIntroduce() {
        HttpUtils.getInstance()
                .createApi(CourseSetApi.class)
                .getCourseSet(mCourseProject.courseSet.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mFragmentLifeProvider.<CourseSet>bindToLifecycle())
                .subscribe(new SubscriberProcessor<CourseSet>() {
                    @Override
                    public void onNext(CourseSet courseSet) {
                        if (!StringUtils.isEmpty(mCourseProject.summary)) {
                            mView.showIntroduce(mCourseProject.summary);
                        } else {
                            mView.showIntroduce(courseSet.summary);
                        }
                    }
                });
    }

    private void showMemberNum(int count) {
        mView.showMemberNum(count);
    }

    private void showMembers(int courseId, String role) {
        HttpUtils.getInstance().createApi(CourseApi.class)
                .getCourseMembers(courseId, role, 0, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mFragmentLifeProvider.<DataPageResult<Member>>bindToLifecycle())
                .subscribe(new SubscriberProcessor<DataPageResult<Member>>() {
                    @Override
                    public void onNext(DataPageResult<Member> memberDataPageResult) {
                        mView.showMembers(memberDataPageResult.data);
                    }
                });
    }

    @Override
    public void unsubscribe() {

    }
}
