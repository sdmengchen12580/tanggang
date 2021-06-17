package com.edusoho.kuozhi.clean.bean.mystudy;

import java.io.Serializable;

/**
 * Created by RexXiang on 2018/3/22.
 */

public class ProjectOfflineCourseItem implements Serializable {

    private String id;
    private String offlineCourseId;
    private String activityId;
    private String type;
    private String title;
    private String seq;
    private String place;
    private String hasHomework;
    private String homeworkDeadline;
    private String homeworkDemand;
    private String creator;
    private String orgId;
    private String startTime;
    private String endTime;
    private String createdTime;
    private String updatedTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOfflineCourseId() {
        return offlineCourseId;
    }

    public void setOfflineCourseId(String offlineCourseId) {
        this.offlineCourseId = offlineCourseId;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getHasHomework() {
        return hasHomework;
    }

    public void setHasHomework(String hasHomework) {
        this.hasHomework = hasHomework;
    }

    public String getHomeworkDeadline() {
        return homeworkDeadline;
    }

    public void setHomeworkDeadline(String homeworkDeadline) {
        this.homeworkDeadline = homeworkDeadline;
    }

    public String getHomeworkDemand() {
        return homeworkDemand;
    }

    public void setHomeworkDemand(String homeworkDemand) {
        this.homeworkDemand = homeworkDemand;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
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
