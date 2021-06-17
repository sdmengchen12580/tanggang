package com.edusoho.kuozhi.clean.bean;

import java.util.List;

public class CheckBean {


    /**
     * schedules : [{"id":"20","start_time":"1622548800","end_time":"1622549700","status":"0","name":"教室1","truename":"系统管理员","content":""},{"id":"19","start_time":"1622546100","end_time":"1622554200","status":"0","name":"教室1","truename":"系统管理员","content":""},{"id":"18","start_time":"1622544300","end_time":"1622562300","status":"0","name":"教室1","truename":"系统管理员","content":""},{"id":"17","start_time":"1622547900","end_time":"1622561400","status":"0","name":"教室1","truename":"系统管理员","content":""},{"id":"16","start_time":"1622547900","end_time":"1622561400","status":"0","name":"教室1","truename":"系统管理员","content":""},{"id":"15","start_time":"1622532600","end_time":"1622537100","status":"0","name":"教室1","truename":"系统管理员","content":""},{"id":"14","start_time":"1622532600","end_time":"1622537100","status":"0","name":"教室1","truename":"系统管理员","content":""},{"id":"13","start_time":"1622532600","end_time":"1622537100","status":"0","name":"教室1","truename":"系统管理员","content":""},{"id":"12","start_time":"1622532600","end_time":"1622537100","status":"0","name":"教室1","truename":"系统管理员","content":""},{"id":"11","start_time":"1622532600","end_time":"1622537100","status":"0","name":"教室1","truename":"系统管理员","content":""}]
     * count : 15
     */

    private String count;
    private List<SchedulesBean> schedules;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public List<SchedulesBean> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<SchedulesBean> schedules) {
        this.schedules = schedules;
    }

    public static class SchedulesBean {
        /**
         * id : 20
         * start_time : 1622548800
         * end_time : 1622549700
         * status : 0
         * name : 教室1
         * truename : 系统管理员
         * content :
         */

        private String id;
        private String start_time;
        private String end_time;
        private String status;
        private String name;
        private String truename;
        private String content;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTruename() {
            return truename;
        }

        public void setTruename(String truename) {
            this.truename = truename;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
