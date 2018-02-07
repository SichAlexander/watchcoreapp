package com.boost.watchcore;


import com.boost.watchcore.utils.Pref;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by cs on 07.08.15.
 */
public class Application extends android.app.Application {
    private  GoogleAnalytics analytics;
    private  Tracker tracker;

    @Override
    public void onCreate() {
        super.onCreate();
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(10);

        tracker = analytics.newTracker(getString(R.string.analytics_tracking_id));
        tracker.enableExceptionReporting(true);
        tracker.enableAutoActivityTracking(false);
    }

    private Tracker getNewTracker(){
        return tracker;
    }

    public void trackScreen(String screenName) {
        if (!Pref.isEnableAnalytics(getBaseContext()))
            return;
        Tracker tracker = getNewTracker();
        tracker.setScreenName(screenName);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void trackEvent(String screenName, String category, String action, String label){
        if (!Pref.isEnableAnalytics(getBaseContext()))
            return;
        Tracker tracker = getNewTracker();
        tracker.setScreenName(screenName);

        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());
    }
}
