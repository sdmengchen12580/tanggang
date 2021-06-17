package com.edusoho.kuozhi.clean.api;

import com.edusoho.kuozhi.clean.bean.DataPageResult;
import com.edusoho.kuozhi.clean.bean.StudyCourse;
import com.edusoho.kuozhi.clean.bean.mystudy.ActivityMembers;
import com.edusoho.kuozhi.clean.bean.mystudy.AssignmentModel;
import com.edusoho.kuozhi.clean.bean.mystudy.EvaluationAnswer;
import com.edusoho.kuozhi.clean.bean.mystudy.ExamAnswer;
import com.edusoho.kuozhi.clean.bean.mystudy.ExamFullMarkResult;
import com.edusoho.kuozhi.clean.bean.mystudy.ExamInfoModel;
import com.edusoho.kuozhi.clean.bean.mystudy.ExamModel;
import com.edusoho.kuozhi.clean.bean.mystudy.ExamResultModel;
import com.edusoho.kuozhi.clean.bean.mystudy.FullMarkAnswer;
import com.edusoho.kuozhi.clean.bean.mystudy.OfflineActivitiesResult;
import com.edusoho.kuozhi.clean.bean.mystudy.OfflineActivityCategory;
import com.edusoho.kuozhi.clean.bean.mystudy.PostCourseModel;
import com.edusoho.kuozhi.clean.bean.mystudy.PostCoursesProgress;
import com.edusoho.kuozhi.clean.bean.mystudy.ProjectCourseItem;
import com.edusoho.kuozhi.clean.bean.mystudy.ProjectExamItem;
import com.edusoho.kuozhi.clean.bean.mystudy.ProjectOfflineCourseItem;
import com.edusoho.kuozhi.clean.bean.mystudy.ProjectOfflineExamItem;
import com.edusoho.kuozhi.clean.bean.mystudy.ProjectPlan;
import com.edusoho.kuozhi.clean.bean.mystudy.Relationship;
import com.edusoho.kuozhi.clean.bean.mystudy.SurveyAnswer;
import com.edusoho.kuozhi.clean.bean.mystudy.SurveyAnswersModel;
import com.edusoho.kuozhi.clean.bean.mystudy.SurveyModel;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by RexXiang on 2018/1/15.
 */

public interface MyStudyApi {

    @GET("me/assignments")
    Observable<List<AssignmentModel>> getAssignment();

    @GET("me/post_courses")
    Observable<List<PostCourseModel>> getPostcourse();

    @GET("me/post_courses_progress")
    Observable<PostCoursesProgress> getPostCoursesProgress();

    @GET("me/courses")
    Observable<DataPageResult<StudyCourse>> getMyStudyCourse(@Query("offset") int offset, @Query("limit") int limit, @Query("isLearned") int status);

    @GET("offline_activities")
    Observable<OfflineActivitiesResult> getOfflineActivitiesResult(@Query("activityTimeStatus") String status);

    @GET("categories/offlineActivity")
    Observable<List<OfflineActivityCategory>> getOfflineActivitiesCategory();

    @POST("offline_activity/{offlineActivityId}/members")
    Observable<JsonObject> joinOfflineActivity(@Path("offlineActivityId") String id);

    @GET("offline_activity/{offlineActivityId}/members")
    Observable<ActivityMembers> getActivityMembers(@Path("offlineActivityId") String id);

    @GET("offline_activities/{id}")
    Observable<OfflineActivitiesResult.DataBean> getActivityDetail(@Path("id") String id);

    @GET("me/relationships")
    Observable<List<Relationship>> getRelationships(@Query("userIds") String ids);

    @FormUrlEncoded
    @POST("followers")
    Observable<JsonObject> followUser(@Field("userId") String userId);

    @DELETE("followers/{userId}")
    Observable<JsonObject> cancelFollow(@Path("userId") String id);

    @GET("plugins/survey/survey/{surveyId}")
    Observable<SurveyModel> getSurvey(@Path("surveyId") String id);

    @FormUrlEncoded
    @POST("plugins/survey/me/survey_results")
    Observable<SurveyModel> doSurvey(@Field("surveyId") String id);

    @PATCH("plugins/survey/me/survey_results/{surveyResultId}")
    Observable<JsonObject> submitSurvey(@Path("surveyResultId") String id, @Body SurveyAnswer answer);

    @PATCH("plugins/survey/me/survey_results/{surveyResultId}")
    Observable<JsonObject> submitEvaluation(@Path("surveyResultId") String id, @Body EvaluationAnswer answer);

    @GET("plugins/survey/survey_result_statistics/{surveyId}")
    Observable<SurveyModel> getSurveyResult(@Path("surveyId") String surveyId);

    @GET("plugins/survey/questionnaire_item_answers")
    Observable<SurveyAnswersModel> getQuestionnaireAnswers(
            @Query("questionnaireItemId") String questionId, @Query("surveyId") String surveyId, @Query("offset") int offset, @Query("limit") int limit);

    @GET("plugins/exam/exams/{id}")
//    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    Observable<ExamInfoModel> getExamInfo(@Path("id") String examId);

    @FormUrlEncoded
    @POST("plugins/exam/me/exam_results")
    Observable<ExamModel> getExam(@Field("examId") String examId);

    @PATCH("plugins/exam/me/exam_results/{examResultId}")
    Observable<JsonObject> submitExam(@Path("examResultId") String id, @Body ExamAnswer answer);

    @GET("plugins/exam/me/exam_results/{id}")
    Observable<ExamResultModel> getExamResult(@Path("id") String resultId, @Query("answerStatus") String answerStatus);

    @POST("plugins/exam/me/exam_result_check")
    Observable<ExamFullMarkResult> getFullMarkResult(@Body FullMarkAnswer answer);

    @GET("project_plan/{id}")
    Observable<ProjectPlan> getProjectPlan(@Path("id") String projectId);

    @GET("project_plan")
    Observable<OfflineActivitiesResult> getProjectList(@Query("currentState") String status);

    @GET("categories/projectPlan")
    Observable<List<OfflineActivityCategory>> getProjectCategory();

    @POST("project_plan/{projectPlanId}/members")
    Observable<JsonObject> joinProject(@Path("projectPlanId") String id);

    @GET("project_plan/{projectPlanId}/item")
    Observable<List<ProjectCourseItem>> getProjectCourseItem(@Path("projectPlanId") String projectId, @Query("targetId") String targetId, @Query("targetType") String targetType);

    @GET("project_plan/{projectPlanId}/item")
    Observable<List<ProjectOfflineCourseItem>> getProjectOfflineCourseItem(@Path("projectPlanId") String projectId, @Query("targetId") String targetId, @Query("targetType") String targetType);

    @GET("project_plan/{projectPlanId}/item")
    Observable<ProjectExamItem> getProjectExamItem(@Path("projectPlanId") String projectId, @Query("targetId") String targetId, @Query("targetType") String targetType);

    @GET("project_plan/{projectPlanId}/item")
    Observable<ProjectOfflineExamItem> getProjectOfflineExamItem(@Path("projectPlanId") String projectId, @Query("targetId") String targetId, @Query("targetType") String targetType);
}
