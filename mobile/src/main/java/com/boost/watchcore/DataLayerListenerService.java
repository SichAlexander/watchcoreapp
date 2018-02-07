package com.boost.watchcore;

import android.content.Intent;
import android.util.Log;

import com.boost.watchcore.utils.Constant;
import com.boost.watchcore.utils.Pref;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by cs on 30.07.15.
 */
public class DataLayerListenerService extends WearableListenerService {

    private static final String TAG = "DataLayerSample";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        if (messageEvent.getPath().equals(Constant.START_ACTIVITY_PATH)) {
            String className = new String(messageEvent.getData());
            Intent startIntent = new Intent(className);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(startIntent);
            }
            catch (Exception e){
                Log.d("cs_c", e.getMessage());
            }
            trackEventAboutFromWear(className);
        }
    }

    private void trackEventAboutFromWear(String className){
        ((Application)getApplication()).trackEvent("About", "event", "click", className);

    }
}