package com.edusoho.kuozhi.clean.utils.biz;

import android.content.Context;

import com.edusoho.kuozhi.clean.api.CommonApi;
import com.edusoho.kuozhi.clean.api.SettingApi;
import com.edusoho.kuozhi.clean.bean.seting.CloudVideoSetting;
import com.edusoho.kuozhi.clean.bean.seting.CourseSetting;
import com.edusoho.kuozhi.clean.bean.seting.UserSetting;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.utils.GsonUtils;
import com.edusoho.kuozhi.clean.utils.SharedPreferencesUtils;
import com.edusoho.kuozhi.clean.utils.StringUtils;
import com.edusoho.kuozhi.v3.EdusohoApp;

import java.lang.reflect.Type;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by JesseHuang on 2017/5/10.
 */

public class SettingHelper {

    public static void sync(final Context context) {
        if (StringUtils.isEmpty(EdusohoApp.app.host)) {
            return;
        }
        getCourseSetting().subscribe(new SubscriberProcessor<CourseSetting>() {
            @Override
            public void onNext(CourseSetting courseSetting) {
                context.getApplicationContext()
                        .getSharedPreferences(SharedPreferencesHelper.CourseSetting.XML_NAME, 0)
                        .edit()
                        .putString(SharedPreferencesHelper.CourseSetting.SHOW_STUDENT_NUM_ENABLED_KEY, courseSetting.showStudentNumEnabled)
                        .putString(SharedPreferencesHelper.CourseSetting.CHAPTER_NAME_KEY, courseSetting.chapterName)
                        .putString(SharedPreferencesHelper.CourseSetting.PART_NAME_KEY, courseSetting.partName)
                        .apply();
            }
        });
        getUserSetting().subscribe(new SubscriberProcessor<UserSetting>() {
            @Override
            public void onNext(UserSetting userSetting) {
                context.getApplicationContext()
                        .getSharedPreferences(SharedPreferencesHelper.SchoolSetting.XML_NAME, 0)
                        .edit()
                        .putString(SharedPreferencesHelper.SchoolSetting.USER_SETTING_KEY, GsonUtils.parseString(userSetting))
                        .apply();
            }

            @Override
            public void onError(String message) {
                UserSetting userSetting = new UserSetting();
                userSetting.init();
                context.getSharedPreferences(SharedPreferencesHelper.SchoolSetting.XML_NAME, 0)
                        .edit()
                        .putString(SharedPreferencesHelper.SchoolSetting.USER_SETTING_KEY, GsonUtils.parseString(userSetting))
                        .apply();
            }
        });
        getCloudVideoSetting().subscribe(new SubscriberProcessor<CloudVideoSetting>() {
            @Override
            public void onNext(CloudVideoSetting cloudVideoSetting) {
                context.getApplicationContext()
                        .getSharedPreferences(SharedPreferencesHelper.SchoolSetting.XML_NAME, 0)
                        .edit()
                        .putString(SharedPreferencesHelper.SchoolSetting.Cloud_VIDEO_SETTING, GsonUtils.parseString(cloudVideoSetting))
                        .apply();
            }
        });
    }

    public static <T> T getSetting(Type type, Context context, String key) {
        String setting = SharedPreferencesUtils
                .getInstance(context)
                .open(SharedPreferencesHelper.SchoolSetting.XML_NAME)
                .getString(key);
        return GsonUtils.parseJson(setting, type);
    }

    public static Observable<CourseSetting> getCourseSetting() {
        return HttpUtils.getInstance()
                .baseOnApi()
                .createApi(CommonApi.class)
                .getCourseSet()
                .filter(new Func1<CourseSetting, Boolean>() {
                    @Override
                    public Boolean call(CourseSetting courseSetting) {
                        return courseSetting != null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<UserSetting> getUserSetting() {
        return HttpUtils.getInstance()
                .baseOnApi()
                .createApi(SettingApi.class)
                .getUserSetting()
                .filter(new Func1<UserSetting, Boolean>() {
                    @Override
                    public Boolean call(UserSetting userSetting) {
                        return userSetting != null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<CloudVideoSetting> getCloudVideoSetting() {
        return HttpUtils.getInstance()
                .baseOnApi()
                .createApi(SettingApi.class)
                .getCloudVideoSetting()
                .filter(new Func1<CloudVideoSetting, Boolean>() {
                    @Override
                    public Boolean call(CloudVideoSetting cloudVideoSetting) {
                        return cloudVideoSetting != null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
