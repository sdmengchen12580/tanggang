package com.edusoho.kuozhi.clean.api;

import com.edusoho.kuozhi.clean.bean.CourseItem;
import com.edusoho.kuozhi.clean.bean.CourseMember;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.kuozhi.clean.bean.DataPageResult;
import com.edusoho.kuozhi.clean.bean.Member;
import com.edusoho.kuozhi.clean.bean.Review;
import com.edusoho.kuozhi.clean.bean.TaskEvent;
import com.edusoho.kuozhi.v3.entity.course.DiscussDetail;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by JesseHuang on 2017/4/18.
 */

public interface CourseApi {

    @GET("courses/{id}/items")
    Observable<List<CourseItem>> getCourseItems(@Path("id") int courseId, @Query("onlyPublished") int status);

    @GET("courses/{courseId}/tasks/{taskId}")
    Observable<CourseTask> getCourseTask(@Path("courseId") int courseId, @Path("taskId") int taskId);

    @GET("courses/{id}")
    Observable<CourseProject> getCourseProject(@Path("id") int courseId);

    @GET("courses/{id}/trial_tasks/first")
    Observable<CourseTask> getTrialFirstTask(@Path("id") int id);

    @GET("courses/{id}/members")
    Observable<DataPageResult<Member>> getCourseMembers(@Path("id") int courseId, @Query("role") String role,
                                                        @Query("offset") int offset, @Query("limit") int limit);

    @GET("courses/{courseId}/reviews")
    Observable<DataPageResult<Review>> getCourseProjectReviews(@Path("courseId") int courseId,
                                                               @Query("offset") int offset, @Query("limit") int limit);

    @POST("courses/{id}/members")
    Observable<CourseMember> joinFreeOrVipCourse(@Path("id") int courseId);

    @GET("courses/{courseId}/threads?limit=15&simplify=0&sort=posted")
    Observable<DiscussDetail> getCourseDiscuss(@Path("courseId") int courseId, @Query("start") int start);

    @PATCH("courses/{courseId}/tasks/{taskId}/events/finish")
    Observable<TaskEvent> setCourseTaskFinish(@Path("courseId") int courseId, @Path("taskId") int taskId);

    @FormUrlEncoded
    @PATCH("courses/{courseId}/tasks/{taskId}/events/doing")
    Observable<TaskEvent> setCourseTaskDoing(@Path("courseId") int courseId, @Path("taskId") int taskId, @FieldMap Map<String, String> fieldMaps);

    @PATCH("courses/{courseId}/events/course_view")
    Observable<JsonObject> setCourseView(@Path("courseId") int courseId);
}
