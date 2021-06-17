package com.edusoho.kuozhi.clean.module.main.mine.project.item;


import com.edusoho.kuozhi.clean.api.UserApi;
import com.edusoho.kuozhi.clean.bean.DataPageResult;
import com.edusoho.kuozhi.clean.bean.mystudy.ExamRecord;
import com.edusoho.kuozhi.clean.bean.mystudy.OfflineActivityRecord;
import com.edusoho.kuozhi.clean.bean.mystudy.PostCourseRecord;
import com.edusoho.kuozhi.clean.bean.mystudy.ProjectPlanRecord;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.module.base.BaseActivity;
import com.edusoho.kuozhi.clean.utils.Constants;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.util.Const;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.navi.NaviLifecycle;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ProjectPlanRecordItemPresenter implements ProjectPlanRecordItemContract.Presenter {

    private ProjectPlanRecordItemContract.View mView;
    private LifecycleProvider<ActivityEvent>   mLifecycleProvider;

    public ProjectPlanRecordItemPresenter(ProjectPlanRecordItemContract.View view) {
        this.mView = view;
        mLifecycleProvider = NaviLifecycle.createActivityLifecycleProvider((BaseActivity) mView);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {

    }

    @Override
    public void init(String type, int offset) {
        switch (type) {
            case Constants.ProjectPlanItemType.TYPE_PROJECT_PLAN:
                HttpUtils
                        .getInstance()
                        .addTokenHeader(EdusohoApp.app.token)
                        .createApi(UserApi.class)
                        .getMyProjectPlanRecords(offset, Const.LIMIT)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(mLifecycleProvider.<DataPageResult<ProjectPlanRecord>>bindToLifecycle())
                        .subscribe(new SubscriberProcessor<DataPageResult<ProjectPlanRecord>>() {
                            @Override
                            public void onNext(DataPageResult<ProjectPlanRecord> projectPlanRecordDataPageResult) {
                                if (projectPlanRecordDataPageResult.paging.total == 0) {
                                    mView.showEmpty(true);
                                } else {
                                    mView.showEmpty(false);
                                    mView.showProjectPlanRecords(projectPlanRecordDataPageResult);
                                }
                            }
                        });
                break;
            case Constants.ProjectPlanItemType.TYPE_POST_COURSE:
                HttpUtils
                        .getInstance()
                        .addTokenHeader(EdusohoApp.app.token)
                        .createApi(UserApi.class)
                        .getMyPostCourseRecords(offset, Const.LIMIT)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(mLifecycleProvider.<DataPageResult<PostCourseRecord>>bindToLifecycle())
                        .subscribe(new SubscriberProcessor<DataPageResult<PostCourseRecord>>() {
                            @Override
                            public void onNext(DataPageResult<PostCourseRecord> postCourseRecordDataPageResult) {
                                if (postCourseRecordDataPageResult.paging.total == 0) {
                                    mView.showEmpty(true);
                                } else {
                                    mView.showEmpty(false);
                                    mView.showPostCourseRecords(postCourseRecordDataPageResult);
                                }
                            }
                        });
                break;
            case Constants.ProjectPlanItemType.TYPE_EXAM:
                HttpUtils
                        .getInstance()
                        .addTokenHeader(EdusohoApp.app.token)
                        .createApi(UserApi.class)
                        .getMyExamRecords(offset, Const.LIMIT)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(mLifecycleProvider.<DataPageResult<ExamRecord>>bindToLifecycle())
                        .subscribe(new SubscriberProcessor<DataPageResult<ExamRecord>>() {
                            @Override
                            public void onNext(DataPageResult<ExamRecord> examRecordDataPageResult) {
                                if (examRecordDataPageResult.paging.total == 0) {
                                    mView.showEmpty(true);
                                } else {
                                    mView.showEmpty(false);
                                    mView.showExamRecords(examRecordDataPageResult);
                                }
                            }
                        });
                break;
            case Constants.ProjectPlanItemType.TYPE_OFFLINEACTIVITY:
                HttpUtils
                        .getInstance()
                        .addTokenHeader(EdusohoApp.app.token)
                        .createApi(UserApi.class)
                        .getMyOfflineActivityRecords(offset, Const.LIMIT)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(mLifecycleProvider.<DataPageResult<OfflineActivityRecord>>bindToLifecycle())
                        .subscribe(new SubscriberProcessor<DataPageResult<OfflineActivityRecord>>() {
                            @Override
                            public void onNext(DataPageResult<OfflineActivityRecord> offlineActivityRecordDataPageResult) {
                                if (offlineActivityRecordDataPageResult.paging.total == 0) {
                                    mView.showEmpty(true);
                                } else {
                                    mView.showEmpty(false);
                                    mView.showOfflineActivityRecord(offlineActivityRecordDataPageResult);
                                }
                            }
                        });
                break;
        }
    }
}
