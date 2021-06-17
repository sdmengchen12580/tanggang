package com.edusoho.kuozhi.clean.module.main.study.exam.base.Widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

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
public class ExamEssayQuestionWidget extends ExamBaseQuestionWidget {

    private EditText contentEdt;
    private List<String> mAnswer = new ArrayList<>(1);

    public ExamEssayQuestionWidget(Context context) {
        super(context);
    }

    public ExamEssayQuestionWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void initView(AttributeSet attrs) {

    }

    @Override
    protected void refreshView() {
        super.refreshView();
        contentEdt = (EditText) this.findViewById(R.id.essay_content);
        contentEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                sendChangeAnswerEvent(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void sendChangeAnswerEvent(String content) {
        mAnswer.clear();
        mAnswer.add(content);
        ExamAnswer.AnswersBean answersBean = new ExamAnswer.AnswersBean(mQuestion.getId(), mAnswer);
        EventBus.getDefault().postSticky(new MessageEvent<>(answersBean, MessageEvent.CHANGE_EXAM_ANSWER));
    }
}
