package com.edusoho.kuozhi.v3.ui.fragment.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;

import com.android.volley.Response;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.kuozhi.v3.adapter.test.TestpaperInfoAdapter;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.test.QuestionType;
import com.edusoho.kuozhi.v3.model.bal.test.Testpaper;
import com.edusoho.kuozhi.v3.model.bal.test.TestpaperItem;
import com.edusoho.kuozhi.v3.model.bal.test.TestpaperResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.CourseDetailsTabActivity;
import com.edusoho.kuozhi.v3.ui.FragmentPageActivity;
import com.edusoho.kuozhi.v3.ui.LessonActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.ui.test.TestpaperActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.ViewUtil;
import com.google.gson.reflect.TypeToken;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by howzhi on 14-9-23.
 */
public class TestpaperLessonFragment extends BaseFragment {

    public static final String RESULT_ID      = "resultId";
    public static final String COURSE_PROJECT = "course_project";
    public static final String COURSE_TASK    = "course_task";

    @ViewUtil("testpaper_listview")
    private ListView mListView;

    @ViewUtil("testpaper_tools_layout")
    private View mToolsLayout;

    @ViewUtil("testpaper_do_btn")
    private View mTestpaperDoBtn;

    @ViewUtil("testpaper_redo_btn")
    private View mTestpaperReDoBtn;

    @ViewUtil("testpaper_show_btn")
    private View mTestpaperShowBtn;

    private int           mTestId;
    private int           mLessonId;
    private int           mResultId;
    private String        mStstus;
    private String        mTitle;
    private CourseProject mCourseProject;
    private CourseTask    mCourseTask;


    private Testpaper mTestpaper;

