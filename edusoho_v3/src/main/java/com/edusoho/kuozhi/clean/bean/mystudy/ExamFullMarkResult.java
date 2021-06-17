package com.edusoho.kuozhi.clean.bean.mystudy;

import java.io.Serializable;
import java.util.List;

/**
 * Created by RexXiang on 2018/2/9.
 */

public class ExamFullMarkResult implements Serializable {




    private String status;
    private List<CheckAnswersBean> checkAnswers;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<CheckAnswersBean> getCheckAnswers() {
        return checkAnswers;
    }

    public void setCheckAnswers(List<CheckAnswersBean> checkAnswers) {
        this.checkAnswers = checkAnswers;
    }

    public static class CheckAnswersBean {

        private String questionId;
        private String checkStatus;
        private String type;

        public String getQuestionId() {
            return questionId;
        }

        public void setQuestionId(String questionId) {
            this.questionId = questionId;
        }

        public String getCheckStatus() {
            return checkStatus;
        }

        public void setCheckStatus(String checkStatus) {
            this.checkStatus = checkStatus;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
