package com.edusoho.kuozhi.clean.api;

import com.edusoho.kuozhi.clean.bean.AppChannel;

import java.util.List;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by JesseHuang on 2017/5/22.
 */

public interface AppApi {

    @GET("app/channels")
    Observable<List<AppChannel>> getAppChannels();

}
