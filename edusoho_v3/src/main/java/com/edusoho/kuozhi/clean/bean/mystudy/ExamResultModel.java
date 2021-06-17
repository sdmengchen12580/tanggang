package com.edusoho.kuozhi.clean.bean.mystudy;

import java.io.Serializable;
import java.util.List;

/**
 * Created by RexXiang on 2018/2/9.
 */

public class ExamResultModel implements Serializable {

    /**
     * examName : 评分不限次数31
     * testPaperScore : 6.0
     * score : 0.0
     * submitTime : 1970-01-01T08:00:00+08:00
     * usedTime : 0
     * status : doing
     * examType : grade
     * canRedoExam : false
     * questionResults : [{"id":"15","type":"single_choice","stem":"<p>单选题11<\/p>\r\n","score":"2.0","answer":["0"],"analysis":"","metas":{"choices":["<p>第一个选项A<\/p>\r\n","<p>第一个选项B<\/p>\r\n","<p>第一个选项C<\/p>\r\n"]},"testResult":{"status":"wrong","score":"0.0","answer":"1","teacherSay":null},"seq":"1","missScore":"0.0","itemResult":{"id":"44","itemId":"8","examId":"10","testPaperId":"4","examResultId":"73","userId":"8","questionId":"15","status":"wrong","score":"0.0","answer":"1","teacherSay":null,"createdTime":"1519350172","updatedTime":"1519439809"}},{"id":"15","type":"single_choice","stem":"<p>单选题11<\/p>\r\n","score":"2.0","answer":["0"],"analysis":"","metas":{"choices":["<p>第一个选项A<\/p>\r\n","<p>第一个选项B<\/p>\r\n","<p>第一个选项C<\/p>\r\n"]},"testResult":{"status":"wrong","score":"0.0","answer":"1","teacherSay":null},"seq":"2","missScore":"0.0","itemResult":{"id":"44","itemId":"8","examId":"10","testPaperId":"4","examResultId":"73","userId":"8","questionId":"15","status":"wrong","score":"0.0","answer":"1","teacherSay":null,"createdTime":"1519350172","updatedTime":"1519439809"}},{"id":"19","type":"essay","stem":"<p>地地道道的地地道道<\/p>\r\n","score":"2.0","answer":["<p>人人人人人人人人人人<\/p>\n"],"analysis":"<p>柔柔弱弱柔柔弱弱人人若若若若若若若若若若若若若若若若若若若若若若若若若若若若若若若若若若若若若若若若若若若若若若若若若若若若若若<\/p>\n","testResult":{"status":"none","score":"0.0","answer":"2","teacherSay":null},"seq":"3","missScore":"0.0","itemResult":{"id":"45","itemId":"9","examId":"10","testPaperId":"4","examResultId":"73","userId":"8","questionId":"19","status":"none","score":"0.0","answer":"2","teacherSay":null,"createdTime":"1519350172","updatedTime":"1519439809"}}]
     */

    private String examName;
    private String testPaperScore;
    private String score;
    private String submitTime;
    private int usedTime;
    private String status;
    private String examType;
    private boolean canRedoExam;
    private List<QuestionResultsBean> questionResults;

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getTestPaperScore() {
        return testPaperScore;
    }

