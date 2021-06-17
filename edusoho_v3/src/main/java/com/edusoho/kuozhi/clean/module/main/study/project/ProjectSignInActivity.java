package com.edusoho.kuozhi.clean.module.main.study.project;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.module.base.BaseActivity;
import com.edusoho.kuozhi.clean.widget.ESIconView;
import com.edusoho.kuozhi.v3.EdusohoApp;

public class ProjectSignInActivity extends BaseActivity<ProjectSignInContract.Presenter> implements ProjectSignInContract.View {

    public static final String QR_CODE_URL = "qr_code_url";

    private ESIconView mBack;
    private TextView   mSignMessage;
    private ImageView  mIconSignIn;

    private String mUrl;

    public static void launch(Context context, String url) {
        Intent intent = new Intent(context, ProjectSignInActivity.class);
        intent.putExtra(QR_CODE_URL, url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_sign_in);
        initView();
        mUrl = getIntent().getStringExtra(QR_CODE_URL);
        mPresenter = new ProjectSignInPresenter(this);
        mPresenter.subscribe();
        signIn(mUrl);
    }

    private void initView() {
        mBack = findViewById(R.id.iv_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mIconSignIn = findViewById(R.id.icon_sign_in);
        mSignMessage = findViewById(R.id.tv_sign_in);
    }

    private void signIn(String requestUrl) {
        mPresenter.requestUrl(String.format("%s?origin=app&nickname=%s", requestUrl, EdusohoApp.app.loginUser.nickname));
    }

    @Override
    public void showSignInSuccess(String message) {
        mIconSignIn.setVisibility(View.VISIBLE);
        mIconSignIn.setImageDrawable(getResources().getDrawable(R.drawable.signin_success));
        mSignMessage.setVisibility(View.VISIBLE);
        mSignMessage.setTextColor(getResources().getColor(R.color.sign_in_success));
        mSignMessage.setText(message);
    }

    @Override
    public void showSignInFailed(String message) {
        mIconSignIn.setVisibility(View.VISIBLE);
        mIconSignIn.setVisibility(View.VISIBLE);
        mIconSignIn.setImageDrawable(getResources().getDrawable(R.drawable.signin_failed));
        mSignMessage.setVisibility(View.VISIBLE);
        mSignMessage.setTextColor(getResources().getColor(R.color.sign_in_failed));
        mSignMessage.setText(message);
    }


    @Override
    public void showSignInResponseError() {
        mIconSignIn.setVisibility(View.GONE);
        mSignMessage.setVisibility(View.VISIBLE);
        mSignMessage.setTextColor(getResources().getColor(R.color.sign_in_failed));
        mSignMessage.setText(R.string.sign_in_error_message);
    }
}
