package com.edusoho.kuozhi.clean.api;


import com.edusoho.kuozhi.clean.bean.seting.CloudVideoSetting;
import com.edusoho.kuozhi.clean.bean.seting.CourseSetting;
import com.edusoho.kuozhi.clean.bean.seting.UserSetting;

import retrofit2.http.GET;
import rx.Observable;

public interface SettingApi {
    @GET("setting/course")
    Observable<CourseSetting> getCourseSet();

    @GET("setting/user")
    Observable<UserSetting> getUserSetting();

    @GET("setting/cloud_video")
    Observable<CloudVideoSetting> getCloudVideoSetting();
}
