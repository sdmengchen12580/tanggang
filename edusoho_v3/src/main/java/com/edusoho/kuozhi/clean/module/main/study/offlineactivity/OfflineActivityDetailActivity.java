package com.edusoho.kuozhi.clean.module.main.study.offlineactivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.mystudy.ActivityMembers;
import com.edusoho.kuozhi.clean.bean.mystudy.OfflineActivitiesResult;
import com.edusoho.kuozhi.clean.module.base.BaseActivity;
import com.edusoho.kuozhi.clean.widget.component.ESImageGetter;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.util.ActivityUtil;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.EduSohoNewIconView;
import com.edusoho.kuozhi.v3.view.ScrollableAppBarLayout;
import com.edusoho.kuozhi.v3.view.circleImageView.CircleImageView;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by RexXiang on 2018/1/29.
 */

public class OfflineActivityDetailActivity extends BaseActivity<OfflineActivityDetailContract.Presenter> implements OfflineActivityDetailContract.View, AppBarLayout.OnOffsetChangedListener {

    private String                           mActivityId;
    private LoadDialog                       mProcessDialog;
    private OfflineActivitiesResult.DataBean mDataBean;
    private ActivityMembers                  mActivityMembers;
    private ImageView                        ivbackground;
    private RelativeLayout                   imagerlayout;
    private EduSohoNewIconView               ivback;
    private Toolbar                          toolbar;
    private CollapsingToolbarLayout          toolbarlayout;
    private ScrollableAppBarLayout           appbar;
    private TextView                         tvactivitytitle;
    private TextView                         tvactivitytype;
    private TextView                         tvactivitytime;
    private TextView                         tvactivityaddress;
    private TextView                         tvactivitylimittime;
    private TextView                         tvmembertip;
    private LinearLayout                     studenticonllayout;
    private TextView                         tvactivitysummary;
    private TextView                         mToolBarText;
    private TextView                         tvactivitiesstucount;
    private android.widget.Button            btnactivityjoin;
    private LinearLayout                     layoutMemberInfo;
    private CoordinatorLayout                parentrlayout;

    public static void launch(Context context, String activityId) {
        Intent intent = new Intent();
        intent.putExtra("activity_id", activityId);
        intent.setClass(context, OfflineActivityDetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_activity_detail);
        getWindow().setBackgroundDrawable(null);
        ActivityUtil.setStatusBarFitsByColor(this, R.color.transparent);
        mActivityId = getIntent().getStringExtra("activity_id");
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        appbar.addOnOffsetChangedListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        appbar.removeOnOffsetChangedListener(this);
    }

    private void initView() {
        this.parentrlayout = findViewById(R.id.parent_rlayout);
        this.btnactivityjoin = findViewById(R.id.btn_activity_join);
        this.tvactivitiesstucount = findViewById(R.id.tv_activities_stu_count);
        this.tvactivitysummary = findViewById(R.id.tv_activity_summary);
        this.studenticonllayout = findViewById(R.id.student_icon_llayout);
        this.tvmembertip = findViewById(R.id.tv_member_tip);
        this.tvactivitylimittime = findViewById(R.id.tv_activity_limit_time);
        this.tvactivityaddress = findViewById(R.id.tv_activity_address);
        this.tvactivitytime = findViewById(R.id.tv_activity_time);
        this.tvactivitytype = findViewById(R.id.tv_activity_type);
        this.tvactivitytitle = findViewById(R.id.tv_activity_title);
        this.appbar = findViewById(R.id.app_bar);
        this.toolbarlayout = findViewById(R.id.toolbar_layout);
        this.toolbar = findViewById(R.id.toolbar);
        this.ivback = findViewById(R.id.iv_back);
        this.imagerlayout = findViewById(R.id.image_rlayout);
        this.ivbackground = findViewById(R.id.iv_background);
        this.mToolBarText = findViewById(R.id.tv_toolbar_title);
        this.layoutMemberInfo = findViewById(R.id.ll_member_info);
    }

