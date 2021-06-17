package com.edusoho.kuozhi.v3.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.api.UserApi;
import com.edusoho.kuozhi.clean.bean.ChatRoomResult;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.module.main.news.notification.center.NotificationCenterActivity;
import com.edusoho.kuozhi.clean.react.ArticleReactActivity;
import com.edusoho.kuozhi.imserver.IMClient;
import com.edusoho.kuozhi.imserver.entity.ConvEntity;
import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.imserver.entity.ReceiverInfo;
import com.edusoho.kuozhi.imserver.entity.Role;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.imserver.entity.message.MessageBody;
import com.edusoho.kuozhi.imserver.listener.IMConnectStatusListener;
import com.edusoho.kuozhi.imserver.listener.IMMessageReceiver;
import com.edusoho.kuozhi.imserver.managar.IMRoleManager;
import com.edusoho.kuozhi.imserver.util.IMConnectStatus;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.adapter.SwipeAdapter;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.push.New;
import com.edusoho.kuozhi.v3.model.bal.push.TypeBusinessEnum;
import com.edusoho.kuozhi.v3.model.provider.IMProvider;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.ClassroomDiscussActivity;
import com.edusoho.kuozhi.v3.ui.DefaultPageActivity;
import com.edusoho.kuozhi.v3.ui.ImChatActivity;
import com.edusoho.kuozhi.v3.ui.NewsCourseActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.edusoho.kuozhi.v3.view.swipemenulistview.SwipeMenu;
import com.edusoho.kuozhi.v3.view.swipemenulistview.SwipeMenuCreator;
import com.edusoho.kuozhi.v3.view.swipemenulistview.SwipeMenuItem;
import com.edusoho.kuozhi.v3.view.swipemenulistview.SwipeMenuListView;
import com.tencent.stat.StatService;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by JesseHuang on 15/4/26.
 * 动态列表
 */
public class NewsFragment extends BaseFragment {
    public static final String TAG                          = "NewsFragment";
    public static final int    UPDATE_UNREAD_MSG            = 17;
    public static final int    UPDATE_UNREAD_BULLETIN       = 18;
    public static final int    UPDATE_UNREAD_NEWS_COURSE    = 19;
    public static final int    UPDATE_UNREAD_ARTICLE_CREATE = 20;
    String[] OTHER_TYPES               = {Destination.USER, Destination.COURSE, Destination.CLASSROOM, Destination.ARTICLE};
    String[] CENTER_NOTIFICATION_TYPES = {Destination.BATCH_NOTIFICATION, Destination.GLOBAL, Destination.NOTIFY, Destination.TESTPAPER, Destination.USER};

    public static final int SHOW    = 60;
    public static final int DISMISS = 61;

