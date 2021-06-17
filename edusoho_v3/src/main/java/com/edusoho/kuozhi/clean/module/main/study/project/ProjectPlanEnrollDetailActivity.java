package com.edusoho.kuozhi.clean.module.main.study.project;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.mystudy.ProjectCourseItem;
import com.edusoho.kuozhi.clean.bean.mystudy.ProjectExamItem;
import com.edusoho.kuozhi.clean.bean.mystudy.ProjectOfflineCourseItem;
import com.edusoho.kuozhi.clean.bean.mystudy.ProjectOfflineExamItem;
import com.edusoho.kuozhi.clean.bean.mystudy.ProjectPlan;
import com.edusoho.kuozhi.clean.module.base.BaseActivity;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.view.EduSohoNewIconView;
import com.edusoho.kuozhi.v3.view.ScrollableAppBarLayout;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RexXiang on 2018/3/22.
 */

public class ProjectPlanEnrollDetailActivity extends BaseActivity<ProjectPlanEnrollDetailContract.Presenter> implements ProjectPlanEnrollDetailContract.View, AppBarLayout.OnOffsetChangedListener {


    private String                   mProjectId;
    private LoadDialog               mProcessDialog;
    private ProjectPlan              mProjectPlan;
    private ImageView                ivbackground;
    private RelativeLayout           imagerlayout;
    private EduSohoNewIconView       ivback;
    private TextView                 tvtoolbartitle;
    private Toolbar                  toolbar;
    private CollapsingToolbarLayout  toolbarlayout;
    private ScrollableAppBarLayout   appbar;
    private TextView                 tvprojecttitle;
    private TextView                 tvprojecttype;
    private TextView                 tvprojecttime;
    private TextView                 tvprojectenrolltime;
    private RelativeLayout           moresummary;
    private TextView                 tvprojectsummary;
    private ExpandableListView       elvprojectitem;
    private TextView                 tvactivitiesstucount;
    private Button                   btnprojectjoin;
    private View                     emptyView;
    private ProjectPlanEnrollAdapter mAdapter;
    private List<ProjectPlan.ItemsDetailBean> mGroupList = new ArrayList<>();
    private ArrayList<List>                   mChildList = new ArrayList<>();

