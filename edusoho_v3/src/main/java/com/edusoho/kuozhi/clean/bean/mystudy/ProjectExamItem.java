package com.edusoho.kuozhi.clean.bean.mystudy;

import java.io.Serializable;

/**
 * Created by RexXiang on 2018/3/22.
 */

public class ProjectExamItem implements Serializable {

    private String id;
    private String name;
    private String type;
    private String orgId;
    private String testPaperId;
    private String projectPlanId;
    private String status;
    private String startTime;
    private String endTime;
    private String length;
    private String passScore;
    private String memberNum;
    private String resitTimes;
    private String createdUserId;
    private String showAnswerAndAnalysis;
    private String createdTime;
    private String updatedTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getTestPaperId() {
        return testPaperId;
    }

    public void setTestPaperId(String testPaperId) {
        this.testPaperId = testPaperId;
    }

    public String getProjectPlanId() {
        return projectPlanId;
    }

    public void setProjectPlanId(String projectPlanId) {
        this.projectPlanId = projectPlanId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getPassScore() {
        return passScore;
    }

    public void setPassScore(String passScore) {
        this.passScore = passScore;
    }

    public String getMemberNum() {
        return memberNum;
    }

    public void setMemberNum(String memberNum) {
        this.memberNum = memberNum;
    }

    public String getResitTimes() {
        return resitTimes;
    }

    public void setResitTimes(String resitTimes) {
        this.resitTimes = resitTimes;
    }

    public String getCreatedUserId() {
        return createdUserId;
    }

    public void setCreatedUserId(String createdUserId) {
        this.createdUserId = createdUserId;
    }

    public String getShowAnswerAndAnalysis() {
        return showAnswerAndAnalysis;
    }

    public void setShowAnswerAndAnalysis(String showAnswerAndAnalysis) {
        this.showAnswerAndAnalysis = showAnswerAndAnalysis;
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
