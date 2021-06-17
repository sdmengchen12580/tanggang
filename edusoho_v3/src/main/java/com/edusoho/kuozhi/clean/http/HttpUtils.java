package com.edusoho.kuozhi.clean.http;

import android.util.Log;

import com.edusoho.kuozhi.clean.utils.StringUtils;
import com.edusoho.kuozhi.v3.EdusohoApp;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by JesseHuang on 2017/4/18.
 */

public class HttpUtils {

    public static final String X_AUTH_TOKEN_KEY = "X-Auth-Token";
    public static final String AUTH_TOKEN_KEY = "Auth-Token";
    public static final String MAPI_V2_TOKEN_KEY = "token";
    private static HttpUtils mInstance;
    private static String mBaseUrl;
    private static Map<String, String> mHeaderMaps = new TreeMap<>();

    public static HttpUtils getInstance() {
        mBaseUrl = "";
        mHeaderMaps.clear();
        if (mInstance == null) {
            synchronized (HttpUtils.class) {
                if (mInstance == null) {
                    mInstance = new HttpUtils();
                }
            }
        }
        return mInstance;
    }

    /**
     * 老接口 /mapi_v2/
     *
     * @return
     */
    public HttpUtils baseOnMapiV2() {
        mBaseUrl = EdusohoApp.app.host + "/mapi_v2/";
        return mInstance;
    }

    public HttpUtils baseOnLiveApi(String url) {
        mBaseUrl = url + "/v1/";
        return mInstance;
    }

    /**
     * 老接口 /api/
     *
     * @return
     */
    public HttpUtils baseOnApi() {
        mBaseUrl = EdusohoApp.app.host + "/api/";
        return mInstance;
    }



    public HttpUtils setBaseUrl(String url) {
        mBaseUrl = url;
        return mInstance;
    }

    public <T> T createApi(final Class<T> clazz) {
        if ("".equals(mBaseUrl) || mBaseUrl == null) {
            return RetrofitClient.getInstance(mHeaderMaps).create(clazz);
        } else {
            return RetrofitClient.getInstance(mBaseUrl, mHeaderMaps).create(clazz);
        }
    }

    public HttpUtils addTokenHeader(String token) {
        if (!StringUtils.isEmpty(token)) {
            Log.e("日志拦截器", "token+ " + token);
            mHeaderMaps.put(X_AUTH_TOKEN_KEY, token);
        }
        return mInstance;
    }

    public HttpUtils addHeader(String key, String value) {
        if (!StringUtils.isEmpty(value)) {
            mHeaderMaps.put(key, value);
        }
        return mInstance;
    }

    public HttpUtils addHeader(Map<String, String> headerMaps) {
        mHeaderMaps.putAll(headerMaps);
        return mInstance;
    }
}
