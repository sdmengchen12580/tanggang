package com.edusoho.kuozhi.clean.module.main.study.exam.base;

import android.os.Bundle;

import com.edusoho.kuozhi.clean.bean.mystudy.ExamQuestions;

/**
 * Created by RexXiang on 2018/2/7.
 */

public class DetermineFragment extends BaseExamQuestionFragment {

    public DetermineFragment() {
    }

    public static DetermineFragment getInstance(ExamQuestions mQuestionsList) {
        DetermineFragment determineFragment = new DetermineFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("question_list", mQuestionsList);
        determineFragment.setArguments(bundle);
        return determineFragment;
    }
}
