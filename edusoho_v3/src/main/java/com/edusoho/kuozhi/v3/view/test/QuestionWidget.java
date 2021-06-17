package com.edusoho.kuozhi.v3.view.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.mystudy.ExamModel;
import com.edusoho.kuozhi.clean.module.main.study.exam.base.ExamBaseQuestionWidget;
import com.edusoho.kuozhi.v3.model.bal.test.Question;
import com.edusoho.kuozhi.v3.model.bal.test.QuestionTypeSeq;

/**
 * Created by howzhi on 14-9-29.
 */
public class QuestionWidget {

    private Context                 mContext;
    private QuestionTypeSeq         mQuestionSeq;
    private int                     mIndex;
    private BaseQuestionWidget      mWidget;
    private ExamBaseQuestionWidget  mExamWidget;
    private ExamModel.QuestionsBean mExamQuestion;

    public QuestionWidget(Context context, QuestionTypeSeq questionSeq, int index) {
        mIndex = index;
        mContext = context;
        mQuestionSeq = questionSeq;
        init();
    }

    //fixme 专项考试，item单个题目的展示
    public QuestionWidget(Context context, ExamModel.QuestionsBean questionsBean, int index) {
        mIndex = index;
        mContext = context;
        mExamQuestion = questionsBean;
        initExamQuestionView(index);
    }

    private void initExamQuestionView(int choicePosition) {
        int layoutId = 0;
        String type = mExamQuestion.getType();
        if (type.equals("single_choice")) {
            layoutId = R.layout.exam_singlechoice_viewpager_item;  //单选题 ExamSingleChoiceQuestionWidget  父类 ExamBaseQuestionWidget
        }
        if (type.equals("choice")) {
            layoutId = R.layout.exam_choice_viewpager_item;        //多选题 ExamChoiceQuestionWidget        父类 ExamBaseQuestionWidget
        }
        if (type.equals("essay")) {
            layoutId = R.layout.exam_essay_viewpager_item;         //问答题 ExamEssayQuestionWidget
        }
        if (type.equals("uncertain_choice")) {
            layoutId = R.layout.exam_choice_viewpager_item;        //不定项选择题 ExamChoiceQuestionWidget   父类 ExamBaseQuestionWidget
        }
        if (type.equals("determine")) {
            layoutId = R.layout.exam_determine_list_item;          //判断题 ExamDetermineQuestionWidget     父类 ExamBaseQuestionWidget
        }
        if (type.equals("fill")) {
            layoutId = R.layout.exam_fill_list_item;
        }
        mExamWidget = (ExamBaseQuestionWidget) LayoutInflater.from(mContext).inflate(layoutId, null);
        mExamWidget.setQuestion(mExamQuestion, mIndex);
    }

    private void init() {
        int layoutId = 0;
        Question mQuestion = mQuestionSeq.question;
        switch (mQuestion.type) {
            case choice:
            case uncertain_choice:
                layoutId = R.layout.choice_viewpager_item;
                break;
            case single_choice:
                layoutId = R.layout.singlechoice_viewpager_item;
                break;
            case essay:
                layoutId = R.layout.essay_viewpager_item;
                break;
            case determine:
                layoutId = R.layout.determine_list_item;
                break;
            case fill:
                layoutId = R.layout.fill_list_item;
        }
        mWidget = (BaseQuestionWidget) LayoutInflater.from(mContext).inflate(
                layoutId, null);
        mWidget.setData(mQuestionSeq, mIndex);
    }

    public View getView() {
        return mWidget;
    }

    public View getExamQuestionView() {
        return mExamWidget;
    }
}
