package com.edusoho.kuozhi.v3.view.test;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewStub;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.widget.ESRadioGroup;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.bal.test.MaterialQuestionTypeSeq;
import com.edusoho.kuozhi.v3.model.bal.test.Question;
import com.edusoho.kuozhi.v3.model.bal.test.QuestionType;
import com.edusoho.kuozhi.v3.model.bal.test.QuestionTypeSeq;
import com.edusoho.kuozhi.v3.ui.test.TestpaperActivity;

import java.util.ArrayList;


/**
 * Created by howzhi on 14-9-29.
 */
public class SingleChoiceQuestionWidget extends BaseQuestionWidget {

    protected ESRadioGroup radioGroup;

    public SingleChoiceQuestionWidget(Context context) {
        super(context);
    }

    public SingleChoiceQuestionWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initView(AttributeSet attrs) {
    }

    @Override
    protected void restoreResult(ArrayList resultData) {
        int count = radioGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            ESRadioGroup.ESChoiceOption child = radioGroup.getChoiceAt(i);
            child.setClickable(false);
            for (Object answer : resultData) {
                if (answer.equals(String.valueOf(i))) {
                    child.setChecked(true);
                    break;
                }
            }
        }
    }

    @Override
    protected void invalidateData() {
        radioGroup = (ESRadioGroup) this.findViewById(R.id.quetion_choice_group);
        Question mQuestion = mQuestionSeq.question;
        ArrayList<String> metas = mQuestion.metas;
        radioGroup.setMetas(metas, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioGroup.setCheck((ESRadioGroup.ESChoiceOption) v.getParent());
                sendMsgToTestpaper();
            }
        });

        if (isAnalysis()) {
            mAnalysisVS = (ViewStub) this.findViewById(R.id.quetion_choice_analysis);
            mAnalysisVS.setOnInflateListener(new ViewStub.OnInflateListener() {
                @Override
                public void onInflate(ViewStub viewStub, View view) {
                    initResultAnalysis(view);
                    initQuestionResult();
                }
            });
            mAnalysisVS.inflate();
        }
        super.invalidateData();
    }

    private void initQuestionResult() {
        int count = radioGroup.getChildCount();
        for (int i = 0; i < count; i++) {

            ESRadioGroup.ESChoiceOption child = radioGroup.getChoiceAt(i);
            child.setClickable(false);
            for (String answer : mQuestion.answer) {
                if (answer.equals(String.valueOf(i))) {
                    child.setChecked(true);
                    break;
                }
            }

        }
    }

    protected void sendMsgToTestpaper() {
        Bundle bundle = new Bundle();
        bundle.putInt("index", mIndex - 1);
        if (mQuestionSeq instanceof MaterialQuestionTypeSeq) {
            bundle.putString("QuestionType", QuestionType.material.name());
        } else {
            bundle.putString("QuestionType", mQuestionSeq.question.type.name());
        }
        int count = radioGroup.getChildCount();
        ArrayList<String> data = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ESRadioGroup.ESChoiceOption choiceOption = radioGroup.getChoiceAt(i);
            if (choiceOption.isChecked()) {
                data.add(i + "");
            }
        }

        bundle.putStringArrayList("data", data);
        EdusohoApp.app.sendMsgToTarget(
                TestpaperActivity.CHANGE_ANSWER, bundle, getTargetClass());
    }

    @Override
    public void setData(QuestionTypeSeq questionSeq, int index) {
        super.setData(questionSeq, index);
        invalidateData();
    }

    private boolean isAnalysis() {
        return mQuestion.testResult != null;
    }
}
