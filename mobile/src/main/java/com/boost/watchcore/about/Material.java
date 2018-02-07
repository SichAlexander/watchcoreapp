package com.boost.watchcore.about;

import android.os.Bundle;

import com.boost.watchcore.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Material extends BaseAboutActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initResources();
        trackScreen(getString(R.string.analytics_material_about));
    }

    private void initResources(){
        List<Integer> img = new ArrayList<>();
        img.add(R.drawable.img_material_display_date);
        img.add(R.drawable.img_material_drop_shadow);
        img.add(R.drawable.img_material_hours_on_the_background);
        img.add(R.drawable.img_material_italyc_style);
        img.add(R.drawable.img_material_light_mode);
        img.add(R.drawable.img_material_minutes_hand);
        img.add(R.drawable.img_material_roman_figures_in_ambient_mode);
        img.add(R.drawable.img_material_second_hand);
        img.add(R.drawable.img_material_show_hour_hand);
        setImgResources(img);
        setDescriptionResources(Arrays.asList(getResources().getStringArray(R.array.about_material_description)));
        setIvZeroLayer(R.drawable.bg_material);
        setTitle(getString(R.string.material_config_text));
    }
}
