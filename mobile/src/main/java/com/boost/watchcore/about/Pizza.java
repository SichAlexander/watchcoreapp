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

public class Pizza extends BaseAboutActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initResources();
        trackScreen(getString(R.string.analytics_pizza_about));
    }

    private void initResources(){
        List<Integer> img = new ArrayList<>();
        img.add(R.drawable.img_pizza_hour_hand);
        img.add(R.drawable.img_pizza_hours_marked_with_slices_of_pizza);
        img.add(R.drawable.img_pizza_minute_hand);
        setImgResources(img);
        setDescriptionResources(Arrays.asList(getResources().getStringArray(R.array.about_pizza_description)));
        setIvZeroLayer(R.drawable.bg_pizza_for_app);
        setTitle(getString(R.string.pizza));
    }
}
