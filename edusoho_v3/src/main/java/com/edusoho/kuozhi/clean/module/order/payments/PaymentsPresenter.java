package com.edusoho.kuozhi.clean.module.order.payments;

import android.support.annotation.NonNull;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.api.OrderApi;
import com.edusoho.kuozhi.clean.bean.OrderInfo;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.http.SubscriberProcessor;
import com.edusoho.kuozhi.clean.module.base.BaseActivity;
import com.edusoho.kuozhi.clean.module.order.payments.PaymentsContract.Presenter;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.util.encrypt.XXTEA;
import com.google.gson.JsonObject;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.navi.NaviLifecycle;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by DF on 2017/4/7.
 */

public class PaymentsPresenter implements Presenter {

    static final         String ALIPAY         = "alipay";
    static final         String COIN           = "coin";
    private static final String ORDER_ID       = "id";
    private static final String TARGET_TYPE    = "targetType";
    private static final String TARGET_ID      = "targetId";
    private static final String APP_PAY      = "appPay";
    private static final String COUPON_CODE    = "couponCode";
    private static final String PAY_PASSWORD   = "payPassword";
    private static final String COIN_PAYAMOUNT = "coinPayAmount";
    private static final String PAYMENT_HTML   = "paymentHtml";
    private static final String STATUS         = "status";
    private static final String STATUS_PAID    = "paid";

    private PaymentsContract.View mView;
    private OrderInfo             mOrderInfo;
    private int                   mPosition;

    private final LifecycleProvider<ActivityEvent> mActivityLifeProvider;

    PaymentsPresenter(PaymentsContract.View view, OrderInfo orderInfo, int position) {
        this.mView = view;
        mActivityLifeProvider = NaviLifecycle.createActivityLifecycleProvider((BaseActivity) view);
        this.mOrderInfo = orderInfo;
        this.mPosition = position;
    }

    @Override
    public void subscribe() {
    }

    @Override
    public void createOrderAndPay(final String payment, String password, float orderPrice) {
        Map<String, String> map = createParameter(payment, password, orderPrice);
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(OrderApi.class)
                .createOrder(map)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(mActivityLifeProvider.<JsonObject>bindToLifecycle())
                .flatMap(new Func1<JsonObject, Observable<JsonObject>>() {
                    @Override
                    public Observable<JsonObject> call(JsonObject jsonObject) {
                        if (jsonObject != null) {
                            String orderId = jsonObject.get(ORDER_ID).getAsString();
                            return HttpUtils.getInstance()
                                    .addTokenHeader(EdusohoApp.app.token)
                                    .createApi(OrderApi.class)
                                    .goPay(orderId, mOrderInfo.targetType, payment);
                        } else {
                            mView.showLoadDialog(false);
                            mView.showToast(R.string.pay_fail);
                            return null;
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SubscriberProcessor<JsonObject>() {
                    @Override
                    public void onError(String e) {
                        mView.showLoadDialog(false);
                        if (e.contains("2002")) {
                            mView.showToast(R.string.password_error_hint);
                        }
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        responseDeal(jsonObject, payment);
                    }
                });
    }

    private void responseDeal(JsonObject jsonObject, String payment) {
        mView.showLoadDialog(false);
        if (STATUS_PAID.equals(jsonObject.get(STATUS).getAsString())) {
            mView.showToast(R.string.join_success);
            mView.sendBroad();
            return;
        }
        if (ALIPAY.equals(payment)) {
            String data = jsonObject.get(PAYMENT_HTML).getAsString();
            Pattern p = Pattern.compile("post");
            Matcher m = p.matcher(data);
            data = m.replaceFirst("get");
            mView.goToAlipay(data);
        }
    }

    @NonNull
    private Map<String, String> createParameter(String payment, String password, float orderPrice) {
        Map<String, String> map = new HashMap<>();
        if (mPosition != -1 && mOrderInfo.availableCoupons != null && mOrderInfo.availableCoupons.size() > 0) {
            map.put(COUPON_CODE, mOrderInfo.availableCoupons.get(mPosition).code);
        }
        if (COIN.equals(payment)) {
            map.put(COIN_PAYAMOUNT, orderPrice + "");
            password = XXTEA.encryptToBase64String(password, "EduSoho");
            map.put(PAY_PASSWORD, password);
        }
        map.put(TARGET_TYPE, mOrderInfo.targetType);
        map.put(TARGET_ID, mOrderInfo.targetId + "");
        map.put(APP_PAY, "Y");
        return map;
    }

    @Override
    public void unsubscribe() {
    }
}
