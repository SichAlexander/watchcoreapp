package com.boost.watchcore.about;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.boost.watchcore.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HeartBeat  extends BaseAboutActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initResources();
        trackScreen(getString(R.string.analytics_heartbeat_about));
    }

    private void initResources(){
        List<Integer> img = new ArrayList<>();
        img.add(R.drawable.img_heartbeat_animation_pulse);
        img.add(R.drawable.img_heartbeat_hours);
        img.add(R.drawable.img_heartbeat_minutes);
        setImgResources(img);
        setDescriptionResources(Arrays.asList(getResources().getStringArray(R.array.about_heartbeat_description)));
        setIvZeroLayer(R.drawable.bg_heartbeat);
        setTitle(getString(R.string.heartbeat));
    }
}
