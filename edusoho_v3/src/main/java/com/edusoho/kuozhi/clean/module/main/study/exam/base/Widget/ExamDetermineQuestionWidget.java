package com.edusoho.kuozhi.clean.module.main.study.exam.base.Widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.bean.mystudy.ExamAnswer;
import com.edusoho.kuozhi.clean.module.main.study.exam.base.ExamBaseQuestionWidget;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by howzhi on 14-9-29.
 */
public class ExamDetermineQuestionWidget extends ExamBaseQuestionWidget {

    protected RadioGroup radioGroup;
    private List<String> mAnswers = new ArrayList<>();

    public ExamDetermineQuestionWidget(Context context) {
        super(context);
    }

    public ExamDetermineQuestionWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void initView(AttributeSet attrs) {

    }

    @Override
    protected void refreshView() {
        super.refreshView();
        radioGroup = (RadioGroup) findViewById(R.id.question_result_radio);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int index) {
                sendChangeAnswerEvent();
            }
        });
    }

    private void sendChangeAnswerEvent() {
        int count = radioGroup.getChildCount();
        mAnswers.clear();
        for (int i = 0; i < count; i++) {
            CompoundButton radioButton = (CompoundButton) radioGroup.getChildAt(i);
            if (radioButton.isChecked()) {
                mAnswers.add(i + "");
            }
        }
        ExamAnswer.AnswersBean answersBean = new ExamAnswer.AnswersBean(mQuestion.getId(), mAnswers);
        EventBus.getDefault().postSticky(new MessageEvent<>(answersBean, MessageEvent.CHANGE_EXAM_ANSWER));
    }
}
