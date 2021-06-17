package com.edusoho.kuozhi.clean.api;

import java.util.LinkedHashMap;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by JesseHuang on 2017/6/22.
 * 平台相关接口
 */

public interface RNVersionApi {

    @GET("version/{code}")
    Observable<LinkedHashMap> getVersion(@Path("code") String code);
}
