package com.edusoho.kuozhi.clean.api;

import com.edusoho.kuozhi.clean.bean.Classroom;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.v3.entity.course.DiscussDetail;
import com.edusoho.kuozhi.v3.model.bal.course.ClassroomMember;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by DF on 2017/5/23.
 */

public interface ClassroomApi {

    @GET("classrooms/{classroomId}")
    Observable<Classroom> getClassroom(@Path("classroomId") int classroomId);

    @GET("classrooms/{classroomId}/courses")
    Observable<List<CourseProject>> getCourseProjects(@Path("classroomId") int classroomId);

    @GET("me/classroom_members/{classroomId}")
    Observable<JsonObject> getClassroomStatus(@Path("classroomId") int classroomId);

    @GET("classrooms/{classroomId}/threads")
    Observable<DiscussDetail> getClassroomDiscuss(@Path("classroomId") int id, @Query("start") int start
            , @Query("limit") int limit, @Query("sort") String sort);

    @POST("classrooms/{classroomId}/members")
    Observable<ClassroomMember> joinClassroom(@Path("classroomId") int id);

}
