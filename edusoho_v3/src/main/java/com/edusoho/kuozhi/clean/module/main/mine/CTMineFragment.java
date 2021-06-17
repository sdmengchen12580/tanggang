package com.edusoho.kuozhi.clean.module.main.mine;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.module.base.BaseFragment;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.main.mine.cache.MyVideoCacheFragment;
import com.edusoho.kuozhi.clean.module.main.mine.examine.MySpActivity;
import com.edusoho.kuozhi.clean.module.main.mine.prestudyhome.PreStudyHomeActivity;
import com.edusoho.kuozhi.clean.module.main.mine.project.ProjectPlanRecordActivity;
import com.edusoho.kuozhi.clean.module.main.mine.question.MyQuestionFragment;
import com.edusoho.kuozhi.clean.widget.pop.AuthNoOkPop;
import com.edusoho.kuozhi.clean.widget.pop.AuthOkPop;
import com.edusoho.kuozhi.clean.widget.pop.AuthTakePicPop;
import com.edusoho.kuozhi.clean.widget.FragmentPageActivity;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.circleImageView.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import myutils.FastClickUtils;

public class CTMineFragment extends BaseFragment<BasePresenter> implements View.OnClickListener {

    private CircleImageView ivAvatar;
    private TextView tvName;
    private RelativeLayout tv_per_school_home;
    private RelativeLayout tvProject;
    private RelativeLayout tvCache;
    private RelativeLayout tvQuestion;
    private RelativeLayout tv_my_sp;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        initUserInfo();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_mine1, container, false);
        tv_my_sp = view.findViewById(R.id.tv_my_sp);
        tv_per_school_home = view.findViewById(R.id.tv_per_school_home);
        tvName = view.findViewById(R.id.tv_name);
        ivAvatar = view.findViewById(R.id.iv_avatar);
        tvProject = view.findViewById(R.id.tv_project);
        tvCache = view.findViewById(R.id.tv_cache);
        tvQuestion = view.findViewById(R.id.tv_question);
        tv_per_school_home.setOnClickListener(this);
        ivAvatar.setOnClickListener(this);
        tvProject.setOnClickListener(this);
        tvCache.setOnClickListener(this);
        tvQuestion.setOnClickListener(this);
        tv_my_sp.setOnClickListener(this);
        return view;
    }

    private void initUserInfo() {
        tvName.setText(EdusohoApp.app.loginUser.nickname);
        ImageLoader.getInstance().displayImage(EdusohoApp.app.loginUser.getMediumAvatar(), ivAvatar, EdusohoApp.app.mAvatarOptions);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_project) {
            ProjectPlanRecordActivity.launch(getActivity());
        } else if (v.getId() == R.id.tv_cache) {
            FragmentPageActivity.launch(getActivity(), MyVideoCacheFragment.class.getName(), null);
        } else if (v.getId() == R.id.tv_question) {
            FragmentPageActivity.launch(getActivity(), MyQuestionFragment.class.getName(), null);
        } else if (v.getId() == R.id.iv_avatar) {
            MobclickAgent.onEvent(getActivity(), "i_userInformationPortal");
            EdusohoApp.app.mEngine.runNormalPlugin("WebViewActivity", getActivity(), new PluginRunCallback() {
                @Override
                public void setIntentDate(Intent startIntent) {
                    String url = String.format(Const.MOBILE_APP_URL, EdusohoApp.app.app.schoolHost, Const.MY_INFO);
                    startIntent.putExtra(Const.WEB_URL, url);
                }
            });
        }
        //fixme 新增：预定教室
        else if (v.getId() == R.id.tv_per_school_home) {
            PreStudyHomeActivity.launch(getActivity());
        }
        //fixme 新增：我的审批
        else if (v.getId() == R.id.tv_my_sp) {
            MySpActivity.launch(getActivity());
        }
    }
}
