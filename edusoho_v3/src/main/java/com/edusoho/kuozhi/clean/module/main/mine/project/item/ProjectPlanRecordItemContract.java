package com.edusoho.kuozhi.clean.module.main.mine.project.item;


import com.edusoho.kuozhi.clean.bean.DataPageResult;
import com.edusoho.kuozhi.clean.bean.mystudy.ExamRecord;
import com.edusoho.kuozhi.clean.bean.mystudy.OfflineActivityRecord;
import com.edusoho.kuozhi.clean.bean.mystudy.PostCourseRecord;
import com.edusoho.kuozhi.clean.bean.mystudy.ProjectPlanRecord;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

public class ProjectPlanRecordItemContract {

    interface View extends BaseView<Presenter> {
        void showProjectPlanRecords(DataPageResult<ProjectPlanRecord> projectPlanRecords);

        void showPostCourseRecords(DataPageResult<PostCourseRecord> postCourseRecords);

        void showExamRecords(DataPageResult<ExamRecord> examRecords);

        void showOfflineActivityRecord(DataPageResult<OfflineActivityRecord> offlineActivityRecords);

        void showEmpty(boolean visible);
    }

    interface Presenter extends BasePresenter {

        void init(String type, int offset);
    }
}
