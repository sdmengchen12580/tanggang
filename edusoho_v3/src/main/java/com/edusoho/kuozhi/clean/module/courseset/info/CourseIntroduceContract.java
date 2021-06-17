package com.edusoho.kuozhi.clean.module.courseset.info;

import com.edusoho.kuozhi.clean.bean.CourseMember;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

import java.util.List;

/**
 * Created by DF on 2017/4/1.
 */

interface CourseIntroduceContract {

    interface View extends BaseView<Presenter> {

        void setData(CourseSet courseSet);

        void setLoadViewVisible(boolean isVis);

        void showHead();

        void showInfoAndPeople();

        void showStudent(List<CourseMember> courseMembers);

    }

    interface Presenter extends BasePresenter {

    }

}