    public void setTestPaperScore(String testPaperScore) {
        this.testPaperScore = testPaperScore;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(String submitTime) {
        this.submitTime = submitTime;
    }

    public int getUsedTime() {
        return usedTime;
    }

    public void setUsedTime(int usedTime) {
        this.usedTime = usedTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExamType() {
        return examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }

    public boolean isCanRedoExam() {
        return canRedoExam;
    }

    public void setCanRedoExam(boolean canRedoExam) {
        this.canRedoExam = canRedoExam;
    }

    public List<QuestionResultsBean> getQuestionResults() {
        return questionResults;
    }

    public void setQuestionResults(List<QuestionResultsBean> questionResults) {
        this.questionResults = questionResults;
    }

    public static class QuestionResultsBean {
        /**
         * id : 15
         * type : single_choice
         * stem : <p>单选题11</p>

         * score : 2.0
         * answer : ["0"]
         * analysis :
         * metas : {"choices":["<p>第一个选项A<\/p>\r\n","<p>第一个选项B<\/p>\r\n","<p>第一个选项C<\/p>\r\n"]}
         * testResult : {"status":"wrong","score":"0.0","answer":"1","teacherSay":null}
         * seq : 1
         * missScore : 0.0
         * itemResult : {"id":"44","itemId":"8","examId":"10","testPaperId":"4","examResultId":"73","userId":"8","questionId":"15","status":"wrong","score":"0.0","answer":"1","teacherSay":null,"createdTime":"1519350172","updatedTime":"1519439809"}
         */

        private String id;
        private String type;
        private String stem;
        private String score;
        private String analysis;
        private MetasBean metas;
        private TestResultBean testResult;
        private String seq;
        private String missScore;
        private ItemResultBean itemResult;
        private List<String> answer;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getStem() {
            return stem;
        }

        public void setStem(String stem) {
            this.stem = stem;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getAnalysis() {
            return analysis;
        }

        public void setAnalysis(String analysis) {
            this.analysis = analysis;
        }

        public MetasBean getMetas() {
            return metas;
        }

        public void setMetas(MetasBean metas) {
            this.metas = metas;
        }

        public TestResultBean getTestResult() {
            return testResult;
        }

        public void setTestResult(TestResultBean testResult) {
            this.testResult = testResult;
        }

        public String getSeq() {
            return seq;
        }

        public void setSeq(String seq) {
            this.seq = seq;
        }

        public String getMissScore() {
            return missScore;
        }

        public void setMissScore(String missScore) {
            this.missScore = missScore;
        }

        public ItemResultBean getItemResult() {
            return itemResult;
        }

        public void setItemResult(ItemResultBean itemResult) {
            this.itemResult = itemResult;
        }

        public List<String> getAnswer() {
            return answer;
        }

        public void setAnswer(List<String> answer) {
            this.answer = answer;
        }

        public static class MetasBean {
            private List<String> choices;

            public List<String> getChoices() {
                return choices;
            }

            public void setChoices(List<String> choices) {
                this.choices = choices;
            }
        }

        public static class TestResultBean {
            /**
             * status : wrong
             * score : 0.0
             * answer : 1
             * teacherSay : null
             */

            private String status;
            private String score;
            private String answer;
            private Object teacherSay;

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getScore() {
                return score;
            }

            public void setScore(String score) {
                this.score = score;
            }

            public String getAnswer() {
                return answer;
            }

            public void setAnswer(String answer) {
                this.answer = answer;
            }

            public Object getTeacherSay() {
                return teacherSay;
            }

            public void setTeacherSay(Object teacherSay) {
                this.teacherSay = teacherSay;
            }
        }

        public static class ItemResultBean {
            /**
             * id : 44
             * itemId : 8
             * examId : 10
             * testPaperId : 4
             * examResultId : 73
             * userId : 8
             * questionId : 15
             * status : wrong
             * score : 0.0
             * answer : 1
             * teacherSay : null
             * createdTime : 1519350172
             * updatedTime : 1519439809
             */

            private String id;
            private String itemId;
            private String examId;
            private String testPaperId;
            private String examResultId;
            private String userId;
            private String questionId;
            private String status;
            private String score;
            private String answer;
            private Object teacherSay;
            private String createdTime;
            private String updatedTime;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getItemId() {
                return itemId;
            }

            public void setItemId(String itemId) {
                this.itemId = itemId;
            }

            public String getExamId() {
                return examId;
            }

            public void setExamId(String examId) {
                this.examId = examId;
            }

            public String getTestPaperId() {
                return testPaperId;
            }

            public void setTestPaperId(String testPaperId) {
                this.testPaperId = testPaperId;
            }

            public String getExamResultId() {
                return examResultId;
            }

            public void setExamResultId(String examResultId) {
                this.examResultId = examResultId;
            }

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }

            public String getQuestionId() {
                return questionId;
            }

            public void setQuestionId(String questionId) {
                this.questionId = questionId;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getScore() {
                return score;
            }

            public void setScore(String score) {
                this.score = score;
            }

            public String getAnswer() {
                return answer;
            }

            public void setAnswer(String answer) {
                this.answer = answer;
            }

            public Object getTeacherSay() {
                return teacherSay;
            }

            public void setTeacherSay(Object teacherSay) {
                this.teacherSay = teacherSay;
            }

            public String getCreatedTime() {
                return createdTime;
            }

            public void setCreatedTime(String createdTime) {
                this.createdTime = createdTime;
            }

            public String getUpdatedTime() {
                return updatedTime;
            }

            public void setUpdatedTime(String updatedTime) {
                this.updatedTime = updatedTime;
            }
        }
    }
}
