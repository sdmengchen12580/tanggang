package com.edusoho.kuozhi.clean.module.main.study.exam.base;

import android.os.Bundle;

import com.edusoho.kuozhi.clean.bean.mystudy.ExamQuestions;

/**
 * Created by RexXiang on 2018/2/7.
 */

public class EssayFragment extends BaseExamQuestionFragment {

    public EssayFragment() {
    }

    public static EssayFragment getInstance(ExamQuestions mQuestionsList) {
        EssayFragment essayFragment = new EssayFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("question_list", mQuestionsList);
        essayFragment.setArguments(bundle);
        return essayFragment;
    }
}
