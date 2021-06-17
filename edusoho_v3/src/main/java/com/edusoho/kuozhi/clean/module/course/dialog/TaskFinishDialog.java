package com.edusoho.kuozhi.clean.module.course.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.api.UserApi;
import com.edusoho.kuozhi.clean.bean.CourseLearningProgress;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.bean.TaskEvent;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.module.course.CourseProjectActivity;
import com.edusoho.kuozhi.clean.utils.biz.ShareHelper;
import com.edusoho.kuozhi.clean.widget.ESIconView;
import com.edusoho.kuozhi.clean.widget.ESProgressBar;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by JesseHuang on 2017/5/2.
 */

public class TaskFinishDialog extends DialogFragment {

    public static final String TASK_EVENT     = "task_event";
    public static final String COURSE_TASK    = "course_task";
    public static final String COURSE_PROJECT = "course_project";

    private static final String DOING  = "doing";
    private static final String FINISH = "finish";

    private ImageView     mClose;
    private TextView      mCourseTitle;
    private ESProgressBar mProgressBar;
    private TextView      mNextTask;
    private TextView      mShareIcon;
    private TextView      mShareCourse;
    private TextView      mTitle;
    private TextView      mRedo;
    private TextView      mTaskComment;

    private TaskEvent                        mTaskEvent;
    private CourseTask                       mCourseTask;
    private CourseProject                    mCourseProject;
    private View.OnClickListener             mRedoClickListener;
    private LifecycleProvider<ActivityEvent> mActivityLifeProvider;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_task_finish, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mClose = (ImageView) view.findViewById(R.id.iv_close);
        mProgressBar = (ESProgressBar) view.findViewById(R.id.pb_progress);
        mCourseTitle = (TextView) view.findViewById(R.id.tv_course_title);
        mShareIcon = (ESIconView) view.findViewById(R.id.icon_share);
        mShareCourse = (TextView) view.findViewById(R.id.tv_share_course);
        mNextTask = (TextView) view.findViewById(R.id.tv_next_task);
        mTitle = (TextView) view.findViewById(R.id.tv_title);
        mRedo = (TextView) view.findViewById(R.id.tv_redo);
        mTaskComment = (TextView) view.findViewById(R.id.tv_task_comment);
        init();
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public static TaskFinishDialog newInstance(TaskEvent taskEvent, CourseTask courseTask, CourseProject courseProject) {
        Bundle args = new Bundle();
        args.putSerializable(TASK_EVENT, taskEvent);
        args.putSerializable(COURSE_TASK, courseTask);
        args.putSerializable(COURSE_PROJECT, courseProject);
        TaskFinishDialog fragment = new TaskFinishDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public void setTestRedoAction(View.OnClickListener onClickListener) {
        mRedoClickListener = onClickListener;
    }

    private void init() {
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            mTaskEvent = (TaskEvent) bundle.getSerializable(TASK_EVENT);
            mCourseTask = (CourseTask) bundle.getSerializable(COURSE_TASK);
            mCourseProject = (CourseProject) bundle.getSerializable(COURSE_PROJECT);
        }

        if (FINISH.equals(mTaskEvent.result.status)) {
            mTitle.setBackgroundResource(R.drawable.icon_task_finish);
            mTitle.setText(R.string.task_finish);
            mProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.bg_course_progress));
            mTaskComment.setText(R.string.congratulation_finish_task);
            mCourseTitle.setText(mCourseTask.title);
            mRedo.setVisibility(View.GONE);
            mShareIcon.setVisibility(View.GONE);
            mShareCourse.setVisibility(View.GONE);
            mClose.setImageResource(R.drawable.close_black);
            mNextTask.setVisibility(View.VISIBLE);
        } else {
            mTitle.setBackgroundResource(R.drawable.icon_task_unfinish);
            mTitle.setText(R.string.task_unfinish);
            mProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.bg_unfinish_course_progress));
            mTaskComment.setText(R.string.comment_unfinish_task);
            mCourseTitle.setText(String.format(getString(R.string.finish_task_need_score), mCourseTask.activity.finishDetail));
            mRedo.setVisibility(View.VISIBLE);
            mShareIcon.setVisibility(View.GONE);
            mShareCourse.setVisibility(View.GONE);
            mNextTask.setVisibility(View.GONE);
            mClose.setImageResource(R.drawable.unfinish_close);
        }

        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mShareIcon.setOnClickListener(mShareListener);
        mShareCourse.setOnClickListener(mShareListener);
        if (mRedoClickListener != null) {
            mRedo.setOnClickListener(mRedoClickListener);
        }
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(UserApi.class)
                .getMyCourseLearningProgress(mTaskEvent.result.courseId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SubscriberProcessor<CourseLearningProgress>() {
                    @Override
                    public void onNext(final CourseLearningProgress courseLearningProgress) {
                        mProgressBar.setProgress(Math.round(courseLearningProgress.progress));
                        mNextTask.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (courseLearningProgress.nextTask != null) {
                                    EventBus.getDefault().postSticky(new MessageEvent<>(courseLearningProgress.nextTask
                                            , MessageEvent.LEARN_NEXT_TASK));
                                    if (getActivity() != null && !(getActivity() instanceof CourseProjectActivity)) {
                                        getActivity().finish();
                                    } else {
                                        dismiss();
                                    }
                                }
                            }
                        });
                    }
                });
    }

    private View.OnClickListener mShareListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                String shareText = Html.fromHtml(mCourseProject.summary).toString();
                ShareHelper
                        .builder()
                        .init(getActivity())
                        .setTitle(mCourseProject.getTitle())
                        .setText(shareText.length() > 20 ? shareText.substring(0, 20) : shareText)
                        .setUrl(EdusohoApp.app.host + "/course/" + mCourseProject.id)
                        .setImageUrl(mCourseProject.courseSet.cover.middle)
                        .build()
                        .share();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
        }
    };
}
