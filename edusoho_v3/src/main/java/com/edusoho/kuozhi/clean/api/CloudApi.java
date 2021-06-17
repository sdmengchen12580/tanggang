package com.edusoho.kuozhi.clean.api;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by JesseHuang on 2017/6/22.
 * 平台相关接口
 */

public interface CloudApi {

    @GET("http://service-stats.qiqiuyun.net/res/player.png")
    Observable<String> uploadUserPlayAnalysis(@Query(value = "data", encoded = true) String data);
}
