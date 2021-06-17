package com.edusoho.kuozhi.clean.bean.mystudy;


import java.io.Serializable;

public class TrainingRecordItem implements Serializable {
    private String type;
    private int    assignNum;
    private int    finishedNum;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAssignNum() {
        return assignNum;
    }

    public void setAssignNum(int assignNum) {
        this.assignNum = assignNum;
    }

    public int getFinishedNum() {
        return finishedNum;
    }

    public void setFinishedNum(int finishedNum) {
        this.finishedNum = finishedNum;
    }
}
