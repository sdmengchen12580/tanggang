package com.edusoho.kuozhi.homework;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.bean.TaskEvent;
import com.edusoho.kuozhi.clean.module.course.dialog.TaskFinishDialog;
import com.edusoho.kuozhi.clean.utils.biz.TaskFinishActionHelper;
import com.edusoho.kuozhi.clean.utils.biz.TaskFinishHelper;
import com.edusoho.kuozhi.homework.model.ExerciseProvider;
import com.edusoho.kuozhi.homework.model.HomeWorkModel;
import com.edusoho.kuozhi.homework.model.HomeworkProvider;
import com.edusoho.kuozhi.homework.util.HomeWorkLearnConfig;
import com.edusoho.kuozhi.v3.entity.lesson.PluginViewItem;
import com.edusoho.kuozhi.v3.listener.BaseLessonPluginCallback;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.provider.ModelProvider;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.util.ApiTokenUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by howzhi on 15/11/2.
 */
public class ExerciseSummaryActivity extends HomeworkSummaryActivity {

    private ExerciseProvider mExerciseProvider;

    @Override
    public String getType() {
        return EXERCISE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        if (mCourseProject != null && mCourseTask != null && !mCourseTask.isFinish()) {
            TaskFinishHelper.Builder builder = new TaskFinishHelper.Builder()
                    .setCourseId(mCourseProject.id)
                    .setCourseTask(mCourseTask)
                    .setEnableFinish(mCourseProject.enableFinish);

            TaskFinishHelper mTaskFinishHelper = new TaskFinishHelper(builder, this)
                    .setActionListener(new TaskFinishActionHelper() {
                        @Override
                        public void onFinish(TaskEvent taskEvent) {
                            EventBus.getDefault().postSticky(new MessageEvent<>(mCourseTask, MessageEvent.FINISH_TASK_SUCCESS));
                            mCourseTask.result = taskEvent.result;
                            if (mCourseProject.enableFinish == 0) {
                                TaskFinishDialog.newInstance(taskEvent, mCourseTask, mCourseProject)
                                        .show(getSupportFragmentManager(), "mTaskFinishDialog");
                            }
                        }
                    });
            mTaskFinishHelper.finish();
        }
        int exerciseId = data.getIntExtra(ExerciseActivity.EXERCISE_ID, 0);
        Bundle bundle = new Bundle();
        bundle.putInt(ExerciseParseActivity.EXERCISE_ID, exerciseId);
        app.mEngine.runNormalPluginWithBundle("ExerciseParseActivity", mContext, bundle);
    }

    @Override
    protected void initView() {
        setBackMode(BACK, "练习");
        renderExerciseView();
    }

    private void renderExerciseView() {
        String fragmentName = "com.edusoho.kuozhi.homework.ui.fragment.HomeWorkSummaryFragment";
        Bundle bundle = getIntent().getExtras();
        bundle.putString(TYPE, "exercise");
        loadFragment(bundle, fragmentName);
    }

    public static class Callback extends BaseLessonPluginCallback {
        private int mExerciseId;

        public Callback(Context context) {
            super(context);
        }

        @Override
        protected RequestUrl getRequestUrl(int lessonId) {
            String url = new StringBuilder()
                    .append(String.format(Const.EXERCISE_CONTENT, lessonId))
                    .append("?_idType=lesson")
                    .toString();
            return ApiTokenUtil.bindNewUrl(mContext, url, true);
        }

        @Override
        public boolean click(View view) {
            if (super.click(view)) {
                return true;
            }
            Toast.makeText(mContext, "课程暂无练习", Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public void initState(PluginViewItem item) {
            super.initState(item);
            boolean learnState = HomeWorkLearnConfig.getHomeworkLocalLearnConfig(mContext, "exercise", mExerciseId);
            setViewLearnState(learnState);
        }

        @Override
        protected void loadPlugin(Bundle bundle) {
            int mediaId = bundle.getInt(MEDIA_ID, 0);
            RequestUrl requestUrl = getRequestUrl(mediaId);
            HomeworkProvider provider = ModelProvider.initProvider(mContext, HomeworkProvider.class);
            provider.getHomeWork(requestUrl).success(new NormalCallback<HomeWorkModel>() {
                @Override
                public void success(HomeWorkModel homeWorkModel) {
                    if (homeWorkModel == null || homeWorkModel.getId() == 0) {
                        setViewStatus(PluginViewItem.UNENABLE);
                        return;
                    }

                    mExerciseId = homeWorkModel.getId();
                    boolean isLearn = HomeWorkLearnConfig.getHomeworkLocalLearnConfig(mContext, "exercise", mExerciseId);
                    setViewStatus(isLearn ? PluginViewItem.ENABLE : PluginViewItem.NEW);
                }
            }).fail(this);
        }
    }
}
