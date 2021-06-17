package com.edusoho.kuozhi.clean.module.main.mine.favorite;

import com.edusoho.kuozhi.clean.api.UserApi;
import com.edusoho.kuozhi.clean.bean.DataPageResult;
import com.edusoho.kuozhi.clean.bean.innerbean.Study;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.module.base.BaseFragment;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.navi.NaviLifecycle;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DF on 2017/5/10.
 */

public class MyFavoritePresenter implements MyFavoriteContract.Presenter {

    private       MyFavoriteContract.View          mView;
    private final LifecycleProvider<FragmentEvent> mFragmentLifeProvider;

    MyFavoritePresenter(MyFavoriteContract.View view) {
        this.mView = view;
        mFragmentLifeProvider = NaviLifecycle.createFragmentLifecycleProvider((BaseFragment) view);
    }


    @Override
    public void subscribe() {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(UserApi.class)
                .getMyFavoriteCourseSet(0, 1000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SubscriberProcessor<DataPageResult<Study>>() {
                    @Override
                    public void onError(String e) {
                        mView.setSwpFreshing(false);
                    }

                    @Override
                    public void onNext(DataPageResult<Study> courseProjectDataPageResult) {
                        mView.setSwpFreshing(false);
                        if (courseProjectDataPageResult != null && courseProjectDataPageResult.data != null && courseProjectDataPageResult.data.size() > 0) {
                            mView.showComplete(courseProjectDataPageResult.data);
                        }
                    }
                });
    }

    @Override
    public void unsubscribe() {

    }
}
