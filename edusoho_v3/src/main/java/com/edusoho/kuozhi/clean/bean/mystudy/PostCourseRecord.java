package com.edusoho.kuozhi.clean.bean.mystudy;


import java.io.Serializable;

public class PostCourseRecord implements Serializable {

    private String courseName;
    private String teacherName;
    private int    totalLearnTime;
    private int    progress;

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public int getTotalLearnTime() {
        return totalLearnTime;
    }

    public void setTotalLearnTime(int totalLearnTime) {
        this.totalLearnTime = totalLearnTime;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
