package com.edusoho.kuozhi.clean.module.main.study.survey;

import com.edusoho.kuozhi.clean.bean.mystudy.EvaluationAnswer;
import com.edusoho.kuozhi.clean.bean.mystudy.SurveyAnswer;
import com.edusoho.kuozhi.clean.bean.mystudy.SurveyModel;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

/**
 * Created by RexXiang on 2018/1/30.
 */

public interface SurveyDetailContract {


    interface View extends BaseView<Presenter> {

        void refreshView(SurveyModel surveyModel);

        void showProcessDialog(boolean isShow);

        void sendBroad();
    }

    interface Presenter extends BasePresenter {


        void doSurvey(String surveyId);

        void submitSurvey(String surveyResultId, SurveyAnswer answer);

        void submitEvaluation(String surveyResultId, EvaluationAnswer answer, int courseId, int taskId);

    }
}
