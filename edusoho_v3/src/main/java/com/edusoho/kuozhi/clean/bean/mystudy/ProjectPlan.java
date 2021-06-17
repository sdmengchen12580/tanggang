package com.edusoho.kuozhi.clean.bean.mystudy;

import java.io.Serializable;
import java.util.List;

/**
 * Created by RexXiang on 2018/3/19.
 */

public class ProjectPlan implements Serializable {

    private String                name;
    private List<ItemsDetailBean> itemsDetail;
    private CoverBean             cover;
    private String                summary;
    private String                endTime;
    private String                startTime;
    private String                enrollmentEndDate;
    private String                enrollmentStartDate;
    private String                maxStudentNum;
    private String                studentNum;
    private String                categoryName;
    private String                applyStatus;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ItemsDetailBean> getItemsDetail() {
        return itemsDetail;
    }


    public void setItemsDetail(List<ItemsDetailBean> itemsDetail) {
        this.itemsDetail = itemsDetail;
    }

    public CoverBean getCover() {
        return cover;
    }

    public void setCover(CoverBean cover) {
        this.cover = cover;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEnrollmentEndDate() {
        return enrollmentEndDate;
    }

    public void setEnrollmentEndDate(String enrollmentEndDate) {
        this.enrollmentEndDate = enrollmentEndDate;
    }

    public String getEnrollmentStartDate() {
        return enrollmentStartDate;
    }

    public void setEnrollmentStartDate(String enrollmentStartDate) {
        this.enrollmentStartDate = enrollmentStartDate;
    }

    public String getMaxStudentNum() {
        return maxStudentNum;
    }

    public void setMaxStudentNum(String maxStudentNum) {
        this.maxStudentNum = maxStudentNum;
    }

    public String getStudentNum() {
        return studentNum;
    }

    public void setStudentNum(String studentNum) {
        this.studentNum = studentNum;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(String applyStatus) {
        this.applyStatus = applyStatus;
    }

    public static class ItemsDetailBean<T> implements Serializable {

        private String title;
        private String targetType;
        private String targetId;
        private String teacherName;
        private String startTime;
        private String endTime;
        private String place;
        private T      studyResult;
        private T      taskInfo;


        public String getPlace() {
            return place;
        }

        public void setPlace(String place) {
            this.place = place;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTargetType() {
            return targetType;
        }

        public void setTargetType(String targetType) {
            this.targetType = targetType;
        }

        public String getTargetId() {
            return targetId;
        }

        public void setTargetId(String targetId) {
            this.targetId = targetId;
        }

        public String getTeacherName() {
            return teacherName;
        }

        public void setTeacherName(String teacherName) {
            this.teacherName = teacherName;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public T getStudyResult() {
            return studyResult;
        }

        public void setStudyResult(T studyResult) {
            this.studyResult = studyResult;
        }

        public T getTaskInfo() {
            return taskInfo;
        }

        public void setTaskInfo(T taskInfo) {
            this.taskInfo = taskInfo;
        }


        public static class StudyResultBean implements Serializable {

            private String id;
            private String examId;
            private String testPaperId;
            private String userId;
            private String status;
            private String passStatus;
            private String limitedTime;
            private String beginTime;
            private String endTime;
            private String deadline;
            private float  score;
            private String objectiveScore;
            private String subjectiveScore;
            private String checkedUserId;
            private Object teacherSay;
            private String createdTime;
            private String updatedTime;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getExamId() {
                return examId;
            }

            public void setExamId(String examId) {
                this.examId = examId;
            }

            public String getTestPaperId() {
                return testPaperId;
            }

            public void setTestPaperId(String testPaperId) {
                this.testPaperId = testPaperId;
            }

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getPassStatus() {
                return passStatus;
            }

            public void setPassStatus(String passStatus) {
                this.passStatus = passStatus;
            }

            public String getLimitedTime() {
                return limitedTime;
            }

            public void setLimitedTime(String limitedTime) {
                this.limitedTime = limitedTime;
            }

            public String getBeginTime() {
                return beginTime;
            }

            public void setBeginTime(String beginTime) {
                this.beginTime = beginTime;
            }

            public String getEndTime() {
                return endTime;
            }

            public void setEndTime(String endTime) {
                this.endTime = endTime;
            }

            public String getDeadline() {
                return deadline;
            }

            public void setDeadline(String deadline) {
                this.deadline = deadline;
            }

            public float getScore() {
                return score;
            }

            public void setScore(float score) {
                this.score = score;
            }

            public String getObjectiveScore() {
                return objectiveScore;
            }

            public void setObjectiveScore(String objectiveScore) {
                this.objectiveScore = objectiveScore;
            }

            public String getSubjectiveScore() {
                return subjectiveScore;
            }

            public void setSubjectiveScore(String subjectiveScore) {
                this.subjectiveScore = subjectiveScore;
            }

            public String getCheckedUserId() {
                return checkedUserId;
            }

            public void setCheckedUserId(String checkedUserId) {
                this.checkedUserId = checkedUserId;
            }

            public Object getTeacherSay() {
                return teacherSay;
            }

            public void setTeacherSay(Object teacherSay) {
                this.teacherSay = teacherSay;
            }

            public String getCreatedTime() {
                return createdTime;
            }

            public void setCreatedTime(String createdTime) {
                this.createdTime = createdTime;
            }

            public String getUpdatedTime() {
                return updatedTime;
            }

            public void setUpdatedTime(String updatedTime) {
                this.updatedTime = updatedTime;
            }
        }

        public static class TaskInfoBean implements Serializable {

            private String taskId;
            private String mediaId;
            private String mediaType;
            private String title;
            private String place;
            private String startTime;
            private String endTime;
            private String hasHomework;
            private String homeworkDemand;
            private String homeworkDeadline;
            private String attendStatus;
            private String homeworkStatus;
            private String questionnaireStatus;

            public String getTaskId() {
                return taskId;
            }

            public void setTaskId(String taskId) {
                this.taskId = taskId;
            }

            public String getMediaId() {
                return mediaId;
            }

            public void setMediaId(String mediaId) {
                this.mediaId = mediaId;
            }

            public String getMediaType() {
                return mediaType;
            }

            public void setMediaType(String mediaType) {
                this.mediaType = mediaType;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getPlace() {
                return place;
            }

            public void setPlace(String place) {
                this.place = place;
            }

            public String getStartTime() {
                return startTime;
            }

            public void setStartTime(String startTime) {
                this.startTime = startTime;
            }

            public String getEndTime() {
                return endTime;
            }

            public void setEndTime(String endTime) {
                this.endTime = endTime;
            }

            public String getHasHomework() {
                return hasHomework;
            }

            public void setHasHomework(String hasHomework) {
                this.hasHomework = hasHomework;
            }

            public String getHomeworkDemand() {
                return homeworkDemand;
            }

            public void setHomeworkDemand(String homeworkDemand) {
                this.homeworkDemand = homeworkDemand;
            }

            public String getHomeworkDeadline() {
                return homeworkDeadline;
            }

            public void setHomeworkDeadline(String homeworkDeadline) {
                this.homeworkDeadline = homeworkDeadline;
            }

            public String getAttendStatus() {
                return attendStatus;
            }

            public void setAttendStatus(String attendStatus) {
                this.attendStatus = attendStatus;
            }

            public String getHomeworkStatus() {
                return homeworkStatus;
            }

            public void setHomeworkStatus(String homeworkStatus) {
                this.homeworkStatus = homeworkStatus;
            }

            public String getQuestionnaireStatus() {
                return questionnaireStatus;
            }

            public void setQuestionnaireStatus(String questionnaireStatus) {
                this.questionnaireStatus = questionnaireStatus;
            }
        }
    }

    public static class CoverBean implements Serializable {

        private String large;
        private String middle;
        private String small;

        public String getLarge() {
            return large;
        }

        public void setLarge(String large) {
            this.large = large;
        }

        public String getMiddle() {
            return middle;
        }

        public void setMiddle(String middle) {
            this.middle = middle;
        }

        public String getSmall() {
            return small;
        }

        public void setSmall(String small) {
            this.small = small;
        }
    }

    public class ExamTaskInfo implements Serializable {

        private String examTimeStatus;
        private float  testPaperScore;
        private int    remainingResitTimes;
        private String resitTimes;

        public String getExamTimeStatus() {
            return examTimeStatus;
        }

        public void setExamTimeStatus(String examTimeStatus) {
            this.examTimeStatus = examTimeStatus;
        }

        public float getTestPaperScore() {
            return testPaperScore;
        }

        public void setTestPaperScore(float testPaperScore) {
            this.testPaperScore = testPaperScore;
        }

        public int getRemainingResitTimes() {
            return remainingResitTimes;
        }

        public void setRemainingResitTimes(int remainingResitTimes) {
            this.remainingResitTimes = remainingResitTimes;
        }

        public String getResitTimes() {
            return resitTimes;
        }

        public void setResitTimes(String resitTimes) {
            this.resitTimes = resitTimes;
        }
    }
}
