package com.edusoho.kuozhi.clean.http;

import com.edusoho.kuozhi.clean.bean.ErrorResult;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.utils.Constants;
import com.edusoho.kuozhi.clean.utils.GsonUtils;
import com.edusoho.kuozhi.clean.utils.biz.ErrorHelper;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.HttpException;
import rx.Subscriber;

/**
 * Created by JesseHuang on 2017/5/29.
 */

public class SubscriberProcessor<T> extends Subscriber<T> {

    public SubscriberProcessor() {
        SubscriptionManager.add(this);
    }

    @Override
    public void onNext(T t) {

    }

    @Override
    public void onCompleted() {
        SubscriptionManager.remove(this);
    }

    @Override
    final public void onError(Throwable e) {
        String json;
        try {
            if (e instanceof JsonSyntaxException) {
                onError(ErrorHelper.getMessage(ErrorHelper.JSON_ERROR));
                return;
            }
            if (e instanceof UnknownHostException || e instanceof SocketTimeoutException || e instanceof ConnectException) {
                onError(ErrorHelper.getMessage(ErrorHelper.INTERNAL_TIMEOUT_OR_BAD));
                return;
            }
            if (e instanceof HttpException) {
                HttpException httpException = (HttpException) e;
                if (httpException.code() == Constants.HttpCode.UNAUTHORIZED) {
                    EventBus.getDefault().post(new MessageEvent<>(MessageEvent.CREDENTIAL_EXPIRED));
                    return;
                } else if (httpException.code() == Constants.HttpCode.URL_REDIRECTION) {
                    onError(httpException.response().headers().get("location"));
                    return;
                }
                json = httpException.response().errorBody().string();
                ErrorResult error = GsonUtils.parseJson(json, new TypeToken<ErrorResult>() {
                });
                if (!error.isNull()) {
                    onError(error.error.message);
                }
            }
        } catch (Exception ex) {
            onError(ErrorHelper.getMessage());
        }
    }

    public void onError(String message) {

    }
}
