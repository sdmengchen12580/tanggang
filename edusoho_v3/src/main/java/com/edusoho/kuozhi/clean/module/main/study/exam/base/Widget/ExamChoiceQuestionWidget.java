package com.edusoho.kuozhi.clean.module.main.study.exam.base.Widget;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;


import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.bean.mystudy.ExamAnswer;
import com.edusoho.kuozhi.clean.module.main.study.exam.base.ExamBaseQuestionWidget;
import com.edusoho.kuozhi.clean.widget.ESRadioGroup;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by howzhi on 14-9-29.
 */
public class ExamChoiceQuestionWidget extends ExamBaseQuestionWidget {

    protected ESRadioGroup radioGroup;
    private List<String> mAnswer = new ArrayList<>();

    public ExamChoiceQuestionWidget(Context context) {
        super(context);
    }

    public ExamChoiceQuestionWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void initView(AttributeSet attrs) {

    }

    @Override
    protected void refreshView() {
        super.refreshView();
        radioGroup = this.findViewById(R.id.quetion_choice_group);

        ArrayList<String> metas;
        if (mQuestion.getMetas() == null) {
            metas = null;
        } else {
            metas = new ArrayList<>(mQuestion.getMetas().getChoices());
        }
        radioGroup.setMetas(metas, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioGroup.setCheck((ESRadioGroup.ESChoiceOption) view.getParent());
                sendChangeAnswerEvent();
            }
        });
    }

    private void sendChangeAnswerEvent() {
        int count = radioGroup.getChildCount();
        mAnswer.clear();
        for (int i = 0; i < count; i++) {
            ESRadioGroup.ESChoiceOption choiceOption = radioGroup.getChoiceAt(i);
            if (choiceOption.isChecked()) {
                mAnswer.add(Integer.toString(i));
            }
        }
        ExamAnswer.AnswersBean answersBean = new ExamAnswer.AnswersBean(mQuestion.getId(), mAnswer);
        EventBus.getDefault().postSticky(new MessageEvent<>(answersBean, MessageEvent.CHANGE_EXAM_ANSWER));
    }
}
