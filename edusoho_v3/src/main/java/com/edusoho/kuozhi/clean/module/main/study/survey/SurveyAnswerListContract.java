package com.edusoho.kuozhi.clean.module.main.study.survey;

import com.edusoho.kuozhi.clean.bean.mystudy.SurveyAnswersModel;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

/**
 * Created by RexXiang on 2018/2/1.
 */

public interface SurveyAnswerListContract {


    interface View extends BaseView<Presenter> {

        void refreshView(SurveyAnswersModel answersModel);

        void showProcessDialog(boolean isShow);

        void loadMoreData(SurveyAnswersModel answersModel);

        void refreshCompleted();

    }


    interface Presenter extends BasePresenter {

        void getQuestionnaireAnswers();

        void getMoreQuestionnaireAnswers(int limit, int offset);


    }
}
