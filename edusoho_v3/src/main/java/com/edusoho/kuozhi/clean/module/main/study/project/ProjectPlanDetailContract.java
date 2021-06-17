package com.edusoho.kuozhi.clean.module.main.study.project;

import com.edusoho.kuozhi.clean.bean.mystudy.ProjectPlan;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

/**
 * Created by RexXiang on 2018/3/20.
 */

public interface ProjectPlanDetailContract {

    interface View extends BaseView<Presenter> {

        void showProcessDialog(boolean isShow);

        void refreshView(ProjectPlan projectPlan);

        void clearData();
    }

    interface Presenter extends BasePresenter {

        void getProjectPlanDetail();
    }
}
