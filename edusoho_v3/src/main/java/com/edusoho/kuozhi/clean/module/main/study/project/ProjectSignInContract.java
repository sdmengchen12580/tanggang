package com.edusoho.kuozhi.clean.module.main.study.project;


import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

public class ProjectSignInContract {

    interface View extends BaseView<Presenter> {

        void showSignInSuccess(String message);

        void showSignInFailed(String message);

        void showSignInResponseError();
    }

    interface Presenter extends BasePresenter {

        void requestUrl(String url);
    }
}
