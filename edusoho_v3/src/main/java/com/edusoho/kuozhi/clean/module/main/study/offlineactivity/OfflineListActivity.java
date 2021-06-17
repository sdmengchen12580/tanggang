package com.edusoho.kuozhi.clean.module.main.study.offlineactivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.mystudy.OfflineActivitiesResult;
import com.edusoho.kuozhi.clean.bean.mystudy.OfflineActivityCategory;
import com.edusoho.kuozhi.clean.module.base.BaseActivity;
import com.edusoho.kuozhi.clean.widget.ESIconView;
import com.edusoho.kuozhi.clean.widget.ESRecyclerView.ESPullAndLoadRecyclerView;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.view.EduSohoNewIconView;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RexXiang on 2018/1/25.
 */

public class OfflineListActivity extends BaseActivity<OfflineListContract.Presenter> implements OfflineListContract.View {

    protected ESPullAndLoadRecyclerView mListRecyclerView;
    protected OfflineListAdapter mAdapter;
    protected List<OfflineActivitiesResult.DataBean> mList = new ArrayList<>();
    protected List<OfflineActivitiesResult.DataBean> mFilterList = new ArrayList<>();
    protected List<OfflineActivityCategory> mCategoryList = new ArrayList<>();
    protected EduSohoNewIconView esivTypeArrow;
    protected EduSohoNewIconView esivStatusArrow;
    protected ESIconView ivBack;
    protected TextView mType;
    protected TextView mStatus;
    protected TextView mBarTitle;
    protected RelativeLayout mTypeView;
    protected RelativeLayout mStatusView;
    protected String mCurrentType;
    protected String mCurrentStatus;
    protected LoadDialog mProcessDialog;


    public static void launch(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, OfflineListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_list);

