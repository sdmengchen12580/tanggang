package com.edusoho.kuozhi.clean.bean.mystudy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RexXiang on 2018/2/5.
 */

public class EvaluationAnswer {


    private List<AnswersBean> answers = new ArrayList<>();

    public List<AnswersBean> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswersBean> answers) {
        this.answers = answers;
    }

    public static class AnswersBean {

        private String questionnaireItemId;
        private List<String> answer = new ArrayList<>();

        public AnswersBean(String itemId, List<String> content) {
            this.questionnaireItemId = itemId;
            this.answer = content;
        }

        public String getQuestionnaireItemId() {
            return questionnaireItemId;
        }

        public void setQuestionnaireItemId(String questionnaireItemId) {
            this.questionnaireItemId = questionnaireItemId;
        }

        public List<String> getAnswer() {
            return answer;
        }

        public void setAnswer(List<String> answer) {
            this.answer = answer;
        }
    }
}


