package com.edusoho.kuozhi.clean.module.classroom;

import android.text.TextUtils;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.api.ClassroomApi;
import com.edusoho.kuozhi.clean.api.PluginsApi;
import com.edusoho.kuozhi.clean.bean.Classroom;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.module.base.BaseActivity;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.bal.VipLevel;
import com.edusoho.kuozhi.v3.model.bal.course.ClassroomMember;
import com.google.gson.JsonObject;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.navi.NaviLifecycle;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DF on 2017/5/23.
 */

public class ClassroomPresenter implements ClassroomContract.Presenter {

    private static final String CLASSROOM_CANJOIN      = "success";
    private static final String CLASSROOM_NOT_FOUND    = "classroom.not_found";
    private static final String CLASSROOM_UNPUBLISHED  = "classroom.unpublished";
    private static final String CLASSROOM_CLOSED       = "classroom.closed";
    private static final String CLASSROOM_NOT_BUYABLE  = "classroom.not_buyable";
    private static final String CLASSROOM_EXPIRED      = "classroom.expired";
    private static final String CLASSROOM_NOT_LOGIN    = "user.not_login";
    private static final String CLASSROOM_USER_LOCKED  = "user.locked";
    private static final String CLASSROOM_MEMBER_EXIST = "member.member_exist";


    private int                              mClassroomId;
    private ClassroomContract.View           mView;
    private LifecycleProvider<ActivityEvent> mActivityLifeProvider;

    public ClassroomPresenter(int mClassroomId, ClassroomContract.View view) {
        this.mClassroomId = mClassroomId;
        mActivityLifeProvider = NaviLifecycle.createActivityLifecycleProvider((BaseActivity) view);
        this.mView = view;
    }

    @Override
    public void subscribe() {
        if (mClassroomId != 0) {
            if (!TextUtils.isEmpty(EdusohoApp.app.token)) {
                getClassStatus();
            } else {
                mView.refreshFragment(false);
                getClassInfo();
            }
        }
    }

    private void getClassStatus() {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(ClassroomApi.class)
                .getClassroomStatus(mClassroomId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivityLifeProvider.<JsonObject>bindToLifecycle())
                .subscribe(new SubscriberProcessor<JsonObject>() {
                    @Override
                    public void onError(String e) {
                        mView.newFinish();
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        if (jsonObject != null && jsonObject.getAsJsonObject("access") != null) {
                            mView.refreshFragment(true);
                        } else {
                            mView.refreshFragment(false);
                        }
                        getClassInfo();
                    }
                });
    }

    private void getClassInfo() {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(ClassroomApi.class)
                .getClassroom(mClassroomId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivityLifeProvider.<Classroom>bindToLifecycle())
                .subscribe(new SubscriberProcessor<Classroom>() {
                    @Override
                    public void onError(String e) {
                        mView.newFinish();
                    }

                    @Override
                    public void onNext(Classroom classroom) {
                        if (classroom != null) {
                            mView.showComplete(classroom);
                        } else {
                            mView.showToast("班级不存在");
                            mView.newFinish();
                        }
                    }
                });
    }

    @Override
    public void showVipInfo(int vipLevelId) {
        HttpUtils.getInstance()
                .createApi(PluginsApi.class)
                .getVipLevel(vipLevelId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivityLifeProvider.<VipLevel>bindToLifecycle())
                .subscribe(new SubscriberProcessor<VipLevel>() {
                    @Override
                    public void onNext(VipLevel vipLevel) {
                        mView.showToast(R.string.only_vip_learn, vipLevel.name);
                    }
                });
    }

    @Override
    public void unsubscribe() {

    }

    public void joinClassroom() {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(ClassroomApi.class)
                .joinClassroom(mClassroomId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivityLifeProvider.<ClassroomMember>bindToLifecycle())
                .subscribe(new SubscriberProcessor<ClassroomMember>() {
                    @Override
                    public void onError(String message) {
                        mView.refreshFragment(false);
                    }

                    @Override
                    public void onNext(ClassroomMember classroomMember) {
                        if (classroomMember != null && classroomMember.id != null) {
                            mView.showToast("加入成功");
                            mView.refreshFragment(true);
                        } else {
                            mView.goToConfirmOrderActivity(mClassroomId);
                        }
                    }
                });
    }

    @Override
    public void checkClassInfo(Classroom classroom) {
        switch (classroom.access.code) {
            case CLASSROOM_NOT_LOGIN:
            case CLASSROOM_USER_LOCKED: {
                mView.showToast(R.string.classroom_locked_or_not_login);
                break;
            }
            case CLASSROOM_NOT_BUYABLE: {
                mView.showToast(R.string.classroom_not_buy_able);
                break;
            }
            case CLASSROOM_NOT_FOUND: {
                mView.showToast(R.string.classroom_not_found);
                break;
            }
            case CLASSROOM_UNPUBLISHED: {
                mView.showToast(R.string.classroom_unpublished);
                break;
            }
            case CLASSROOM_CLOSED: {
                mView.showToast(R.string.classroom_closed);
                break;
            }
            case CLASSROOM_EXPIRED: {
                mView.showToast(R.string.classroom_expired);
                break;
            }
            case CLASSROOM_MEMBER_EXIST: {
                mView.showToast(R.string.classroom_member_exist);
                break;
            }
            case CLASSROOM_CANJOIN:
            default: {
                joinClassroom();
                break;
            }
        }
    }
}
