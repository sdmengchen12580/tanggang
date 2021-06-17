package com.edusoho.kuozhi.clean.http.newutils;

import android.util.Log;

import com.edusoho.kuozhi.clean.http.RetrofitClient;
import com.edusoho.kuozhi.clean.utils.StringUtils;
import com.edusoho.kuozhi.v3.EdusohoApp;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by JesseHuang on 2017/4/18.
 */

public class NewHttpUtils {

    public static final boolean isTestApi = false; //fixme 测试还是正式
    public static String BASE_NEW_API_URL; //域名
    public static final String X_AUTH_TOKEN_KEY = "X-Auth-Token";
    public static final String AUTH_TOKEN_KEY = "Auth-Token";
    public static final String MAPI_V2_TOKEN_KEY = "token";
    private static NewHttpUtils mInstance;
    private static String mBaseUrl;
    private static Map<String, String> mHeaderMaps = new TreeMap<>();

    public static NewHttpUtils getInstance() {
        if (isTestApi) {
            BASE_NEW_API_URL = "http://218.94.158.227:61013/"; // 测试
        } else {
            BASE_NEW_API_URL = "https://elearning.jtport.com/"; // 正式
        }
        mBaseUrl = "";
        mHeaderMaps.clear();
        if (mInstance == null) {
            synchronized (NewHttpUtils.class) {
                if (mInstance == null) {
                    mInstance = new NewHttpUtils();
                }
            }
        }
        return mInstance;
    }

    public NewHttpUtils setBaseUrl(String url) {
        mBaseUrl = url;
        return mInstance;
    }

    public <T> T createApi(final Class<T> clazz) {
        if ("".equals(mBaseUrl) || mBaseUrl == null) {
            return NewRetrofitClient.getInstance(BASE_NEW_API_URL, mHeaderMaps).create(clazz);
        } else {
            return NewRetrofitClient.getInstance(mBaseUrl, mHeaderMaps).create(clazz);
        }
    }

    public <T> T createApiPeopleAuth(final Class<T> clazz, String date1, String date2) {
        return NewRetrofitClient.getInstance(mBaseUrl, date1, date2).create(clazz);
    }

    public NewHttpUtils addTokenHeader(String token) {
        if (!StringUtils.isEmpty(token)) {
            Log.e("日志拦截器", "token+ " + token);
            mHeaderMaps.put(X_AUTH_TOKEN_KEY, token);
        }
        return mInstance;
    }

    public NewHttpUtils addHeader(String key, String value) {
        if (!StringUtils.isEmpty(value)) {
            mHeaderMaps.put(key, value);
        }
        return mInstance;
    }

    public NewHttpUtils addHeader(Map<String, String> headerMaps) {
        mHeaderMaps.putAll(headerMaps);
        return mInstance;
    }
}
