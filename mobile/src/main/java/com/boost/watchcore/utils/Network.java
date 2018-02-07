package com.boost.watchcore.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public abstract class Network {
    /**
     * check existing internet connection
     *
     * @param _context
     * @return
     */
    public static final boolean isInternetConnectionAvailable(final Context _context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            return false;
        }

        return true;
    }

    /**
     *
     * @param context activity context
     * @param state
     * true     - turn on
     * false    - turn off
     */
    public static void changeWifiState(final Context context, final boolean state) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(state);
    }
}
