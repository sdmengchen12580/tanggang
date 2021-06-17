package com.edusoho.kuozhi.clean.bean.mystudy;

import java.io.Serializable;
import java.util.List;

/**
 * Created by RexXiang on 2018/2/7.
 */

public class ExamQuestions implements Serializable {

    private List<ExamModel.QuestionsBean> questionList;
    private String questionType;
    private String typeName;

    public ExamQuestions(List<ExamModel.QuestionsBean> questions, String type) {
        this.questionList = questions;
        this.questionType = type;
    }

    public List<ExamModel.QuestionsBean> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<ExamModel.QuestionsBean> questionList) {
        this.questionList = questionList;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getTypeName() {
        if (this.questionType.equals("single_choice")) {
            return "单选题";
        }
        if (this.questionType.equals("choice")) {
            return "多选题";
        }
        if (this.questionType.equals("essay")) {
            return "问答题";
        }
        if (this.questionType.equals("uncertain_choice")) {
            return "不定项选择题";
        }
        if (this.questionType.equals("determine")) {
            return "判断题";
        }
        if (this.questionType.equals("fill")) {
            return "填空题";
        }
        return this.typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }


}
