package com.edusoho.kuozhi.clean.utils;

/**
 * Created by JesseHuang on 2017/3/27.
 */

public class Constants {
    public static final int LIMIT = 10;

    public static class HttpCode {
        public static final int URL_REDIRECTION       = 302;
        public static final int BAD_REQUEST           = 400;
        public static final int UNAUTHORIZED          = 401;
        public static final int NOT_FOUND             = 404;
        public static final int METHOD_NOT_ALLOWED    = 405;
        public static final int TOO_MANY_REQUESTS     = 429;
        public static final int INTERNAL_SERVER_ERROR = 500;
        public static final int SERVICE_UNAVAILABLE   = 503;
    }

    public static class LiveTaskReplayStatus {
        public static final String VIDEO_GENERATED = "videoGenerated";
        public static final String GENERATED       = "generated";
        public static final String UNGENERATED     = "ungenerated";
    }

    public static class LiveProvider {
        public static final String GENSEE   = "gensee";
        public static final String TALKFUN  = "talkfun";
        public static final String LONGINUS = "longinus";
        public static final String ATHENA   = "athena";
    }

    public static class ProjectPlanItemType {
        public static final String TYPE_PROJECT_PLAN    = "projectPlan";
        public static final String TYPE_POST_COURSE     = "postCourse";
        public static final String TYPE_EXAM            = "exam";
        public static final String TYPE_OFFLINEACTIVITY = "offlineActivity";
    }

    public static class ProjectPlanExamStatus {
        public static final String NOTSTART   = "notStart";
        public static final String DOING = "doing";
        public static final String REVIEWING = "reviewing";
        public static final String FINISHED = "finished";
        public static final String ABSENT = "absent";
    }

    public static class ProjectPlanExamPassedStatus {
        public static final String PASSED   = "passed";
        public static final String UNPASSED = "unpassed";
    }

    public static class ActivityAttentStatus {
        public static final String ATTENDED   = "attended";
        public static final String UNATTENDED = "unattended";
        public static final String NONE       = "none";
    }

    public static class ActivityPassedStatus {
        public static final String PASSED   = "passed";
        public static final String UNPASSED = "unpassed";
        public static final String NONE     = "none";
    }
}
