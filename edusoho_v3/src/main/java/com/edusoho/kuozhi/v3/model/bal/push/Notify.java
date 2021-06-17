package com.edusoho.kuozhi.v3.model.bal.push;

import com.edusoho.kuozhi.clean.utils.GsonUtils;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

/**
 * Created by suju on 16/11/10.
 */
public class Notify {

    private String                        msgNo;
    private String                        title;
    private String                        content;
    private LinkedTreeMap<String, String> objectContent;
    private String                        type;
    private long                          createdTime;
    private String                        lessonTitle;
    private String                        message;
    private int                           courseId;
    private int                           lessonId;
    private int                           ownerId;


    public String getMsgNo() {
        return msgNo;
    }

    public void setMsgNo(String msgNo) {
        this.msgNo = msgNo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public LinkedTreeMap<String, String> getObjectContent() {
        if (objectContent == null) {
            objectContent = GsonUtils.parseJson(content, new TypeToken<LinkedTreeMap<String, String>>() {
            });
        }
        return objectContent;
    }

    public String getLessonTitle() {
        try {
            lessonTitle = getObjectContent().get("lessonTitle");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return lessonTitle;
    }

    public String getMessage() {
        try {
            message = getObjectContent().get("message");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return message;
    }

    public int getCourseId() {
        try {
            courseId = Integer.parseInt(getObjectContent().get("courseId"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return courseId;
    }

    public int getLessonId() {
        try {
            lessonId = Integer.parseInt(getObjectContent().get("lessonId"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return lessonId;
    }
}
