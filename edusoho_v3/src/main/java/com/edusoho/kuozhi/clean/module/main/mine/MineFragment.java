package com.edusoho.kuozhi.clean.module.main.mine;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.PluginFragmentCallback;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.DefaultPageActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.ui.fragment.NotLoginFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.circleImageView.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.navi.NaviLifecycle;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JesseHuang on 2017/2/6.
 */

public class MineFragment extends BaseFragment implements AppBarLayout.OnOffsetChangedListener {

    private View            rlayoutUserInfo;
    private TextView        tvName;
    private CircleImageView ivAvatar;
    private TextView        tvUserPoint;
    private TabLayout       tbTitles;
    private ViewPager       vpContent;
    private String[]        mTabTitles;
    private String[]        mFragmentNames;
    private AppBarLayout    mAppBarLayout;
    private ImageButton     mPointRule;

    private MinePagerAdapter minePagerAdapter;

    private List<RefreshFragment> mRefreshFragmentList = new ArrayList<>();

    private LifecycleProvider<FragmentEvent> mFragmentLifeProvider = NaviLifecycle.createFragmentLifecycleProvider(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_mine);
    }

    @Override
    public void onResume() {
        super.onResume();
        initUserInfo();
        mAppBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mAppBarLayout.removeOnOffsetChangedListener(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            mAppBarLayout.removeOnOffsetChangedListener(this);
        } else {
            mAppBarLayout.addOnOffsetChangedListener(this);
        }
    }

    @Override
    protected void initView(View view) {
        rlayoutUserInfo = view.findViewById(R.id.rlayout_user_info);
        tvName = (TextView) view.findViewById(R.id.tv_name);
        ivAvatar = (CircleImageView) view.findViewById(R.id.iv_avatar);
        tvUserPoint = (TextView) view.findViewById(R.id.tv_reward_point);
        tbTitles = (TabLayout) view.findViewById(R.id.tl_titles);
        vpContent = (ViewPager) view.findViewById(R.id.vp_content);
        vpContent.setOffscreenPageLimit(4);
        mAppBarLayout = (AppBarLayout) view.findViewById(R.id.app_bar);
        mPointRule = (ImageButton) view.findViewById(R.id.ib_point_rule);
        mPointRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String url = String.format("%s%s", EdusohoApp.app.host, Const.HTML5_POINT_INFO);
                mActivity.app.mEngine.runNormalPlugin("WebViewActivity", mContext, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(Const.WEB_URL, url);
                    }
                });
            }
        });
        initUserInfo();
        initViewPager();
        bindClickListener();
    }

    private void initUserInfo() {
        if (app.loginUser != null) {
            tvName.setText(app.loginUser.nickname);
            ImageLoader.getInstance().displayImage(app.loginUser.getMediumAvatar(), ivAvatar, app.mAvatarOptions);
        } else {
            tvName.setText(R.string.tap_to_login);
            ivAvatar.setImageDrawable(getResources().getDrawable(R.drawable.new_default_avatar));
        }
    }

    private void initViewPager() {
        mTabTitles = new String[]{"学习", "缓存", "收藏", "问答"};
        if (app.loginUser == null) {
            mFragmentNames = new String[]{"NotLoginFragment"};
        } else {
            mFragmentNames = new String[]{"MyStudyFragment", "MyVideoCacheFragment", "MyFavoriteFragment", "MyQuestionFragment"};
        }
        minePagerAdapter = new MinePagerAdapter(getFragmentManager(), mTabTitles, mFragmentNames);
        vpContent.setAdapter(minePagerAdapter);
        tbTitles.setupWithViewPager(vpContent);
        vpContent.addOnPageChangeListener(getOnPageChangeListener());
    }

    private void bindClickListener() {
        rlayoutUserInfo.setOnClickListener(getUserViewClickListener(rlayoutUserInfo.getId()));
        ivAvatar.setOnClickListener(getUserViewClickListener(ivAvatar.getId()));
    }

    private ViewPager.OnPageChangeListener getOnPageChangeListener() {
        return new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (0 == position) {
                    MobclickAgent.onEvent(mActivity, "i_study");
                } else if (1 == position) {
                    MobclickAgent.onEvent(mActivity, "i_cache");
                } else if (2 == position) {
                    MobclickAgent.onEvent(mActivity, "i_collection");
                } else if (3 == position) {
                    MobclickAgent.onEvent(mActivity, "i_Q&A");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
    }

    private class MinePagerAdapter extends FragmentStatePagerAdapter {
        private String[] tabTitles;
        private String[] fragmentTags;

        public MinePagerAdapter(FragmentManager fm, String[] titles, String[] tags) {
            super(fm);
            tabTitles = titles;
            fragmentTags = tags;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (fragmentTags.length == 1) {
                fragment = app.mEngine.runPluginWithFragment(
                        fragmentTags[0], mActivity, new PluginFragmentCallback() {
                            @Override
                            public void setArguments(Bundle bundle) {
                                bundle.putInt("FragmentType", NotLoginFragment.FRAGMENT_MINE);
                            }
                        });
                return fragment;
            }
            switch (position) {
                case 0:
                    fragment = app.mEngine.runPluginWithFragment(
                            fragmentTags[position], mActivity, null);
                    break;
                case 1:
                    fragment = app.mEngine.runPluginWithFragment(
                            fragmentTags[position], mActivity, null);
                    break;
                case 2:
                    fragment = app.mEngine.runPluginWithFragment(
                            fragmentTags[position], mActivity, null);
                    break;
                case 3:
                    fragment = app.mEngine.runPluginWithFragment(
                            fragmentTags[position], getActivity(), null);
                    break;
            }
            if (!mRefreshFragmentList.contains(fragment) && fragment instanceof RefreshFragment) {
                mRefreshFragmentList.add((RefreshFragment) fragment);
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        return new MessageType[]{new MessageType(Const.LOGIN_SUCCESS), new MessageType(Const.THIRD_PARTY_LOGIN_SUCCESS), new MessageType(Const.LOGOUT_SUCCESS)};
    }

    @Override
    public void invoke(WidgetMessage message) {
        MessageType messageType = message.type;
        if (messageType.type.equals(Const.LOGIN_SUCCESS) || messageType.type.equals(Const.THIRD_PARTY_LOGIN_SUCCESS)) {
            initUserInfo();
            initViewPager();
            bindClickListener();
            for (RefreshFragment fragment : mRefreshFragmentList) {
                fragment.refreshData();
            }
        }
        if (messageType.type.equals(Const.LOGOUT_SUCCESS)) {
            initUserInfo();
            initViewPager();
            bindClickListener();
        }
    }

    public interface RefreshFragment {
        void refreshData();
    }

    private View.OnClickListener getUserViewClickListener(int viewId) {
        if (app.loginUser != null) {
            if (viewId == ivAvatar.getId()) {
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MobclickAgent.onEvent(mContext, "i_userInformationPortal");
                        mActivity.app.mEngine.runNormalPlugin("WebViewActivity", mContext, new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                String url = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, Const.MY_INFO);
                                startIntent.putExtra(Const.WEB_URL, url);
                            }
                        });
                    }
                };
            } else {
                return null;
            }
        } else {
            return new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    app.mEngine.runNormalPluginWithAnim("LoginActivity", mContext, null, new NormalCallback() {
                        @Override
                        public void success(Object obj) {
                            mActivity.overridePendingTransition(R.anim.down_to_up, R.anim.none);
                        }
                    });
                }
            };
        }
    }

    public static class EmptyViewHolder extends RecyclerView.ViewHolder {
        public EmptyViewHolder(View view) {
            super(view);
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (!((DefaultPageActivity) getActivity()).mCurrentTag.equals("MineFragment")) {
            return;
        }
        int maxHeight = AppUtil.dp2px(getContext(), 148);
        if (-verticalOffset == maxHeight) {
            changeToolbarStyle(true);
            return;
        }
        changeToolbarStyle(false);
    }

    private void changeToolbarStyle(boolean isTop) {
        DefaultPageActivity defaultPageActivity = (DefaultPageActivity) getActivity();
        if (isTop) {
            defaultPageActivity.mActionBar.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            defaultPageActivity.setTitle("我");
            defaultPageActivity.tvSitting.setTextColor(getResources().getColor(R.color.primary_font_color));
            defaultPageActivity.tvTitle.setTextColor(getResources().getColor(R.color.primary_font_color));
        } else {
            defaultPageActivity.mActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary_color)));
            defaultPageActivity.setTitle("");
            defaultPageActivity.tvSitting.setTextColor(Color.WHITE);
        }
    }
}
