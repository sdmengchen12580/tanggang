package com.edusoho.kuozhi.clean.api;

import com.edusoho.kuozhi.clean.bean.CourseMember;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseReview;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.bean.DataPageResult;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by JesseHuang on 2017/4/18.
 */

public interface CourseSetApi {

    @GET("course_sets/{id}")
    Observable<CourseSet> getCourseSet(@Path("id") int courseSetId);

    @GET("course_sets/{id}/reviews")
    Observable<DataPageResult<CourseReview>> getCourseReviews(@Path("id") int courseSetId, @Query("limit") int limit, @Query("offset") int offset);

    @GET("course_sets/{courseSetId}/latest_members")
    Observable<List<CourseMember>> getCourseSetMembers(@Path("courseSetId") int courseSetId, @Query("offset") int offset, @Query("limit") int limit);

    @GET("me/course_sets/{courseSetId}/course_members")
    Observable<List<CourseMember>> getMeCourseSetProject(@Path("courseSetId") int courseSetId);

    @GET("course_sets/{id}/courses")
    Observable<List<CourseProject>> getCourseProjects(@Path("id") int courseSetId);

    @GET("course_sets/{id}/my_join_courses")
    Observable<List<CourseProject>> getMyCourseProject(@Path("courseSetId") int id);

}
