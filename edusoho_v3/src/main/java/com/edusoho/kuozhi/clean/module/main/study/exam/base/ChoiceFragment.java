package com.edusoho.kuozhi.clean.module.main.study.exam.base;


import android.os.Bundle;
import com.edusoho.kuozhi.clean.bean.mystudy.ExamQuestions;

public class ChoiceFragment extends BaseExamQuestionFragment {

    public ChoiceFragment() {
    }

    public static ChoiceFragment getInstance(ExamQuestions mQuestionsList) {
        ChoiceFragment choiceFragment = new ChoiceFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("question_list", mQuestionsList);
        choiceFragment.setArguments(bundle);
        return choiceFragment;
    }
}
