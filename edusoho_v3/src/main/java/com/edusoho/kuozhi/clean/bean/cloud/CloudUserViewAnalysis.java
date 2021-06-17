package com.edusoho.kuozhi.clean.bean.cloud;

import java.io.Serializable;

/**
 * Created by JesseHuang on 2017/6/22.
 */

public class CloudUserViewAnalysis extends CloudUserAnalysis implements Serializable {

    private int loading_time;

    private CloudUserViewAnalysis(Builder builder) {
        this.action = builder.action;
        this.res_id = builder.res_id;
        this.client_uuid = builder.client_uuid;
        this.view_uuid = builder.view_uuid;
        this.level = builder.level;
        this.res_type = builder.res_type;
        this.client_type = builder.client_type;
        this.user_name = builder.user_name;
        this.user_id = builder.user_id;
        this.loading_time = builder.loading_time;
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
        public int    loading_time;

        public CloudUserViewAnalysis build() {
            return new CloudUserViewAnalysis(this);
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

        public Builder setLoadingTime(int loadingTime) {
            this.loading_time = loadingTime;
            return this;
        }
    }
}
