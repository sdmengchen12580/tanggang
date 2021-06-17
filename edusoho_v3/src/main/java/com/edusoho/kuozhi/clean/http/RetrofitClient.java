package com.edusoho.kuozhi.clean.http;

import android.util.Log;

import com.edusoho.kuozhi.v3.EdusohoApp;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by JesseHuang on 2017/4/18.
 */

public class RetrofitClient {

    private static Retrofit.Builder retrofitBuilder;
    private static RequestInterceptor mRequestInterceptor;

    public static Retrofit getInstance(Map<String, String> headerMaps) {
        if (retrofitBuilder == null) {
            synchronized (RetrofitClient.class) {
                if (retrofitBuilder == null) {
                    retrofitBuilder = new Retrofit.Builder()
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
                }
            }
        }
        headerMaps.put("Accept", "application/vnd.edusoho.v2+json");
        mRequestInterceptor = new RequestInterceptor(headerMaps);
        retrofitBuilder.baseUrl(getBaseUrl()).client(getClient());
        return retrofitBuilder.build();
    }

    public static Retrofit getInstance(String baseUrl, Map<String, String> headerMaps) {
        if (retrofitBuilder == null) {
            retrofitBuilder = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
        }
        if (headerMaps != null) {
            mRequestInterceptor = new RequestInterceptor(headerMaps);
        }
        retrofitBuilder.baseUrl(baseUrl).client(getClient());
        return retrofitBuilder.build();
    }

    private static OkHttpClient getClient() {
        //拦截器
        HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.BODY;
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.e("日志拦截器: ", message);
            }
        });
        loggingInterceptor.setLevel(level);
        return new OkHttpClient.Builder()
                .followRedirects(false)
                .followSslRedirects(false)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(mRequestInterceptor)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    public static String getBaseUrl() {
        return EdusohoApp.app.host + "/api/";
    }
}
