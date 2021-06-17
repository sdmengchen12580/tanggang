package com.edusoho.kuozhi.clean.module.main.study.exam.base;


import android.os.Bundle;

import com.edusoho.kuozhi.clean.bean.mystudy.ExamQuestions;

public class UncertainChoiceFragment extends BaseExamQuestionFragment {

    public UncertainChoiceFragment() {
    }

    public static UncertainChoiceFragment getInstance(ExamQuestions mQuestionsList) {
        UncertainChoiceFragment uncertainChoiceFragment = new UncertainChoiceFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("question_list", mQuestionsList);
        uncertainChoiceFragment.setArguments(bundle);
        return uncertainChoiceFragment;
    }
}
