package com.edusoho.kuozhi.clean.module.course.task.menu.info;

import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.VipInfo;
import com.edusoho.kuozhi.clean.bean.innerbean.Teacher;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

import java.util.List;

/**
 * Created by JesseHuang on 2017/4/24.
 */

public class CourseMenuInfoContract {

    interface View extends BaseView<Presenter> {
        void showServices(CourseProject.Service[] services);

        void showTeacher(Teacher teacher);

        void showRelativeCourseProjects(List<CourseProject> courseList, List<VipInfo> vipInfos);
    }

    interface Presenter extends BasePresenter {
    }
}
