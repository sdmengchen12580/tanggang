package com.edusoho.kuozhi.clean.module.main.mine.cache;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.module.main.mine.MineFragment;
import com.edusoho.kuozhi.clean.widget.ESIconView;
import com.edusoho.kuozhi.v3.entity.course.DownloadCourse;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.provider.AppSettingProvider;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.CourseCacheHelper;
import com.edusoho.kuozhi.v3.util.M3U8Util;

import java.util.List;

/**
 * Created by JesseHuang on 2017/2/10.
 */

public class MyVideoCacheFragment extends BaseFragment implements MineFragment.RefreshFragment {

    private SwipeRefreshLayout  srlContent;
    private RecyclerView        rvContent;
    private CourseCacheHelper   mCourseCacheHelper;
    private MyVideoCacheAdapter mAdapter;
    private ESIconView          esivBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_video_cache);
    }

    @Override
    protected void initView(View view) {
        srlContent = view.findViewById(R.id.srl_content);
        srlContent.setColorSchemeResources(R.color.primary_color);

        rvContent = view.findViewById(R.id.rv_content);
        esivBack = view.findViewById(R.id.iv_back);

        rvContent.setLayoutManager(new LinearLayoutManager(getActivity()));
        initData();
        loadData();
        srlContent.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
        esivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    private void initData() {
        mAdapter = new MyVideoCacheAdapter(mContext);
        rvContent.setAdapter(mAdapter);
    }

    private void loadData() {
        User user = getAppSettingProvider().getCurrentUser();
        if (user == null) {
            return;
        }
        School school = getAppSettingProvider().getCurrentSchool();
        mCourseCacheHelper = new CourseCacheHelper(getContext(), school.getDomain(), user.id);
        List<DownloadCourse> downloadCourses = mCourseCacheHelper.getLocalCourseList(M3U8Util.ALL, null, null);
        mAdapter.setData(downloadCourses);
        disabledLoadingView();
    }

    private void disabledLoadingView() {
        srlContent.setRefreshing(false);
    }

    @Override
    public void refreshData() {
        loadData();
    }

    public static class VideoCacheViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView  tvCourseTitle;
        TextView  tvSource;
        TextView  ivVideoSum;
        TextView  ivVideoSizes;
        TextView  tvExpiredView;
        View      rlayoutContent;

        VideoCacheViewHolder(View view) {
            super(view);
            ivCover = (ImageView) view.findViewById(R.id.iv_avatar);
            tvCourseTitle = (TextView) view.findViewById(R.id.tv_course_title);
            ivVideoSum = (TextView) view.findViewById(R.id.tv_video_sum);
            ivVideoSizes = (TextView) view.findViewById(R.id.tv_video_size);
            tvSource = (TextView) view.findViewById(R.id.tv_download_source);
            tvExpiredView = (TextView) view.findViewById(R.id.tv_download_expird);
            rlayoutContent = view.findViewById(R.id.rlayout_cache_layout);
        }
    }

    protected AppSettingProvider getAppSettingProvider() {
        return FactoryManager.getInstance().create(AppSettingProvider.class);
    }
}
