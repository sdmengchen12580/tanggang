package com.edusoho.kuozhi.v3.ui.fragment.test;

import android.app.Activity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.bean.TaskEvent;
import com.edusoho.kuozhi.clean.bean.TaskFinishType;
import com.edusoho.kuozhi.clean.module.course.dialog.TaskFinishDialog;
import com.edusoho.kuozhi.clean.utils.biz.TaskFinishActionHelper;
import com.edusoho.kuozhi.clean.utils.biz.TaskFinishHelper;
import com.edusoho.kuozhi.v3.adapter.test.TestpaperResultListAdapter;
import com.edusoho.kuozhi.v3.model.bal.test.Accuracy;
import com.edusoho.kuozhi.v3.model.bal.test.PaperResult;
import com.edusoho.kuozhi.v3.model.bal.test.QuestionType;
import com.edusoho.kuozhi.v3.model.bal.test.Testpaper;
import com.edusoho.kuozhi.v3.model.bal.test.TestpaperResultType;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.CourseDetailsTabActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.ui.test.TestpaperActivity;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.EduSohoButton;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;

import static com.edusoho.kuozhi.clean.bean.TaskFinishType.SUBMIT;

/**
 * Created by howzhi on 14-9-21.
 */
public class TestpaperResultFragment extends BaseFragment {

    private ListView      mListView;
    private TextView      mTotalView;
    private TextView      mReviewView;
    private EduSohoButton mResultParseBtn;

    private int           mTestpaperResultId;
    private Testpaper     mTestpaper;
    private int           mMediaId;
    private CourseProject mCourseProject;
    private CourseTask    mCourseTask;
    private PaperResult   mPaperResult;
    public static final String RESULT_ID      = "testpaperResultId";
    public static final String COURSE_PROJECT = "course_project";
    public static final String COURSE_TASK    = "course_task";

