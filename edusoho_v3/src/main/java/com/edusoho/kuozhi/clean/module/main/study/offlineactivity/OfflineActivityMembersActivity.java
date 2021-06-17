package com.edusoho.kuozhi.clean.module.main.study.offlineactivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.mystudy.ActivityMembers;
import com.edusoho.kuozhi.clean.bean.mystudy.Relationship;
import com.edusoho.kuozhi.clean.module.base.BaseActivity;
import com.edusoho.kuozhi.clean.widget.ESIconView;
import com.edusoho.kuozhi.clean.widget.ESRecyclerView.ESPullAndLoadRecyclerView;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.view.circleImageView.CircularImageView;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RexXiang on 2018/1/29.
 */

public class OfflineActivityMembersActivity extends BaseActivity<OfflineActivityMembersContract.Presenter>  implements OfflineActivityMembersContract.View {

    private LoadDialog mProcessDialog;
    private ActivityMembers mActivityMembers;
    private com.edusoho.kuozhi.clean.widget.ESIconView ivback;
    private android.widget.TextView tvtoolbartitle;
    private com.edusoho.kuozhi.clean.widget.ESRecyclerView.ESPullAndLoadRecyclerView rvmemberslist;
    private OfflineActivityMembersAdapter mAdapter;
    private List<Relationship> relationshipList = new ArrayList<>();


    public static void launch(Context context, ActivityMembers activityMembers) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("activity_members", activityMembers);
        intent.putExtras(bundle);
        intent.setClass(context, OfflineActivityMembersActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMembers = (ActivityMembers) getIntent().getSerializableExtra("activity_members");
        setContentView(R.layout.activity_offline_activity_members);
        initView();
        initData();
    }

    private void initView() {
        this.rvmemberslist = (ESPullAndLoadRecyclerView) findViewById(R.id.rv_members_list);
        this.tvtoolbartitle = (TextView) findViewById(R.id.tv_toolbar_title);
        this.ivback = (ESIconView) findViewById(R.id.iv_back);
    }

    private void initData() {
        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OfflineActivityMembersActivity.this.finish();
            }
        });
        tvtoolbartitle.setText(String.format("活动成员（%d）", mActivityMembers.getData().size()));
        rvmemberslist.setLinearLayout();
        rvmemberslist.setPushRefreshEnable(false);
        rvmemberslist.setPullRefreshEnable(false);
        String ids = "";
        for (ActivityMembers.DataBean dataBean : mActivityMembers.getData()) {
            ids = ids + dataBean.getUser().getId() + ",";
        }
        mPresenter = new OfflineActivityMembersPresenter(this, ids);
        mPresenter.subscribe();
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

    private void followUser(String userId) {
        mPresenter.followUser(userId);
    }

    private void cancelFollowUser(String userId) {
        mPresenter.cancelFollowUser(userId);
    }

    @Override
    public void refreshView(List<Relationship> list) {
        relationshipList = list;
        mAdapter = new OfflineActivityMembersAdapter(this, mActivityMembers.getData(), list);
        rvmemberslist.setAdapter(mAdapter);
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
    public void clearData() {
        if (mAdapter != null) {
            mAdapter.clearData();
        }
    }

    private class OfflineActivityMembersAdapter extends RecyclerView.Adapter {

        private Context mContext;
        private List<ActivityMembers.DataBean> mList = new ArrayList<>();
        private List<Relationship> mRelations = new ArrayList<>();

        private OfflineActivityMembersAdapter(Context context, List<ActivityMembers.DataBean> list, List<Relationship> relations) {
            mContext = context;
            mList = list;
            mRelations = relations;
        }

        private void clearData() {
            mRelations.clear();
        }

        @Override
        public int getItemCount() {
            return mRelations.size();
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            holder = (ActivityMembersViewHolder) holder;
            final ActivityMembers.DataBean dataBean = mList.get(position);
//            Log.e("UserBean", "nickName"+dataBean.getUser().getNickname() +"\n" + dataBean.getUser().getPostName() + "\n" + dataBean.getUser().getTitle());
            ImageLoader.getInstance().displayImage(dataBean.getUser().getAvatar().getMiddle(), ((ActivityMembersViewHolder) holder).mAvatar, EdusohoApp.app.mOptions);
            ((ActivityMembersViewHolder) holder).mName.setText(dataBean.getUser().getNickname());
            ((ActivityMembersViewHolder) holder).mTitle.setText(dataBean.getUser().getPostName());
            for (Relationship relationship : mRelations) {
                if (relationship.getUserId().equals(dataBean.getUser().getId())) {
                    if (relationship.isFollowed()) {
                        ((ActivityMembersViewHolder) holder).mFollowText.setText("已关注");
                        ((ActivityMembersViewHolder) holder).mFollowText.setTextColor(getResources().getColor(R.color.default_grey_text_color));
                        ((ActivityMembersViewHolder) holder).mMarkImage.setImageDrawable(getResources().getDrawable(R.drawable.icon_activity_members_check));
                        ((ActivityMembersViewHolder) holder).mFollow.setBackground(getResources().getDrawable(R.drawable.shape_button_grey_radius));
                        ((ActivityMembersViewHolder) holder).mFollow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                cancelFollowUser(dataBean.getUser().getId());
                            }
                        });
                    } else {
                        ((ActivityMembersViewHolder) holder).mFollowText.setText("关注");
                        ((ActivityMembersViewHolder) holder).mFollowText.setTextColor(getResources().getColor(R.color.primary));
                        ((ActivityMembersViewHolder) holder).mMarkImage.setImageDrawable(getResources().getDrawable(R.drawable.icon_activity_members_add));
                        ((ActivityMembersViewHolder) holder).mFollow.setBackground(getResources().getDrawable(R.drawable.shape_button_blue_radius));
                        ((ActivityMembersViewHolder) holder).mFollow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                followUser(dataBean.getUser().getId());
                            }
                        });
                    }
                    break;
                }
            }

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_activity_member, parent, false);
            return new ActivityMembersViewHolder(view);
        }
    }

    private class ActivityMembersViewHolder extends RecyclerView.ViewHolder {

        private CircularImageView mAvatar;
        private TextView mName;
        private TextView mTitle;
        private LinearLayout mFollow;
        private ImageView mMarkImage;
        private TextView mFollowText;

        private ActivityMembersViewHolder(View view) {
            super(view);
            mAvatar = (CircularImageView) view.findViewById(R.id.iv_member_avatar);
            mName = (TextView) view.findViewById(R.id.tv_member_name);
            mTitle = (TextView) view.findViewById(R.id.tv_member_title);
            mFollow = (LinearLayout) view.findViewById(R.id.ll_member_follow);
            mMarkImage = (ImageView) view.findViewById(R.id.iv_member_follow);
            mFollowText = (TextView) view.findViewById(R.id.tv_member_follow);
            mFollowText.setGravity(Gravity.CENTER_VERTICAL);
        }
    }
}
