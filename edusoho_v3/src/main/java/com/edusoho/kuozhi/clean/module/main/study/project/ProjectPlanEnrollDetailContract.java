package com.edusoho.kuozhi.clean.module.main.study.project;

import com.edusoho.kuozhi.clean.bean.mystudy.ProjectPlan;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

/**
 * Created by RexXiang on 2018/3/22.
 */

public class ProjectPlanEnrollDetailContract {

    interface View extends BaseView<Presenter> {

        void showProcessDialog(boolean isShow);

        void refreshView(ProjectPlan projectPlan);

        void expandChildView(Object itemDetail, String type, int index);

    }

    interface Presenter extends BasePresenter {

        void getProject();

        void joinProject();

        void getItemDetail(String targetId, String targetType, int index);
    }
}
