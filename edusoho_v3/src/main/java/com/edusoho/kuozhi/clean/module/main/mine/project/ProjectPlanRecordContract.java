package com.edusoho.kuozhi.clean.module.main.mine.project;


import com.edusoho.kuozhi.clean.bean.mystudy.TrainingRecordItem;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

import java.util.List;

public class ProjectPlanRecordContract {

    interface View extends BaseView<Presenter> {

        void showTrainingRecords(List<TrainingRecordItem> trainingRecordItems);
    }

    interface Presenter extends BasePresenter {

    }
}
