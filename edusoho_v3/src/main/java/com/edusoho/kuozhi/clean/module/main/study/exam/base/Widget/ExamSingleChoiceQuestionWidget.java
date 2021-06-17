package com.edusoho.kuozhi.clean.module.main.study.exam.base.Widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.bean.mystudy.ExamAnswer;
import com.edusoho.kuozhi.clean.module.main.study.exam.ExamActivity;
import com.edusoho.kuozhi.clean.module.main.study.exam.base.ExamBaseQuestionWidget;
import com.edusoho.kuozhi.clean.widget.ESRadioGroup;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by howzhi on 14-9-29.
 */
public class ExamSingleChoiceQuestionWidget extends ExamBaseQuestionWidget {

    protected ESRadioGroup radioGroup;
    private List<String> mAnswer = new ArrayList<>();

    public ExamSingleChoiceQuestionWidget(Context context) {
        super(context);
    }

    public ExamSingleChoiceQuestionWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initView(AttributeSet attrs) {

    }

    @Override
    protected void refreshView() {
        try {
            super.refreshView();
            radioGroup = this.findViewById(R.id.quetion_choice_group);
            ArrayList<String> metas = new ArrayList<>(mQuestion.getMetas().getChoices());
            radioGroup.setMetas(metas, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    radioGroup.setCheck((ESRadioGroup.ESChoiceOption) view.getParent());
                    sendChangeAnswerEvent();
                }
            });
            if (ExamActivity.mAnswer != null) {
                for (ExamAnswer.AnswersBean answer : ExamActivity.mAnswer.getAnswers()) {
                    if (mQuestion.getId().equals(answer.getQuestionId())) {
                        radioGroup.getChoiceAt(Integer.parseInt(mAnswer.get(0))).setChecked(true);
                        Log.d("flag--", "refreshView: ");
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void sendChangeAnswerEvent() {
        int count = radioGroup.getChildCount();
        mAnswer.clear();
        for (int i = 0; i < count; i++) {
            ESRadioGroup.ESChoiceOption choiceOption = radioGroup.getChoiceAt(i);
            if (choiceOption.isChecked()) {
                mAnswer.add(i + "");
                break;
            }
        }
        ExamAnswer.AnswersBean answersBean = new ExamAnswer.AnswersBean(mQuestion.getId(), mAnswer);
        EventBus.getDefault().postSticky(new MessageEvent<>(answersBean, MessageEvent.CHANGE_EXAM_ANSWER));
    }
}
