package com.edusoho.kuozhi.clean.api;

import com.edusoho.kuozhi.clean.bean.Announcement;
import com.edusoho.kuozhi.clean.bean.seting.CourseSetting;
import com.edusoho.kuozhi.clean.bean.DataPageResult;
import com.edusoho.kuozhi.clean.bean.Notification;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;


public interface CommonApi {

    @GET("setting/course")
    Observable<CourseSetting> getCourseSet();

    @GET("notifications")
    Observable<DataPageResult<Notification>> getNotifications(@Query("type") String type, @Query("startTime") int startTime, @Query("offset") int offset, @Query("limit") int limit);

    @GET("announcements")
    Observable<DataPageResult<Announcement>> getAnnouncements(@Query("startTime") int startTime, @Query("offset") int offset, @Query("limit") int limit);

    @GET
    Observable<ResponseBody> requestUrl(@Url String url);
}
