package com.edusoho.kuozhi.v3.util;


/**
 * Created by JesseHuang on 15/9/16.
 */
public class PushUtil {
    public static class CourseType {
        /**
         * 课程群聊
         */
        public static final String TYPE                = "course";
        public static final String LIVE_NOTIFY         = "live.notify";
        public static final String LESSON_PUBLISH      = "lesson.publish";
        public static final String TESTPAPER_REVIEWED  = "testpaper.reviewed";
        public static final String COURSE_OPEN         = "course.open";
        public static final String COURSE_CLOSE        = "course.close";
        public static final String COURSE_ANNOUNCEMENT = "announcement.create";
        public static final String HOMEWORK_REVIEWED   = "homework.reviewed";
        public static final String QUESTION_ANSWERED   = "question.answered";
        public static final String QUESTION_CREATED    = "question.created";
        public static final String LESSON_FINISH       = "lesson.finish";
        public static final String LESSON_START        = "lesson.start";
    }

    public static class AnnouncementType {
        public static final String COURSE = "course";
        public static final String GLOBAL = "global";
    }

    public static class ChatUserType {
        public static final String USER      = "user";
        public static final String TEACHER   = "teacher";
        public static final String FRIEND    = "friend";
        public static final String CLASSROOM = "classroom";
        public static final String COURSE    = "course";
        public static final String STUDENT   = "student";
        public static final String NEWS      = "news";
        public static final String NOTIFY    = "notify";
    }

    /**
     * custom 中的 typeMsg，消息类型
     */
    public static class ChatMsgType {
        public static final String TEXT  = "text";
        public static final String AUDIO = "audio";
        public static final String IMAGE = "image";
        public static final String MULTI = "multi";
        public static final String PUSH  = "push";
    }

    public static class ThreadMsgType {
        public static final String THREAD      = "thread";
        public static final String THREAD_POST = "thread.post";
    }

    public static class MsgDeliveryType {
        public static final int SUCCESS   = 1;
        public static final int FAILED    = 0;
        public static final int UPLOADING = 2;
        public static final int NONE      = -1;
    }

    public static class BulletinType {
        public static final String TYPE = "global";
    }

    public static class ArticleType {
        public static final String TYPE        = "news";
        public static final String NEWS_CREATE = "news.create";
    }

    public static class LessonType {
        public static final String TYPE       = "lesson";
        public static final String LIVE_START = "live_start";
    }

    public static class FriendVerified {
        public static final String TYPE = "verified";
    }

    public static class DiscountType {
        public static final String DISCOUNT          = "discount";
        public static final String DISCOUNT_FREE     = "discount.free";
        public static final String DISCOUNT_DISCOUNT = "discount.discount";
        public static final String DISCOUNT_GLOBAL   = "discount.global";
    }

    public static final class Classroom {
        public static final String TYPE = "classroom";
    }

    public static final class Coupon {
        public static final String TYPE = "coupon";
    }

    public static final class Vip {
        public static final String TYPE = "vip";
    }

    public static final class Batch_Notification {
        public static final String TYPE = "batch_notification";
    }
}
