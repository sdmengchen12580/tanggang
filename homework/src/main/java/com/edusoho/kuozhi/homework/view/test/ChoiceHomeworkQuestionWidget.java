package com.edusoho.kuozhi.homework.view.test;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewStub;

import com.edusoho.kuozhi.clean.widget.ESRadioGroup;
import com.edusoho.kuozhi.homework.ExerciseActivity;
import com.edusoho.kuozhi.homework.HomeworkActivity;
import com.edusoho.kuozhi.homework.HomeworkSummaryActivity;
import com.edusoho.kuozhi.homework.R;
import com.edusoho.kuozhi.homework.model.HomeWorkQuestion;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.model.bal.test.QuestionType;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by howzhi on 14-9-29.
 */
public class ChoiceHomeworkQuestionWidget extends BaseHomeworkQuestionWidget {

    protected ESRadioGroup radioGroup;

    public ChoiceHomeworkQuestionWidget(Context context) {
        super(context);
    }

    public ChoiceHomeworkQuestionWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void sendMsgToHomework() {
        Bundle bundle = new Bundle();
        bundle.putInt("index", mIndex - 1);
        bundle.putString("QuestionType", QuestionType.material.name());

        int count = radioGroup.getChildCount();
        ArrayList<String> data = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            ESRadioGroup.ESChoiceOption radioButton = radioGroup.getChoiceAt(i);
            if (radioButton.isChecked()) {
                data.add(i + "");
            }
        }

        bundle.putStringArrayList("data", data);
        if (HomeworkSummaryActivity.HOMEWORK.equals(mType)) {
            MessageEngine.getInstance().sendMsgToTaget(
                    HomeworkActivity.CHANGE_ANSWER, bundle, HomeworkActivity.class);
        } else {
            MessageEngine.getInstance().sendMsgToTaget(
                    ExerciseActivity.CHANGE_ANSWER, bundle, ExerciseActivity.class);
        }
    }

    @Override
    protected void invalidateData() {
        super.invalidateData();
        radioGroup = (ESRadioGroup) this.findViewById(R.id.homework_quetion_choice_group);

        List<String> metas = mQuestion.getMetas();
        if (metas != null) {
            radioGroup.setMetas(metas, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMsgToHomework();
                }
            });
        }

        restoreResult(mQuestion.getAnswer());
        parseQuestionAnswer();
    }

    @Override
    protected void restoreResult(List<String> resultData) {
        if (resultData == null) {
            return;
        }

        int count = radioGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            ESRadioGroup.ESChoiceOption child = radioGroup.getChoiceAt(i);
            for (String answer : resultData) {
                child.setClickable(false);
                if (answer.equals(String.valueOf(i))) {
                    child.setChecked(true);
                }
            }
        }
    }

    @Override
    protected void parseQuestionAnswer() {
        mWorkMode = PARSE;
        if (mQuestion.getResult() != null && mQuestion.getResult().resultId != 0) {
            enable(radioGroup, false);
            mAnalysisVS = (ViewStub) this.findViewById(R.id.hw_quetion_analysis);
            mAnalysisVS.setOnInflateListener(new ViewStub.OnInflateListener() {
                @Override
                public void onInflate(ViewStub viewStub, View view) {
                    initResultAnalysis(view);
                }
            });
            mAnalysisVS.inflate();
        }
    }

    @Override
    protected void initView(AttributeSet attrs) {
    }

    @Override
    public void setData(HomeWorkQuestion questionSeq, int index) {
        super.setData(questionSeq, index);
        invalidateData();
    }
}
