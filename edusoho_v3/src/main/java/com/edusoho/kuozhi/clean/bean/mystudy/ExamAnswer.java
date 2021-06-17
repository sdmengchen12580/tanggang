package com.edusoho.kuozhi.clean.bean.mystudy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by RexXiang on 2018/2/8.
 */

public class ExamAnswer implements Serializable {


    private List<AnswersBean> answers = new ArrayList<>();

    public List<AnswersBean> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswersBean> answers) {
        this.answers = answers;
    }

    public static class AnswersBean implements Serializable {

        private String       questionId;
        private List<String> answer;

        public AnswersBean(String itemId, List<String> answers) {
            this.questionId = itemId;
            this.answer = answers;
        }

        public String getQuestionId() {
            return questionId;
        }

        public void setQuestionId(String questionId) {
            this.questionId = questionId;
        }

        public List<String> getAnswer() {
            return answer;
        }

        public void setAnswer(List<String> answer) {
            this.answer = answer;
        }
    }
}
