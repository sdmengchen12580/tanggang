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
        mNotificationList.put(CLASSROOM_DEADLINE, "班级到期");
        mNotificationList.put(CLASSROOM_JOIN, "加入班级");
        mNotificationList.put(CLASSROOM_QUIT, "退出班级");
        mNotificationList.put(CLASSROOM_ANNOUNCEMENT_CREATE, "班级公告");

        mNotificationList.put(COURSE_DEADLINE, "课程到期");
        mNotificationList.put(COURSE_QUIT, "退出课程");
        mNotificationList.put(COURSE_JOIN, "加入课程");
        mNotificationList.put(COURSE_ANNOUNCEMENT_CREATE, "课程公告");
        mNotificationList.put(COURSE_LIVE_START, "直播通知");
        mNotificationList.put(COURSE_LIVE_START1, "直播通知");

        mNotificationList.put(VIP_DEADLINE, "会员过期");
        mNotificationList.put(INVITE_REWARD, "奖励通知");

        mNotificationList.put(VIP_ADD, "会员通知");
        mNotificationList.put(VIP_DELETE, "会员取消");
        mNotificationList.put(VIP_DEADLINE, "会员到期");

        mNotificationList.put(COUPON_RECEIVE, "优惠券");
        mNotificationList.put(INVITE_REWARD, "奖励通知");

        mNotificationList.put(QUESTION_ANSWERED, "问答通知");
        mNotificationList.put(QUESTION_CREATED, "问答通知");
        mNotificationList.put(COURSE_THREAD_STICK, "问答通知");
        mNotificationList.put(COURSE_THREAD_UNSTICK, "问答通知");
        mNotificationList.put(COURSE_THREAD_UPDATE, "问答通知");
        mNotificationList.put(COURSE_THREAD_DELETE, "问答通知");
        mNotificationList.put(COURSE_THREAD_ELITE, "问答通知");
        mNotificationList.put(COURSE_THREAD_UNELITE, "问答通知");
        mNotificationList.put(COURSE_THREAD_POST_UPDATE, "问答通知");
        mNotificationList.put(COURSE_THREAD_POST_DELETE, "问答通知");
        mNotificationList.put(COURSE_THREAD_POST_AT, "问答通知");

        mNotificationList.put(BATCH_NOTIFICATION_PUBLISH, "站内通知");
        mNotificationList.put(ANNOUNCEMENT_CREATE, "网站公告");

        mNotificationList.put(TESTPAPER_FINISHED, "考试通知");
        mNotificationList.put(TESTPAPER_REVIEWED, "考试通知");
    }

    public static String getName(String key) {
        return mNotificationList.get(key);
    }
}
