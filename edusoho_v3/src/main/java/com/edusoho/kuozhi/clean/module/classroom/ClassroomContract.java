package com.edusoho.kuozhi.clean.module.classroom;

import com.edusoho.kuozhi.clean.bean.Classroom;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

/**
 * Created by DF on 2017/5/23.
 */

public interface ClassroomContract {

    interface View extends BaseView<Presenter> {

        void showComplete(Classroom classroom);

        void refreshFragment(boolean isJoin);

        void newFinish();

        void goToConfirmOrderActivity(int classroomId);

        void showToast(int resId, String param);
    }

    interface Presenter extends BasePresenter {

        void checkClassInfo(Classroom classroom);

        void showVipInfo(int vipLevelId);

    }

}