    @Override
    public String getTitle() {
        return "考试结果";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.testpaper_result_fragment);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mTestpaperResultId = bundle.getInt(RESULT_ID, 0);
            mTitle = bundle.getString(Const.ACTIONBAR_TITLE);
            mCourseProject = (CourseProject) bundle.getSerializable(COURSE_PROJECT);
            mCourseTask = (CourseTask) bundle.getSerializable(COURSE_TASK);
            mMediaId = bundle.getInt(Const.MEDIA_ID);
        }
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mResultParseBtn = (EduSohoButton) view.findViewById(R.id.testpaper_result_show);
        mListView = (ListView) view.findViewById(R.id.testpaper_result_listview);
        mTotalView = (TextView) view.findViewById(R.id.testpaper_result_total);
        mReviewView = (TextView) view.findViewById(R.id.testpaper_result_review);

        RequestUrl requestUrl = mActivity.app.bindUrl(Const.TESTPAPER_RESULT, true);
        requestUrl.setParams(new String[]{
                "id", mTestpaperResultId + ""
        });

        final LoadDialog loadDialog = LoadDialog.create(mActivity);
        loadDialog.show();

        mActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadDialog.dismiss();
                TestpaperResultType testpaperResultType = mActivity.handleJsonValue(
                        response, new TypeToken<TestpaperResultType>() {
                        });
                if (testpaperResultType == null) {
                    mResultParseBtn.setBackgroundColor(getResources().getColor(R.color.grey_cccccc));
                    return;
                }

                mTestpaper = testpaperResultType.testpaper;

                if (mTitle == null) {
                    mTitle = mTestpaper.name;
                    changeTitle(mTitle);
                }

                LinkedTreeMap<QuestionType, Accuracy> accuracy = testpaperResultType.accuracy;
                ArrayList<QuestionType> questionTypeSeqs = mTestpaper.metas.question_type_seq;
                TestpaperResultListAdapter adapter = new TestpaperResultListAdapter(
                        mContext, accuracy, questionTypeSeqs, R.layout.testpaper_result_item
                );

                mListView.setAdapter(adapter);
                mPaperResult = testpaperResultType.paperResult;
                if ("reviewing".equals(mPaperResult.status)) {
                    setTotalText(mTotalView, "待批阅");
                    mReviewView.setText(R.string.testpaper_reviewing);
                    if (TaskFinishType.fromString(mCourseTask.activity.finishType) == SUBMIT) {
                        showFinishDialog();
                    }
                } else if ("finished".equals(mPaperResult.status)) {
                    setTotalText(mTotalView, mPaperResult.score + "");
                    mReviewView.setText(
                            TextUtils.isEmpty(mPaperResult.teacherSay) ? "没有评语" : "评语:" + mPaperResult.teacherSay);
                    showFinishDialog();
                }

                mResultParseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putString(Const.ACTIONBAR_TITLE, mTitle);
                        bundle.putInt(TestpaperResultFragment.RESULT_ID, mTestpaperResultId);
                        bundle.putInt(Const.LESSON_ID, 0);
                        bundle.putStringArray(CourseDetailsTabActivity.TITLES, getTestpaperQSeq());
                        bundle.putStringArray(CourseDetailsTabActivity.LISTS, getTestpaperFragments());
                        startActivityWithBundle("TestpaperParseActivity", bundle);
                    }
                });
            }
        }, null);
    }

    private void showFinishDialog() {
        if (mCourseProject != null && mCourseTask != null && !mCourseTask.isFinish()) {
            TaskFinishHelper.Builder builder = new TaskFinishHelper.Builder()
                    .setCourseId(mCourseProject.id)
                    .setCourseTask(mCourseTask)
                    .setScore(mPaperResult.score)
                    .setEnableFinish(mCourseProject.enableFinish);
            TaskFinishHelper helper = new TaskFinishHelper(builder, getActivity())
                    .setActionListener(new TaskFinishActionHelper() {
                        @Override
                        public void onFinish(TaskEvent taskEvent) {
                            EventBus.getDefault().postSticky(new MessageEvent<>(mCourseTask, MessageEvent.FINISH_TASK_SUCCESS));
                            mCourseTask.result = taskEvent.result;
                            TaskFinishDialog.newInstance(taskEvent, mCourseTask, mCourseProject)
                                    .show(getActivity().getSupportFragmentManager(), "mTaskFinishDialog");
                        }

                        @Override
                        public void onDoing(TaskEvent taskEvent) {
                            mCourseTask.result = taskEvent.result;
                            final TaskFinishDialog dialog = TaskFinishDialog.newInstance(taskEvent, mCourseTask, mCourseProject);
                            dialog.setTestRedoAction(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString(Const.ACTIONBAR_TITLE, mTitle);
                                    bundle.putInt(Const.MEDIA_ID, mMediaId);
                                    bundle.putInt(Const.LESSON_ID, mCourseTask.id);
                                    bundle.putInt(Const.TESTPAPER_DO_TYPE, TestpaperActivity.REDO);
                                    bundle.putStringArray(CourseDetailsTabActivity.TITLES, getTestpaperQSeq());
                                    bundle.putStringArray(CourseDetailsTabActivity.LISTS, getTestpaperFragments());
                                    bundle.putSerializable(COURSE_PROJECT, mCourseProject);
                                    bundle.putSerializable(COURSE_TASK, mCourseTask);
                                    startActivityWithBundle("TestpaperActivity", bundle);
                                    dialog.dismiss();
                                    getActivity().finish();
                                }
                            });
                            dialog.show(getActivity().getSupportFragmentManager(), "mTaskFinishDialog");
                        }
                    });
            helper.finish();
        }
    }

    private void setTotalText(TextView rightText, String text) {
        StringBuffer stringBuffer = new StringBuffer("总分:");
        int start = stringBuffer.length();
        stringBuffer.append(text);

        SpannableString spannableString = new SpannableString(stringBuffer);
        int color = mContext.getResources().getColor(R.color.testpaper_result_item_total);
        spannableString.setSpan(
                new ForegroundColorSpan(color),
                start,
                stringBuffer.length(),
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        );
        rightText.setText(spannableString);
    }

    private ArrayList<Accuracy> getAccuracys(
            HashMap<QuestionType, Accuracy> accuracys
    ) {
        ArrayList<Accuracy> list = new ArrayList<Accuracy>();
        for (Accuracy accuracy : accuracys.values()) {
            list.add(accuracy);
        }

        return list;
    }

    private ArrayList<QuestionType> getQuestionTypes(
            HashMap<QuestionType, Accuracy> accuracy) {
        ArrayList<QuestionType> list = new ArrayList<QuestionType>();
        for (QuestionType type : accuracy.keySet()) {
            list.add(type);
        }

        return list;
    }

    private String[] getTestpaperQSeq() {
        ArrayList<QuestionType> questionTypeSeqs = mTestpaper.metas.question_type_seq;
        String[] TESTPAPER_QUESTION_TYPE = new String[questionTypeSeqs.size()];
        for (int i = 0; i < TESTPAPER_QUESTION_TYPE.length; i++) {
            TESTPAPER_QUESTION_TYPE[i] = questionTypeSeqs.get(i).title();
        }

        return TESTPAPER_QUESTION_TYPE;
    }

    private String[] getTestpaperFragments() {
        ArrayList<QuestionType> questionTypeSeqs = mTestpaper.metas.question_type_seq;
        String[] TESTPAPER_QUESTIONS = new String[questionTypeSeqs.size()];
        for (int i = 0; i < TESTPAPER_QUESTIONS.length; i++) {
            switch (questionTypeSeqs.get(i)) {
                case choice:
                    TESTPAPER_QUESTIONS[i] = "ChoiceFragment";
                    break;
                case single_choice:
                    TESTPAPER_QUESTIONS[i] = "SingleChoiceFragment";
                    break;
                case essay:
                    TESTPAPER_QUESTIONS[i] = "EssayFragment";
                    break;
                case uncertain_choice:
                    TESTPAPER_QUESTIONS[i] = "UncertainChoiceFragment";
                    break;
                case fill:
                    TESTPAPER_QUESTIONS[i] = "FillFragment";
                    break;
                case determine:
                    TESTPAPER_QUESTIONS[i] = "DetermineFragment";
                    break;
                case material:
                    TESTPAPER_QUESTIONS[i] = "MaterialFragment";
                    break;
            }
        }

        return TESTPAPER_QUESTIONS;
    }

}