        initView();
        initData();
    }

    protected void initView() {
        ivBack = (ESIconView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OfflineListActivity.this.finish();
            }
        });
        mBarTitle = (TextView) findViewById(R.id.tv_toolbar_title);
        mType = (TextView) findViewById(R.id.tv_activities_type);
        mType.setText("类型");
        esivTypeArrow = (EduSohoNewIconView) findViewById(R.id.tv_activities_type_arrow);
        mTypeView = (RelativeLayout) findViewById(R.id.rl_activities_type);
        mStatus = (TextView) findViewById(R.id.tv_activities_status);
        mStatus.setText("进行中");
        esivStatusArrow = (EduSohoNewIconView) findViewById(R.id.tv_activities_status_arrow);
        mStatusView = (RelativeLayout) findViewById(R.id.rl_activities_status);
        mListRecyclerView = (ESPullAndLoadRecyclerView) findViewById(R.id.rv_activities_list);
        mListRecyclerView.setLinearLayout();
        mListRecyclerView.setOnPullLoadMoreListener(new ESPullAndLoadRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                mCurrentType = "";
                mCurrentStatus = "进行中";
                mType.setText("类型");
                mStatus.setText("进行中");
                mPresenter.subscribe();
            }

            @Override
            public void onLoadMore() {

            }
        });
        mListRecyclerView.setPullRefreshEnable(true);
        mListRecyclerView.setPushRefreshEnable(false);
        mTypeView.setOnClickListener(getSelectClickListener());
        mStatusView.setOnClickListener(getSelectClickListener());
    }

    protected void initData() {
        mCurrentType = "";
        mCurrentStatus = "进行中";
        mPresenter = new OfflineListPresenter(this);
        mPresenter.subscribe();
    }

    protected View.OnClickListener getSelectClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListView listView = new ListView(OfflineListActivity.this);
                final ArrayList<String> list = new ArrayList<>();
                final int ViewId = view.getId();
                if (ViewId == R.id.rl_activities_type) {
                    esivTypeArrow.setText(getString(R.string.new_font_fold));
                    for (OfflineActivityCategory category : mCategoryList) {
                        list.add(category.getName());
                    }
                }
                if (ViewId == R.id.rl_activities_status) {
                    esivStatusArrow.setText(getString(R.string.new_font_fold));
                    list.add("进行中");
                    list.add("已结束");
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        OfflineListActivity.this, R.layout.ppt_lesson_popwindow_list_item, list
                );
                listView.setAdapter(arrayAdapter);
                final PopupWindow popupWindow = new PopupWindow(listView, ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setWidth(view.getWidth());
                popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setOutsideTouchable(true);
                popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.card_bg));
                popupWindow.setFocusable(true);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        popupWindow.dismiss();
                        if (ViewId == R.id.rl_activities_type) {
                            mType.setText(list.get(i));
                            esivTypeArrow.setText(getString(R.string.new_font_unfold));
                            filterByType(list.get(i));
                        }
                        if (ViewId == R.id.rl_activities_status) {
                            mStatus.setText(list.get(i));
                            esivStatusArrow.setText(getString(R.string.new_font_unfold));
                            filterByStatus(list.get(i));
                        }
                    }
                });
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        if (ViewId == R.id.rl_activities_type) {
                            esivTypeArrow.setText(getString(R.string.new_font_unfold));
                        }
                        if (ViewId == R.id.rl_activities_status) {
                            esivStatusArrow.setText(getString(R.string.new_font_unfold));
                        }
                        popupWindow.dismiss();
                    }
                });
                popupWindow.showAsDropDown(view);
            }
        };
    }

    protected void filterByType(String type) {
//        if (mCurrentType.equals(type)) {
//            return;
//        }
        mCurrentType = type;
        mFilterList.clear();
        for (OfflineActivitiesResult.DataBean dataBean : mList) {
            if (dataBean.getCategoryName().equals(type)) {
                mFilterList.add(dataBean);
            }
        }
        mAdapter.setList(mFilterList);
    }

    protected void filterByStatus(String status) {
        if (mCurrentStatus.equals(status)) {
            return;
        }
        mCurrentStatus = status;
        if (status.equals("进行中")) {
            mPresenter.getList("ongoing");
        }
        if (status.equals("已结束")) {
            mPresenter.getList("end");
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

    protected void joinActivity(String id) {
        showProcessDialog();
        mPresenter.joinActivity(id);
    }

    private void activityDetail(String id) {
        OfflineActivityDetailActivity.launch(this, id);
    }

    @Override
    public void showProcessDialog(boolean isShow) {
        if (isShow) {
            showProcessDialog();
        } else {
            hideProcessDialog();
        }
    }

    @Override
    public void refreshCompleted() {
        mListRecyclerView.setPullLoadMoreCompleted();
    }

    @Override
    public void refreshView(List<OfflineActivitiesResult.DataBean> list, List<OfflineActivityCategory> categoryList) {
        mList = list;
        mCategoryList = categoryList;
        mAdapter = new OfflineListAdapter(this, mList);
        mListRecyclerView.setAdapter(mAdapter);
        if (!TextUtils.isEmpty(mCurrentType)) {
            filterByType(mCurrentType);
        }
    }

    @Override
    public void clearData() {
        mCurrentType = "";
        mCurrentStatus = "进行中";
        mType.setText("类型");
        mStatus.setText("进行中");
        if (mAdapter != null) {
            mAdapter.clearData();
        }
    }

    protected class OfflineListAdapter extends RecyclerView.Adapter {
        private Context mContext;
        private List<OfflineActivitiesResult.DataBean> mList;

        protected OfflineListAdapter(Context context, List<OfflineActivitiesResult.DataBean> list) {
            mContext = context;
            mList = list;
        }

        private void clearData() {
            mList.clear();
        }

        private void setList(List<OfflineActivitiesResult.DataBean> list) {
            mList = list;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_study_assignment_activity, parent, false);
            return new OfflineListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            OfflineListViewHolder offlineListViewHolder = (OfflineListViewHolder) holder;
            final OfflineActivitiesResult.DataBean dataBean = mList.get(position);
            ImageLoader.getInstance().displayImage(dataBean.getCover().getMiddle(), offlineListViewHolder.mAssignmentImage, EdusohoApp.app.mOptions);
            offlineListViewHolder.mAssignmentTitle.setText(dataBean.getTitle());
            offlineListViewHolder.mAssignmentType.setText(dataBean.getCategoryName());
            if (Long.parseLong(dataBean.getEndDate()) - Long.parseLong(dataBean.getStartDate()) > 86400) {
                offlineListViewHolder.mAssignmentTime.setText(String.format("活动时间：%s至%s", AppUtil.timeStampToDate(dataBean.getStartDate(), "MM-dd"), AppUtil.timeStampToDate(dataBean.getEndDate(), "MM-dd")));
            } else {
                offlineListViewHolder.mAssignmentTime.setText(String.format("活动时间：%s-%s", AppUtil.timeStampToDate(dataBean.getStartDate(), "MM-dd HH:mm"), AppUtil.timeStampToDate(dataBean.getEndDate(), "HH:mm")));
            }
            if (TextUtils.isEmpty(dataBean.getAddress())) {
                offlineListViewHolder.mAssignmentSubTitle.setText("活动地点：未定");
            } else {
                offlineListViewHolder.mAssignmentSubTitle.setText(String.format("活动地点：%s", dataBean.getAddress()));
            }

            //报名人数判断
            if (!dataBean.getMaxStudentNum().equals("0")) {
                SpannableString spannableString = new SpannableString(String.format("报名人数：%s/%s", dataBean.getStudentNum(), dataBean.getMaxStudentNum()));
                spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.assignment_exam_blue)), 5, dataBean.getStudentNum().length() + 5, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                offlineListViewHolder.mAssignmentExamResult.setText(spannableString);
            }

            //报名状态判断
            offlineListViewHolder.mAssignmentExamResult.setVisibility(View.GONE);
            offlineListViewHolder.mAssignmentBottomButton.setVisibility(View.INVISIBLE);
            offlineListViewHolder.mAssignmentExamStatus.setVisibility(View.VISIBLE);
            offlineListViewHolder.mAssignmentExamStatus.setBackground(getResources().getDrawable(R.drawable.shape_assignment_exam_grey));
            offlineListViewHolder.mAssignmentExamStatus.setTextColor(getResources().getColor(R.color.assignment_exam_grey));

            if (dataBean.getActivityTimeStatus().equals("ongoing") || dataBean.getActivityTimeStatus().equals("notStart")) {
                if (dataBean.getApplyStatus().equals("submitted")) {
                    offlineListViewHolder.mAssignmentExamStatus.setBackground(getResources().getDrawable(R.drawable.shape_assignment_exam_yellow));
                    offlineListViewHolder.mAssignmentExamStatus.setTextColor(getResources().getColor(R.color.assignment_exam_yellow));
                    offlineListViewHolder.mAssignmentExamStatus.setText("审核中");
                }
                if (dataBean.getApplyStatus().equals("join")) {
                    offlineListViewHolder.mAssignmentExamStatus.setBackground(getResources().getDrawable(R.drawable.shape_assignment_exam_green));
                    offlineListViewHolder.mAssignmentExamStatus.setTextColor(getResources().getColor(R.color.assignment_exam_green));
                    offlineListViewHolder.mAssignmentExamStatus.setText("报名成功");
                }
                if (dataBean.getApplyStatus().equals("enrollmentEnd")) {
                    offlineListViewHolder.mAssignmentExamStatus.setText("报名结束");
                }
                if (dataBean.getApplyStatus().equals("enrollUnable")) {
                    offlineListViewHolder.mAssignmentExamStatus.setText("名额已满");
                }
                if (dataBean.getApplyStatus().equals("enrollAble")) {
                    offlineListViewHolder.mAssignmentExamStatus.setVisibility(View.GONE);
                    offlineListViewHolder.mAssignmentBottomButton.setVisibility(View.VISIBLE);
                    offlineListViewHolder.mAssignmentExamResult.setVisibility(View.VISIBLE);
                    offlineListViewHolder.mAssignmentBottomButton.setText("立即报名");
                    offlineListViewHolder.mAssignmentBottomButton.setBackground(getResources().getDrawable(R.drawable.shape_bottom_right_corner_radius));
                    offlineListViewHolder.mAssignmentBottomButton.setEnabled(true);
                    offlineListViewHolder.mAssignmentBottomButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            joinActivity(dataBean.getId());
                        }
                    });
                }
            }

            if (dataBean.getActivityTimeStatus().equals("end")) {
                offlineListViewHolder.mAssignmentExamStatus.setText("已结束");
            }

