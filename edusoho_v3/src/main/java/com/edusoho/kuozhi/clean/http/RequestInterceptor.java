package com.edusoho.kuozhi.clean.http;

import android.net.Uri;
import android.util.Log;

import com.edusoho.kuozhi.v3.view.qr.decode.Intents;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Created by JesseHuang on 2017/4/18.
 */

public class RequestInterceptor implements Interceptor {

    private Map<String, String> mHeaderMaps = new TreeMap<>();

    public RequestInterceptor(Map<String, String> headerMaps) {
        this.mHeaderMaps = headerMaps;
    }

    //String urlDeal = URLEncoder.encode(request.url().toString()).replaceAll("\\+", "%20");
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder requestBuilder = chain.request().newBuilder();
        if (mHeaderMaps.size() > 0) {
            for (Map.Entry<String, String> entry : mHeaderMaps.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
                Log.e("intercept: ", "entry.getKey()=" + entry.getKey() + "      entry.getValue()=" + entry.getValue());
            }
        }
        //打印日志
        Request original = chain.request();
        Response response = chain.proceed(original);
        Log.e("日志打印", String.format("...\n请求链接：%s\n请求参数：%s\n请求响应%s",
                original.url(),
                getRequestInfo(original),
                getResponseInfo(response)));
        return chain.proceed(requestBuilder.build());
    }

    public URL convertToURL(String string) {
        try {
            String decodedURL = URLDecoder.decode(string, "UTF-8");
            URL url = new URL(decodedURL);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            return uri.toURL();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 打印请求消息
     *
     * @param request 请求的对象
     */
    private String getRequestInfo(Request request) {
        String str = "";
        if (request == null) {
            return str;
        }
        RequestBody requestBody = request.body();
        if (requestBody == null) {
            return str;
        }
        try {
            Buffer bufferedSink = new Buffer();
            requestBody.writeTo(bufferedSink);
            Charset charset = Charset.forName("utf-8");
            str = bufferedSink.readString(charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 打印返回消息
     *
     * @param response 返回的对象
     */
    private String getResponseInfo(Response response) {
        String str = "";
        if (response == null || !response.isSuccessful()) {
            return str;
        }
        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();
        BufferedSource source = responseBody.source();
        try {
            source.request(Long.MAX_VALUE); // Buffer the entire body.
        } catch (IOException e) {
            e.printStackTrace();
        }
        Buffer buffer = source.buffer();
        Charset charset = Charset.forName("utf-8");
        if (contentLength != 0) {
            str = buffer.clone().readString(charset);
        }
        return str;
    }

}
