package com.edusoho.kuozhi.clean.react;


import android.graphics.Bitmap;
import android.view.View;

import com.edusoho.kuozhi.clean.utils.biz.ShareHelper;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.imageaware.NonViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;

public class InjectModule extends ReactContextBaseJavaModule {

    public InjectModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "KuozhiAndroid";
    }

    @ReactMethod
    public void share(final ReadableMap params) {
        if (params != null) {
            final String imageUrl = params.getString("imageUrl");
            if (new File(imageUrl).exists()) {
                ShareHelper
                        .builder()
                        .init(getCurrentActivity())
                        .setTitle(params.getString("title"))
                        .setText(params.getString("content"))
                        .setUrl(params.getString("url"))
                        .setImageUrl(imageUrl)
                        .build()
                        .share();
            } else {
                NonViewAware nonViewAware = new NonViewAware(new ImageSize(0, 0), ViewScaleType.FIT_INSIDE);
                ImageLoader.getInstance().displayImage(imageUrl, nonViewAware, EdusohoApp.app.mOptions, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        ShareHelper
                                .builder()
                                .init(getCurrentActivity())
                                .setTitle(params.getString("title"))
                                .setText(params.getString("content"))
                                .setUrl(params.getString("url"))
                                .setImageUrl(imageUri)
                                .build()
                                .share();
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });
            }
        }
    }

    @ReactMethod
    public void getSchooInfo(Promise promise) {
        if (EdusohoApp.app.loginUser != null) {
            WritableMap params = Arguments.createMap();
            params.putString("host", EdusohoApp.app.host);
            params.putString("token", EdusohoApp.app.token);
            promise.resolve(params);
        }
    }

    @ReactMethod
    public void exit() {
        if (getCurrentActivity() != null) {
            getCurrentActivity().finish();
        }
    }
}
