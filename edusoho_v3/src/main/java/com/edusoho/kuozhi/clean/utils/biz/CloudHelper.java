package com.edusoho.kuozhi.clean.utils.biz;

import android.content.Context;

import com.edusoho.kuozhi.clean.api.CloudApi;
import com.edusoho.kuozhi.clean.api.CommonApi;
import com.edusoho.kuozhi.clean.bean.cloud.CloudUserAnalysis;
import com.edusoho.kuozhi.clean.bean.cloud.CloudUserPlayAnalysis;
import com.edusoho.kuozhi.clean.bean.cloud.CloudUserViewAnalysis;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.utils.DeviceUtils;
import com.edusoho.kuozhi.clean.utils.GsonUtils;
import com.edusoho.kuozhi.clean.utils.StringUtils;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.tencent.stat.StatService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by JesseHuang on 2017/6/22.
 */

public class CloudHelper {

    private static final String VIEW = "view";
    private static final String PLAY = "play";

    private static CloudHelper mInstance;

    private String                  mResId;
    private List<CloudUserAnalysis> mList;
    private Context                 mContext;
    private int                     mStartTime;
    private String                  mViewUUID;
    private int                     mInitPlayTime;
    private boolean                 mIsInit;

    public static CloudHelper getInstance() {
        if (mInstance == null) {
            synchronized (CloudHelper.class) {
                if (mInstance == null) {
                    mInstance = new CloudHelper();
                }
            }
        }
        return mInstance;
    }

    public boolean isRecordView() {
        return mIsInit;
    }

    public void init(Context context, String m3u8Url, long startTime) {
        mContext = context;
        if (mList == null) {
            mList = new ArrayList<>();
        } else {
            mList.clear();
        }
        Observable
                .zip(onProcessCloudVideo(m3u8Url), Observable.just(startTime), new Func2<ResponseBody, Long, Void>() {
                    @Override
                    public Void call(ResponseBody responseBody, Long time) {
                        try {
                            Pattern pattern = Pattern.compile("fileGlobalId=\\w{1,}");
                            Matcher matcher = pattern.matcher(responseBody.string());
                            if (matcher.find()) {
                                String fileGroupId = matcher.group(0);
                                mResId = fileGroupId.split("=")[1];
                            }
                            mInitPlayTime = (int) ((System.currentTimeMillis() - time) / 1000);
                            startUp();
                            createViewRecord();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SubscriberProcessor<>());
    }

    private void startUp() {
        try {
            mViewUUID = UUID.randomUUID().toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void record(int startTime) {
        try {
            mStartTime = startTime;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void stop(int endTime) {
        if (endTime != 0 && endTime > mStartTime) {
            createPlayRecord(mStartTime, endTime);
        }
    }

    public void uploadWatchRecords() {
        try {
            if (mList != null && mList.size() != 0 || !StringUtils.isEmpty(mResId)) {
                HttpUtils.getInstance()
                        .createApi(CloudApi.class)
                        .uploadUserPlayAnalysis(GsonUtils.parseJsonArray(mList))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SubscriberProcessor<String>() {
                        });
            }
            mList = null;
            mResId = "";
            mViewUUID = "";
            mIsInit = false;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void createViewRecord() {
        if (mList == null || StringUtils.isEmpty(mResId)) {
            return;
        }
        if (mIsInit) {
            return;
        }
        CloudUserViewAnalysis viewAnalysis = CloudUserViewAnalysis.builder()
                .setAction(VIEW)
                .setResId(mResId)
                .setClientUUID(DeviceUtils.getUUID(mContext))
                .setViewUUID(mViewUUID)
                .setLoadingTime(mInitPlayTime)
                .setLevel("sd")
                .setResType("video")
                .setClientType("app")
                .setUserId(EdusohoApp.app.loginUser != null ? EdusohoApp.app.loginUser.id : 0)
                .setUserName(EdusohoApp.app.loginUser != null ? EdusohoApp.app.loginUser.nickname : "")
                .build();
        mIsInit = true;
        mList.add(viewAnalysis);
    }

    private void createPlayRecord(int startTime, int endTime) {
        if (mList == null || StringUtils.isEmpty(mResId)) {
            return;
        }

        CloudUserPlayAnalysis viewAnalysis = CloudUserPlayAnalysis.builder()
                .setAction(PLAY)
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setResId(mResId)
                .setClientUUID(DeviceUtils.getUUID(mContext))
                .setViewUUID(mViewUUID)
                .setLevel("sd")
                .setResType("video")
                .setClientType("app")
                .setUserId(EdusohoApp.app.loginUser != null ? EdusohoApp.app.loginUser.id : 0)
                .setUserName(EdusohoApp.app.loginUser != null ? EdusohoApp.app.loginUser.nickname : "")
                .build();
        mList.add(viewAnalysis);
    }

    private Observable<ResponseBody> onProcessCloudVideo(String m3u8Url) {
        return HttpUtils.getInstance()
                .createApi(CommonApi.class)
                .requestUrl(m3u8Url)
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<ResponseBody, Observable<ResponseBody>>() {
                    @Override
                    public Observable<ResponseBody> call(ResponseBody responseBody) {
                        String url = "";
                        try {
                            Pattern pattern = Pattern.compile("http\\S+");
                            Matcher matcher = pattern.matcher(responseBody.string());
                            if (matcher.find()) {
                                url = matcher.group(0);
                            }
                        } catch (Exception ex) {
                            StatService.reportException(mContext, ex);
                        }
                        return HttpUtils.getInstance().createApi(CommonApi.class).requestUrl(url);
                    }
                });
    }
}
