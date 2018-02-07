package com.boost.watchcore.ui.activity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.boost.watchcore.Application;
import com.boost.watchcore.R;
import com.boost.watchcore.about.ZoomOutPageTransformer;
import com.boost.watchcore.about.adapter.AboutImagePagerAdapter;
import com.boost.watchcore.utils.Pref;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.List;

/**
 * Created by cs on 03.08.15.
 */
public class SettingsActivity extends AppCompatActivity {
    private final String TAG = SettingsActivity.class.getSimpleName();
    CheckBox cbEnabledAnalytics;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        findUI();
        setActionBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUI();
    }

    private void findUI() {
        mToolbar    = (Toolbar) findViewById(R.id.toolbar_activity_settings);
        cbEnabledAnalytics = (CheckBox) findViewById(R.id.enabledAnalytics_CheckBox_SettingsActivity);
    }

    private void setUI(){
        cbEnabledAnalytics.setChecked(Pref.isEnableAnalytics(SettingsActivity.this));
        cbEnabledAnalytics.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Pref.setEnableAnalytics(SettingsActivity.this, isChecked);
            }
        });
    }

    private void setActionBar(){
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle(getString(R.string.sp_settings));
    }
}
