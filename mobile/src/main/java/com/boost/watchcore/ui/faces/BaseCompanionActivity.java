package com.boost.watchcore.ui.faces;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.boost.watchcore.Application;
import com.boost.watchcore.R;
import com.boost.watchcore.utils.Pref;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by Alex_Jobs on 03.06.2015.
 */
public class BaseCompanionActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void trackScreen(String screenName) {
       ((Application)getApplication()).trackScreen(screenName);
    }
}
