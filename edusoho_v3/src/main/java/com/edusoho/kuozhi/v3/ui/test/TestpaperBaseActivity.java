package com.edusoho.kuozhi.v3.ui.test;


import com.edusoho.kuozhi.v3.model.bal.Answer;
import com.edusoho.kuozhi.v3.model.bal.test.PaperResult;
import com.edusoho.kuozhi.v3.model.bal.test.QuestionType;
import com.edusoho.kuozhi.v3.model.bal.test.QuestionTypeSeq;
import com.edusoho.kuozhi.v3.model.bal.test.Testpaper;
import com.edusoho.kuozhi.v3.ui.CourseDetailsTabActivity;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by howzhi on 14-10-10.
 */
public class TestpaperBaseActivity extends CourseDetailsTabActivity {

    protected Testpaper mTestpaper;
    protected PaperResult mTestpaperResult;
    protected ArrayList<Integer> mFavorites;

    protected LinkedTreeMap<QuestionType, ArrayList<Answer>> answerMap;

    protected LinkedTreeMap<QuestionType, ArrayList<QuestionTypeSeq>> mQuestions;

    public ArrayList<QuestionTypeSeq> getQuesions(QuestionType type) {
        if (mQuestions == null) {
            return null;
        }
        return mQuestions.get(type);
    }

    public PaperResult getPaperResult() {
        return mTestpaperResult;
    }

    public ArrayList<Integer> getFavorites() {
        return mFavorites;
    }
}
