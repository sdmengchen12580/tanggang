package com.edusoho.kuozhi.clean.bean.mystudy;


import java.io.Serializable;

public class OfflineActivityRecord implements Serializable {

    private String attendedStatus;
    private String passedStatus;
    private String offlineActivityName;
    private String offlineActivityPlace;
    private String startTime;
    private String endTime;
    private String categoryName;

    public String getAttendedStatus() {
        return attendedStatus;
    }

    public void setAttendedStatus(String attendedStatus) {
        this.attendedStatus = attendedStatus;
    }

    public String getPassedStatus() {
        return passedStatus;
    }

    public void setPassedStatus(String passedStatus) {
        this.passedStatus = passedStatus;
    }

    public String getOfflineActivityName() {
        return offlineActivityName;
    }

    public void setOfflineActivityName(String offlineActivityName) {
        this.offlineActivityName = offlineActivityName;
    }

    public String getOfflineActivityPlace() {
        return offlineActivityPlace;
    }

    public void setOfflineActivityPlace(String offlineActivityPlace) {
        this.offlineActivityPlace = offlineActivityPlace;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
