package com.edusoho.kuozhi.clean.module.main.study.exam;

import com.edusoho.kuozhi.clean.bean.mystudy.ExamInfoModel;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

/**
 * Created by RexXiang on 2018/2/6.
 */

public interface ExamInfoContract {

    interface View extends BaseView<Presenter> {

        void refreshView(ExamInfoModel examInfoModel);

        void showProcessDialog(boolean isShow);

    }

    interface Presenter extends BasePresenter {

        void getExamInfo();
    }
}
