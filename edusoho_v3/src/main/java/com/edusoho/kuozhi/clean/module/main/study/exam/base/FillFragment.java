package com.edusoho.kuozhi.clean.module.main.study.exam.base;

import android.os.Bundle;

import com.edusoho.kuozhi.clean.bean.mystudy.ExamQuestions;

/**
 * Created by RexXiang on 2018/2/7.
 */

public class FillFragment extends BaseExamQuestionFragment {

    public FillFragment() {
    }

    public static FillFragment getInstance(ExamQuestions mQuestionsList) {
        FillFragment fillFragment = new FillFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("question_list", mQuestionsList);
        fillFragment.setArguments(bundle);
        return fillFragment;
    }
}
