package com.edusoho.kuozhi.clean.module.main.study.offlineactivity;

import com.edusoho.kuozhi.clean.bean.mystudy.OfflineActivitiesResult.DataBean;
import com.edusoho.kuozhi.clean.bean.mystudy.OfflineActivityCategory;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

import java.util.List;

/**
 * Created by RexXiang on 2018/1/25.
 */

public interface OfflineListContract {

    interface View extends BaseView<Presenter> {

        void refreshView(List<DataBean> list, List<OfflineActivityCategory> categoryList);

        void clearData();

        void refreshCompleted();

        void showProcessDialog(boolean isShow);

    }


    interface Presenter extends BasePresenter {
        void getList(String status);

        void getCategoryList();

        void joinActivity(String activityId);
    }
}
