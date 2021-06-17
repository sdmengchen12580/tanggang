package com.edusoho.kuozhi.clean.bean.mystudy;


import java.io.Serializable;

public class ProjectPlanRecord implements Serializable {

    private int    projectPlanId;
    private String projectPlanName;
    private String startTime;
    private String endTime;
    private int    progress;

    public int getProjectPlanId() {
        return projectPlanId;
    }

    public void setProjectPlanId(int projectPlanId) {
        this.projectPlanId = projectPlanId;
    }

    public String getProjectPlanName() {
        return projectPlanName;
    }

    public void setProjectPlanName(String projectPlanName) {
        this.projectPlanName = projectPlanName;
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

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
