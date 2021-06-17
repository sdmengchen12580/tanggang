package com.edusoho.kuozhi.clean.module.main.study.assignment;

import com.edusoho.kuozhi.clean.bean.mystudy.AssignmentModel;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

import java.util.List;

/**
 * Created by RexXiang on 2018/1/15.
 */

public interface MyAssignmentsContract {

    interface View extends BaseView<Presenter> {

        void refreshView(List<AssignmentModel> list);

        void clearData();

        void refreshCompleted();
    }

    interface Presenter extends BasePresenter {
        void getMyAssignments();
    }
}
