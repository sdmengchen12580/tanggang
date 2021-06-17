package com.edusoho.kuozhi.clean.module.course.rate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.Review;
import com.edusoho.kuozhi.clean.module.base.BaseFragment;
import com.edusoho.kuozhi.clean.module.course.CourseProjectFragmentListener;
import com.edusoho.kuozhi.clean.widget.ESDividerItemDecoration;
import com.edusoho.kuozhi.clean.widget.ESRecyclerView.ESPullAndLoadRecyclerView;

import java.util.List;

/**
 * Created by JesseHuang on 2017/3/26.
 */

public class CourseProjectRatesFragment extends BaseFragment<CourseProjectRatesContract.Presenter> implements
        CourseProjectRatesContract.View, CourseProjectFragmentListener {

    private static final String COURSE_PROJECT_MODEL = "CourseProjectModel";
    private ESPullAndLoadRecyclerView mRateRecyclerView;
    private TextView                  mEmpty;
    CourseProjectRatingAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        CourseProject courseProject = (CourseProject) bundle.getSerializable(COURSE_PROJECT_MODEL);
        mPresenter = new CourseProjectRatesPresenter(courseProject, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_course_project_rates, container, false);
        mRateRecyclerView = (ESPullAndLoadRecyclerView) view.findViewById(R.id.rv_rates);
        mEmpty = (TextView) view.findViewById(R.id.tv_empty);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        mPresenter.subscribe();
    }

    public void reFreshView() {
        mPresenter.subscribe();
    }

    private void initView() {
        adapter = new CourseProjectRatingAdapter(getActivity());
        mRateRecyclerView.setLinearLayout();
        ESDividerItemDecoration esDividerItemDecoration = new ESDividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        esDividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.course_project_rate_divider));
        mRateRecyclerView.addItemDecoration(esDividerItemDecoration);

        mRateRecyclerView.getRecyclerView();
        mRateRecyclerView.setOnPullLoadMoreListener(new ESPullAndLoadRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                mPresenter.getRates();
            }
        });

        mRateRecyclerView.setPullRefreshEnable(false);
        mRateRecyclerView.setPushRefreshEnable(true);
        mRateRecyclerView.setAdapter(adapter);
    }

    @Override
    public String getBundleKey() {
        return COURSE_PROJECT_MODEL;
    }

    @Override
    public void loadRates(List<Review> reviews) {
        adapter.addDatas(reviews);
        mEmpty.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void clearData() {
        adapter.clearData();
    }

    @Override
    public int getDataCount() {
        return adapter.getItemCount();
    }

    @Override
    public void loadMoreCompleted() {
        mRateRecyclerView.setPullLoadMoreCompleted();
    }
}