    public static void launch(Context context, String projectId) {
        Intent intent = new Intent();
        intent.putExtra("project_id", projectId);
        intent.setClass(context, ProjectPlanEnrollDetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProjectId = getIntent().getStringExtra("project_id");
        setContentView(R.layout.activity_project_plan_enroll_detail);
        initView();
        initData();
    }

    private void initView() {
        this.btnprojectjoin = findViewById(R.id.btn_project_join);
        this.tvactivitiesstucount = findViewById(R.id.tv_activities_stu_count);
        this.elvprojectitem = findViewById(R.id.elv_project_item);
        this.elvprojectitem.setSelector(new ColorDrawable(Color.TRANSPARENT));
        this.tvprojectsummary = findViewById(R.id.tv_project_summary);
        this.moresummary = findViewById(R.id.more_summary);
        this.tvprojectenrolltime = findViewById(R.id.tv_project_enroll_time);
        this.tvprojecttime = findViewById(R.id.tv_project_time);
        this.tvprojecttype = findViewById(R.id.tv_project_type);
        this.tvprojecttitle = findViewById(R.id.tv_project_title);
        this.appbar = findViewById(R.id.app_bar);
        this.toolbarlayout = findViewById(R.id.toolbar_layout);
        this.toolbar = findViewById(R.id.toolbar);
        this.tvtoolbartitle = findViewById(R.id.tv_toolbar_title);
        this.ivback = findViewById(R.id.iv_back);
        this.imagerlayout = findViewById(R.id.image_rlayout);
        this.ivbackground = findViewById(R.id.iv_background);
        this.emptyView = findViewById(R.id.rl_empty_view);
    }

    private void initData() {
        mPresenter = new ProjectPlanEnrollDetailPresenter(this, mProjectId);
        mPresenter.subscribe();
        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProjectPlanEnrollDetailActivity.this.finish();
            }
        });
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


    @Override
    public void showProcessDialog(boolean isShow) {
        if (isShow) {
            showProcessDialog();
        } else {
            hideProcessDialog();
        }
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
    public void refreshView(ProjectPlan projectPlan) {
        mProjectPlan = projectPlan;
        ImageLoader.getInstance().displayImage(mProjectPlan.getCover().getMiddle(), ivbackground, EdusohoApp.app.mOptions);
        tvprojecttitle.setText(mProjectPlan.getName());
        tvprojecttype.setText(mProjectPlan.getCategoryName());
        if (Long.parseLong(mProjectPlan.getEndTime()) - Long.parseLong(mProjectPlan.getStartTime()) >= 86400) {
            tvprojecttime.setText(String.format("项目时间：%s - %s", AppUtil.timeStampToDate(mProjectPlan.getStartTime(), "yyyy-MM-dd"), AppUtil.timeStampToDate(mProjectPlan.getEndTime(), "yyyy-MM-dd")));
        } else {
            tvprojecttime.setText(String.format("项目时间：%s - %s", AppUtil.timeStampToDate(mProjectPlan.getStartTime(), "yyyy-MM-dd HH:mm"), AppUtil.timeStampToDate(mProjectPlan.getEndTime(), "HH:mm")));
        }

        if (Long.parseLong(mProjectPlan.getEnrollmentEndDate()) - Long.parseLong(mProjectPlan.getEnrollmentStartDate()) >= 86400) {
            tvprojectenrolltime.setText(String.format("报名时间：%s - %s", AppUtil.timeStampToDate(mProjectPlan.getEnrollmentStartDate(), "yyyy-MM-dd"), AppUtil.timeStampToDate(mProjectPlan.getEnrollmentEndDate(), "yyyy-MM-dd")));
        } else {
            tvprojectenrolltime.setText(String.format("报名时间：%s - %s", AppUtil.timeStampToDate(mProjectPlan.getEnrollmentStartDate(), "yyyy-MM-dd HH:mm"), AppUtil.timeStampToDate(mProjectPlan.getEnrollmentEndDate(), "HH:mm")));
        }

        if (TextUtils.isEmpty(mProjectPlan.getSummary())) {
            tvprojectsummary.setText("详情编辑中， 敬请期待。");
        } else {
            tvprojectsummary.setText(AppUtil.removeHtmlTag(mProjectPlan.getSummary()));
            moresummary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProjectSummaryActivity.launch(ProjectPlanEnrollDetailActivity.this, mProjectPlan.getSummary());
                }
            });
        }
        setListView();
        showBottomState();
    }

    private void setListView() {
        mGroupList = mProjectPlan.getItemsDetail();
        for (int i = 0; i < mGroupList.size(); i++) {
            mChildList.add(new ArrayList());
        }
        mAdapter = new ProjectPlanEnrollAdapter(this, mGroupList, mChildList);
        elvprojectitem.setAdapter(mAdapter);
        elvprojectitem.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                ProjectPlan.ItemsDetailBean bean = mGroupList.get(i);
                if (mChildList.get(i).size() > 0) {
                    return false;
                }
                mPresenter.getItemDetail(bean.getTargetId(), bean.getTargetType(), i);
                return false;
            }
        });
        emptyView.setVisibility((mAdapter.getGroupCount() == 0) ? View.VISIBLE : View.GONE);
        elvprojectitem.setVisibility((mAdapter.getGroupCount() != 0) ? View.VISIBLE : View.GONE);
        elvprojectitem.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                return false;
            }
        });
    }

    private void showBottomState() {
        SpannableString spannableString;
        if (mProjectPlan.getMaxStudentNum().equals("0")) {
            spannableString = new SpannableString(String.format("报名人数：%s/不限", mProjectPlan.getStudentNum()));
        } else {
            spannableString = new SpannableString(String.format("报名人数：%s/%s", mProjectPlan.getStudentNum(), mProjectPlan.getMaxStudentNum()));
        }
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.assignment_exam_blue)), 5, mProjectPlan.getStudentNum().length() + 5, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        tvactivitiesstucount.setText(spannableString);
        btnprojectjoin.setBackground(getResources().getDrawable(R.drawable.shape_bottom_right_corner_radius_grey));
        btnprojectjoin.setEnabled(false);
        if (mProjectPlan.getApplyStatus().equals("submitted")) {
            btnprojectjoin.setText("审核中");
        }
        if (mProjectPlan.getApplyStatus().equals("enrollmentEnd")) {
            btnprojectjoin.setText("报名结束");
        }
        if (mProjectPlan.getApplyStatus().equals("enrollUnable")) {
            btnprojectjoin.setText("名额已满");
        }
        if (mProjectPlan.getApplyStatus().equals("success")) {
            btnprojectjoin.setBackground(getResources().getDrawable(R.drawable.shape_bottom_right_corner_radius));
            btnprojectjoin.setText("报名成功");
        }
        if (mProjectPlan.getApplyStatus().equals("enrollInWeb")) {
            btnprojectjoin.setText("请前往PC端报名");
        }
        if (mProjectPlan.getApplyStatus().equals("enrollmentUnStart")) {
            btnprojectjoin.setText("暂未开始");
        }
        if (mProjectPlan.getApplyStatus().equals("enrollAble") || mProjectPlan.getApplyStatus().equals("rejected") || mProjectPlan.getApplyStatus().equals("reset")) {
            btnprojectjoin.setBackground(getResources().getDrawable(R.drawable.shape_bottom_right_corner_radius));
            btnprojectjoin.setText("立即报名");
            btnprojectjoin.setEnabled(true);
            btnprojectjoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    joinProject();
                }
            });
        }
    }

    @Override
    public void expandChildView(Object itemDetail, String type, int index) {
        switch (type) {
            case "course": {
                List<ProjectCourseItem> object = (List<ProjectCourseItem>) itemDetail;
                mChildList.set(index, object);
                break;
            }
            case "offline_course": {
                List<ProjectOfflineCourseItem> object = (List<ProjectOfflineCourseItem>) itemDetail;
                mChildList.set(index, object);
                break;
            }
            case "exam": {
                ProjectExamItem item = (ProjectExamItem) itemDetail;
                List object = new ArrayList();
                object.add(item);
                mChildList.set(index, object);
                break;
            }
            default: {
                ProjectOfflineExamItem item = (ProjectOfflineExamItem) itemDetail;
                List object = new ArrayList();
                object.add(item);
                mChildList.set(index, object);
            }
        }
        showProcessDialog(false);
        mAdapter.notifyDataSetChanged();
    }

    private void joinProject() {
        showProcessDialog();
        mPresenter.joinProject();
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
            tvtoolbartitle.setText(R.string.project_plan_info);
        } else {
            setToolbarLayoutBackground(ContextCompat.getColor(this, R.color.transparent));
            tvtoolbartitle.setText("");
        }
    }

    protected void setToolbarLayoutBackground(int color) {
        toolbarlayout.setContentScrimColor(color);
    }
}