//            //报名按钮判断
//            if (dataBean.getActivityTimeStatus().equals("notStart")) {
//                offlineListViewHolder.mAssignmentExamStatus.setVisibility(View.GONE);
//                offlineListViewHolder.mAssignmentBottomButton.setVisibility(View.VISIBLE);
//                offlineListViewHolder.mAssignmentExamResult.setVisibility(View.VISIBLE);
//                offlineListViewHolder.mAssignmentBottomButton.setText("暂未开始");
//                offlineListViewHolder.mAssignmentBottomButton.setBackground(getResources().getDrawable(R.drawable.shape_bottom_right_corner_radius_grey));
//                offlineListViewHolder.mAssignmentBottomButton.setEnabled(false);
//            }

            offlineListViewHolder.mTopView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activityDetail(dataBean.getId());
                }
            });
        }
    }

    protected class OfflineListViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout mBottomView;
        public RelativeLayout mTopView;
        public ImageView mAssignmentImage;
        public TextView mAssignmentTitle;
        public TextView mAssignmentType;
        public TextView mAssignmentTime;
        public TextView mAssignmentSubTitle;
        public TextView mAssignmentExamCount;
        public TextView mAssignmentExamStatus;
        public TextView mAssignmentExamResult;
        public Button mAssignmentBottomButton;

        private OfflineListViewHolder(View view) {
            super(view);
            mTopView = (RelativeLayout) view.findViewById(R.id.rl_assignment_top);
            mBottomView = (RelativeLayout) view.findViewById(R.id.rl_assignment_bottom);
            mAssignmentImage = (ImageView) view.findViewById(R.id.iv_assignment);
            mAssignmentTitle = (TextView) view.findViewById(R.id.tv_assignment_title);
            mAssignmentType = (TextView) view.findViewById(R.id.tv_assignment_type);
            mAssignmentTime = (TextView) view.findViewById(R.id.tv_assignment_time);
            mAssignmentSubTitle = (TextView) view.findViewById(R.id.tv_assignment_sub_title);
            mAssignmentBottomButton = (Button) view.findViewById(R.id.btn_assignment_bottom);
            mAssignmentExamCount = (TextView) view.findViewById(R.id.tv_assignment_exam_count);
            mAssignmentExamStatus = (TextView) view.findViewById(R.id.tv_assignment_exam_status);
            mAssignmentExamResult = (TextView) view.findViewById(R.id.tv_assignment_exam_result);
        }

    }


}
