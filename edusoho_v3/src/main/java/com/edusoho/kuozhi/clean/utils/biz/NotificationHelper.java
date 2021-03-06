package com.edusoho.kuozhi.clean.utils.biz;


import java.util.HashMap;

public class NotificationHelper {
    public static final String CLASSROOM_DEADLINE            = "classroom.deadline";
    public static final String CLASSROOM_JOIN                = "classroom.join";
    public static final String CLASSROOM_QUIT                = "classroom.quit";
    public static final String CLASSROOM_ANNOUNCEMENT_CREATE = "classroom.announcement.create";

    public static final String COURSE_QUIT                = "course.quit";
    public static final String COURSE_DEADLINE            = "course.deadline";
    public static final String COURSE_JOIN                = "course.join";
    public static final String COURSE_ANNOUNCEMENT_CREATE = "course.announcement.create";
    public static final String COURSE_LIVE_START          = "live.start";
    public static final String COURSE_LIVE_START1         = "live_start";

    public static final String COUPON_RECEIVE = "coupon.receive";
    public static final String INVITE_REWARD  = "invite.reward";

    public static final String TESTPAPER_FINISHED = "testpaper.finished";
    public static final String TESTPAPER_REVIEWED = "testpaper.reviewed";

    public static final String VIP_ADD      = "vip.add";
    public static final String VIP_DELETE   = "vip.delete";
    public static final String VIP_DEADLINE = "vip.deadline";

    public static final String COURSE_THREAD_UPDATE      = "course.thread.update";
    public static final String COURSE_THREAD_DELETE      = "course.thread.delete";
    public static final String COURSE_THREAD_STICK       = "course.thread.stick";
    public static final String COURSE_THREAD_UNSTICK     = "course.thread.unstick";
    public static final String COURSE_THREAD_ELITE       = "course.thread.elite";
    public static final String COURSE_THREAD_UNELITE     = "course.thread.unelite";
    public static final String COURSE_THREAD_POST_UPDATE = "course.thread.post.update";
    public static final String COURSE_THREAD_POST_DELETE = "course.thread.post.delete";
    public static final String COURSE_THREAD_POST_AT     = "course.thread.post.at";
    public static final String QUESTION_ANSWERED         = "question.answered";
    public static final String QUESTION_CREATED          = "question.created";

    public static final String BATCH_NOTIFICATION_PUBLISH = "batch_notification.publish";
    public static final String ANNOUNCEMENT_CREATE        = "announcement.create";

    public static final String USER_FOLLOW   = "user.follow";
    public static final String USER_UNFOLLOW = "user.unfollow";


    private static HashMap<String, String> mNotificationList = new HashMap<>();

    static {
        mNotificationList.put(CLASSROOM_DEADLINE, "????????????");
        mNotificationList.put(CLASSROOM_JOIN, "????????????");
        mNotificationList.put(CLASSROOM_QUIT, "????????????");
        mNotificationList.put(CLASSROOM_ANNOUNCEMENT_CREATE, "????????????");

        mNotificationList.put(COURSE_DEADLINE, "????????????");
        mNotificationList.put(COURSE_QUIT, "????????????");
        mNotificationList.put(COURSE_JOIN, "????????????");
        mNotificationList.put(COURSE_ANNOUNCEMENT_CREATE, "????????????");
        mNotificationList.put(COURSE_LIVE_START, "????????????");
        mNotificationList.put(COURSE_LIVE_START1, "????????????");

        mNotificationList.put(VIP_DEADLINE, "????????????");
        mNotificationList.put(INVITE_REWARD, "????????????");

        mNotificationList.put(VIP_ADD, "????????????");
        mNotificationList.put(VIP_DELETE, "????????????");
        mNotificationList.put(VIP_DEADLINE, "????????????");

        mNotificationList.put(COUPON_RECEIVE, "?????????");
        mNotificationList.put(INVITE_REWARD, "????????????");

        mNotificationList.put(QUESTION_ANSWERED, "????????????");
        mNotificationList.put(QUESTION_CREATED, "????????????");
        mNotificationList.put(COURSE_THREAD_STICK, "????????????");
        mNotificationList.put(COURSE_THREAD_UNSTICK, "????????????");
        mNotificationList.put(COURSE_THREAD_UPDATE, "????????????");
        mNotificationList.put(COURSE_THREAD_DELETE, "????????????");
        mNotificationList.put(COURSE_THREAD_ELITE, "????????????");
        mNotificationList.put(COURSE_THREAD_UNELITE, "????????????");
        mNotificationList.put(COURSE_THREAD_POST_UPDATE, "????????????");
        mNotificationList.put(COURSE_THREAD_POST_DELETE, "????????????");
        mNotificationList.put(COURSE_THREAD_POST_AT, "????????????");

        mNotificationList.put(BATCH_NOTIFICATION_PUBLISH, "????????????");
        mNotificationList.put(ANNOUNCEMENT_CREATE, "????????????");

        mNotificationList.put(TESTPAPER_FINISHED, "????????????");
        mNotificationList.put(TESTPAPER_REVIEWED, "????????????");
    }

    public static String getName(String key) {
        return mNotificationList.get(key);
    }
}
