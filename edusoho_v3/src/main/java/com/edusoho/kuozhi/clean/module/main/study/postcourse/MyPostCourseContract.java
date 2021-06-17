package com.edusoho.kuozhi.clean.module.main.study.postcourse;

import com.edusoho.kuozhi.clean.bean.mystudy.PostCourseModel;
import com.edusoho.kuozhi.clean.bean.mystudy.PostCoursesProgress;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

import java.util.List;
/**
 * Created by RexXiang on 2018/1/18.
 */

public interface MyPostCourseContract {

    interface View extends BaseView<Presenter> {

        void refreshView(List<PostCourseModel> list, String postName, PostCoursesProgress progress);

        void clearData();

        void refreshCompleted();
    }

    interface Presenter extends BasePresenter {
        void getMyPostCourse();
    }
}
