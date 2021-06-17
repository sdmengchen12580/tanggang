package com.edusoho.kuozhi.clean.bean.mystudy;

import java.io.Serializable;
import java.util.List;

/**
 * Created by RexXiang on 2018/1/15.
 */

public class AssignmentModel implements Serializable {

    private String               type;
    private String               id;
    private String               title;
    private String               startTime;
    private String               endTime;
    private float                examScore;
    private float                userScore;
    private String               joinTime;
    private String               memberStatus;
    private String               passStatus;
    private String               finishedCount;
    private String               resitTimes;
    private int                  remainingResitTimes;
    private String               examStatus;
    private String               description;
    private String               isResultVisible;
    private String               surveyStatus;
    private CoverBean            cover;
    private String               startDate;
    private String               endDate;
    private String               address;
    private String               categoryName;
    private double               progress;
    private List<TodayFocusBean> todayFocus;
    private String               examType;


    public String getExamType() {
        return examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public float getExamScore() {
        return examScore;
    }

    public void setExamScore(float examScore) {
        this.examScore = examScore;
    }

    public float getUserScore() {
        return userScore;
    }

    public void setUserScore(float userScore) {
        this.userScore = userScore;
    }

    public String getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(String joinTime) {
        this.joinTime = joinTime;
    }

    public String getMemberStatus() {
        return memberStatus;
    }

    public void setMemberStatus(String memberStatus) {
        this.memberStatus = memberStatus;
    }

    public String getPassedStatus() {
        return passStatus;
    }

    public void setPassedStatus(String passedStatus) {
        this.passStatus = passedStatus;
    }

    public String getFinishedCount() {
        return finishedCount;
    }

    public void setFinishedCount(String finishedCount) {
        this.finishedCount = finishedCount;
    }

    public String getResitTimes() {
        return resitTimes;
    }

    public void setResitTimes(String resitTimes) {
        this.resitTimes = resitTimes;
    }

    public int getRemainingResitTimes() {
        return remainingResitTimes;
    }

    public void setRemainingResitTimes(int remainingResitTimes) {
        this.remainingResitTimes = remainingResitTimes;
    }

    public String getExamStatus() {
        return examStatus;
    }

    public void setExamStatus(String examStatus) {
        this.examStatus = examStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIsResultVisible() {
        return isResultVisible;
    }

    public void setIsResultVisible(String isResultVisible) {
        this.isResultVisible = isResultVisible;
    }

    public String getSurveyStatus() {
        return surveyStatus;
    }

    public void setSurveyStatus(String surveyStatus) {
        this.surveyStatus = surveyStatus;
    }

    public CoverBean getCover() {
        return cover;
    }

    public void setCover(CoverBean cover) {
        this.cover = cover;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public List<TodayFocusBean> getTodayFocus() {
        return todayFocus;
    }

    public void setTodayFocus(List<TodayFocusBean> todayFocus) {
        this.todayFocus = todayFocus;
    }

    public static class CoverBean implements Serializable {

        private String large;
        private String middle;
        private String small;

        public String getLarge() {
            return large;
        }

        public void setLarge(String large) {
            this.large = large;
        }

        public String getMiddle() {
            return middle;
        }

        public void setMiddle(String middle) {
            this.middle = middle;
        }

        public String getSmall() {
            return small;
        }

        public void setSmall(String small) {
            this.small = small;
        }
    }

    public static class TodayFocusBean implements Serializable {

        private String id;
        private String tagName;
        private String title;
        private String startTime;
        private String endTime;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTagName() {
            return tagName;
        }

        public void setTagName(String tagName) {
            this.tagName = tagName;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
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
    }
}
