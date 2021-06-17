package com.edusoho.kuozhi.clean.module.main.study.offlineactivity;

import com.edusoho.kuozhi.clean.api.MyStudyApi;
import com.edusoho.kuozhi.clean.bean.mystudy.Relationship;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.module.base.BaseActivity;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.google.gson.JsonObject;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.navi.NaviLifecycle;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by RexXiang on 2018/1/29.
 */

public class OfflineActivityMembersPresenter implements OfflineActivityMembersContract.Presenter {

    private LifecycleProvider<ActivityEvent> mActivityLifeProvider;
    private OfflineActivityMembersContract.View mView;
    private String mIds;

    public OfflineActivityMembersPresenter(OfflineActivityMembersContract.View view, String ids) {
        mView = view;
        mIds = ids;
        mActivityLifeProvider = NaviLifecycle.createActivityLifecycleProvider((BaseActivity) view);
    }


    @Override
    public void getRelationships(String ids) {
        mView.showProcessDialog(true);
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(MyStudyApi.class)
                .getRelationships(ids)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivityLifeProvider.<List<Relationship>>bindToLifecycle())
                .subscribe(new SubscriberProcessor<List<Relationship>>() {
                    @Override
                    public void onError(String message) {
                        mView.showProcessDialog(false);
                        mView.showToast(message);
                    }

                    @Override
                    public void onNext(List<Relationship> relationships) {
                        mView.showProcessDialog(false);
                        mView.refreshView(relationships);
                    }
                });
    }

    @Override
    public void followUser(String userId) {
        mView.showProcessDialog(true);
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(MyStudyApi.class)
                .followUser(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivityLifeProvider.<JsonObject>bindToLifecycle())
                .subscribe(new SubscriberProcessor<JsonObject>() {
                    @Override
                    public void onError(String message) {
                        super.onError(message);
                        mView.showProcessDialog(false);
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        mView.showProcessDialog(false);
                        if (jsonObject != null && jsonObject.get("followed").getAsBoolean()) {
                            mView.showToast("关注成功");
                            subscribe();
                        } else {
                            mView.showToast("关注失败");
                        }
                    }
                });
    }

    @Override
    public void cancelFollowUser(String userId) {
        mView.showProcessDialog(true);
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(MyStudyApi.class)
                .cancelFollow(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivityLifeProvider.<JsonObject>bindToLifecycle())
                .subscribe(new SubscriberProcessor<JsonObject>() {
                    @Override
                    public void onError(String message) {
                        super.onError(message);
                        mView.showProcessDialog(false);
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        mView.showProcessDialog(false);
                        if (jsonObject != null && jsonObject.get("cancelFollow").getAsBoolean()) {
                            mView.showToast("取消关注成功");
                            subscribe();
                        } else {
                            mView.showToast("取消关注失败");
                        }
                    }
                });
    }

    @Override
    public void subscribe() {
        mView.clearData();
        getRelationships(mIds);
    }

    @Override
    public void unsubscribe() {

    }
}
