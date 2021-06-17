package com.edusoho.kuozhi.clean.module.main.study.offlineactivity;

import com.edusoho.kuozhi.clean.bean.mystudy.Relationship;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

import java.util.List;

/**
 * Created by RexXiang on 2018/1/29.
 */

public interface OfflineActivityMembersContract {


    interface View extends BaseView<Presenter> {

        void showProcessDialog(boolean isShow);

        void refreshView(List<Relationship> list);

        void clearData();

    }

    interface Presenter extends BasePresenter {

        void getRelationships(String ids);

        void followUser(String userId);

        void cancelFollowUser(String userId);

    }
}
