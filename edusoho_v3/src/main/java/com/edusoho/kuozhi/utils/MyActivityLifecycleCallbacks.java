package com.edusoho.kuozhi.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.edusoho.kuozhi.v3.ui.LoginActivity;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class MyActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    private int foregroundActivites = 0;
    private boolean isChangingConfiguration;

    private ApplicationLifecycle mLifecycle = ApplicationLifecycle.Stopped;

    public void setApplicationLifecycle(ApplicationLifecycle lifecycle) {
        mLifecycle = lifecycle;
    }

    public ApplicationLifecycle getApplicationLifecycle() {
        return mLifecycle;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (!isChangingConfiguration) {
            foregroundActivites++;
            if (foregroundActivites == 1) {
                setApplicationLifecycle(ApplicationLifecycle.Started);
                if(activity instanceof LoginActivity){
                    Toast.makeText(activity, "登录页面程序进入前台", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(activity, "非登录页面程序进入前台", Toast.LENGTH_SHORT).show();
                }
            }
        }
        isChangingConfiguration = false;
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        isChangingConfiguration = activity.isChangingConfigurations();
        if (!isChangingConfiguration) {
            foregroundActivites--;
            if (foregroundActivites == 0) {
                setApplicationLifecycle(ApplicationLifecycle.Stopped);
                if(activity instanceof LoginActivity){
                    Toast.makeText(activity, "登录页面程序进入后台", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(activity, "非登录页面程序进入后台", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
