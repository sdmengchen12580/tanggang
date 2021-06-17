package com.edusoho.kuozhi.clean.bean.cloud;

import java.io.Serializable;

/**
 * Created by JesseHuang on 2017/6/22.
 */

public class CloudUserPlayAnalysis extends CloudUserAnalysis implements Serializable {

    public int start_time;
    public int end_time;

    private CloudUserPlayAnalysis(Builder builder) {
        this.action = builder.action;
        this.res_id = builder.res_id;
        this.client_uuid = builder.client_uuid;
        this.view_uuid = builder.view_uuid;
        this.level = builder.level;
        this.res_type = builder.res_type;
        this.client_type = builder.client_type;
        this.user_name = builder.user_name;
        this.user_id = builder.user_id;
        this.start_time = builder.start_time;
        this.end_time = builder.end_time;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        public String action;
        public String res_id;
        public String client_uuid;
        public String view_uuid;
        public String level;
        public String res_type;
        public String client_type;
        public String user_name;
        public int    user_id;
        public int    start_time;
        public int    end_time;

        public CloudUserPlayAnalysis build() {
            return new CloudUserPlayAnalysis(this);
        }

        public Builder setAction(String action) {
            this.action = action;
            return this;
        }

        public Builder setResId(String res_id) {
            this.res_id = res_id;
            return this;
        }

        public Builder setClientUUID(String client_uuid) {
            this.client_uuid = client_uuid;
            return this;
        }

        public Builder setViewUUID(String view_uuid) {
            this.view_uuid = view_uuid;
            return this;
        }

        public Builder setLevel(String level) {
            this.level = level;
            return this;
        }

        public Builder setResType(String res_type) {
            this.res_type = res_type;
            return this;
        }

        public Builder setClientType(String client_type) {
            this.client_type = client_type;
            return this;
        }

        public Builder setUserName(String user_name) {
            this.user_name = user_name;
            return this;
        }

        public Builder setUserId(int user_id) {
            this.user_id = user_id;
            return this;
        }

        public Builder setStartTime(int start_time) {
            this.start_time = start_time;
            return this;
        }

        public Builder setEndTime(int end_time) {
            this.end_time = end_time;
            return this;
        }
    }
}
