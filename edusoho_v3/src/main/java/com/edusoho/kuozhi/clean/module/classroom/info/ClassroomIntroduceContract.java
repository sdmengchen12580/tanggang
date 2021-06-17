package com.edusoho.kuozhi.clean.module.classroom.info;

import com.edusoho.kuozhi.clean.bean.Classroom;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

/**
 * Created by DF on 2017/5/23.
 */

public interface ClassroomIntroduceContract {

    interface View extends BaseView<Presenter> {

        void setLoadViewStatus(int isVis);

        void showComplete(Classroom classroom);

        void showVipAdvertising(String vipName);

    }

    interface Presenter extends BasePresenter {

        void getClassroomInfo();

        void showVip(int vipLevelId);

    }

}