    private SwipeMenuListView       lvNewsList;
    private SwipeAdapter            mSwipeAdapter;
    private View                    mEmptyView;
    private TextView                tvEmptyText;
    private TextView                mHeaderView;
    private View                    mLoadProgressBar;
    private LoadingHandler          mLoadingHandler;
    private DefaultPageActivity     mParentActivity;
    private IMMessageReceiver       mIMMessageReceiver;
    private IMConnectStatusListener mIMConnectStatusListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_news);
        mLoadingHandler = new LoadingHandler(this);
        syncIMData();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        getRestCourse();
        registIMMessageReceiver();
        mIMConnectStatusListener = getIMConnectStatusListener();
        IMClient.getClient().addConnectStatusListener(mIMConnectStatusListener);
        updateIMConnectStatus(IMClient.getClient().getIMConnectStatus());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mIMMessageReceiver != null) {
            IMClient.getClient().removeReceiver(mIMMessageReceiver);
            mIMMessageReceiver = null;
        }
        if (mIMConnectStatusListener != null) {
            IMClient.getClient().removeConnectStatusListener(mIMConnectStatusListener);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    private void registIMMessageReceiver() {
        if (mIMMessageReceiver != null) {
            return;
        }

        mIMMessageReceiver = getIMMessageListener();
        IMClient.getClient().addMessageReceiver(mIMMessageReceiver);
    }

    protected IMMessageReceiver getIMMessageListener() {
        return new IMMessageReceiver() {

            private boolean filterMessageEntity(MessageEntity messageEntity) {
                if (Destination.LESSON.equals(messageEntity.getConvNo())) {
                    return false;
                }
                return true;
            }

            @Override
            public boolean onReceiver(MessageEntity msg) {
                if (filterMessageEntity(msg)) {
                    initData();
                    //handleMessage(msg);
                }
                return false;
            }

            @Override
            public boolean onOfflineMsgReceiver(List<MessageEntity> messageEntities) {
                handleOfflineMessage(messageEntities);
                return false;
            }

            @Override
            public void onSuccess(MessageEntity extr) {
            }

            @Override
            public ReceiverInfo getType() {
                return new ReceiverInfo(Destination.LIST, "0");
            }
        };
    }

    protected void handleOfflineMessage(List<MessageEntity> list) {
        for (MessageEntity messageEntity : list) {
            handleMessage(messageEntity);
        }
    }

    protected void handleMessage(MessageEntity messageEntity) {
        try {
            //通知中心+资讯
            if (Destination.BATCH_NOTIFICATION.equals(messageEntity.getConvNo())
                    || Destination.GLOBAL.equals(messageEntity.getConvNo())
                    || Destination.NOTIFY.equals(messageEntity.getConvNo())
                    || Destination.ARTICLE.equals(messageEntity.getConvNo())) {
                int count = mSwipeAdapter.getCount();
                New item = null;
                for (int i = 0; i < count; i++) {
                    //通知中心处理
                    if (Destination.GLOBAL.equals(messageEntity.getConvNo())
                            || Destination.NOTIFY.equals(messageEntity.getConvNo())
                            || Destination.BATCH_NOTIFICATION.equals(messageEntity.getConvNo())) {
                        if (Destination.GLOBAL.equals(mSwipeAdapter.getItem(i).convNo)
                                || Destination.NOTIFY.equals(mSwipeAdapter.getItem(i).convNo)
                                || Destination.BATCH_NOTIFICATION.equals(mSwipeAdapter.getItem(i).convNo)) {
                            item = mSwipeAdapter.getItem(i);
                            item.setContent(new MessageBody(messageEntity));
                            item.createdTime = messageEntity.getTime() * 1000L;
                            ConvEntity notifyConvEntity = IMClient.getClient().getConvManager().getConvByConvNo(Destination.NOTIFY);
                            ConvEntity globalConvEntity = IMClient.getClient().getConvManager().getConvByConvNo(Destination.GLOBAL);
                            ConvEntity batchNotificationConvEntity = IMClient.getClient().getConvManager().getConvByConvNo(Destination.BATCH_NOTIFICATION);
                            int notifyUnread = notifyConvEntity != null ? notifyConvEntity.getUnRead() : 0;
                            int globalUnread = globalConvEntity != null ? globalConvEntity.getUnRead() : 0;
                            int batchNotificationUnread = batchNotificationConvEntity != null ? batchNotificationConvEntity.getUnRead() : 0;
                            item.unread = notifyUnread + globalUnread + batchNotificationUnread;
                            mSwipeAdapter.push2Top(item);
                            break;
                        }
                        //资讯处理
                    } else if (Destination.ARTICLE.equals(messageEntity.getConvNo())) {
                        if (Destination.ARTICLE.equals(mSwipeAdapter.getItem(i).convNo)) {
                            item = mSwipeAdapter.getItem(i);
                            item.setContent(new MessageBody(messageEntity));
                            item.createdTime = messageEntity.getTime() * 1000L;
                            ConvEntity convEntity = IMClient.getClient().getConvManager().getConvByConvNo(Destination.ARTICLE);
                            item.unread = convEntity != null ? convEntity.getUnRead() : 0;
                            mSwipeAdapter.push2Top(item);
                        }
                    }
                }
                if (item == null) {
                    item = new New(messageEntity);
                    if (Destination.GLOBAL.equals(messageEntity.getConvNo())
                            || Destination.NOTIFY.equals(messageEntity.getConvNo())
                            || Destination.BATCH_NOTIFICATION.equals(messageEntity.getConvNo())) {
                        item.type = Destination.NOTIFY;
                        item.convNo = Destination.NOTIFY;
                    } else if (Destination.ARTICLE.equals(messageEntity.getConvNo())) {
                        item.type = messageEntity.getConvNo();
                        item.convNo = messageEntity.getConvNo();
                    }
                    item.setUnread(1);
                    mSwipeAdapter.addItem(item);
                    setListVisibility(mSwipeAdapter.getCount() == 0);
                }
                return;
            }

            New newItem = findItemInList(messageEntity.getConvNo());
            if (newItem == null) {
                newItem = new New(messageEntity);
                Role role = IMClient.getClient().getRoleManager().getRole(newItem.type, newItem.fromId);
                if (role.getRid() != 0) {
                    newItem.setImgUrl(role.getAvatar());
                    newItem.setTitle(role.getNickname());
                }
                newItem.setUnread(1);
                mSwipeAdapter.addItem(newItem);
                setListVisibility(mSwipeAdapter.getCount() == 0);
            } else {
                newItem.setUnread(++newItem.unread);
                MessageBody messageBody = new MessageBody(messageEntity);
                newItem.setContent(messageBody);
                mSwipeAdapter.updateItem(newItem);
            }
        } catch (Exception ex) {
            StatService.reportException(mContext, ex);
        }
    }

    private New findItemInList(String convNo) {
        int count = mSwipeAdapter.getCount();
        for (int i = 0; i < count; i++) {
            New item = mSwipeAdapter.getItem(i);
            if (convNo.equals(item.convNo)) {
                return item;
            }
        }
        return null;
    }

    private void updateIMConnectStatus(int status) {
        switch (status) {
            case IMConnectStatus.CLOSE:
            case IMConnectStatus.END:
            case IMConnectStatus.ERROR:
                if (!getAppSettingProvider().getAppConfig().isEnableIMChat) {
                    mLoadingHandler.sendEmptyMessage(DISMISS);
                    updateNetWorkStatusHeader("聊天功能已关闭, 请联系管理员");
                    return;
                }
                mLoadingHandler.sendEmptyMessage(SHOW);
                //updateNetWorkStatusHeader("消息服务器连接失败，请重试");
                updateNetWorkStatusHeader("");
                break;
            case IMConnectStatus.CONNECTING:
                mLoadingHandler.sendEmptyMessage(SHOW);
                //updateNetWorkStatusHeader("正在连接...");
                updateNetWorkStatusHeader("");
                break;
            case IMConnectStatus.NO_READY:
                mLoadingHandler.sendEmptyMessage(DISMISS);
                if (!getAppSettingProvider().getAppConfig().isEnableIMChat) {
                    updateNetWorkStatusHeader("聊天功能已关闭, 请联系管理员");
                    return;
                }
                updateNetWorkStatusHeader("消息服务未连接，请重试");
                break;
            case IMConnectStatus.OPEN:
            default:
                mLoadingHandler.sendEmptyMessage(DISMISS);
                updateNetWorkStatusHeader("");
        }
    }

    private IMConnectStatusListener getIMConnectStatusListener() {
        return new IMConnectStatusListener() {
            @Override
            public void onError() {
                updateIMConnectStatus(IMConnectStatus.ERROR);
            }

            @Override
            public void onClose() {
                updateIMConnectStatus(IMConnectStatus.CLOSE);
            }

            @Override
            public void onConnect() {
                updateIMConnectStatus(IMConnectStatus.CONNECTING);
            }

            @Override
            public void onOpen() {
                hideNetWorkStatusHeader();
            }

            @Override
            public void onInvalid(String[] ig) {
            }
        };
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getRestCourse();
        }
    }

    @Override
    protected void initView(View view) {
        mParentActivity = (DefaultPageActivity) getActivity();
        mLoadProgressBar = view.findViewById(R.id.news_progressbar);
        lvNewsList = (SwipeMenuListView) view.findViewById(R.id.lv_news_list);
        mEmptyView = view.findViewById(R.id.view_empty);
        tvEmptyText = (TextView) view.findViewById(R.id.tv_empty_text);
        tvEmptyText.setText(getResources().getString(R.string.news_empty_text));
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        mContext);
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                deleteItem.setWidth(AppUtil.dp2px(mContext, 65));
                deleteItem.setIcon(R.drawable.ic_delete);
                menu.addMenuItem(deleteItem);
            }
        };
        addHeadView();
        mSwipeAdapter = new SwipeAdapter(mContext, R.layout.news_item);
        lvNewsList.setAdapter(mSwipeAdapter);
        lvNewsList.setMenuCreator(creator);
        lvNewsList.setOnMenuItemClickListener(mMenuItemClickListener);
        lvNewsList.setOnItemClickListener(mItemClickListener);
        lvNewsList.setOnSwipeListener(getOnSwipeListener());
    }

    private SwipeMenuListView.OnSwipeListener getOnSwipeListener() {
        return new SwipeMenuListView.OnSwipeListener() {
            @Override
            public void onSwipeStart(int position) {
            }

            @Override
            public void onSwipeEnd(int position) {
            }

            @Override
            public boolean canSwipe(int position) {
                New item = mSwipeAdapter.getItem(position);
                if (item == null) {
                    return false;
                }
                return !Destination.NOTIFY.equals(item.getType());
            }
        };
    }

    private void initData() {
        if (app.loginUser == null) {
            return;
        }
        List<ConvEntity> newsList = new ArrayList<>();
        ConvEntity notificationConvEntity = IMClient.getClient().getConvManager().getNotificationCenterEntity(IMClient.getClient().getClientId(), CENTER_NOTIFICATION_TYPES);
        List<ConvEntity> otherList = IMClient.getClient().getConvManager().getOtherListInNew(IMClient.getClient().getClientId(), OTHER_TYPES);
        if (notificationConvEntity != null) {
            newsList.add(notificationConvEntity);
        }
        newsList.addAll(otherList);
        mSwipeAdapter.update(coverConvListToNewList(newsList));
        setListVisibility(mSwipeAdapter.getCount() == 0);
        mLoadProgressBar.setVisibility(View.GONE);
    }

    private List<New> coverConvListToNewList(List<ConvEntity> convEntityList) {
        List<New> newList = new ArrayList<>();
        IMRoleManager roleManager = IMClient.getClient().getRoleManager();
        for (ConvEntity convEntity : convEntityList) {
            New newItem = new New(convEntity);
            Role role = roleManager.getRole(convEntity.getType(), convEntity.getTargetId());
            if (role.getRid() != 0) {
                newItem.setImgUrl(role.getAvatar());
            }
            newList.add(newItem);
        }
        return newList;
    }

    private void syncIMData() {
        if (app.loginUser == null) {
            return;
        }
        new IMProvider(mContext).syncIMRoleData();
    }

    private SwipeMenuListView.OnMenuItemClickListener mMenuItemClickListener = new SwipeMenuListView.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
            New newModel = mSwipeAdapter.getItem(position);
            IMClient.getClient().getMessageManager().deleteByConvNo(newModel.convNo);
            IMClient.getClient().getConvManager().deleteConv(newModel.convNo);

            mSwipeAdapter.removeItem(position);
            setListVisibility(mSwipeAdapter.getCount() == 0);
            return false;
        }
    };

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mHeaderView != null && position == 0) {
                return;
            }
            final New newItem = (New) parent.getItemAtPosition(position);
            TypeBusinessEnum.getName(newItem.type);
            switch (newItem.type) {
                case Destination.NOTIFY:
                    NotificationCenterActivity.launch(getActivity());
                    break;
                case Destination.USER:
                    app.mEngine.runNormalPlugin("ImChatActivity", mContext, new PluginRunCallback() {
                        @Override
                        public void setIntentDate(Intent startIntent) {
                            startIntent.putExtra(ImChatActivity.FROM_ID, newItem.fromId);
                            startIntent.putExtra(ImChatActivity.FROM_NAME, newItem.title);
                            startIntent.putExtra(Const.NEWS_TYPE, newItem.type);
                            startIntent.putExtra(ImChatActivity.CONV_NO, newItem.convNo);
                            startIntent.putExtra(ImChatActivity.HEAD_IMAGE_URL, newItem.imgUrl);
                        }
                    });
                    break;
                case Destination.CLASSROOM:
                    app.mEngine.runNormalPlugin("ClassroomDiscussActivity", mContext, new PluginRunCallback() {
                        @Override
                        public void setIntentDate(Intent startIntent) {
                            startIntent.putExtra(ClassroomDiscussActivity.FROM_ID, newItem.fromId);
                            startIntent.putExtra(ClassroomDiscussActivity.FROM_NAME, newItem.title);
                            startIntent.putExtra(ClassroomDiscussActivity.CONV_NO, newItem.convNo);
                        }
                    });
                    break;
                case PushUtil.BulletinType.TYPE:
                    app.mEngine.runNormalPlugin("BulletinActivity", mContext, new PluginRunCallback() {
                        @Override
                        public void setIntentDate(Intent startIntent) {
                            startIntent.putExtra("", newItem.convNo);
                        }
                    });
                    break;
                case Destination.COURSE:
                    app.mEngine.runNormalPlugin("NewsCourseActivity", mContext, new PluginRunCallback() {
                        @Override
                        public void setIntentDate(Intent startIntent) {
                            startIntent.putExtra(NewsCourseActivity.COURSE_ID, newItem.fromId);
                            startIntent.putExtra(NewsCourseActivity.FROM_NAME, newItem.title);
                            startIntent.putExtra(NewsCourseActivity.CONV_NO, newItem.convNo);
                            startIntent.putExtra(
                                    NewsCourseActivity.SHOW_TYPE,
                                    newItem.unread > 0 ? NewsCourseActivity.DISCUSS_TYPE : NewsCourseActivity.LEARN_TYPE
                            );
                        }
                    });
                    break;
                case Destination.ARTICLE:
//                    app.mEngine.runNormalPlugin("ServiceProviderActivity", mContext, new PluginRunCallback() {
//                        @Override
//                        public void setIntentDate(Intent startIntent) {
//                            startIntent.putExtra(ServiceProviderActivity.SERVICE_TYPE, PushUtil.ArticleType.TYPE);
//                            startIntent.putExtra(ServiceProviderActivity.SERVICE_ID, newItem.fromId);
//                            startIntent.putExtra(ServiceProviderActivity.CONV_NO, newItem.convNo);
//                            startIntent.putExtra(Const.ACTIONBAR_TITLE, "资讯");
//                        }
//                    });
                    ArticleReactActivity.launchArticleList(getActivity());
                    break;
            }
        }
    };

    @Override
    public void invoke(WidgetMessage message) {
        switch (message.type.code) {
            case Const.REFRESH_LIST:
                Log.d("flag--", "onNext: REFRESH_LIST");
                initData();
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        return new MessageType[]{
                new MessageType(Const.ADD_MSG, source),
                new MessageType(Const.ADD_BULLETIN_MSG, source),
                new MessageType(Const.ADD_ARTICLE_CREATE_MAG, source),
                new MessageType(Const.LOGIN_SUCCESS),
                new MessageType(UPDATE_UNREAD_MSG, source),
                new MessageType(UPDATE_UNREAD_BULLETIN, source),
                new MessageType(UPDATE_UNREAD_NEWS_COURSE, source),
                new MessageType(Const.REFRESH_LIST, source),
                new MessageType(Const.ADD_THREAD_POST, source)};
    }

    /**
     * 设置空数据背景ICON
     *
     * @param visibility 是否空数据
     */

    private void setListVisibility(boolean visibility) {
        lvNewsList.setVisibility(visibility ? View.GONE : View.VISIBLE);
        mEmptyView.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    private void hideNetWorkStatusHeader() {
        updateNetWorkStatusHeader("");
    }

    private void addHeadView() {
        View headerRootView = LayoutInflater.from(mContext).inflate(R.layout.view_new_header_layout, lvNewsList, false);
        mHeaderView = (TextView) headerRootView.findViewById(R.id.header_title);
        lvNewsList.addHeaderView(headerRootView);
    }

    public void updateNetWorkStatusHeader(String statusText) {
        mHeaderView.setText(statusText);
        ViewGroup.LayoutParams lp = mHeaderView.getLayoutParams();
        if (lp == null) {
            lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mHeaderView.setLayoutParams(lp);
        }
        lp.height = TextUtils.isEmpty(statusText) ? 0 : 64;
        mHeaderView.setVisibility(TextUtils.isEmpty(statusText) ? View.GONE : View.VISIBLE);
        mHeaderView.setLayoutParams(lp);
    }

    private void getRestCourse() {
        try {
            mLoadingHandler.sendEmptyMessage(SHOW);
            HttpUtils.getInstance()
                    .baseOnApi()
                    .addTokenHeader(EdusohoApp.app.token)
                    .createApi(UserApi.class)
                    .getChatRooms()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SubscriberProcessor<ChatRoomResult>() {
                        @Override
                        public void onCompleted() {
                            super.onCompleted();
                            mLoadingHandler.sendEmptyMessage(DISMISS);
                        }

                        @Override
                        public void onNext(ChatRoomResult chatRooms) {
                            Log.d("flag--", "onNext: getRestCourse");
                            filterMyCourses(chatRooms.resources);
                        }

                        @Override
                        public void onError(String message) {
                            super.onError(message);
                            mLoadingHandler.sendEmptyMessage(DISMISS);
                        }
                    });

        } catch (Exception ex) {
            StatService.reportException(mContext, ex);
            mLoadingHandler.sendEmptyMessage(DISMISS);
        }
    }

    private void filterMyCourses(List<ChatRoomResult.ChatRoom> chatRooms) {
        List<ConvEntity> convEntityList = IMClient.getClient().getConvManager().getCourseConvList(IMClient.getClient().getClientId());
        convEntityList.addAll(IMClient.getClient().getConvManager().getClassroomConvList(IMClient.getClient().getClientId()));
        //本地已经存在的course ids
        List<ConvEntity> exitDiscusses = new ArrayList<>();
        for (ConvEntity convEntity : convEntityList) {
            int affectId = 0;
            for (ChatRoomResult.ChatRoom chatRoom : chatRooms) {
                if (chatRoom.id == convEntity.getTargetId() && chatRoom.type.equals(convEntity.getType())) {
                    convEntity.setAvatar(chatRoom.picture);
                    convEntity.setTargetName(chatRoom.title);
                    affectId = IMClient.getClient().getConvManager().updateConvByConvNo(convEntity);
                    break;
                }
            }
            if (affectId == 0) {
                exitDiscusses.add(convEntity);
            }
        }

        updateRolesByCourse(chatRooms);
        for (ConvEntity convEntity : exitDiscusses) {
            IMClient.getClient().getConvManager().deleteById(convEntity.getId());
        }

        initData();
    }

    private int[] getIntArrayFromListByCourse(List<ChatRoomResult.ChatRoom> chatRooms) {
        int[] array = new int[chatRooms.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = chatRooms.get(i).id;
        }

        return array;
    }

    public void updateRolesByCourse(List<ChatRoomResult.ChatRoom> chatRooms) {
        IMRoleManager roleManager = IMClient.getClient().getRoleManager();
        Map<Integer, Role> roleMap = roleManager.getRoleMap(getIntArrayFromListByCourse(chatRooms));
        for (ChatRoomResult.ChatRoom chatRoom : chatRooms) {
            Role role = new Role();
            role.setRid(chatRoom.id);
            role.setAvatar(chatRoom.picture);
            role.setNickname(chatRoom.title);
            role.setType(chatRoom.type);
            if (roleMap.containsKey(chatRoom.id)) {
                roleManager.updateRole(role);
                continue;
            }

            roleManager.createRole(role);
        }
    }

    private static class LoadingHandler extends Handler {
        private final WeakReference<NewsFragment> mFragment;

        public LoadingHandler(NewsFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            NewsFragment fragment = mFragment.get();
            if (fragment != null) {
                try {
                    switch (msg.what) {
                        case SHOW:
                            fragment.mParentActivity.setTitleLoading(true);
                            break;
                        case DISMISS:
                            fragment.mParentActivity.setTitleLoading(false);
                            break;
                    }
                } catch (Exception ex) {
                    Log.e(TAG, "handleMessage: " + ex.getMessage());
                }
            }
        }
    }
}
