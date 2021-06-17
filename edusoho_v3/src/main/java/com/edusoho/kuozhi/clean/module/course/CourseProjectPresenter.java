package com.edusoho.kuozhi.clean.module.course;

import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.api.CourseApi;
import com.edusoho.kuozhi.clean.api.LessonApi;
import com.edusoho.kuozhi.clean.api.PluginsApi;
import com.edusoho.kuozhi.clean.api.UserApi;
import com.edusoho.kuozhi.clean.bean.CourseMember;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.eslive.athena.entity.LiveTicket;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.bean.innerbean.Teacher;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.module.base.BaseActivity;
import com.edusoho.kuozhi.clean.module.course.task.catalog.TaskTypeEnum;
import com.edusoho.kuozhi.clean.utils.Constants;
import com.edusoho.kuozhi.clean.utils.StringUtils;
import com.edusoho.kuozhi.clean.utils.biz.CourseHelper;
import com.edusoho.kuozhi.clean.widget.ESAlertDialog;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.entity.lesson.LessonItem;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.bal.VipLevel;
import com.google.gson.JsonObject;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.navi.NaviLifecycle;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by JesseHuang on 2017/3/23.
 */

public class CourseProjectPresenter implements CourseProjectContract.Presenter {

    private static final String IS_JOIN_SUCCESS = "success";
    private static final int FREE = 1;
    private CourseProjectContract.View mView;
    private int mCourseProjectId;
    private Teacher mTeacher;
    private CourseMember mCourseMember;
    private CourseProject mCourseProject;
    private LifecycleProvider<ActivityEvent> mActivityLifeProvider;
    private boolean mIsJoin = false;

    public CourseProjectPresenter(int courseProjectId, CourseProjectContract.View view) {
        mCourseProjectId = courseProjectId;
        mActivityLifeProvider = NaviLifecycle.createActivityLifecycleProvider((BaseActivity) view);
        mView = view;
    }

    @Override
    public void consult() {
        //这里需要修改，应该要和RxJava结合使用
        mView.launchImChatWithTeacher(mTeacher);
    }

