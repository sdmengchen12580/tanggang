package com.edusoho.kuozhi.clean.module.main.study.courseset;

import com.edusoho.kuozhi.clean.bean.StudyCourse;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

import java.util.List;

/**
 * Created by RexXiang on 2018/1/18.
 */

public interface MyCourseSetContract {

    interface View extends BaseView<Presenter> {

        void refreshView(List<StudyCourse> studyCourses, int status);

        void clearData();

        void refreshCompleted();
    }


    interface Presenter extends BasePresenter {
        void getCourseSet(int status);
    }

}
