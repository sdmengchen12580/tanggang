package com.edusoho.kuozhi.clean.module.main.study.project;


import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.api.CommonApi;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.module.base.BaseActivity;
import com.edusoho.kuozhi.clean.utils.GsonUtils;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.navi.NaviLifecycle;

import java.io.IOException;

import okhttp3.ResponseBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ProjectSignInPresenter implements ProjectSignInContract.Presenter {

    private ProjectSignInContract.View       mView;
    private LifecycleProvider<ActivityEvent> mActivityLifeProvider;

    public ProjectSignInPresenter(ProjectSignInContract.View view) {
        this.mView = view;
        mActivityLifeProvider = NaviLifecycle.createActivityLifecycleProvider((BaseActivity) view);
    }

    @Override
    public void requestUrl(String url) {
        HttpUtils.getInstance()
                .createApi(CommonApi.class)
                .requestUrl(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mActivityLifeProvider.<ResponseBody>bindToLifecycle())
                .subscribe(new SubscriberProcessor<ResponseBody>() {
                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            String response = responseBody.string();
                            LinkedTreeMap<String, String> result = GsonUtils.parseJson(response, new TypeToken<LinkedTreeMap<String, String>>() {
                            });
                            if (result != null && "true".equals(result.get("success"))) {
                                mView.showSignInSuccess(result.get("message"));
                            } else if (result != null && "false".equals(result.get("success"))) {
                                mView.showSignInFailed(result.get("message"));
                            } else {
                                mView.showSignInResponseError();
                            }
                        } catch (IOException e) {
                            mView.showToast(R.string.sign_in_error_message);
                        }

                    }
                });
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {

    }
}
