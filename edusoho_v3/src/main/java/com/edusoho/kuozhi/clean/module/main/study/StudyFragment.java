package com.edusoho.kuozhi.clean.module.main.study;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.view.NoScrollViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * Created by RexXiang on 2018/1/11.
 */
public class StudyFragment extends BaseFragment {

    private NoScrollViewPager mStudyViewPager;
    private StudyViewPagerAdapter mAdapter;
    private String[] mFragments;
    private String mCurrentTag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_study);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mStudyViewPager = (NoScrollViewPager) view.findViewById(R.id.vp_study_content);
        mStudyViewPager.setNoScroll(true);
        mCurrentTag = "Assignments";
        initViewPager();
    }

    private void initViewPager() {
        mFragments = new String[]{"AssignmentsFragment", "PostCourseFragment", "CourseSetFragment"};
        mAdapter = new StudyViewPagerAdapter(getFragmentManager(), mFragments);
        mStudyViewPager.setAdapter(mAdapter);
        mStudyViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void switchViewPager(String tag) {
        if (tag.equals(mCurrentTag)) {
            return;
        }
        if (tag.equals("Assignments")) {
            mStudyViewPager.setCurrentItem(0);
        }
        if (tag.equals("PostCourse")) {
            mStudyViewPager.setCurrentItem(1);
        }
        if (tag.equals("CourseSet")) {
            mStudyViewPager.setCurrentItem(2);
        }
        mCurrentTag = tag;
    }


    private class StudyViewPagerAdapter extends FragmentPagerAdapter {

        private String[] fragmentTags;

        private StudyViewPagerAdapter(FragmentManager fm, String[] tags) {
            super(fm);
            fragmentTags = tags;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = app.mEngine.runPluginWithFragment(fragmentTags[position], mActivity, null);
            return fragment;
        }

        @Override
        public int getCount() {
            return fragmentTags.length;
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onReceiveStickyMessage(MessageEvent messageEvent) {
        if (messageEvent.getType() == MessageEvent.SWITCH_STUDY) {
            EventBus.getDefault().removeAllStickyEvents();
            switchViewPager(messageEvent.getMessageBody().toString());
        }
    }

}
