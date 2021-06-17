package com.edusoho.kuozhi.clean.module.courseset.dialog.courses;

import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

import java.util.List;

/**
 * Created by DF on 2017/4/13.
 */

class SelectProjectDialogContract {

    interface View extends BaseView<Presenter> {

        void showWayAndServiceView(CourseProject courseProject);

        void showTaskView(int num);

        void showVipView(int vipLevelId);

        void showPriceView(CourseProject courseProject);

        void showValidateView(int content);

        void showValidateView(int format, String content);

        void showValidityView(int format, String textOne, String textTwo);

        void showToastOrFinish(int content, boolean isFinish);

        void showProcessDialog(boolean isShow);

        void goToConfirmOrderActivity(CourseProject courseProject);

        void goToCourseProjectActivity(int courseProjectId);

        void showVipToast(int courseProjectId, int resId);
    }

    interface Presenter extends BasePresenter {

        void confirm();

        void setData(int positon);

        void reFreshData(List<CourseProject> courseProjects);
    }

}
