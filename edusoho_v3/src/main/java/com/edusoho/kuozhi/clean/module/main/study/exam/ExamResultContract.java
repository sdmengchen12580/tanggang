package com.edusoho.kuozhi.clean.module.main.study.exam;

import com.edusoho.kuozhi.clean.bean.mystudy.ExamResultModel;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

/**
 * Created by RexXiang on 2018/2/9.
 */

public interface ExamResultContract {


    interface View extends BaseView<Presenter> {

        void showProcessDialog(boolean isShow);

        void refreshView(ExamResultModel resultModel);
    }



    interface Presenter extends BasePresenter {

        void getExamResult();
    }


}
