package com.edusoho.kuozhi.clean.bean;

/**
 * Created by JesseHuang on 2017/4/25.
 */

public class MessageEvent<T> {

    public static final int NO_CODE                  = -1;
    public static final int COURSE_EXIT              = 0;
    public static final int LEARN_TASK               = 1;
    public static final int SHOW_NEXT_TASK           = 2;
    public static final int LOGIN                    = 3;
    public static final int FINISH_TASK_SUCCESS      = 4;
    public static final int FULL_SCREEN              = 5;
    /**
     * 更新Adapter item状态：高亮、半圈
     */
    public static final int COURSE_TASK_ITEM_UPDATE  = 6;
    public static final int LEARN_NEXT_TASK          = 7;
    public static final int SHOW_VIP_BUTTON          = 8;
    public static final int PAY_SUCCESS              = 9;
    public static final int PPT_DOWNLOAD_DOING       = 10;
    public static final int PPT_DONWLOAD_FINISH      = 11;
    public static final int CREDENTIAL_EXPIRED       = 401;
    public static final int SWITCH_STUDY             = 555;
    public static final int REWARD_POINT_NOTIFY      = 501;
    public static final int BIND_THIRD_SUCCESS       = 502;
    public static final int CHANGE_EXAM_ANSWER       = 556;
    public static final int FINISH_SURVEY            = 557;
    public static final int CLICK_QUESTION_CARD_ITEM = 558;

    private T   mMessage;
    private int mCode;

    public MessageEvent(T message) {
        mMessage = message;
        mCode = NO_CODE;
    }

    public MessageEvent(T message, int code) {
        mMessage = message;
        mCode = code;
    }

    public MessageEvent(int code) {
        mMessage = null;
        mCode = code;
    }

    public T getMessageBody() {
        return mMessage;
    }

    public int getType() {
        return mCode;
    }
}
