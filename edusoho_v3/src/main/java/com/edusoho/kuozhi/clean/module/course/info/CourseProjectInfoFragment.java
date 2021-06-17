package com.edusoho.kuozhi.clean.module.course.info;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.Member;
import com.edusoho.kuozhi.clean.bean.innerbean.Price;
import com.edusoho.kuozhi.clean.bean.innerbean.Teacher;
import com.edusoho.kuozhi.clean.module.base.BaseFragment;
import com.edusoho.kuozhi.clean.module.course.CourseProjectFragmentListener;
import com.edusoho.kuozhi.clean.module.course.dialog.ServicesDialog;
import com.edusoho.kuozhi.clean.utils.SharedPreferencesUtils;
import com.edusoho.kuozhi.clean.utils.StringUtils;
import com.edusoho.kuozhi.clean.utils.biz.SharedPreferencesHelper;
import com.edusoho.kuozhi.clean.widget.component.ESImageGetter;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.ui.LoginActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.ReviewStarView;
import com.edusoho.kuozhi.v3.view.circleImageView.CircularImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wefika.flowlayout.FlowLayout;

import java.util.List;

/**
 * Created by JesseHuang on 2017/3/26.
 * 教学计划简介
 */

public class CourseProjectInfoFragment extends BaseFragment<CourseProjectInfoContract.Presenter>
        implements CourseProjectInfoContract.View, CourseProjectFragmentListener {

    public static final String COURSE_PROJECT_MODEL = "CourseProjectModel";

    private FlowLayout        mPromise;
    private TextView          mTitle;
    private TextView          mStudentNum;
    private ReviewStarView    mCourseRate;
    private TextView          mSalePrice;
    private TextView          mOriginalPrice;
    private TextView          mSaleWord;
    private View              mVipLine;
    private View              mVipLayout;
    private TextView          mVipText;
    private View              mServicesLayout;
    private TextView          mIntroduce;
    private View              mIntroduceLayout;
    private View              mAudiencesLayout;
    private TextView          mAudiences;
    private CircularImageView mTeacherAvatar;
    private TextView          mTeacherName;
    private TextView          mTeacherTitle;
    private View              mCourseMemberCountLayout;
    private TextView          mCourseMemberCount;
    private LinearLayout      mCourseMembers;
    private CourseProject     mCourseProject;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mCourseProject = (CourseProject) bundle.getSerializable(COURSE_PROJECT_MODEL);
        mPresenter = new CourseProjectInfoPresenter(mCourseProject, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_course_project_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mPromise = view.findViewById(R.id.fl_promise_layout);
        mTitle = view.findViewById(R.id.tv_course_project_title);
        mStudentNum = view.findViewById(R.id.tv_student_num);
        mCourseRate = view.findViewById(R.id.rb_course_rate);
        mSalePrice = view.findViewById(R.id.tv_sale_price);
        mOriginalPrice = view.findViewById(R.id.tv_original_price);
        mSaleWord = view.findViewById(R.id.tv_sale_word);
        mVipLine = view.findViewById(R.id.v_vip_line);
        mVipLayout = view.findViewById(R.id.rl_vip_layout);
        mVipText = view.findViewById(R.id.tv_vip_text);
        mServicesLayout = view.findViewById(R.id.ll_services_layout);
        mIntroduce = view.findViewById(R.id.tv_course_project_info);
        mIntroduceLayout = view.findViewById(R.id.ll_course_project_introduce);
        mAudiencesLayout = view.findViewById(R.id.ll_audiences_layout);
        mAudiences = view.findViewById(R.id.tv_audiences);
        mTeacherAvatar = view.findViewById(R.id.civ_teacher_avatar);
        mTeacherName = view.findViewById(R.id.tv_teacher_name);
        mTeacherTitle = view.findViewById(R.id.tv_teacher_title);
        mCourseMemberCountLayout = view.findViewById(R.id.rl_course_member_num);
        mCourseMemberCount = view.findViewById(R.id.tv_course_member_count);
        mCourseMembers = view.findViewById(R.id.ll_course_members);

        mVipLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EdusohoApp.app.loginUser == null) {
                    LoginActivity.startLogin(getActivity());
                    return;
                }
                final String url = String.format(
                        Const.MOBILE_APP_URL,
                        EdusohoApp.app.schoolHost,
                        Const.VIP_LIST
                );
                CoreEngine.create(getContext()).runNormalPlugin("WebViewActivity"
                        , getContext(), new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                startIntent.putExtra(Const.WEB_URL, url);
                            }
                        });
            }
        });

        mCourseMemberCountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String url = String.format(
                        Const.MOBILE_APP_URL,
                        EdusohoApp.app.schoolHost,
                        String.format(Const.COURSE_MEMBER_LIST, mCourseProject.id)
                );
                CoreEngine.create(getContext()).runNormalPlugin("WebViewActivity"
                        , getContext(), new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                startIntent.putExtra(Const.WEB_URL, url);
                            }
                        });
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter.subscribe();
    }

    @Override
    public void initCourseProjectInfo(CourseProject course) {
        mTitle.setText(course.getTitle());
        String showStudentNumEnabled = SharedPreferencesUtils.getInstance(getActivity())
                .open(SharedPreferencesHelper.CourseSetting.XML_NAME)
                .getString(SharedPreferencesHelper.CourseSetting.SHOW_STUDENT_NUM_ENABLED_KEY);
        mStudentNum.setText(showStudentNumEnabled != null && "1".equals(showStudentNumEnabled) ?
                String.format(getString(R.string.course_student_count), course.studentNum) : "");
        mCourseRate.setRating((int) course.rating);
    }

    @Override
    public void showPrice(CourseProjectPriceEnum type, Price price, Price originPrice) {
        switch (type) {
            case FREE:
                mOriginalPrice.setText(R.string.free_course_project);
                mOriginalPrice.setTextColor(getResources().getColor(R.color.primary_color));
                mSalePrice.setVisibility(View.GONE);
                break;
            case ORIGINAL:
                mOriginalPrice.setText(originPrice.getPriceWithUnit());
                mOriginalPrice.setTextColor(getResources().getColor(R.color.secondary_color));
                mSalePrice.setVisibility(View.GONE);
                break;
            case SALE:
                mSalePrice.setText(price.getPriceWithUnit());
                mOriginalPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                mOriginalPrice.setText(originPrice.getPriceWithUnit());
                mSaleWord.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void showVipAdvertising(String vipName) {
        mVipLine.setVisibility(View.VISIBLE);
        mVipLayout.setVisibility(View.VISIBLE);
        mVipText.setText(String.format(getString(R.string.join_vip), vipName));
    }

    @Override
    public void showServices(final CourseProject.Service[] services) {
        if (services == null || services.length == 0) {
            return;
        }
        mServicesLayout.setVisibility(View.VISIBLE);
        for (CourseProject.Service service : services) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_course_project_promise, null);
            ((TextView) view.findViewById(R.id.tv_promise)).setText(service.fullName);
            FlowLayout.LayoutParams lp = new FlowLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            mPromise.addView(view);
        }

        mServicesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServicesDialog.newInstance(services).show(getFragmentManager(), "ServicesDialog");
            }
        });
    }

    @Override
    public void showIntroduce(String content) {
        if (!StringUtils.isEmpty(content)) {
            mIntroduceLayout.setVisibility(View.VISIBLE);
            mIntroduce.setText(Html.fromHtml(content, new ESImageGetter(getActivity(), mIntroduce), null));

        }
    }

    @Override
    public void showAudiences(String[] audiences) {
        if (audiences != null && audiences.length > 0) {
            mAudiencesLayout.setVisibility(View.VISIBLE);
            StringBuilder sb = new StringBuilder();
            for (String audience : audiences) {
                sb.append(audience).append("、");
            }
            mAudiences.setText(sb.replace(sb.length() - 1, sb.length(), "").toString());
        }
    }

    @Override
    public void showTeacher(final Teacher teacher) {
        mTeacherName.setText(teacher.nickname);
        mTeacherTitle.setText(teacher.title);
        ImageLoader.getInstance().displayImage(teacher.avatar.middle, mTeacherAvatar, EdusohoApp.app.mAvatarOptions);
        mTeacherAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String url = String.format(
                        Const.MOBILE_APP_URL,
                        EdusohoApp.app.schoolHost,
                        String.format(Const.USER_PROFILE, teacher.id));
                CoreEngine.create(getContext()).runNormalPlugin("WebViewActivity"
                        , getContext(), new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                startIntent.putExtra(Const.WEB_URL, url);
                            }
                        });
            }
        });
    }

    @Override
    public void showMemberNum(int count) {
        mCourseMemberCount.setText(String.format(getString(R.string.course_member_count), count));
    }

    @Override
    public void showMembers(List<Member> members) {
        if (members != null && members.size() > 0) {
            mCourseMembers.setVisibility(View.VISIBLE);
            mCourseMemberCountLayout.setVisibility(View.VISIBLE);
            int screenWidth = EdusohoApp.screenW;
            int memberAvatarWidth = CommonUtil.dip2px(getActivity(), 50);
            int avatarMargin = CommonUtil.dip2px(getActivity(), 24);
            int viewMargin = CommonUtil.dip2px(getActivity(), 15);
            int showMemberCount;
            showMemberCount = (screenWidth + avatarMargin - 2 * viewMargin) / (memberAvatarWidth + avatarMargin);
            int size = (showMemberCount < members.size() ? showMemberCount : members.size());
            for (int i = 0; i < size; i++) {
                LinearLayout memberView = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.item_course_member, null);
                ImageLoader.getInstance().displayImage(members.get(i).user.getMediumAvatar(), (CircularImageView) memberView.findViewById(R.id.avatar_course_member), EdusohoApp.app.mAvatarOptions);
                ((TextView) memberView.findViewById(R.id.tv_member_name)).setText(members.get(i).user.nickname);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                if (i != size - 1) {
                    lp.rightMargin = CommonUtil.dip2px(getActivity(), 24);
                }
                memberView.setLayoutParams(lp);
                mCourseMembers.addView(memberView);
                final int userId = members.get(i).user.id;
                memberView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String url = String.format(
                                Const.MOBILE_APP_URL,
                                EdusohoApp.app.schoolHost,
                                String.format(Const.USER_PROFILE, userId));
                        CoreEngine.create(getContext()).runNormalPlugin("WebViewActivity"
                                , getContext(), new PluginRunCallback() {
                                    @Override
                                    public void setIntentDate(Intent startIntent) {
                                        startIntent.putExtra(Const.WEB_URL, url);
                                    }
                                });
                    }
                });
            }
        }
    }

    @Override
    public String getBundleKey() {
        return COURSE_PROJECT_MODEL;
    }
}
