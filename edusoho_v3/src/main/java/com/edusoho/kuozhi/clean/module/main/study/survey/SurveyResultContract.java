package com.edusoho.kuozhi.clean.module.main.study.survey;

import com.edusoho.kuozhi.clean.bean.mystudy.SurveyModel;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

/**
 * Created by RexXiang on 2018/2/1.
 */

public interface SurveyResultContract {


    interface View extends BaseView<Presenter> {

        void showProcessDialog(boolean isShow);

        void sendBroad();

        void refreshView(SurveyModel surveyModel);
    }


    interface Presenter extends BasePresenter {

        void getSurveyResult();

    }
}
