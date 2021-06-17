package com.edusoho.kuozhi.v3.service.message.push;

import android.content.Context;
import android.content.Intent;

import com.edusoho.kuozhi.clean.react.ArticleReactActivity;
import com.edusoho.kuozhi.clean.utils.GsonUtils;
import com.edusoho.kuozhi.imserver.entity.message.MessageBody;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.util.LinkedHashMap;

/**
 * Created by suju on 16/11/10.
 */
public class ArticlePushProcessor implements IPushProcessor {

    private Context     mContext;
    private MessageBody mMessageBody;

    public ArticlePushProcessor(Context context, MessageBody messageBody) {
        this.mContext = context;
        this.mMessageBody = messageBody;
    }

    @Override
    public void processor() {

    }

    @Override
    public Intent getNotifyIntent() {
        Intent notifyIntent = new Intent(mContext, ArticleReactActivity.class);
        notifyIntent.removeCategory(Intent.CATEGORY_LAUNCHER);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //notifyIntent.putExtra(ServiceProviderActivity.SERVICE_TYPE, PushUtil.ArticleType.TYPE);
        LinkedTreeMap<String, String> body = GsonUtils.parseJson(mMessageBody.getBody(), new TypeToken<LinkedTreeMap<String, String>>() {
        });
        notifyIntent.putExtra(ArticleReactActivity.ARTICLE_ID, Integer.parseInt(body.get("id")));
        //notifyIntent.putExtra(Const.ACTIONBAR_TITLE, "资讯");
        //notifyIntent.putExtra(Const.INTENT_TARGET, ArticleReactActivity.class);
        return notifyIntent;
    }

    @Override
    public String[] getNotificationContent(String body) {
        LinkedHashMap<String, String> linkedHashMap = new Gson().fromJson(body, LinkedHashMap.class);
        return new String[]{linkedHashMap.get("title"), AppUtil.coverCourseAbout(linkedHashMap.get("content"))};
    }
}
