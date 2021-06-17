package com.edusoho.kuozhi.clean.bean;

public class PeopleABean {

    private String img1Base64;
    private String img2Base64;

    public PeopleABean(String img1Base64, String img2Base64) {
        this.img1Base64 = img1Base64;
        this.img2Base64 = img2Base64;
    }

    public String getImg1Base64() {
        return img1Base64;
    }

    public void setImg1Base64(String img1Base64) {
        this.img1Base64 = img1Base64;
    }

    public String getImg2Base64() {
        return img2Base64;
    }

    public void setImg2Base64(String img2Base64) {
        this.img2Base64 = img2Base64;
    }
}
