package com.edusoho.kuozhi.clean.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkChangeBroadcastReceiver extends BroadcastReceiver {

    private MobileConnectListener mMobileConnectListener;

    public NetworkChangeBroadcastReceiver(MobileConnectListener mobileConnectListener) {
        this.mMobileConnectListener = mobileConnectListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (info != null) {
                if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
                    if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                        if (mMobileConnectListener != null) {
                            mMobileConnectListener.invokeMobileNetwork();
                        }
                    }
                }
            }
        }
    }

    public interface MobileConnectListener {
        void invokeMobileNetwork();
    }

}
