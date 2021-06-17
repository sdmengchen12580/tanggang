package com.edusoho.kuozhi.clean.react;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.edusoho.kuozhi.clean.api.RNVersionApi;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.utils.FileUtils;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.LinkedHashMap;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by suju on 2017/8/25.
 */

class BundleManager {

    private Context mContext;
    private String  mCode;
    private static final String UPDATE_HOST = "http://www.edusoho.com/";

    public BundleManager(Context context, String code) {
        this.mCode = code;
        this.mContext = context;
    }

    private HttpGet getHttpGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("User-Agent", String.format("%s%s%s-rn-update", Build.MODEL, " Android-kuozhi ", Build.VERSION.SDK));

        return httpGet;
    }

    private HttpClient initHttpClient() {
        HttpParams params = new BasicHttpParams();
        ConnManagerParams.setMaxTotalConnections(params, 50);
        //超时
        HttpConnectionParams.setConnectionTimeout(params, 3000);
        HttpConnectionParams.setSoTimeout(params, 3000);
        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory
                .getSocketFactory(), 80));
        return new DefaultHttpClient(new ThreadSafeClientConnManager(params, schReg), params);
    }

    public Observable<Boolean> verifiBundleVersion() {
        return HttpUtils.getInstance()
                .setBaseUrl(UPDATE_HOST)
                .createApi(RNVersionApi.class)
                .getVersion(String.format("edusoho-rn-%s", mCode))
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<LinkedHashMap, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(LinkedHashMap newVersion) {
                        boolean saveResult = false;
                        JSONObject localVersion = getVersion();
                        if (localVersion == null ||
                                CommonUtil.compareVersion(
                                        localVersion.optString("version"), newVersion.get("version").toString()) == Const.LOW_VERSIO) {
                            Log.d("BundleManager", "update bundle");
                            saveBundlMeta(newVersion);
                            saveResult = saveBunbldFile(newVersion.get("AndroidUrl").toString());
                        }
                        return Observable.just(saveResult);
                    }
                });
    }

    private File downloadBunbldFile(File bundleDir, String name, String url) {
        File cache = new File(bundleDir, name + "_temp");
        HttpGet httpGet = getHttpGet(url);
        try {
            if (cache.exists()) {
                httpGet.setHeader("Range", "bytes=" + cache.length());
            }

            HttpClient httpClient = initHttpClient();
            HttpResponse response = httpClient.execute(httpGet);
            if (AppUtil.saveStreamToFile(response.getEntity().getContent(), cache, true)) {
                File realFile = new File(cache.getAbsolutePath().replace("_temp", ""));
                if (cache.renameTo(realFile)) {
                    return realFile;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpGet.abort();
        }

        return null;
    }

    private boolean saveBundlMeta(LinkedHashMap newVersion) {
        File bundleDir = mContext.getDir("rn_bundle", Context.MODE_PRIVATE);
        if (!bundleDir.exists()) {
            return false;
        }

        JSONObject jsonObject = new JSONObject(newVersion);
        return FileUtils.writeFile(new File(bundleDir, "version.meta").getAbsolutePath(), jsonObject.toString());
    }

    private boolean saveBunbldFile(String url) {
        File bundleDir = mContext.getDir("rn_bundle", Context.MODE_PRIVATE);
        if (!bundleDir.exists()) {
            return false;
        }
        File zipFile = downloadBunbldFile(bundleDir, String.format("%s.android.zip", mCode), url);
        if (zipFile != null) {
            return AppUtil.unZipFile(bundleDir, zipFile);
        }

        return false;
    }

    public String getLocalBundleFilePath() {
        File bundleDir = mContext.getDir("rn_bundle", Context.MODE_PRIVATE);
        if (!bundleDir.exists()) {
            return String.format("assets://%s.android.jsbundle", mCode);
        }

        File bundleFile = new File(bundleDir, String.format("%s.android.jsbundle", mCode));
        if (!bundleFile.exists()) {
            return String.format("assets://%s.android.jsbundle", mCode);
        }
        return bundleFile.getAbsolutePath();
    }

    private JSONObject getVersion() {
        File bundleDir = mContext.getDir("rn_bundle", Context.MODE_PRIVATE);
        if (!bundleDir.exists()) {
            return null;
        }
        File versionFile = new File(bundleDir, "version.meta");
        StringBuilder content = FileUtils.readFile(versionFile.getAbsolutePath(), "utf-8");
        if (content == null) {
            return null;
        }
        try {
            return new JSONObject(content.toString());
        } catch (JSONException e) {
        }

        return null;
    }
}
