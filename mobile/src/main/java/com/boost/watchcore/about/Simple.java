package com.boost.watchcore.about;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.boost.watchcore.R;
import com.boost.watchcore.about.adapter.AboutImagePagerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Simple  extends BaseAboutActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initResources();
        trackScreen(getString(R.string.analytics_simple_about));
    }

    private void initResources(){
        List<Integer> img = new ArrayList<>();
        img.add(R.drawable.img_simple_display_date);
        img.add(R.drawable.img_simple_display_time);
        setImgResources(img);
        setDescriptionResources(Arrays.asList(getResources().getStringArray(R.array.about_simple_description)));
        setIvZeroLayer(R.drawable.bg_simple);
        setTitle(getString(R.string.simple));
    }
}
