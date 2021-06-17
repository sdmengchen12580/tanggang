package com.edusoho.kuozhi.v3.entity.course;

import com.edusoho.kuozhi.v3.model.bal.Classroom;
import com.edusoho.kuozhi.v3.model.bal.Member;
import com.edusoho.kuozhi.v3.model.bal.VipLevel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Zhang on 2016/12/13.
 */

public class ClassroomDetail implements Serializable {

    private Classroom classRoom;
    private boolean userFavorited;
    private Member member;
    private Object discount;
    private List<VipLevel> vipLevels;

    public Classroom getClassRoom() {
        return classRoom;
    }

    public void setClassRoom(Classroom classRoom) {
        this.classRoom = classRoom;
    }

    public boolean isUserFavorited() {
        return userFavorited;
    }

    public void setUserFavorited(boolean userFavorited) {
        this.userFavorited = userFavorited;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Object getDiscount() {
        return discount;
    }

    public void setDiscount(Object discount) {
        this.discount = discount;
    }

    public List<VipLevel> getVipLevels() {
        return vipLevels;
    }

    public void setVipLevels(List<VipLevel> vipLevels) {
        this.vipLevels = vipLevels;
    }

}
