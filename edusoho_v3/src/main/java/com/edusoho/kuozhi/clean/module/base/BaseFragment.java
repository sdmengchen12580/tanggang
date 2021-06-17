package com.edusoho.kuozhi.clean.module.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.tencent.stat.StatService;
import com.trello.navi.component.support.NaviFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by JesseHuang on 2017/4/21.
 */

public class BaseFragment<T extends BasePresenter> extends NaviFragment implements BaseView<T> {

    public T mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().removeAllStickyEvents();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            StatService.onResume(getActivity());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null) {
            StatService.onPause(getActivity());
        }
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void close() {
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    @Subscribe
    public void onReceiveMessage(MessageEvent messageEvent) {

    }

    @Override
    public void showToast(int resId) {
        Toast.makeText(getActivity(), resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}
