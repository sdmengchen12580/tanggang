package com.edusoho.kuozhi.clean.bean.mystudy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by RexXiang on 2018/1/31.
 */

public class SurveyAnswer implements Serializable {

    private List<AnswersBean> answers = new ArrayList<>();

    public List<AnswersBean> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswersBean> answers) {
        this.answers = answers;
    }

    public static class AnswersBean {

        private String questionnaireItemId;
        private String answer;

        public AnswersBean(String itemId, String content) {
            this.questionnaireItemId = itemId;
            this.answer = content;
        }

        public String getQuestionnaireItemId() {
            return questionnaireItemId;
        }

        public void setQuestionnaireItemId(String questionnaireItemId) {
            this.questionnaireItemId = questionnaireItemId;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }
    }
}
