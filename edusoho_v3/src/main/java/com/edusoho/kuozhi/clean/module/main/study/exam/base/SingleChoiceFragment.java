package com.edusoho.kuozhi.clean.module.main.study.exam.base;

import android.os.Bundle;
import com.edusoho.kuozhi.clean.bean.mystudy.ExamQuestions;

/**
 * Created by RexXiang on 2018/2/7.
 */

public class SingleChoiceFragment extends BaseExamQuestionFragment {

    public SingleChoiceFragment() {
    }

    public static SingleChoiceFragment getInstance(ExamQuestions mQuestionsList) {
        SingleChoiceFragment singleChoiceFragment = new SingleChoiceFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("question_list", mQuestionsList);
        singleChoiceFragment.setArguments(bundle);
        return singleChoiceFragment;
    }
}
