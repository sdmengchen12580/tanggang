package com.edusoho.kuozhi.clean.module.classroom.review;

import android.view.View;

import com.edusoho.kuozhi.clean.api.ClassroomApi;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.module.base.BaseFragment;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.entity.course.DiscussDetail;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.navi.NaviLifecycle;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DF on 2017/5/23.
 */

public class ClassDiscussPresenter implements ClassDiscussContract.Presenter {

    private ClassDiscussContract.View        mView;
    private int                              mClassroomId;
    private LifecycleProvider<FragmentEvent> mFragmentLifeProvider;

    public ClassDiscussPresenter(int mClassroomId, ClassDiscussContract.View view) {
        this.mClassroomId = mClassroomId;
        mFragmentLifeProvider = NaviLifecycle.createFragmentLifecycleProvider((BaseFragment) view);
        this.mView = view;
    }

    @Override
    public void subscribe() {
        HttpUtils.getInstance()
                .baseOnApi()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(ClassroomApi.class)
                .getClassroomDiscuss(mClassroomId, 0, 20, "posted")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mFragmentLifeProvider.<DiscussDetail>bindToLifecycle())
                .subscribe(new SubscriberProcessor<DiscussDetail>() {
                    @Override
                    public void onError(String e) {
                        mView.setLoadViewVisibleible(View.GONE);
                        mView.setEmptyViewVis(View.VISIBLE);
                        mView.setRecyclerViewStatus(ClassDiscussAdapter.NO_LOAD_MORE);
                    }

                    @Override
                    public void onNext(DiscussDetail discussDetail) {
                        if (discussDetail.getResources() != null && discussDetail.getResources().size() != 0) {
                            mView.initDiscuss(discussDetail);
                        } else {
                            mView.initDiscuss(null);
                        }
                    }
                });
    }

    @Override
    public void loadMore(int start) {
        HttpUtils.getInstance()
                .baseOnApi()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(ClassroomApi.class)
                .getClassroomDiscuss(mClassroomId, start, 20, "posted")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mFragmentLifeProvider.<DiscussDetail>bindToLifecycle())
                .subscribe(new SubscriberProcessor<DiscussDetail>() {
                    @Override
                    public void onError(String e) {
                        mView.setRecyclerViewStatus(ClassDiscussAdapter.NO_LOAD_MORE);
                    }

                    @Override
                    public void onNext(DiscussDetail discussDetail) {
                        if (discussDetail.getResources() != null) {
                            mView.showCompanies(discussDetail);
                        }
                    }
                });
    }

    @Override
    public void refresh() {
        HttpUtils.getInstance()
                .baseOnApi()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(ClassroomApi.class)
                .getClassroomDiscuss(mClassroomId, 0, 20, "posted")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mFragmentLifeProvider.<DiscussDetail>bindToLifecycle())
                .subscribe(new SubscriberProcessor<DiscussDetail>() {
                    @Override
                    public void onError(String e) {
                        mView.setSwipeStatus(false);
                    }

                    @Override
                    public void onNext(DiscussDetail discussDetail) {
                        mView.setSwipeStatus(false);
                        if (discussDetail.getResources() != null && discussDetail.getResources().size() != 0) {
                            mView.adapterRefresh(discussDetail.getResources());
                        }
                    }
                });
    }

    @Override
    public void unsubscribe() {

    }

}
