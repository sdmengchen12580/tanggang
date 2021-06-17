package com.edusoho.kuozhi.clean.http.newutils;

import android.util.Log;

import com.edusoho.kuozhi.clean.bean.PeopleABean;
import com.edusoho.kuozhi.clean.http.RequestInterceptor;
import com.edusoho.kuozhi.clean.utils.DigestUtils;
import com.edusoho.kuozhi.clean.utils.PhotoUtils;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Headers;

/**
 * Created by JesseHuang on 2017/4/18.
 */

public class NewRetrofitClient {

    private static Retrofit.Builder retrofitBuilder;
    private static RequestInterceptor mRequestInterceptor;

    public static Retrofit getInstance(String baseUrl, Map<String, String> headerMaps) {
        if (retrofitBuilder == null) {
            retrofitBuilder = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
        }
        headerMaps.put("Accept", "application/vnd.edusoho.v2+json");
        headerMaps.put("Content-Type", "application/x-www-form-urlencoded");
        headerMaps.put("charset", "utf-8");
        if (headerMaps != null) {
            mRequestInterceptor = new RequestInterceptor(headerMaps);
        }
        Log.e("拦截连接: ", "baseUrl=" + baseUrl);
        retrofitBuilder.baseUrl(baseUrl).client(getClient());
        return retrofitBuilder.build();
    }

    public static Retrofit getInstance(String baseUrl, String date1, String date2) {
        if (retrofitBuilder == null) {
            retrofitBuilder = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
        }
//        Map<String, String> headerMaps = new HashMap<>();
//        String timeS = PhotoUtils.getNetTimeCu();
//        String timeS = System.currentTimeMillis()+"";
//        headerMaps.put("AppKey", "eb792643147d3026489ce6c9e5384332");
//        headerMaps.put("Timestamp", timeS);
//        headerMaps.put("Content-Type", "application/json");
//        headerMaps.put("Sign", DigestUtils.getMd5Value("eb792643147d3026489ce6c9e5384332" +
//                "8ec2c669d077538ffb39f0a6fc70ec48" +
//                timeS +
//                "/v1/aiop/api/2f7awxekgvls/face/compare/PERSON/person/compareFromBase64" +
//                new Gson().toJson(new PeopleABean(date1, date2))
//        ));
//        mRequestInterceptor = new RequestInterceptor(headerMaps);
        retrofitBuilder.baseUrl(baseUrl).client(getClientN());
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

    private static OkHttpClient getClientN() {
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
//                .addInterceptor(mRequestInterceptor)
                .readTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(50, TimeUnit.SECONDS)
                .connectTimeout(600, TimeUnit.SECONDS)
                .build();
    }
}
