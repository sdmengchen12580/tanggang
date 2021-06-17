package com.edusoho.kuozhi.v3.model.bal;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by howzhi on 14-8-29.
 */
public class Vip implements Serializable {
    public int id;
    public int seq;
    public int userId;
    public int levelId;
    @SerializedName("VipDeadLine")
    public String deadline;
    public String boughtType;
    public String boughtTime;
    public int boughtDuration;
    public String boughtUnit;
    public double boughtAmount;
    public String createdTime;
}