    private void initData() {
        mPresenter = new OfflineActivityDetailPresenter(this, mActivityId);
        mPresenter.subscribe();
        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OfflineActivityDetailActivity.this.finish();
            }
        });
    }

    private void showProcessDialog() {
        if (mProcessDialog == null) {
            mProcessDialog = LoadDialog.create(this);
        }
        mProcessDialog.show();
    }

    private void hideProcessDialog() {
        if (mProcessDialog == null) {
            return;
        }
        if (mProcessDialog.isShowing()) {
            mProcessDialog.dismiss();
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxHeight = AppUtil.dp2px(this, 44);
        int toolbarHeight = AppUtil.dp2px(getBaseContext(), 210);
        if (toolbarHeight + verticalOffset > maxHeight * 2) {
            changeToolbarStyle(false);
            return;
        }
        changeToolbarStyle(true);
    }

    private void changeToolbarStyle(boolean isTop) {
        if (isTop) {
            setToolbarLayoutBackground(ContextCompat.getColor(this, R.color.primary));
            mToolBarText.setText("活动详情");
        } else {
            setToolbarLayoutBackground(ContextCompat.getColor(this, R.color.transparent));
            mToolBarText.setText("");
        }
    }

    protected void setToolbarLayoutBackground(int color) {
        toolbarlayout.setContentScrimColor(color);
    }

    @Override
    public void refreshView(OfflineActivitiesResult.DataBean dataBean, ActivityMembers members) {
        mDataBean = dataBean;
        mActivityMembers = members;
        ImageLoader.getInstance().displayImage(dataBean.getCover().getMiddle(), ivbackground, EdusohoApp.app.mOptions);
        tvactivitytitle.setText(dataBean.getTitle());
        tvactivitytype.setText(dataBean.getCategoryName());
        tvactivityaddress.setText(String.format("活动地点：%s", dataBean.getAddress()));
        if (Long.parseLong(dataBean.getEndDate()) - Long.parseLong(dataBean.getStartDate()) >= 86400) {
            tvactivitytime.setText(String.format("活动时间：%s至%s", AppUtil.timeStampToDate(dataBean.getStartDate(), "MM-dd"), AppUtil.timeStampToDate(dataBean.getEndDate(), "MM-dd")));
        } else {
            tvactivitytime.setText(String.format("活动时间：%s-%s", AppUtil.timeStampToDate(dataBean.getStartDate(), "MM-dd HH:mm"), AppUtil.timeStampToDate(dataBean.getEndDate(), "HH:mm")));
        }
        tvactivitylimittime.setText(String.format("报名截止：%s", AppUtil.timeStampToDate(dataBean.getEnrollmentEndDate(), "yyyy-MM-dd HH:mm")));
        if (TextUtils.isEmpty(dataBean.getSummary())) {
            tvactivitysummary.setText("详情编辑中， 敬请期待。");
        } else {
            tvactivitysummary.setText(Html.fromHtml(dataBean.getSummary(), new ESImageGetter(this, tvactivitysummary), null));
        }
        showMembers();
        showBottomState();
    }

    private void showMembers() {
//        View.OnClickListener onClickListener =
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String id = String.valueOf(v.getTag());
//                        jumpToMember(id);
//                    }
//                };
        View.OnClickListener moreClickListener =
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OfflineActivityMembersActivity.launch(OfflineActivityDetailActivity.this, mActivityMembers);
                    }
                };
        int memberCount = mActivityMembers.getData().size();
        SpannableString spannableString = new SpannableString(String.format("已有%d人加入活动", memberCount));
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.assignment_exam_blue)), 2, Integer.toString(memberCount).length() + 3, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        tvmembertip.setText(spannableString);
        studenticonllayout.removeAllViews();
        if (memberCount == 0) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_activity_detail_member, studenticonllayout, false);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, -1);
            params.weight = 1;
            params.rightMargin = AppUtil.dp2px(OfflineActivityDetailActivity.this, 10);
            view.setLayoutParams(params);
            ImageView image = (CircleImageView) view.findViewById(R.id.iv_avatar_icon);
            image.setImageDrawable(getResources().getDrawable(R.drawable.new_default_avatar));
            studenticonllayout.addView(view);
            return;
        }
        if (memberCount <= 5) {
            for (int i = 0; i < memberCount; i++) {
                View view = LayoutInflater.from(this).inflate(R.layout.item_activity_detail_member, studenticonllayout, false);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, -1);
                params.weight = 1;
                params.rightMargin = AppUtil.dp2px(OfflineActivityDetailActivity.this, 10);
                view.setLayoutParams(params);
                ImageView image = (CircleImageView) view.findViewById(R.id.iv_avatar_icon);
                image.setTag(mActivityMembers.getData().get(i).getUser().getId());
                ImageLoader.getInstance().displayImage(mActivityMembers.getData().get(i).getUser().getAvatar().getMiddle(), image, EdusohoApp.app.mOptions);
                studenticonllayout.addView(view);
            }
        }
        if (memberCount > 5) {
            memberCount = 5;
            for (int i = 0; i < memberCount + 1; i++) {
                View view = LayoutInflater.from(this).inflate(R.layout.item_activity_detail_member, studenticonllayout, false);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, -1);
                params.weight = 1;
                params.rightMargin = AppUtil.dp2px(OfflineActivityDetailActivity.this, 10);
                view.setLayoutParams(params);
                ImageView image = (CircleImageView) view.findViewById(R.id.iv_avatar_icon);
                if (i == memberCount) {
                    image.setImageDrawable(getResources().getDrawable(R.drawable.icon_activity_detail_more));
                    image.setOnClickListener(moreClickListener);
                } else {
                    image.setTag(mActivityMembers.getData().get(i).getUser().getId());
                    ImageLoader.getInstance().displayImage(mActivityMembers.getData().get(i).getUser().getAvatar().getMiddle(), image, EdusohoApp.app.mOptions);
                }
                studenticonllayout.addView(view);
            }
        }
        layoutMemberInfo.setOnClickListener(moreClickListener);
    }

    private void showBottomState() {
        //人数
        SpannableString spannableString;
        if (mDataBean.getMaxStudentNum().equals("0")) {
            spannableString = new SpannableString(String.format("报名人数：%s/不限", mDataBean.getStudentNum()));
        } else {
            spannableString = new SpannableString(String.format("报名人数：%s/%s", mDataBean.getStudentNum(), mDataBean.getMaxStudentNum()));
        }
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.assignment_exam_blue)), 5, mDataBean.getStudentNum().length() + 5, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        tvactivitiesstucount.setText(spannableString);
        btnactivityjoin.setBackground(getResources().getDrawable(R.drawable.shape_bottom_right_corner_radius_grey));
        btnactivityjoin.setEnabled(false);
        if (mDataBean.getActivityTimeStatus().equals("ongoing") || mDataBean.getActivityTimeStatus().equals("notStart")) {
            if (mDataBean.getApplyStatus().equals("submitted")) {
                btnactivityjoin.setText("审核中");
            }
            if (mDataBean.getApplyStatus().equals("enrollmentEnd")) {
                btnactivityjoin.setText("报名结束");
            }
            if (mDataBean.getApplyStatus().equals("enrollUnable")) {
                btnactivityjoin.setText("名额已满");
            }
            if (mDataBean.getApplyStatus().equals("join")) {
                btnactivityjoin.setBackground(getResources().getDrawable(R.drawable.shape_bottom_right_corner_radius));
                btnactivityjoin.setText("报名成功");
            }
            if (mDataBean.getApplyStatus().equals("enrollAble")) {
                btnactivityjoin.setBackground(getResources().getDrawable(R.drawable.shape_bottom_right_corner_radius));
                btnactivityjoin.setText("立即报名");
                btnactivityjoin.setEnabled(true);
                btnactivityjoin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        joinActivity(mDataBean.getId());
                    }
                });
            }
        }
        if (mDataBean.getActivityTimeStatus().equals("end")) {
            btnactivityjoin.setText("活动结束");
        }
    }

    private void joinActivity(String id) {
        showProcessDialog();
        mPresenter.joinActivity(id);
    }

    private void jumpToMember(String id) {
        final String url = String.format(
                Const.MOBILE_APP_URL,
                EdusohoApp.app.schoolHost,
                String.format("main#/userinfo/%s",
                        id)
        );
        CoreEngine.create(getApplicationContext()).runNormalPlugin("WebViewActivity"
                , getApplicationContext(), new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(Const.WEB_URL, url);
                    }
                });
    }

    @Override
    public void showProcessDialog(boolean isShow) {
        if (isShow) {
            showProcessDialog();
        } else {
            hideProcessDialog();
        }
    }
}
