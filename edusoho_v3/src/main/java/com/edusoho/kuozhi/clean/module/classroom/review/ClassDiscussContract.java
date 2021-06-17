package com.edusoho.kuozhi.clean.module.classroom.review;

import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;
import com.edusoho.kuozhi.v3.entity.course.DiscussDetail;

import java.util.List;

/**
 * Created by DF on 2017/5/23.
 */

public interface ClassDiscussContract {

    interface View extends BaseView<Presenter>{

        void initDiscuss(final DiscussDetail discussDetail);

        void setLoadViewVisibleible(int isVis);

        void setEmptyViewVis(int isVis);

        void showCompanies(DiscussDetail discussDetail);

        void setRecyclerViewStatus(int status);

        void setSwipeStatus(boolean status);

        void adapterRefresh(List<DiscussDetail.ResourcesBean> list);

    }

    interface Presenter extends BasePresenter{

        void loadMore(int start);

        void refresh();

    }
}
