package com.edusoho.kuozhi.clean.module.main.news.notification.center;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.module.base.BaseActivity;
import com.edusoho.kuozhi.clean.module.main.news.notification.course.CourseNotificationActivity;
import com.edusoho.kuozhi.clean.module.main.news.notification.course.CourseNotificationDecoration;
import com.edusoho.kuozhi.clean.utils.ItemClickSupport;
import com.edusoho.kuozhi.imserver.entity.ConvEntity;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.v3.ui.BulletinActivity;
import com.edusoho.kuozhi.v3.ui.friend.FriendNewsActivity;
import com.edusoho.kuozhi.v3.util.PushUtil;

import java.util.List;

public class NotificationCenterActivity extends BaseActivity<NotificationCenterContract.Presenter> implements NotificationCenterContract.View {

    private RecyclerView        mNotificationList;
    private NotificationAdapter mAdapter;
    private View                mBack;

    public static void launch(Context context) {
        Intent intent = new Intent(context, NotificationCenterActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_center_activity);
        init();
        mPresenter = new NotificationCenterPresenter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    private void init() {
        mBack = findViewById(R.id.iv_back);
        mNotificationList = (RecyclerView) findViewById(R.id.rv_notifications);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void initNotificationList(final List<ConvEntity> list) {
        mAdapter = new NotificationAdapter(this, list);
        mNotificationList.setLayoutManager(new LinearLayoutManager(this));
        mNotificationList.setAdapter(mAdapter);
        ItemClickSupport.addTo(mNotificationList).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                ConvEntity convEntity = list.get(position);
                switch (convEntity.getType()) {
                    case Destination.NOTIFY:
                        CourseNotificationActivity.launch(NotificationCenterActivity.this);
                        break;
                    case Destination.GLOBAL:
                    case Destination.BATCH_NOTIFICATION:
                        BulletinActivity.launch(NotificationCenterActivity.this);
                        break;
                    case Destination.USER:
                        FriendNewsActivity.launch(NotificationCenterActivity.this);
                        break;
                }
            }
        });
        mNotificationList.addItemDecoration(new CourseNotificationDecoration(this));
    }
}
