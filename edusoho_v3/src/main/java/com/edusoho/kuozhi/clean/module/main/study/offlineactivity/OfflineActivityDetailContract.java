package com.edusoho.kuozhi.clean.module.main.study.offlineactivity;

import com.edusoho.kuozhi.clean.bean.mystudy.ActivityMembers;
import com.edusoho.kuozhi.clean.bean.mystudy.OfflineActivitiesResult;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

/**
 * Created by RexXiang on 2018/1/29.
 */

public interface OfflineActivityDetailContract {

    interface View extends BaseView<Presenter> {

        void showProcessDialog(boolean isShow);

        void refreshView(OfflineActivitiesResult.DataBean dataBean, ActivityMembers members);

    }

    interface Presenter extends BasePresenter {

        void getOfflineActivityDetail(String activityId);

        void getOfflineActivityMembers(String activityId);

        void joinActivity(String id);
    }
}
