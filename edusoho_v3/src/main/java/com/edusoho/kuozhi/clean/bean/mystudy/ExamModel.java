package com.edusoho.kuozhi.clean.bean.mystudy;

import com.edusoho.kuozhi.v3.model.bal.test.QuestionType;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by RexXiang on 2018/2/6.
 */

public class ExamModel implements Serializable {

    /**
     * exam : {"name":"全部类型","length":"0","type":"grade"}
     * examInfo : {"single_choice":{"number":1,"score":"2.0"},"choice":{"number":1,"score":"2.0"},"uncertain_choice":{"number":1,"score":"2.0"},"determine":{"number":2,"score":"2.0"},"essay":{"number":1,"score":"2.0"},"fill":{"number":1,"score":"2.0"}}
     * testPaper : {"score":"14.0","questionsCount":"7"}
     * examResult : {"id":"67","beginTime":"1517908544"}
     * questions : [{"id":"15","type":"single_choice","stem":"<p>单选题11<\/p>\r\n","score":"2.0","metas":{"choices":["<p>第一个选项A<\/p>\r\n","<p>第一个选项B<\/p>\r\n","<p>第一个选项C<\/p>\r\n"]},"testResult":[],"seq":"1","missScore":"0.0"},{"id":"16","type":"choice","stem":"<p>多选题01<\/p>\r\n","score":"2.0","metas":{"choices":["<p>反反复复<\/p>\n","<p>点点滴滴到底<\/p>\n","<p>dddddddd<\/p>\n"]},"testResult":[],"seq":"2","missScore":"0.0"},{"id":"20","type":"uncertain_choice","stem":"<pre>\r\nuncertain_choice<\/pre>\r\n","score":"2.0","metas":{"choices":["<p>A<\/p>\r\n","<p>B<\/p>\r\n","<p>C<\/p>\r\n","<p>D<\/p>\r\n"]},"testResult":[],"seq":"3","missScore":"0.0"},{"id":"18","type":"determine","stem":"<p>顶顶顶顶顶顶顶顶顶顶顶顶顶顶顶顶顶顶顶顶顶顶顶顶地地道道的地地道道地地道道地地道道<\/p>\r\n","score":"2.0","metas":[],"testResult":[],"seq":"4","missScore":"0.0"},{"id":"17","type":"determine","stem":"<p>顶顶顶顶顶顶顶顶顶顶顶顶顶顶顶顶顶顶顶顶顶顶顶顶地地道道的地地道道地地道道地地道道<\/p>\r\n","score":"2.0","metas":[],"testResult":[],"seq":"5","missScore":"0.0"},{"id":"19","type":"essay","stem":"<p>地地道道的地地道道<\/p>\r\n","score":"2.0","metas":[],"testResult":[],"seq":"6","missScore":"0.0"},{"id":"21","type":"fill","stem":"<pre>\r\n今天是[[晴]]天<\/pre>\r\n","score":"2.0","metas":[[]],"testResult":[],"seq":"7","missScore":"0.0"}]
     */

    private ExamBean                          exam;
    private LinkedHashMap<QuestionType, item> examInfo;
    private TestPaperBean                     testPaper;
    private ExamResultBean                    examResult;
    private List<QuestionsBean>               questions;

    public ExamBean getExam() {
        return exam;
    }

    public void setExam(ExamBean exam) {
        this.exam = exam;
    }

    public LinkedHashMap<QuestionType, item> getExamInfo() {
        return examInfo;
    }

    public void setExamInfo(LinkedHashMap<QuestionType, item> examInfo) {
        this.examInfo = examInfo;
    }

    public TestPaperBean getTestPaper() {
        return testPaper;
    }

    public void setTestPaper(TestPaperBean testPaper) {
        this.testPaper = testPaper;
    }

    public ExamResultBean getExamResult() {
        return examResult;
    }

    public void setExamResult(ExamResultBean examResult) {
        this.examResult = examResult;
    }

    public List<QuestionsBean> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionsBean> questions) {
        this.questions = questions;
    }

    public static class ExamBean implements Serializable {
        /**
         * name : 全部类型
         * length : 0
         * type : grade
         */

        private String name;
        private String length;
        private String type;
        private String remainingTime;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLength() {
            return length;
        }

        public void setLength(String length) {
            this.length = length;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getRemainingTime() {
            return remainingTime;
        }

        public void setRemainingTime(String remainingTime) {
            this.remainingTime = remainingTime;
        }
    }

    public static class TestPaperBean implements Serializable {
        /**
         * score : 14.0
         * questionsCount : 7
         */

        private String score;
        private String questionsCount;

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getQuestionsCount() {
            return questionsCount;
        }

        public void setQuestionsCount(String questionsCount) {
            this.questionsCount = questionsCount;
        }
    }

    public static class ExamResultBean implements Serializable {
        /**
         * id : 67
         * beginTime : 1517908544
         */

        private String id;
        private String beginTime;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getBeginTime() {
            return beginTime;
        }

        public void setBeginTime(String beginTime) {
            this.beginTime = beginTime;
        }
    }

    public static class QuestionsBean implements Serializable {
        /**
         * id : 15
         * type : single_choice
         * stem : <p>单选题11</p>
         * <p>
         * score : 2.0
         * metas : {"choices":["<p>第一个选项A<\/p>\r\n","<p>第一个选项B<\/p>\r\n","<p>第一个选项C<\/p>\r\n"]}
         * testResult : []
         * seq : 1
         * missScore : 0.0
         */

        private String    id;
        private String    type;
        private String    stem;
        private String    score;
        private MetasBean metas;
        private String    seq;
        private String    missScore;
        private List<?>   testResult;

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

        public MetasBean getMetas() {
            return metas;
        }

        public void setMetas(MetasBean metas) {
            this.metas = metas;
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

        public List<?> getTestResult() {
            return testResult;
        }

        public void setTestResult(List<?> testResult) {
            this.testResult = testResult;
        }

        public static class MetasBean implements Serializable {
            private List<String> choices;

            public List<String> getChoices() {
                return choices;
            }

            public void setChoices(List<String> choices) {
                this.choices = choices;
            }
        }
    }


    public static class item implements Serializable {

        private int    number;
        private String score;

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }
    }

}