    @Override
    public String getTitle() {
        return "????????????";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setContainerView(R.layout.testpaper_fragment_layout);
        if (getActivity() != null && getActivity() instanceof LessonActivity) {
            ((LessonActivity) getActivity()).setLearnTaskFinishVisibility(false);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mTestId = bundle.getInt(Const.MEDIA_ID);
            mLessonId = bundle.getInt(Const.LESSON_ID);
            mResultId = bundle.getInt(RESULT_ID);
            mStstus = bundle.getString(Const.STATUS);
            mTitle = bundle.getString(Const.ACTIONBAR_TITLE);
            mCourseProject = (CourseProject) bundle.getSerializable(COURSE_PROJECT);
            mCourseTask = (CourseTask) bundle.getSerializable(COURSE_TASK);
        }
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        viewInject(view);

        loadTestpaperInfo();
        if ("nodo".equals(mStstus)) {
            //nothing
        } else if ("reviewing".equals(mStstus)) {
            mTestpaperShowBtn.setVisibility(View.VISIBLE);
            mTestpaperDoBtn.setVisibility(View.GONE);
        } else if ("finished".equals(mStstus)) {
            mTestpaperDoBtn.setVisibility(View.GONE);
            mTestpaperReDoBtn.setVisibility(View.VISIBLE);
            mTestpaperShowBtn.setVisibility(View.VISIBLE);
        }
        showToolsByAnim();

        mTestpaperReDoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doTestpaper(TestpaperActivity.REDO);
            }
        });
        mTestpaperDoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doTestpaper(TestpaperActivity.DO);
            }
        });

        mTestpaperShowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                app.mEngine.runNormalPlugin("FragmentPageActivity", mActivity, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(FragmentPageActivity.FRAGMENT, "TestpaperResultFragment");
                        startIntent.putExtra(Const.ACTIONBAR_TITLE, mTitle + " ????????????");
                        startIntent.putExtra(Const.MEDIA_ID, mTestId);
                        startIntent.putExtra(TestpaperResultFragment.RESULT_ID, mResultId);
                        startIntent.putExtra(Const.STATUS, mStstus);
                        startIntent.putExtra(COURSE_PROJECT, mCourseProject);
                        startIntent.putExtra(COURSE_TASK, mCourseTask);
                    }
                });
                mActivity.finish();
            }
        });
    }

    private void doTestpaper(int type) {
        Bundle bundle = new Bundle();
        bundle.putString(Const.ACTIONBAR_TITLE, mTitle);
        bundle.putInt(Const.MEDIA_ID, mTestId);
        bundle.putInt(Const.LESSON_ID, mLessonId);
        bundle.putInt(Const.TESTPAPER_DO_TYPE, type);
        bundle.putStringArray(CourseDetailsTabActivity.TITLES, getTestpaperQSeq());
        bundle.putStringArray(CourseDetailsTabActivity.LISTS, getTestpaperFragments());
        bundle.putSerializable(COURSE_PROJECT, mCourseProject);
        bundle.putSerializable(COURSE_TASK, mCourseTask);
        startActivityWithBundle("TestpaperActivity", bundle);
        mActivity.finish();
    }

    private String[] getTestpaperQSeq() {
        if (mTestpaper == null || mTestpaper.metas == null) {
            return new String[0];
        }
        ArrayList<QuestionType> questionTypeSeqs = mTestpaper.metas.question_type_seq;
        String[] TESTPAPER_QUESTION_TYPE = new String[questionTypeSeqs.size()];
        for (int i = 0; i < TESTPAPER_QUESTION_TYPE.length; i++) {
            TESTPAPER_QUESTION_TYPE[i] = questionTypeSeqs.get(i).title();
        }

        return TESTPAPER_QUESTION_TYPE;
    }

    private String[] getTestpaperFragments() {
        if (mTestpaper.metas == null) {
            return new String[0];
        }
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void showToolsByAnim() {
        mToolsLayout.setVisibility(View.VISIBLE);
    }

    private void loadTestpaperInfo() {
        RequestUrl requestUrl = app.bindUrl(Const.TESTPAPER_INFO, true);
        requestUrl.setParams(new String[]{
                "testId", mTestId + ""
        });

        mActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                TestpaperResult testpaperResult = mActivity.parseJsonValue(
                        response, new TypeToken<TestpaperResult>() {
                        });

                if (testpaperResult == null) {
                    return;
                }

                mTestpaper = testpaperResult.testpaper;
                Log.d(null, "mTestpaper->" + mTestpaper);
                initTestPaperItem(testpaperResult);
            }
        }, null);
    }

    private void initTestPaperItem(TestpaperResult testpaperResult) {
        Testpaper testpaper = testpaperResult.testpaper;

        ArrayList<TestpaperItem> contents = new ArrayList<TestpaperItem>();
        contents.add(new TestpaperItem(
                "????????????", new String[]{testpaper.name}, false
        ));
        contents.add(new TestpaperItem(
                "????????????", new String[]{AppUtil.coverCourseAbout(testpaper.description)}, false
        ));
        contents.add(new TestpaperItem(
                "????????????", new String[]{
                getTestpaperInstruction(testpaperResult, testpaper.score),
                String.format("????????????:%s", testpaper.limitedTime == 0 ? "?????????" : testpaper.limitedTime + "??????")
        }, true
        ));
        contents.add(new TestpaperItem(
                "????????????", new String[]{
                String.format("????????????????????????%s????????????????????????????????????",
                        testpaper.limitedTime == 0 ? "?????????" : testpaper.limitedTime + "??????"),
                "??????????????????????????????????????????????????????????????????"
        }, true
        ));

        TestpaperInfoAdapter adapter = new TestpaperInfoAdapter(
                mContext, contents, R.layout.testpaperinfo_item
        );
        mListView.setAdapter(adapter);
    }

    private String getTestpaperInstruction(TestpaperResult testpaperResult, double score) {
        HashMap<QuestionType, Integer> items = testpaperResult.items;
        Testpaper testpaper = testpaperResult.testpaper;
        if (items.isEmpty()) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer("???????????????");
        String format = "";
        ArrayList<QuestionType> types = testpaper.metas.question_type_seq;
        for (QuestionType type : types) {
            switch (type) {
                case choice:
                    format = "?????????%d???";
                    break;
                case determine:
                    format = "?????????%d???";
                    break;
                case essay:
                    format = "?????????%d???";
                    break;
                case fill:
                    format = "?????????%d???";
                    break;
                case material:
                    format = "?????????%d???";
                    break;
                case single_choice:
                    format = "?????????%d???";
                    break;
                case uncertain_choice:
                    format = "????????????%d???";
                    break;
            }

            stringBuffer.append(String.format(format, items.get(type)));
            stringBuffer.append(",");
        }


        stringBuffer.append(String.format("??????%s???", new DecimalFormat("#.00").format(score)));
        return stringBuffer.toString();
    }
}
