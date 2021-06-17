package com.edusoho.kuozhi.clean.module.base;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.trello.navi.Event;
import com.trello.navi.Listener;
import com.trello.navi.NaviComponent;
import com.trello.navi.internal.NaviEmitter;

/**
 * RxLifeCycle 兼容 KitKat 版本, 去掉 PersistableBundle 的所有参数
 */
public class ESNaviAppCompatActivity extends AppCompatActivity implements NaviComponent {
    private final NaviEmitter base = NaviEmitter.createActivityEmitter();

    @Override
    public final boolean handlesEvents(Event... events) {
        return base.handlesEvents(events);
    }

    @Override
    public final <T> void addListener(Event<T> event, Listener<T> listener) {
        base.addListener(event, listener);
    }

    @Override
    public final <T> void removeListener(Listener<T> listener) {
        base.removeListener(listener);
    }

    @Override
    @CallSuper
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        base.onCreate(savedInstanceState);
    }

    @Override
    @CallSuper
    protected void onStart() {
        super.onStart();
        base.onStart();
    }

    @Override
    @CallSuper
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        base.onPostCreate(savedInstanceState);
    }

    @Override
    @CallSuper
    protected void onResume() {
        super.onResume();
        base.onResume();
    }

    @Override
    @CallSuper
    protected void onPause() {
        base.onPause();
        super.onPause();
    }

    @Override
    @CallSuper
    protected void onStop() {
        base.onStop();
        super.onStop();
    }

    @Override
    @CallSuper
    protected void onDestroy() {
        base.onDestroy();
        super.onDestroy();
    }

    @Override
    @CallSuper
    protected void onRestart() {
        super.onRestart();
        base.onRestart();
    }

    @Override
    @CallSuper
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        base.onSaveInstanceState(outState);
    }

    @Override
    @CallSuper
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        base.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    @CallSuper
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        base.onNewIntent(intent);
    }

    @Override
    @CallSuper
    public void onBackPressed() {
        super.onBackPressed();
        base.onBackPressed();
    }

    @Override
    @CallSuper
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        base.onAttachedToWindow();
    }

    @Override
    @CallSuper
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        base.onDetachedFromWindow();
    }

    @Override
    @CallSuper
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        base.onConfigurationChanged(newConfig);
    }

    @Override
    @CallSuper
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        base.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    @CallSuper
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        base.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
