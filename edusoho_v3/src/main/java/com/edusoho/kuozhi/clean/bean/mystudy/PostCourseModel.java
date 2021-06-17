package com.edusoho.kuozhi.clean.bean.mystudy;

import java.io.Serializable;

/**
 * Created by RexXiang on 2018/1/18.
 */

public class PostCourseModel implements Serializable {

    private String courseId;
    private float learnedCompulsoryTaskNum;
    private float compulsoryTaskNum;
    private int totalLearnTime;
    private CourseSetBean courseSet;

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public float getLearnedCompulsoryTaskNum() {
        return learnedCompulsoryTaskNum;
    }

    public void setLearnedCompulsoryTaskNum(float learnedCompulsoryTaskNum) {
        this.learnedCompulsoryTaskNum = learnedCompulsoryTaskNum;
    }

    public float getCompulsoryTaskNum() {
        return compulsoryTaskNum;
    }

    public void setCompulsoryTaskNum(float compulsoryTaskNum) {
        this.compulsoryTaskNum = compulsoryTaskNum;
    }

    public int getTotalLearnTime() {
        return totalLearnTime;
    }

    public void setTotalLearnTime(int totalLearnTime) {
        this.totalLearnTime = totalLearnTime;
    }

    public CourseSetBean getCourseSet() {
        return courseSet;
    }

    public void setCourseSet(CourseSetBean courseSet) {
        this.courseSet = courseSet;
    }

    public static class CourseSetBean {

        private String id;
        private String type;
        private String title;
        private String subtitle;
        private String summary;
        private CoverBean cover;
        private String studentNum;
        private String discount;
        private String maxCoursePrice;
        private String minCoursePrice;
        private MinCoursePrice2Bean minCoursePrice2;
        private MaxCoursePrice2Bean maxCoursePrice2;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public void setSubtitle(String subtitle) {
            this.subtitle = subtitle;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public CoverBean getCover() {
            return cover;
        }

        public void setCover(CoverBean cover) {
            this.cover = cover;
        }

        public String getStudentNum() {
            return studentNum;
        }

        public void setStudentNum(String studentNum) {
            this.studentNum = studentNum;
        }

        public String getDiscount() {
            return discount;
        }

        public void setDiscount(String discount) {
            this.discount = discount;
        }

        public String getMaxCoursePrice() {
            return maxCoursePrice;
        }

        public void setMaxCoursePrice(String maxCoursePrice) {
            this.maxCoursePrice = maxCoursePrice;
        }

        public String getMinCoursePrice() {
            return minCoursePrice;
        }

        public void setMinCoursePrice(String minCoursePrice) {
            this.minCoursePrice = minCoursePrice;
        }

        public MinCoursePrice2Bean getMinCoursePrice2() {
            return minCoursePrice2;
        }

        public void setMinCoursePrice2(MinCoursePrice2Bean minCoursePrice2) {
            this.minCoursePrice2 = minCoursePrice2;
        }

        public MaxCoursePrice2Bean getMaxCoursePrice2() {
            return maxCoursePrice2;
        }

        public void setMaxCoursePrice2(MaxCoursePrice2Bean maxCoursePrice2) {
            this.maxCoursePrice2 = maxCoursePrice2;
        }

        public static class CoverBean {
            /**
             * small : http://dev.training.com/assets/img/default/course.png
             * middle : http://dev.training.com/assets/img/default/course.png
             * large : http://dev.training.com/assets/img/default/course.png
             */

            private String small;
            private String middle;
            private String large;

            public String getSmall() {
                return small;
            }

            public void setSmall(String small) {
                this.small = small;
            }

            public String getMiddle() {
                return middle;
            }

            public void setMiddle(String middle) {
                this.middle = middle;
            }

            public String getLarge() {
                return large;
            }

            public void setLarge(String large) {
                this.large = large;
            }
        }

        public static class MinCoursePrice2Bean {
            /**
             * currency : RMB
             * amount : 0.00
             */

            private String currency;
            private String amount;

            public String getCurrency() {
                return currency;
            }

            public void setCurrency(String currency) {
                this.currency = currency;
            }

            public String getAmount() {
                return amount;
            }

            public void setAmount(String amount) {
                this.amount = amount;
            }
        }

        public static class MaxCoursePrice2Bean {
            /**
             * currency : RMB
             * amount : 0.00
             */

            private String currency;
            private String amount;

            public String getCurrency() {
                return currency;
            }

            public void setCurrency(String currency) {
                this.currency = currency;
            }

            public String getAmount() {
                return amount;
            }

            public void setAmount(String amount) {
                this.amount = amount;
            }
        }
    }
}
