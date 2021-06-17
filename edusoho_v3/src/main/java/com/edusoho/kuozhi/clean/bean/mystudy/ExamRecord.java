package com.edusoho.kuozhi.clean.bean.mystudy;


import java.io.Serializable;

public class ExamRecord implements Serializable {

    private String id;
    private String examId;
    private String status;
    private String passStatus;
    private String examName;
    private String examType;
    private float  testPaperScore;
    private String startTime;
    private String endTime;
    private float  score;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPassStatus() {
        return passStatus;
    }

    public void setPassStatus(String passStatus) {
        this.passStatus = passStatus;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getExamType() {
        return examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }

    public float getTestPaperScore() {
        return testPaperScore;
    }

    public void setTestPaperScore(float testPaperScore) {
        this.testPaperScore = testPaperScore;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }
}
