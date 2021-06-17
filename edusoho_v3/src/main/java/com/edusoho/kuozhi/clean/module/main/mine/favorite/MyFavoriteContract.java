package com.edusoho.kuozhi.clean.module.main.mine.favorite;

import com.edusoho.kuozhi.clean.bean.innerbean.Study;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

import java.util.List;

/**
 * Created by DF on 2017/5/10.
 */

interface MyFavoriteContract {

    interface View extends BaseView<Presenter>{

        void showComplete(List<Study> courseSets);

        void setSwpFreshing(boolean isRefreshing);

    }

    interface Presenter extends BasePresenter{

    }
}
