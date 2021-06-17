package com.edusoho.kuozhi.clean.module.course.tasks.ppt;


import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

import java.util.List;

public class PPTLessonContract {

    public interface View extends BaseView<Presenter> {
        void showPTT(List<String> pptUrls);
    }

    interface Presenter extends BasePresenter {
    }
}
