package com.edusoho.kuozhi.clean.module.courseset.review;


import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.api.CourseSetApi;
import com.edusoho.kuozhi.clean.bean.CourseReview;
import com.edusoho.kuozhi.clean.bean.DataPageResult;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.module.base.BaseFragment;
import com.edusoho.kuozhi.clean.module.course.task.menu.question.QuestionAdapter;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.navi.NaviLifecycle;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DF on 2017/3/31.
 */

class CourseEvaluatePresenter implements CourseEvaluateContract.Presenter {

    private CourseEvaluateContract.View mView;
    private int                         mCourseSetId;
    private int     mStart   = 0;
    private boolean mIsHave  = true;
    private boolean mIsFirst = true;
    private final LifecycleProvider<FragmentEvent> mFragmentLifeProvider;

    CourseEvaluatePresenter(CourseEvaluateContract.View view, int mCourseSetId) {
        mFragmentLifeProvider = NaviLifecycle.createFragmentLifecycleProvider((BaseFragment) view);
        this.mView = view;
        this.mCourseSetId = mCourseSetId;
    }

    @Override
    public void subscribe() {
        HttpUtils.getInstance()
                .createApi(CourseSetApi.class)
                .getCourseReviews(mCourseSetId, 10, mStart)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mFragmentLifeProvider.<DataPageResult<CourseReview>>bindToLifecycle())
                .subscribe(new SubscriberProcessor<DataPageResult<CourseReview>>() {
                    @Override
                    public void onError(String e) {
                        mView.setLoadViewVisible(false);
                    }

                    @Override
                    public void onNext(DataPageResult<CourseReview> dataPageResult) {
                        mView.setLoadViewVisible(false);
                        if (dataPageResult != null) {
                            firstLoad(dataPageResult.data);
                        }
                    }
                });
    }

    private void firstLoad(List<CourseReview> courseReviews) {
        if (courseReviews != null) {
            int length = courseReviews.size();
            if (length < 10) {
                mIsHave = false;
                mView.setRecyclerViewStatus(CourseEvaluateAdapter.NO_LOAD_MORE);
            }
            mStart += 10;
            if (length == 0) {
                mView.setEmptyViewVis(true);
            } else {
                mView.showCompanies(courseReviews);
            }
        }
    }

    @Override
    public void loadMore() {
        if (!mIsHave) {
            if (mIsFirst) {
                mIsFirst = false;
                mView.showToast(R.string.discuss_load_data_finish);
            }
            mView.changeMoreStatus(QuestionAdapter.NO_LOAD_MORE);
            return;
        }
        HttpUtils.getInstance()
                .createApi(CourseSetApi.class)
                .getCourseReviews(mCourseSetId, 10, mStart)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mFragmentLifeProvider.<DataPageResult<CourseReview>>bindToLifecycle())
                .subscribe(new SubscriberProcessor<DataPageResult<CourseReview>>() {
                    @Override
                    public void onError(String e) {
                        mView.changeMoreStatus(CourseEvaluateAdapter.NO_LOAD_MORE);
                    }

                    @Override
                    public void onNext(DataPageResult<CourseReview> dataPageResult) {
                        if (dataPageResult != null) {
                            int length = dataPageResult.data.size();
                            mStart += 10;
                            mIsHave = length > 10;
                            mView.addData(dataPageResult.data);
                        }
                    }
                });
    }

    @Override
    public void unsubscribe() {

    }
}
