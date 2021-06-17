package com.edusoho.kuozhi.clean.module.classroom.catalog;

import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

import java.util.List;

/**
 * Created by DF on 2017/5/22.
 */

public interface ClassCatalogContract {

    interface View extends BaseView<Presenter> {

        void setLoadStatus(int visibility);

        void setClassStatus(String code);

        void showComplete(List<CourseProject> courses);

        void setLessonEmptyViewVisibility(int visibility);

    }

    interface Presenter extends BasePresenter {

    }
}
