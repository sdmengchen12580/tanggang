package com.edusoho.kuozhi.clean.bean.mystudy;

import java.io.Serializable;

/**
 * Created by RexXiang on 2018/1/30.
 */

public class Relationship implements Serializable {

    /**
     * userId : 1
     * followed : false
     */

    private String userId;
    private boolean followed;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isFollowed() {
        return followed;
    }

    public void setFollowed(boolean followed) {
        this.followed = followed;
    }
}
