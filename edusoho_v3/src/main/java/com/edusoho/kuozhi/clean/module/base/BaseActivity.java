package com.edusoho.kuozhi.clean.module.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.http.SubscriptionManager;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.provider.AppSettingProvider;
import com.edusoho.kuozhi.v3.model.provider.IMServiceProvider;
import com.edusoho.kuozhi.v3.ui.DefaultPageActivity;
import com.edusoho.kuozhi.v3.ui.LoginActivity;
import com.edusoho.kuozhi.v3.util.Const;
import com.tencent.stat.StatService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by JesseHuang on 2017/4/21.
 */

public class BaseActivity<T extends BasePresenter> extends ESNaviAppCompatActivity implements BaseView<T> {

    public T mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().removeAllStickyEvents();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onReceiveMessage(MessageEvent messageEvent) {

    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onCredentialExpired(MessageEvent messageEvent) {
        synchronized (this) {
            if (messageEvent.getType() == MessageEvent.CREDENTIAL_EXPIRED) {
                EventBus.getDefault().cancelEventDelivery(messageEvent);
                SubscriptionManager.unsubscribeAll();
                showToast(R.string.token_lose_notice);
                AppSettingProvider provider = FactoryManager.getInstance().create(AppSettingProvider.class);
                provider.setUser(null);
                new IMServiceProvider(getBaseContext()).unBindServer();
                EdusohoApp.app.removeToken();
                MessageEngine.getInstance().sendMsg(Const.LOGOUT_SUCCESS, null);
                LoginActivity.launchAndClear(this);
                MessageEngine.getInstance().sendMsgToTaget(Const.SWITCH_TAB, null, DefaultPageActivity.class);
            }
        }
    }

    @Override
    public void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void close() {
        finish();
    }
}
