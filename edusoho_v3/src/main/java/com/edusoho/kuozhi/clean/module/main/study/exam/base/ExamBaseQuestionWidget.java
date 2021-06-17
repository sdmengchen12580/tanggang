package com.edusoho.kuozhi.clean.module.main.study.exam.base;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.ViewStub;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.mystudy.ExamModel;
import com.edusoho.kuozhi.v3.util.html.EduImageGetterHandler;
import com.edusoho.kuozhi.v3.util.html.EduTagHandler;

/**
 * Created by RexXiang on 2018/2/8.
 */

public abstract class ExamBaseQuestionWidget extends RelativeLayout {

    protected TextView                stemView;
    protected Context                 mContext;
    protected ViewStub                mAnal1ysisVS;
    protected ExamModel.QuestionsBean mQuestion;
    protected int                     mIndex;


    public ExamBaseQuestionWidget(Context context) {
        super(context);
        mContext = context;
        initView(null);
    }

    public ExamBaseQuestionWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(attrs);
    }

    protected abstract void initView(AttributeSet attrs);

    public void setQuestion(ExamModel.QuestionsBean mQuestion, int index) {
        this.mQuestion = mQuestion;
        this.mIndex = index;
        refreshView();
    }

    protected void refreshView() {
        stemView = (TextView) this.findViewById(R.id.question_stem);
        String stem = String.format(
                "%d. (<font color='#ffca4a'>%.2f分</font>) %s",
                mIndex,
                Double.parseDouble(mQuestion.getScore()),
                mQuestion.getStem());
        stemView.setText(Html.fromHtml(
                stem,
                new EduImageGetterHandler(mContext, stemView),
                new EduTagHandler()
        ));
    }

}
