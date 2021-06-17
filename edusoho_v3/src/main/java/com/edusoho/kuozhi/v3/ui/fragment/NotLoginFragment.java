package com.edusoho.kuozhi.v3.ui.fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by RexXiang on 2017/7/18.
 */

public class NotLoginFragment extends BaseFragment {

    public static final int FRAGMENT_MINE = 1;
    public static final int FRAGMENT_NEWS = 0;

    private int mType = FRAGMENT_NEWS;
    private Button mLoginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mType = getArguments().getInt("FragmentType", 0);
        }
        if (mType == FRAGMENT_MINE) {
            setContainerView(R.layout.fragment_not_login);
        } else {
            setContainerView(R.layout.fragment_not_login_news);
        }
        setHasOptionsMenu(true);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        if (mType == FRAGMENT_NEWS) {
            mLoginButton = (Button) view.findViewById(R.id.btn_not_login);
            mLoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    app.mEngine.runNormalPluginWithAnim("LoginActivity", mContext, null, new NormalCallback() {
                        @Override
                        public void success(Object obj) {
                            mActivity.overridePendingTransition(R.anim.down_to_up, R.anim.none);
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mType == FRAGMENT_NEWS) {
            inflater.inflate(R.menu.news_menu, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mType == FRAGMENT_NEWS) {
            if (item.getItemId() == R.id.news_search) {
                MobclickAgent.onEvent(mContext, "dynamic_sweepButton");
                app.mEngine.runNormalPlugin("QrSearchActivity", mContext, null);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
