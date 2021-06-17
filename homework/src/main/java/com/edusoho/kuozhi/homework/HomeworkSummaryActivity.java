package com.edusoho.kuozhi.homework;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.bean.TaskEvent;
import com.edusoho.kuozhi.clean.bean.TaskResultEnum;
import com.edusoho.kuozhi.clean.module.course.dialog.TaskFinishDialog;
import com.edusoho.kuozhi.clean.utils.biz.TaskFinishActionHelper;
import com.edusoho.kuozhi.clean.utils.biz.TaskFinishHelper;
import com.edusoho.kuozhi.homework.model.HomeWorkModel;
import com.edusoho.kuozhi.homework.model.HomeWorkResult;
import com.edusoho.kuozhi.homework.model.HomeworkProvider;
import com.edusoho.kuozhi.homework.util.HomeWorkLearnConfig;
import com.edusoho.kuozhi.v3.entity.lesson.PluginViewItem;
import com.edusoho.kuozhi.v3.listener.BaseLessonPluginCallback;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.provider.ModelProvider;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.ActivityUtil;
import com.edusoho.kuozhi.v3.util.ApiTokenUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Melomelon on 2015/10/13.
 */
public class HomeworkSummaryActivity extends ActionBarBaseActivity {

    public static final String HOMEWORK = "homework";
    public static final String EXERCISE = "exercise";
    public static final String TYPE = "type";
    public static final int REQUEST_DO = 0010;
    public static final String MEDIA_ID = "media_id";
    public static final String TASK_FINISH_HELPER = "task_finish_helper";
    public static final String COURSEPROJECT = "courseproject";
    public static final String COURSETASK = "coursetask";

    private int mLessonId;
    private int mMediaId;
    private String mType;

    private Bundle mBundle;
    private HomeworkProvider mHomeworkProvider;
    private HomeWorkResult mHomeWorkResult;
    private TaskFinishHelper mTaskFinishHelper;
    protected CourseProject mCourseProject;
    protected CourseTask mCourseTask;

    private FrameLayout mLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mBundle = intent.getExtras();

        if (mBundle != null) {
            mLessonId = mBundle.getInt(Const.LESSON_ID);
            mMediaId = mBundle.getInt(MEDIA_ID);
            mCourseProject = (CourseProject) mBundle.getSerializable(COURSEPROJECT);
            mCourseTask = (CourseTask) mBundle.getSerializable(COURSETASK);
        }
        mType = HOMEWORK;
        setContentView(R.layout.homework_summary_layout);
        mLoading = (FrameLayout) findViewById(R.id.load_layout);
        ModelProvider.init(getBaseContext(), this);
        initView();
    }

    public String getType() {
        return mType;
    }

    private void renderHomeworkView(final HomeWorkResult homeWorkResult) {
        String fragmentName;
        if (homeWorkResult == null || homeWorkResult.userId == 0 || "doing".equals(homeWorkResult.status)) {
            fragmentName = "com.edusoho.kuozhi.homework.ui.fragment.HomeWorkSummaryFragment";
        } else {
            fragmentName = "com.edusoho.kuozhi.homework.ui.fragment.HomeWorkResultFragment";
        }
        Bundle bundle = getIntent().getExtras();
        bundle.putString("type", mType);
        loadFragment(bundle, fragmentName);
    }

    protected void loadFragment(Bundle bundle, String fragmentName) {
        try {
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            Fragment fragment = Fragment.instantiate(getBaseContext(), fragmentName);
            if (bundle.getString(TYPE) == null) {
                bundle.putString(TYPE, "homework");
            }
            fragment.setArguments(bundle);
            fragmentTransaction.replace(android.R.id.content, fragment);
            fragmentTransaction.commit();
        } catch (Exception ex) {
            Log.d("HomeworkSummaryActivity", ex.toString());
        }
    }

    protected void initView() {
        setBackMode(BACK, "作业");
        loadHomeWork();
    }

    private void loadHomeWork() {
        String url = String.format(Const.HOMEWORK_RESULT, mLessonId);
        RequestUrl requestUrl = app.bindNewUrl(url, true);
        mLoading.setVisibility(View.VISIBLE);
        mHomeworkProvider.getHomeWorkResult(requestUrl, false).success(new NormalCallback<HomeWorkResult>() {
            @Override
            public void success(HomeWorkResult homeWorkModel) {
                mLoading.setVisibility(View.GONE);
                renderHomeworkView(homeWorkModel);
                mHomeWorkResult = homeWorkModel;
                if (homeWorkModel != null && homeWorkModel.userId != 0) {
                    HomeWorkLearnConfig.saveHomeworkLocalLearnConfig(mContext, "homework", homeWorkModel.homeworkId, true);
                }
            }
        }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError obj) {
                mLoading.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DO && resultCode == HomeworkActivity.RESULT_DO) {
            loadHomeWork();
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
                                        .show(getSupportFragmentManager(), "TaskFinishDialog");
                            }
                        }
                    });
            mTaskFinishHelper.finish();
        }

    }

    public static class Callback extends BaseLessonPluginCallback {

        private int mHomeworkId;

        public Callback(Context context) {
            super(context);
        }

        @Override
        protected RequestUrl getRequestUrl(int mediaId) {
            String url = new StringBuilder()
                    .append(String.format(Const.HOMEWORK_CONTENT, mediaId))
                    .toString();
            return ApiTokenUtil.bindNewUrl(mContext, url, true);
        }

        @Override
        public boolean click(View view) {
            if (super.click(view)) {
                return true;
            }
            Toast.makeText(mContext, "课程暂无作业", Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public void initState(PluginViewItem item) {
            super.initState(item);
            boolean isLearn = HomeWorkLearnConfig.getHomeworkLocalLearnConfig(mContext, "homework", mHomeworkId);
            setViewLearnState(isLearn);
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

                    mHomeworkId = homeWorkModel.getId();
                    boolean isLearn = HomeWorkLearnConfig.getHomeworkLocalLearnConfig(mContext, "homework", homeWorkModel.getId());
                    setViewStatus(isLearn ? PluginViewItem.ENABLE : PluginViewItem.NEW);
                }
            }).fail(this);
        }
    }
}
