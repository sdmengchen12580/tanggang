package com.edusoho.kuozhi.clean.bean.mystudy;

import java.io.Serializable;

/**
 * Created by RexXiang on 2018/1/18.
 */

public class PostCoursesProgress implements Serializable {


    private String totalCount;
    private String finishedCount;

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public String getFinishedCount() {
        return finishedCount;
    }

    public void setFinishedCount(String finishedCount) {
        this.finishedCount = finishedCount;
    }
}
