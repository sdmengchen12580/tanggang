package com.edusoho.kuozhi.clean.api;

import com.edusoho.kuozhi.clean.bean.CheckBean;
import com.edusoho.kuozhi.clean.bean.CheckOrNo;
import com.edusoho.kuozhi.clean.bean.CompareFromBase64;
import com.edusoho.kuozhi.clean.bean.ScheduleBean;
import com.edusoho.kuozhi.clean.bean.ScheduleListBean;
import com.edusoho.kuozhi.clean.bean.mystudy.ExamInfoModel;
import com.edusoho.kuozhi.clean.bean.studyhome.TrainRoom;
import com.edusoho.kuozhi.v3.model.result.UserResult;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

//接口文件中加上@FormUrlEncoded的时候，content-type是text格式
public interface SpStudyHomeApi {

    //测试登录
    @GET("mapi_v2/User/login")
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    Observable<UserResult> login(@Query("_username") String page, @Query("_password") String limit);

    //培训室列表
    @GET("api/plugins/TrainRoom/TrainRoom")
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    Observable<List<TrainRoom>> trainroom(@Query("page") int page, @Query("limit") int limit);

    //发起申请培训室
    @POST("api/plugins/trainRoom/Schedule")
    @FormUrlEncoded
    Observable<ScheduleBean> Schedule(@Field("id") String id,
                                      @Field("start_time") String start_time,
                                      @Field("end_time") String end_time);

    //申请列表
    @GET("api/plugins/trainRoom/Schedule")
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    Observable<ScheduleListBean> ScheduleList();

    //审核列表
    @GET("api/plugins/trainRoom/Schedule?check=1")
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    Observable<CheckBean> check();

    //审核是否通过
    @PATCH("api/plugins/trainRoom/Schedule/{id}")
    @FormUrlEncoded
    Observable<CheckOrNo> checkOrNo(@Path("id") String id,
                                    @Field("id") String ids,
                                    @Field("status") String status);

    //fixme 人脸识别
    @POST("PERSON/person/compareFromBase64")
    @FormUrlEncoded
    @Headers("Content-Type:application/json")
    Observable<CompareFromBase64> compareFromBase64(@Field("img1Base64") String img1Base64,
                                                    @Field("img2Base64") String img2Base64,
                                                    @Header("AppKey") String AppKey,
                                                    @Header("Timestamp") String Timestamp,
                                                    @Header("Content-Type") String type,
                                                    @Header("Sign") String Sign);
}
