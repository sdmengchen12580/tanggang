package com.edusoho.kuozhi.clean.module.classroom.info;

import android.view.View;

import com.edusoho.kuozhi.clean.api.ClassroomApi;
import com.edusoho.kuozhi.clean.api.PluginsApi;
import com.edusoho.kuozhi.clean.bean.Classroom;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.module.base.BaseFragment;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.bal.VipLevel;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.navi.NaviLifecycle;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DF on 2017/5/22.
 */

public class ClassroomIntroducePresenter implements ClassroomIntroduceContract.Presenter {

    private int                              mClassroomId;
    private ClassroomIntroduceContract.View  mView;
    private LifecycleProvider<FragmentEvent> mFragmentLifeProvider;

    public ClassroomIntroducePresenter(int mClassroomId, ClassroomIntroduceContract.View view) {
        this.mClassroomId = mClassroomId;
        mFragmentLifeProvider = NaviLifecycle.createFragmentLifecycleProvider((BaseFragment) view);
        this.mView = view;
    }

    @Override
    public void subscribe() {

    }

    public void getClassroomInfo() {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(ClassroomApi.class)
                .getClassroom(mClassroomId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mFragmentLifeProvider.<Classroom>bindToLifecycle())
                .subscribe(new SubscriberProcessor<Classroom>() {
                    @Override
                    public void onError(String e) {
                        mView.setLoadViewStatus(View.GONE);
                    }

                    @Override
                    public void onNext(Classroom classroom) {
                        mView.setLoadViewStatus(View.GONE);
                        if (classroom != null) {
                            mView.showComplete(classroom);
                            showVip(classroom.vipLevelId);
                        }
                    }
                });
    }

    public void showVip(int vipLevelId) {
        if (0 != vipLevelId) {
            HttpUtils.getInstance()
                    .createApi(PluginsApi.class)
                    .getVipLevel(vipLevelId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(mFragmentLifeProvider.<VipLevel>bindToLifecycle())
                    .subscribe(new SubscriberProcessor<VipLevel>() {
                        @Override
                        public void onNext(VipLevel vipLevel) {
                            mView.showVipAdvertising(vipLevel.name);
                        }
                    });
        }
    }

    @Override
    public void unsubscribe() {

    }
}
