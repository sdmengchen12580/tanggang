package com.edusoho.kuozhi.clean.react;


import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import com.edusoho.kuozhi.imserver.IMClient;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.shell.MainReactPackage;
import rx.functions.Action1;

public class ArticleReactActivity extends AppCompatActivity implements DefaultHardwareBackBtnHandler {
    public static final String ARTICLE_ID = "articleId";

    private BundleManager        mBundleManager;
    private ReactRootView        mReactRootView;
    private ReactInstanceManager mReactInstanceManager;

    public static void launchArticleList(Context context) {
        context.startActivity(new Intent(context, ArticleReactActivity.class));
    }

    public static void launchArticleDetail(Context context, int articleId) {
        Intent intent = new Intent(context, ArticleReactActivity.class);
        intent.putExtra(ARTICLE_ID, articleId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundleManager = new BundleManager(getApplicationContext(), "index");
        String bundleFile = mBundleManager.getLocalBundleFilePath();
        try {
            mReactRootView = new ReactRootView(this);
            mReactInstanceManager = ReactInstanceManager.builder()
                    .setApplication(getApplication())
                    .setJSBundleFile(bundleFile)
                    .setJSMainModuleName("index.android")
                    .addPackage(new MainReactPackage())
                    .addPackage(new ESReactPackage())
                    .setUseDeveloperSupport(false)
                    .setInitialLifecycleState(LifecycleState.RESUMED)
                    .build();
            int articleId = getIntent().getIntExtra(ARTICLE_ID, 0);
            if (articleId == 0) {
                mReactRootView.startReactApplication(mReactInstanceManager, "ArticleProject", null);
            } else {
                mReactRootView.startReactApplication(mReactInstanceManager, "DetailProject", getIntent().getExtras());
            }

            setContentView(mReactRootView);
        } catch (Exception ex) {
            Log.d("flag--", "onCreate: " + ex.getMessage());
        }
        mBundleManager.verifiBundleVersion().subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean isSave) {
                Log.d("verifiBundleVersion", "result:" + isSave);
            }
        });
    }

    @Override
    public void invokeDefaultOnBackPressed() {
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostPause(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IMClient.getClient().getConvManager().clearReadCount(Destination.ARTICLE);
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostResume(this, this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostDestroy();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mReactInstanceManager != null) {
            mReactInstanceManager.onBackPressed();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_MENU && mReactInstanceManager != null) {
            mReactInstanceManager.showDevOptionsDialog();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
