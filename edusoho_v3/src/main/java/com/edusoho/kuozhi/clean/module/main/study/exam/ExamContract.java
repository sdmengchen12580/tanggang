package com.edusoho.kuozhi.clean.module.main.study.exam;

import com.edusoho.kuozhi.clean.bean.mystudy.ExamAnswer;
import com.edusoho.kuozhi.clean.bean.mystudy.ExamModel;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;
import com.google.gson.JsonObject;

/**
 * Created by RexXiang on 2018/2/6.
 */

public interface ExamContract {

    interface View extends BaseView<Presenter> {

        void refreshView(ExamModel examModel);

        void showProcessDialog(boolean isShow);

        void sendBroad();

        void timeFinishSubmit(JsonObject jsonObject);

    }

    interface Presenter extends BasePresenter {
        void getExam();

        void submitExam(String examResultId, ExamAnswer answer);
    }
}
