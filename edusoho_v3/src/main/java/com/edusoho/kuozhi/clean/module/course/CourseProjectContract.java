package com.edusoho.kuozhi.clean.module.course;

import com.edusoho.eslive.athena.entity.LiveTicket;
import com.edusoho.kuozhi.clean.bean.CourseMember;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.kuozhi.clean.bean.innerbean.Teacher;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;
import com.edusoho.kuozhi.clean.widget.ESAlertDialog;
import com.edusoho.kuozhi.v3.entity.lesson.LessonItem;

import java.util.List;


/**
 * Created by JesseHuang on 2017/3/22.
 * 学习计划Contract
 */
public interface CourseProjectContract {

    interface View extends BaseView<Presenter> {

        void showCover(String imageUrl);

        void showBottomLayout(boolean visible);

        void showFragments(List<CourseProjectEnum> courseProjectModules, CourseProject courseProject, boolean isMember);

        void launchImChatWithTeacher(Teacher teacher);

        void showCacheButton(boolean visible);

        void showShareButton(boolean visible);

        void initJoinCourseLayout(CourseProject.LearnMode mode);

        void initTrailTask(CourseTask trialTask);

        void initLearnLayout(CourseProject.LearnMode mode);

        void showAudioMode(boolean visible);

        void setCurrentTaskId(int taskId);

        void setJoinButton(CourseProjectActivity.JoinButtonStatusEnum statusEnum);

        void launchConfirmOrderActivity(int courseSetId, int courseId);

        void showExitDialog(int msgId, ESAlertDialog.DialogButtonClickListener positive);

        void setShowError(CourseProjectPresenter.ShowActionHelper helper);

        void setPlayLayoutVisible(boolean visible);

        void exitCourseLayout();

        void setTaskFinishButtonBackground(boolean learned);

        void launchLoginActivity();

        void launchVipListWeb();

        void clearCoursesCache(int... courseIds);

        void learnTask(CourseTask task, CourseProject courseProject, CourseMember courseMember);

        void playVideo(CourseTask task, LessonItem lessonItem);

        void showToast(int resId, String param);

        void showLoading();

        void showComplete();

        void showError();

        void launchLiveTask(LiveTicket liveTicket, CourseTask task);
    }

    interface Presenter extends BasePresenter {

        void consult();

        void joinCourseProject();

        void exitCourse();

        CourseProject getCourseProject();

        CourseMember getCourseMember();

        void learnTask(CourseTask courseTask);

        void learnTask(int courseId);

        /**
         * 包括云视频，云直播，第三方直播供应商
         *
         * @param task
         */
        void playVideo(CourseTask task);

        boolean isJoin();

        void syncLoginUserInfo();
    }
}
