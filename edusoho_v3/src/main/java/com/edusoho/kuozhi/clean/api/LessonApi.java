package com.edusoho.kuozhi.clean.api;

import com.edusoho.eslive.athena.entity.LiveTicket;
import com.edusoho.kuozhi.v3.entity.lesson.LessonItem;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by JesseHuang on 2017/6/13.
 */

public interface LessonApi {

    @GET("lessons/{id}?hls_encryption=1")
    Observable<LessonItem> getLesson(@Path("id") int lessonId);

    @FormUrlEncoded
    @POST("lessons/{lessonId}/live_tickets")
    Observable<LiveTicket> getLiveTicket(@Path("lessonId") int taskId, @Field("device") String device);

    @GET("lessons/{lessonId}/live_tickets/{ticket}")
    Observable<LiveTicket> getLiveTicketInfo(@Path("lessonId") int taskId, @Path("ticket") String ticket);

    @GET("lessons/{lessonId}/replay")
    Observable<LiveTicket> getLiveReplay(@Path("lessonId") int taskId, @Query("device") String device);
}
