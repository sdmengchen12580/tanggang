package com.edusoho.kuozhi.v3.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.utils.ToastUtils;
import com.edusoho.kuozhi.imserver.IMClient;
import com.edusoho.kuozhi.imserver.entity.ConvEntity;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.imserver.entity.message.MessageBody;
import com.edusoho.kuozhi.v3.handler.ChatSendHandler;
import com.edusoho.kuozhi.v3.handler.ClassRoomChatSendHandler;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.Friend;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.bal.push.New;
import com.edusoho.kuozhi.v3.model.bal.push.RedirectBody;
import com.edusoho.kuozhi.v3.model.provider.IMProvider;
import com.edusoho.kuozhi.v3.model.provider.UserProvider;
import com.edusoho.kuozhi.v3.ui.FragmentPageActivity;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.google.gson.internal.LinkedHashTreeMap;
import com.google.gson.internal.LinkedTreeMap;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by howzhi on 15/9/28.
 */
public class ChatSelectFragment extends AbstractChatSendFragment {

    public static final int    REQUEST_SELECT = 0010;
    public static final int    RESULT_SEND_OK = 0020;
    public static final String BODY           = "body";

    private ChatSelectListAdapter mChatSelectListAdapter;
    private ListView              mChatSelectListView;
    private View                  mSelectFrientBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.chatselect_layout);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        initBundleData();
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mSelectFrientBtn = view.findViewById(R.id.select_friend_btn);
        mChatSelectListView = (ListView) view.findViewById(R.id.chat_select_list);
        mChatSelectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                New item = (New) parent.getItemAtPosition(position);

                RedirectBody redirectBody = getShowRedirectBody(item.title, item.imgUrl);
                if (PushUtil.ChatUserType.CLASSROOM.equals(item.type)) {
                    new ClassRoomChatSendHandler(mActivity, redirectBody, position).handleClick(mSendMessageHandlerCallback);
                    return;
                } else if (PushUtil.ChatUserType.USER.equals(item.type)) {
                    new ChatSendHandler(mActivity, redirectBody, position).handleClick(mUserSendMessageHandlerCallback);
                    return;
                }

                new ChatSendHandler(mActivity, redirectBody, position).handleClick(mSendMessageHandlerCallback);
            }
        });

        mSelectFrientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.mEngine.runNormalPluginForResult(
                        "FragmentPageActivity",
                        mActivity,
                        ChatSendHandler.REQUEST_SELECT_FRIEND,
                        new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                startIntent.putExtra(Const.ACTIONBAR_TITLE, "选择校友");
                                startIntent.putExtra(FragmentPageActivity.FRAGMENT, "FriendSelectFragment");
                                startIntent.putExtra(FriendSelectFragment.BODY, mRedirectBody);
                            }
                        });
            }
        });

        initChatList();
    }

    private NormalCallback<Integer> mUserSendMessageHandlerCallback = new NormalCallback<Integer>() {
        @Override
        public void success(Integer index) {
            New newItem = mChatSelectListAdapter.getItem(index);
            ConvEntity convEntity = IMClient.getClient().getConvManager()
                    .getConvByTypeAndId(Destination.USER, newItem.fromId);
            if (convEntity == null) {
                createChatConvNo(newItem.fromId);
                return;
            }
            sendMsg(newItem.id, convEntity.getConvNo(), Destination.USER, newItem.getTitle());
        }
    };

    protected void createChatConvNo(final int fromId) {
        final LoadDialog loadDialog = LoadDialog.create(mActivity);
        loadDialog.show();

        User currentUser = getAppSettingProvider().getCurrentUser();
        new UserProvider(mContext).createConvNo(new int[]{currentUser.id, fromId})
                .success(new NormalCallback<LinkedTreeMap>() {
                    @Override
                    public void success(LinkedTreeMap linkedHashMap) {
                        final String mConvNo;
                        if (linkedHashMap == null || (mConvNo = linkedHashMap.get("no").toString()) == null) {
                            ToastUtils.show(mActivity.getBaseContext(), "创建聊天失败!");
                            return;
                        }

                        new IMProvider(mContext).createConvInfoByUser(mConvNo, fromId)
                                .success(new NormalCallback<ConvEntity>() {
                                    @Override
                                    public void success(ConvEntity convEntity) {
                                        loadDialog.dismiss();
                                        sendMsg(fromId, mConvNo, Destination.USER, convEntity.getTargetName());
                                    }
                                });
                    }
                });
    }

    protected void sendMsg(int fromId, String convNo, String type, String title) {
        MessageBody messageBody = saveMessageToLoacl(fromId, convNo, type, title);
        sendMessageBody(convNo, messageBody);
    }

    private NormalCallback<Integer> mSendMessageHandlerCallback = new NormalCallback<Integer>() {
        @Override
        public void success(Integer index) {
            New newItem = mChatSelectListAdapter.getItem(index);
            MessageBody messageBody = saveMessageToLoacl(newItem.getFromId(), newItem.convNo, newItem.getType(), newItem.getTitle());
            sendMessageToServer(newItem.convNo, messageBody);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ChatSendHandler.REQUEST_SELECT_FRIEND
                && resultCode == ChatSendHandler.RESULT_SELECT_FRIEND_OK) {
            mActivity.setResult(RESULT_SEND_OK);
            mActivity.finish();
        }
    }

    @Override
    protected void sendFailCallback() {
    }

    @Override
    protected void sendSuccessCallback() {
        mActivity.setResult(RESULT_SEND_OK);
        mActivity.finish();
    }

    private void initBundleData() {
        Bundle bundle = getArguments();
        mRedirectBody = (RedirectBody) bundle.getSerializable(BODY);
    }

    private void initChatList() {

        List<ConvEntity> convEntityList = IMClient.getClient().getConvManager().getConvList();
        mChatSelectListAdapter = new ChatSelectListAdapter(mContext, filterChatSelectList(convEntityList));
        mChatSelectListView.setAdapter(mChatSelectListAdapter);
    }

    private List<New> filterChatSelectList(List<ConvEntity> convEntityList) {
        List<New> news = new ArrayList<>();
        for (ConvEntity item : convEntityList) {
            if (Destination.ARTICLE.equals(item.getType())
                    || Destination.GLOBAL.equals(item.getType())
                    || Destination.NOTIFY.equals(item.getType())) {
                continue;
            }
            news.add(new New(item));
        }

        return news;
    }

    private class ChatSelectListAdapter extends BaseAdapter {

        private Context             mContext;
        private List<New>           mChatList;
        private DisplayImageOptions mOptions;

        public ChatSelectListAdapter(Context context, List<New> chatList) {

            mContext = context;
            mChatList = chatList;
            mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).
                    showImageForEmptyUri(R.drawable.default_avatar).
                    showImageOnFail(R.drawable.default_avatar).build();
        }

        @Override
        public int getCount() {
            return mChatList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.chat_select_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.mAvatarView = (ImageView) convertView.findViewById(R.id.chat_select_avatar);
                viewHolder.mNameView = (TextView) convertView.findViewById(R.id.chat_select_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            New item = mChatList.get(position);
            viewHolder.mNameView.setText(item.title);
            ImageLoader.getInstance().displayImage(item.getImgUrl(), viewHolder.mAvatarView, mOptions);
            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public New getItem(int position) {
            return mChatList.get(position);
        }

        private class ViewHolder {
            public ImageView mAvatarView;
            public TextView  mNameView;
        }
    }
}
