package com.edusoho.kuozhi.clean.utils.biz;

import android.content.Context;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.api.CourseApi;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.kuozhi.clean.bean.TaskEvent;
import com.edusoho.kuozhi.clean.bean.TaskFinishType;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.module.course.task.catalog.TaskTypeEnum;
import com.edusoho.kuozhi.clean.utils.StringUtils;
import com.edusoho.kuozhi.clean.utils.ToastUtils;
import com.edusoho.kuozhi.v3.EdusohoApp;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.edusoho.kuozhi.clean.bean.TaskFinishType.TIME;
import static com.edusoho.kuozhi.clean.module.course.task.catalog.TaskTypeEnum.DOC;
import static com.edusoho.kuozhi.clean.module.course.task.catalog.TaskTypeEnum.PPT;
import static com.edusoho.kuozhi.clean.module.course.task.catalog.TaskTypeEnum.TEXT;
import static com.edusoho.kuozhi.clean.module.course.task.catalog.TaskTypeEnum.VIDEO;

/**
 * Created by JesseHuang on 2017/5/11.
 */

public class TaskFinishHelper {

    private static final int    INTERVAL_TIME = 60 * 1000;
    public static final  String DEFAULT       = null;

    public static final String DOING  = "doing";
    public static final String START  = "start";
    public static final String FINISH = "finish";

    private int            mEnableFinish;
    private int            mCourseId;
    private double         mScore;
    private CourseTask     mCourseTask;
    private ActionListener mActionListener;
    private Timer          mTimer;
    private String              mLastTime  = "";
    private Map<String, String> mFieldMaps = new HashMap<>();

    private Context mContext;

    public TaskFinishHelper(Builder builder, Context context) {
        this.mEnableFinish = builder.mEnableFinish;
        this.mCourseId = builder.mCourseId;
        this.mCourseTask = builder.mCourseTask;
        this.mScore = builder.mScore;
        this.mContext = context;
    }

    public TaskFinishHelper setActionListener(ActionListener actionListener) {
        this.mActionListener = actionListener;
        return this;
    }

    public void finish() {
//        if (mEnableFinish == 1) {
//            onRecord(FINISH);
//        } else {
        TaskTypeEnum taskType = TaskTypeEnum.fromString(mCourseTask.type);
        switch (taskType) {
            case VIDEO:
                if (mEnableFinish == 1) {
                    onRecord(FINISH);
                } else {
                    String videoLimit = TIME.toString().equalsIgnoreCase(mCourseTask.activity.finishType)
                            ? mContext.getString(R.string.task_finish_limit, mCourseTask.activity.finishDetail)
                            : mContext.getString(R.string.video_task_finish_limit);
                    ToastUtils.show(mContext, videoLimit);
                }
                break;
            case AUDIO:
                if (mEnableFinish == 1) {
                    onRecord(FINISH);
                } else {
                    ToastUtils.show(mContext, R.string.audio_task_finish_limit);
                }
                break;
            case TEXT:
            case DOC:
                if (mEnableFinish == 1) {
                    onRecord(FINISH);
                } else {
                    ToastUtils.show(mContext, mContext.getString(R.string.task_finish_limit, mCourseTask.activity.finishDetail));
                }
                break;
            case PPT:
                if (mEnableFinish == 1) {
                    onRecord(FINISH);
                } else {
                    String pptLimit = TIME.toString().equalsIgnoreCase(mCourseTask.activity.finishType)
                            ? mContext.getString(R.string.task_finish_limit, mCourseTask.activity.finishDetail)
                            : mContext.getString(R.string.ppt_task_finish_limit);
                    ToastUtils.show(mContext, pptLimit);
                }
                break;
            case TESTPAPER:
                TaskFinishType finishType = TaskFinishType.fromString(mCourseTask.activity.finishType);
                if (TaskFinishType.SUBMIT == finishType) {
                    stickyFinish();
                } else if (TaskFinishType.SCORE == finishType) {
                    if (mScore >= mCourseTask.activity.finishDetail) {
                        stickyFinish();
                    } else {
                        doing();
                    }
                }
                break;
            case HOMEWORK:
            case EXERCISE:
                onRecord(FINISH);
                break;
        }
    }

    public void stickyFinish() {
        onRecord(FINISH);
    }

    public void doing() {
        onRecord(DOING);
    }

    private void onRecord(String status) {
        if (mCourseTask == null) {
            throw new RuntimeException("CourseTask is null");
        }
        if (mActionListener == null) {
            throw new RuntimeException("actionListener cannot be null!");
        }
        if (mCourseTask.isFinish()) {
            return;
        }
        getTaskResult(status)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SubscriberProcessor<TaskEvent>() {
                    @Override
                    public void onNext(TaskEvent taskEvent) {
                        mCourseTask.result = taskEvent.result;
                        mLastTime = taskEvent.lastTime;
                        if (FINISH.equals(taskEvent.result.status)) {
                            mActionListener.onFinish(taskEvent);
                            mCourseTask.result = taskEvent.result;
                            onDestroyTimer();
                        } else if (START.equals(taskEvent.result.status)) {
                            mActionListener.onDoing(taskEvent);
                            mCourseTask.result = taskEvent.result;
                        }
                    }
                });
    }

    public void onInvoke() {
        if (!mCourseTask.isFinish() && mCourseTask.activity != null) {
            TaskTypeEnum taskType = TaskTypeEnum.fromString(mCourseTask.type);
            if ((taskType == VIDEO && TIME == TaskFinishType.fromString(mCourseTask.activity.finishType))
                    || (taskType == DOC && null == TaskFinishType.fromString(mCourseTask.activity.finishType))
                    || (taskType == TEXT && null == TaskFinishType.fromString(mCourseTask.activity.finishType))
                    || (taskType == PPT && TIME == TaskFinishType.fromString(mCourseTask.activity.finishType))) {
                if (mTimer == null) {
                    mTimer = new Timer();
                }
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        doing();
                    }
                }, 0, INTERVAL_TIME);
            }
        }
    }

    private Observable<TaskEvent> getTaskResult(String status) {
        if (DOING.equals(status)) {
            mFieldMaps.clear();
            if (!StringUtils.isEmpty(mLastTime)) {
                mFieldMaps.put("lastTime", mLastTime);
            }
            return HttpUtils.getInstance()
                    .addTokenHeader(EdusohoApp.app.token)
                    .createApi(CourseApi.class)
                    .setCourseTaskDoing(mCourseId, mCourseTask.id, mFieldMaps);
        } else {
            return HttpUtils.getInstance()
                    .addTokenHeader(EdusohoApp.app.token)
                    .createApi(CourseApi.class)
                    .setCourseTaskFinish(mCourseId, mCourseTask.id);
        }
    }

    public void onDestroyTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    public static class Builder {
        private int        mEnableFinish;
        private int        mCourseId;
        private double     mScore;
        private CourseTask mCourseTask;

        public Builder setCourseId(int courseId) {
            this.mCourseId = courseId;
            return this;
        }

        public Builder setCourseTask(CourseTask courseTask) {
            this.mCourseTask = courseTask;
            return this;
        }

        public Builder setEnableFinish(int enableFinish) {
            mEnableFinish = enableFinish;
            return this;
        }

        public Builder setScore(double score) {
            mScore = score;
            return this;
        }

        public TaskFinishHelper build(Context context) {
            return new TaskFinishHelper(this, context);
        }
    }

    public interface ActionListener {
        void onFinish(TaskEvent taskEvent);

        void onDoing(TaskEvent taskEvent);

        void onError(Throwable e);
    }
}
