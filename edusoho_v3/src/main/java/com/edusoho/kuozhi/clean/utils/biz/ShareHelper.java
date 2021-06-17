package com.edusoho.kuozhi.clean.utils.biz;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.bal.push.RedirectBody;
import com.edusoho.kuozhi.v3.ui.FragmentPageActivity;
import com.edusoho.kuozhi.v3.ui.fragment.ChatSelectFragment;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.sharelib.PlatformActionListenerProcessor;
import com.edusoho.sharelib.Share;
import com.edusoho.sharelib.onekeyshare.CustomerLogo;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.sharesdk.framework.Platform;

public class ShareHelper {
    private static final String SHARE_COURSE_TO_USER = "http://%s/mapi_v2/mobile/main#/%s/%s";
    private Context mContext;
    private String  mUrl;
    private String  mTitle;
    private String  mText;
    private String  mImageUrl;

    public static Builder builder() {
        return new Builder();
    }

    private ShareHelper(Builder builder) {
        this.mContext = builder.mContext;
        this.mUrl = builder.mUrl;
        this.mTitle = builder.mTitle;
        this.mText = builder.mText;
        this.mImageUrl = builder.mImageUrl;
    }

    public static class Builder {
        private Context mContext;
        private String  mUrl;
        private String  mTitle;
        private String  mText;
        private String  mImageUrl;

        public Builder init(Context context) {
            this.mContext = context;
            return this;
        }

        public Builder setUrl(String url) {
            this.mUrl = url;
            return this;
        }

        public Builder setTitle(String title) {
            this.mTitle = title;
            return this;
        }

        public Builder setText(String text) {
            if (text == null) {
                this.mText = "";
            } else {
                String shareText = Html.fromHtml(text).toString();
                this.mText = shareText.length() > 20 ? shareText.substring(0, 20) : shareText;
            }
            return this;
        }

        public Builder setImageUrl(String imageUrl) {
            this.mImageUrl = imageUrl;
            return this;
        }

        public ShareHelper build() {
            return new ShareHelper(this);
        }
    }

    private void shareToUser() {
        RedirectBody redirectBody = RedirectBody.createByShareContent(
                coverWebUrl(mUrl), mTitle, mText, mImageUrl);

        Intent startIntent = new Intent();
        startIntent.putExtra(Const.ACTIONBAR_TITLE, "选择");
        startIntent.putExtra(ChatSelectFragment.BODY, redirectBody);
        startIntent.putExtra(FragmentPageActivity.FRAGMENT, "ChatSelectFragment");
        startIntent.setComponent(new ComponentName(mContext, FragmentPageActivity.class));
        mContext.startActivity(startIntent);
    }

    private String coverWebUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return "";
        }
        Pattern WEB_URL_PAT = Pattern.compile("(http://)?(.+)/(course|classroom|article)/(\\d+)", Pattern.DOTALL);
        Matcher matcher = WEB_URL_PAT.matcher(url);
        if (matcher.find()) {
            return String.format(SHARE_COURSE_TO_USER, matcher.group(2), matcher.group(3), matcher.group(4));
        }
        return url;
    }

    private View.OnClickListener createClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareToUser();
            }
        };
    }

    public void share() {
        CustomerLogo customerLogo = null;
        if (EdusohoApp.app.loginUser != null) {
            customerLogo = new CustomerLogo(mContext.getResources().getString(R.string.send_to_friend)
                    , BitmapFactory.decodeResource(mContext.getResources()
                    , R.drawable.share_user)
                    , createClickListener());
        }

        Share.Builder()
                .setTitle(mTitle)
                .setTitleUrl(mUrl)
                .setText(mText)
                .setUrl(mUrl)
                .setImagePath(ImageLoader.getInstance().getDiskCache().get(mImageUrl).getAbsolutePath())
                .setAction(new PlatformActionListenerProcessor() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                        super.onComplete(platform, i, hashMap);
                    }
                })
                .setCustomLogo(customerLogo)
                .build()
                .share();
    }
}