    @Override
    public void subscribe() {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(CourseApi.class)
                .getCourseProject(mCourseProjectId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivityLifeProvider.<CourseProject>bindToLifecycle())
                .subscribe(new SubscriberProcessor<CourseProject>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        mView.showLoading();
                    }

                    @Override
                    public void onNext(CourseProject courseProject) {
                        Log.e("测试", "id=" + courseProject.id);
                        mCourseProject = courseProject;
                        mView.showCover(mCourseProject.courseSet.cover.middle);
                        if (courseProject.teachers.length > 0) {
                            mTeacher = courseProject.teachers[0];
                        }
                        int errorRes = CourseHelper.getErrorResId(courseProject.access.code);
                        switch (courseProject.access.code) {
                            case CourseHelper.USER_NOT_LOGIN:
                                initLogoutCourseMemberStatus(courseProject);
                                initTrialFirstTask(mCourseProjectId);
                                mView.setShowError(new ShowActionHelper().showErrorType(ShowActionHelper.TYPE_NOT_LOGIN));
                                break;
                            case CourseHelper.COURSE_SUCCESS:
                                initLoginCourseMemberStatus(courseProject);
                                break;
                            case CourseHelper.COURSE_EXPIRED:
                            case CourseHelper.COURSE_CLOSED:
                            case CourseHelper.COURSE_NOT_BUYABLE:
                            case CourseHelper.COURSE_BUY_EXPIRED:
                                initLoginCourseMemberStatus(courseProject);
                                mView.setShowError(new ShowActionHelper().showErrorType(ShowActionHelper.TYPE_TOAST)
                                        .showErrorMsgResId(errorRes));
                                break;
                            case CourseHelper.ONLY_VIP_JOIN_WAY:
                                initLoginCourseMemberStatus(courseProject);
                                break;
                            default:
                                initLoginCourseMemberStatus(courseProject);
                        }
                    }

                    @Override
                    public void onError(String message) {
                        mView.showToast(message);
                        mView.showError();
                    }
                });
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(CourseApi.class)
                .setCourseView(mCourseProjectId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivityLifeProvider.<JsonObject>bindToLifecycle())
                .subscribe(new SubscriberProcessor<JsonObject>());
    }

    private void initLoginCourseMemberStatus(final CourseProject courseProject) {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(UserApi.class)
                .getCourseMember(courseProject.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivityLifeProvider.<CourseMember>bindToLifecycle())
                .subscribe(new SubscriberProcessor<CourseMember>() {

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        mView.showComplete();
                    }

                    @Override
                    public void onNext(CourseMember member) {
                        mCourseMember = member;
                        mIsJoin = member.user != null;
                        mView.showAudioMode(mIsJoin && "1".equals(courseProject.isAudioOn));
                        if (mIsJoin) {
                            mView.showFragments(initCourseModules(true), courseProject, mIsJoin);
                            mView.initLearnLayout(CourseProject.LearnMode.getMode(courseProject.learnMode));
                            if (CourseHelper.COURSE_EXPIRED.equals(courseProject.access.code)) {
                                mView.setShowError(new ShowActionHelper().showErrorType(ShowActionHelper.TYPE_DIALOG)
                                        .showErrorMsgResId(R.string.course_expired_dialog)
                                        .setAction(ShowActionHelper.POSITIVE_ACTION_EXIT_COURSE)
                                        .doAction());
                                return;
                            } else if (CourseHelper.COURSE_CLOSED.equals(courseProject.access.code)
                                    || CourseHelper.COURSE_NOT_BUYABLE.equals(courseProject.access.code)
                                    || CourseHelper.COURSE_BUY_EXPIRED.equals(courseProject.access.code)) {
                                //用户已经加入，仍可学
                                mView.setShowError(null);
                                return;
                            }

                            int errRes = CourseHelper.getErrorResId(member.access.code);
                            if (member.access.code.equals(CourseHelper.MEMBER_SUCCESS)) {
                                mView.setShowError(null);
                            } else if (member.access.code.equals(CourseHelper.MEMBER_EXPIRED)) {
                                mView.setShowError(new ShowActionHelper().showErrorType(ShowActionHelper.TYPE_DIALOG)
                                        .showErrorMsgResId(R.string.course_member_expired_dialog)
                                        .setAction(ShowActionHelper.POSITIVE_ACTION_EXIT_COURSE)
                                        .doAction());
                            } else if (member.access.code.equals(CourseHelper.COURSE_NOT_ARRIVE)) {
                                mView.setShowError(new ShowActionHelper().showErrorType(ShowActionHelper.TYPE_TOAST)
                                        .showErrorMsgResId(errRes)
                                        .doAction());
                            } else if (member.access.code.equals(CourseHelper.MEMBER_VIP_EXPIRED)) {
                                mView.setShowError(new ShowActionHelper().showErrorType(ShowActionHelper.TYPE_DIALOG)
                                        .showErrorMsgResId(R.string.course_member_vip_expired_dialog)
                                        .setAction(ShowActionHelper.POSITIVE_ACTION_BUY_VIP)
                                        .doAction());
                            } else {
                                mView.setShowError(new ShowActionHelper().showErrorType(ShowActionHelper.TYPE_TOAST)
                                        .showErrorMsgResId(errRes)
                                        .doAction());
                            }
                        } else {
                            mView.showFragments(initCourseModules(false), courseProject, mIsJoin);
                            initTrialFirstTask(mCourseProjectId);
                            if (CourseHelper.COURSE_EXPIRED.equals(courseProject.access.code)
                                    || CourseHelper.COURSE_REACH_MAX_STUDENT_NUM.equals(courseProject.access.code)
                                    || CourseHelper.COURSE_CLOSED.equals(courseProject.access.code)
                                    || CourseHelper.COURSE_NOT_BUYABLE.equals(courseProject.access.code)
                                    || CourseHelper.COURSE_BUY_EXPIRED.equals(courseProject.access.code)) {
                                mView.setShowError(new ShowActionHelper().showErrorType(ShowActionHelper.TYPE_TOAST)
                                        .showErrorMsgResId(CourseHelper.getErrorResId(courseProject.access.code))
                                        .doAction());
                                return;
                            }
                            if (CourseHelper.COURSE_SUCCESS.equals(courseProject.access.code)) {
                                mView.setShowError(new ShowActionHelper().showErrorType(ShowActionHelper.TYPE_TOAST)
                                        .showErrorMsgResId(R.string.join_course_first).setLearnClick(true));
                            } else if (CourseHelper.ONLY_VIP_JOIN_WAY.equals(courseProject.access.code)) {
                                HttpUtils.getInstance()
                                        .createApi(PluginsApi.class)
                                        .getVipLevel(courseProject.vipLevelId)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .compose(mActivityLifeProvider.<VipLevel>bindToLifecycle())
                                        .subscribe(new SubscriberProcessor<VipLevel>() {
                                            @Override
                                            public void onNext(VipLevel vipLevel) {
                                                mView.setShowError(new ShowActionHelper().showErrorType(ShowActionHelper.TYPE_TOAST)
                                                        .setParams(vipLevel.name)
                                                        .showErrorMsgResId(R.string.only_vip_learn));
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    private void initLogoutCourseMemberStatus(final CourseProject courseProject) {
        mView.showFragments(initCourseModules(false), courseProject, false);
        if (mCourseProject != null && courseProject.learningExpiryDate != null && courseProject.learningExpiryDate.expired) {
            mView.setJoinButton(CourseProjectActivity.JoinButtonStatusEnum.COURSE_EXPIRED);
        }
        mView.showComplete();
    }

    private void initTrialFirstTask(final int courseId) {
        HttpUtils.getInstance()
                .createApi(CourseApi.class)
                .getTrialFirstTask(courseId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivityLifeProvider.<CourseTask>bindToLifecycle())
                .subscribe(new SubscriberProcessor<CourseTask>() {
                    @Override
                    public void onNext(CourseTask trialTask) {
                        if (trialTask != null && trialTask.id != 0) {
                            mView.initTrailTask(trialTask);
                        } else {
                            mView.setPlayLayoutVisible(false);
                        }
                    }
                });
    }

    private boolean isVipExpired(String deadline) {
        return System.currentTimeMillis() > Long.parseLong(deadline) * 1000L;
    }

    @Override
    public void joinCourseProject() {
        if (mCourseProject != null && mCourseProject.learningExpiryDate != null && !mCourseProject.learningExpiryDate.expired) {
            if (mCourseProject.isFree == FREE) {
                joinFreeOrVipCourse(mCourseProjectId);
            } else if (EdusohoApp.app.loginUser.vip != null
                    && !isVipExpired(EdusohoApp.app.loginUser.vip.deadline)) {
                HttpUtils.getInstance()
                        .createApi(PluginsApi.class)
                        .getVipLevel(mCourseProject.vipLevelId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(mActivityLifeProvider.<VipLevel>bindToLifecycle())
                        .subscribe(new SubscriberProcessor<VipLevel>() {
                            @Override
                            public void onNext(VipLevel vipLevel) {
                                if (EdusohoApp.app.loginUser.vip.seq >= vipLevel.seq) {
                                    joinFreeOrVipCourse(mCourseProjectId);
                                } else {
                                    mView.launchConfirmOrderActivity(mCourseProject.courseSet.id, mCourseProjectId);
                                }
                            }

                            @Override
                            public void onError(String message) {
                                mView.showToast(message);
                                mView.launchConfirmOrderActivity(mCourseProject.courseSet.id, mCourseProjectId);
                            }
                        });
            } else {
                mView.launchConfirmOrderActivity(mCourseProject.courseSet.id, mCourseProjectId);
            }
        }
    }

    @Override
    public void exitCourse() {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(UserApi.class)
                .exitCourse(mCourseProjectId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivityLifeProvider.<JsonObject>bindToLifecycle())
                .subscribe(new SubscriberProcessor<JsonObject>() {
                    @Override
                    public void onNext(JsonObject jsonObject) {
                        if (jsonObject.get(IS_JOIN_SUCCESS).getAsBoolean()) {
                            mView.setShowError(new ShowActionHelper().showErrorType(ShowActionHelper.TYPE_TOAST)
                                    .showErrorMsgResId(R.string.join_course_first).setLearnClick(true));
                            EventBus.getDefault().post(new MessageEvent(MessageEvent.COURSE_EXIT));
                            mIsJoin = false;
                            mView.showToast(R.string.exit_course_success);
                            initTrialFirstTask(mCourseProjectId);
                            mView.exitCourseLayout();
                            mView.clearCoursesCache(mCourseProjectId);
                            mView.showAudioMode(false);
                        } else {
                            mView.showToast(R.string.exit_course_failure);
                        }
                    }
                });
    }

    @Override
    public CourseProject getCourseProject() {
        return mCourseProject;
    }

    @Override
    public CourseMember getCourseMember() {
        return mCourseMember;
    }

    @Override
    public void learnTask(CourseTask courseTask) {
        mView.learnTask(courseTask, mCourseProject, mCourseMember);
        mView.setCurrentTaskId(courseTask.id);
    }

    @Override
    public void learnTask(int taskId) {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(CourseApi.class)
                .getCourseTask(mCourseProjectId, taskId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivityLifeProvider.<CourseTask>bindToLifecycle())
                .subscribe(new SubscriberProcessor<CourseTask>() {
                    @Override
                    public void onNext(CourseTask courseTask) {
                        mView.learnTask(courseTask, mCourseProject, mCourseMember);
                    }
                });
        mView.setCurrentTaskId(taskId);
    }

    @Override
    public void playVideo(final CourseTask task) {
        //type为直播类型
        if (TaskTypeEnum.LIVE.toString().equals(task.type)) {
            if (Constants.LiveTaskReplayStatus.UNGENERATED.equals(task.activity.replayStatus)) {
                long currentTime = System.currentTimeMillis() / 1000;
                if (task.endTime != 0 && task.endTime > currentTime) {
                    playLive(task);
                } else {
                    mView.showToast("直播已结束");
                }
            } else if (Constants.LiveTaskReplayStatus.GENERATED.equals(task.activity.replayStatus)) {
                playLiveReplay(task);
            } else if (Constants.LiveTaskReplayStatus.VIDEO_GENERATED.equals(task.activity.replayStatus)) {
                playCloudVideo(task);
            }
        } else {
            playCloudVideo(task);
        }
    }

    public void playLive(final CourseTask task) {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .baseOnApi()
                .createApi(LessonApi.class)
                .getLiveTicket(task.id, "android")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivityLifeProvider.<LiveTicket>bindToLifecycle())
                .flatMap(new Func1<LiveTicket, Observable<String>>() {
                    @Override
                    public Observable<String> call(LiveTicket liveTicket) {
                        return Observable.just(liveTicket.getNo());
                    }
                })
                .subscribe(new SubscriberProcessor<String>() {
                    @Override
                    public void onNext(String ticket) {
                        getLiveTicketInfo(task, ticket)
                                .repeatWhen(new Func1<Observable<? extends Void>, Observable<?>>() {
                                    @Override
                                    public Observable<?> call(Observable<? extends Void> observable) {
                                        return observable.delay(200, TimeUnit.MILLISECONDS);
                                    }
                                })
                                .takeUntil(new Func1<LiveTicket, Boolean>() {
                                    @Override
                                    public Boolean call(LiveTicket liveTicket) {
                                        return liveTicket != null && liveTicket.getExtra() != null;
                                    }
                                })
                                .filter(new Func1<LiveTicket, Boolean>() {
                                    @Override
                                    public Boolean call(LiveTicket liveTicket) {
                                        return liveTicket != null && liveTicket.getExtra() != null;
                                    }
                                })
                                .subscribe(new SubscriberProcessor<LiveTicket>() {

                                    @Override
                                    public void onNext(LiveTicket liveTicket) {
                                        if (liveTicket.isNonsupport()) {
                                            mView.showToast(R.string.live_task_unsupport);
                                            return;
                                        }
                                        liveTicket.setReplayStatus(false);
                                        mView.launchLiveTask(liveTicket, task);
                                    }
                                });
                    }
                });
    }

    public void playLiveReplay(final CourseTask task) {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .baseOnApi()
                .createApi(LessonApi.class)
                .getLiveReplay(task.id, "android")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivityLifeProvider.<LiveTicket>bindToLifecycle())
                .subscribe(new SubscriberProcessor<LiveTicket>() {
                    @Override
                    public void onNext(LiveTicket liveTicket) {
                        if (liveTicket.isNonsupport()) {
                            mView.showToast(R.string.replay_task_unsupport);
                            return;
                        }
                        liveTicket.setReplayStatus(true);
                        mView.launchLiveTask(liveTicket, task);
                    }
                });
    }

    //hls_encryption
    public void playCloudVideo(final CourseTask task) {
        HttpUtils.getInstance()
                .baseOnApi()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(LessonApi.class)
                .getLesson(task.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivityLifeProvider.<LessonItem>bindToLifecycle())
                .subscribe(new SubscriberProcessor<LessonItem>() {
                    @Override
                    public void onNext(LessonItem lessonItem) {
                        mView.playVideo(task, lessonItem);
                    }
                });
    }

    private void joinFreeOrVipCourse(final int courseId) {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(CourseApi.class)
                .joinFreeOrVipCourse(courseId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivityLifeProvider.<CourseMember>bindToLifecycle())
                .subscribe(new SubscriberProcessor<CourseMember>() {
                    @Override
                    public void onNext(CourseMember courseMember) {
                        if (courseMember != null && courseMember.id != 0) {
                            mIsJoin = true;
                            mView.setShowError(null);
                            mView.showToast(R.string.join_course_success);
                            mView.initJoinCourseLayout(CourseProject.LearnMode.getMode(mCourseProject.learnMode));
                            if ("1".equals(mCourseProject.isAudioOn)) {
                                mView.showAudioMode(true);
                            }
                        } else {
                            mView.launchConfirmOrderActivity(mCourseProject.courseSet.id, mCourseProjectId);
                        }
                    }
                });
    }

    private List<CourseProjectEnum> initCourseModules(boolean isLearning) {
        List<CourseProjectEnum> list = new ArrayList<>();
        if (isLearning) {
            list.add(CourseProjectEnum.TASKS);
        } else {
            list.add(CourseProjectEnum.INFO);
            list.add(CourseProjectEnum.TASKS);
            list.add(CourseProjectEnum.RATE);
        }
        return list;
    }

    @Override
    public boolean isJoin() {
        return mIsJoin;
    }

    @Override
    public void syncLoginUserInfo() {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(UserApi.class)
                .getUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivityLifeProvider.<User>bindToLifecycle())
                .subscribe(new SubscriberProcessor<User>() {
                    @Override
                    public void onNext(User user) {
                        EdusohoApp.app.loginUser.vip = user.vip;
                    }
                });
    }

    private Observable<LiveTicket> getLiveTicketInfo(CourseTask task, String ticket) {
        return HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .baseOnApi()
                .createApi(LessonApi.class)
                .getLiveTicketInfo(task.id, ticket)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivityLifeProvider.<LiveTicket>bindToLifecycle());
    }

    @Override
    public void unsubscribe() {

    }

    class ShowActionHelper {
        public static final int TYPE_TOAST = 1;
        public static final int TYPE_DIALOG = 2;
        public static final int TYPE_NOT_LOGIN = 3;
        public static final int POSITIVE_ACTION_EXIT_COURSE = 13;
        public static final int POSITIVE_ACTION_BUY_VIP = 14;

        private int mShowType;
        private int mMsgResId;
        private int mActionType;
        private boolean isLearnClick = false;
        private String mMsgParam;

        ShowActionHelper doAction() {
            if (mShowType == TYPE_TOAST) {
                if (StringUtils.isEmpty(mMsgParam)) {
                    mView.showToast(mMsgResId);
                } else {
                    mView.showToast(mMsgResId, mMsgParam);
                }
            } else if (mShowType == TYPE_DIALOG) {
                mView.showExitDialog(mMsgResId, new ESAlertDialog.DialogButtonClickListener() {
                    @Override
                    public void onClick(DialogFragment dialog) {
                        dialog.dismiss();
                        if (mActionType == POSITIVE_ACTION_EXIT_COURSE) {
                            exitCourse();
                            mView.setShowError(null);
                        } else if (mActionType == POSITIVE_ACTION_BUY_VIP) {
                            mView.launchVipListWeb();
                        }
                    }
                });
            } else if (mShowType == TYPE_NOT_LOGIN) {
                mView.launchLoginActivity();
            }
            return this;
        }

        public ShowActionHelper showErrorType(int showType) {
            mShowType = showType;
            return this;
        }

        public ShowActionHelper showErrorMsgResId(int msgResId) {
            mMsgResId = msgResId;
            return this;
        }

        public ShowActionHelper setParams(String param) {
            mMsgParam = param;
            return this;
        }

        public ShowActionHelper setAction(int action) {
            mActionType = action;
            return this;
        }

        public ShowActionHelper setLearnClick(boolean isClick) {
            isLearnClick = isClick;
            return this;
        }

        public boolean isLearnClick() {
            return isLearnClick;
        }

        public int getErrorType() {
            return mShowType;
        }
    }
}
