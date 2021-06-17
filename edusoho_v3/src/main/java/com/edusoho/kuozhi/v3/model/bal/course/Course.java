package com.edusoho.kuozhi.v3.model.bal.course;

import android.util.Log;

import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.v3.model.bal.Teacher;

import java.io.Serializable;

/**
 * Created by JesseHuang on 15/6/14.
 */
public class Course implements Serializable {
    public String    title;
    public int       studentNum;
    public double    rating;
    public String    smallPicture;
    public double    price;
    public double    originPrice;
    public String    coinPrice;
    public String    originCoinPrice;
    public int       id;
    public int       courseSetId;
    public int       parentId;
    public String    subtitle;
    public String    expiryDay;
    public String    showStudentNumType;
    public String    income;
    public String    status;
    public int       lessonNum;
    public int       giveCredit;
    public int       maxStudentNum;
    public String    ratingNum;
    public String    categoryId;
    public String    serializeMode;
    public String    middlePicture;
    public String    largePicture;
    public String    about;
    public String[]  goals;
    public String[]  audiences;
    public String    address;
    public String    hitNum;
    public String    userId;
    public int       vipLevelId;
    public String    createdTime;
    public Teacher[] teachers;
    public String    type;
    public String    buyable;
    public String    convNo;
    public int       learnedNum;
    public int       totalLesson;
    public long      courseDeadline;
    public int       liveState;
    public String    courseSetTitle;

    private String sourceName;
    public  String source;

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getLargePicture() {
        int schemIndex = largePicture.lastIndexOf("http://");
        if (schemIndex != -1) {
            return largePicture.substring(schemIndex);
        }
        if (largePicture.startsWith("//")) {
            return "http:" + largePicture;
        }
        return largePicture;
    }

    public Course parse(CourseProject courseProject) {
        try {
            this.id = courseProject.id;
            this.title = courseProject.getTitle();
            this.middlePicture = courseProject.courseSet.cover.middle;
            this.courseSetTitle = courseProject.courseSet.title;
        } catch (Exception ex) {
            Log.d("flag--", "parse: " + ex.getMessage());
        }
        return this;
    }
}
