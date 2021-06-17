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
public class

SingleChoiceHomeworkQuestionWidget extends BaseHomeworkQuestionWidget {

    protected ESRadioGroup radioGroup;

    public SingleChoiceHomeworkQuestionWidget(Context context) {
        super(context);
    }

    public SingleChoiceHomeworkQuestionWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initView(AttributeSet attrs) {
    }

    @Override
    protected void invalidateData() {
        super.invalidateData();
        radioGroup = (ESRadioGroup) this.findViewById(R.id.hw_quetion_choice_group);

        List<String> metas = mQuestion.getMetas();
        radioGroup.setMetas(metas, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioGroup.setCheck((ESRadioGroup.ESChoiceOption) (v.getParent()));
                sendMsgToTestpaper();
            }
        });
        restoreResult(mQuestion.getAnswer());
        parseQuestionAnswer();
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

    protected void sendMsgToTestpaper() {
        Bundle bundle = new Bundle();
        bundle.putInt("index", mIndex - 1);
        bundle.putString("QuestionType", QuestionType.material.name());
        int count = radioGroup.getChildCount();
        ArrayList<String> data = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            if (radioGroup.getChoiceAt(i).isChecked()) {
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
    public void setData(HomeWorkQuestion questionSeq, int index) {
        super.setData(questionSeq, index);
        invalidateData();
    }
}
