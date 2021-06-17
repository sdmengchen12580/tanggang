package com.edusoho.kuozhi.clean.bean.mystudy;

import java.io.Serializable;
import java.util.List;

/**
 * Created by RexXiang on 2018/1/25.
 */

public class OfflineActivitiesResult implements Serializable{

    private PagingBean paging;
    private List<DataBean> data;

    public PagingBean getPaging() {
        return paging;
    }

    public void setPaging(PagingBean paging) {
        this.paging = paging;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class PagingBean {

        private int total;
        private int offset;
        private int limit;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }
    }

    public static class DataBean {

        private String title;
        private String startDate;
        private String endDate;
        private String address;
        private String studentNum;
        private String activityTimeStatus;
        private String id;
        private String name;
        private String startTime;
        private String endTime;
        private String status;
        private String summary;
        private CoverBean cover;
        private String itemNum;
        private String createdUserId;
        private String orgId;
        private String orgCode;
        private String createdTime;
        private String updatedTime;
        private String maxStudentNum;
        private String requireAudit;
        private String enrollmentEndDate;
        private String requireEnrollment;
        private String enrollmentStartDate;
        private String categoryId;
        private String currentState;
        private String categoryName;
        private String applyStatus;


        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getStudentNum() {
            return studentNum;
        }

        public void setStudentNum(String studentNum) {
            this.studentNum = studentNum;
        }

        public String getActivityTimeStatus() {
            return activityTimeStatus;
        }

        public void setActivityTimeStatus(String activityTimeStatus) {
            this.activityTimeStatus = activityTimeStatus;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
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

        public String getItemNum() {
            return itemNum;
        }

        public void setItemNum(String itemNum) {
            this.itemNum = itemNum;
        }

        public String getCreatedUserId() {
            return createdUserId;
        }

        public void setCreatedUserId(String createdUserId) {
            this.createdUserId = createdUserId;
        }

        public String getOrgId() {
            return orgId;
        }

        public void setOrgId(String orgId) {
            this.orgId = orgId;
        }

        public String getOrgCode() {
            return orgCode;
        }

        public void setOrgCode(String orgCode) {
            this.orgCode = orgCode;
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

        public String getMaxStudentNum() {
            return maxStudentNum;
        }

        public void setMaxStudentNum(String maxStudentNum) {
            this.maxStudentNum = maxStudentNum;
        }

        public String getRequireAudit() {
            return requireAudit;
        }

        public void setRequireAudit(String requireAudit) {
            this.requireAudit = requireAudit;
        }

        public String getEnrollmentEndDate() {
            return enrollmentEndDate;
        }

        public void setEnrollmentEndDate(String enrollmentEndDate) {
            this.enrollmentEndDate = enrollmentEndDate;
        }

        public String getRequireEnrollment() {
            return requireEnrollment;
        }

        public void setRequireEnrollment(String requireEnrollment) {
            this.requireEnrollment = requireEnrollment;
        }

        public String getEnrollmentStartDate() {
            return enrollmentStartDate;
        }

        public void setEnrollmentStartDate(String enrollmentStartDate) {
            this.enrollmentStartDate = enrollmentStartDate;
        }

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        public String getProjectPlanTimeStatus() {
            return currentState;
        }

        public void setProjectPlanTimeStatus(String projectPlanTimeStatus) {
            this.currentState = projectPlanTimeStatus;
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

        public static class CoverBean {
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
    }
}
