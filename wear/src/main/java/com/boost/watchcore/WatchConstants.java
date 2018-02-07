package com.boost.watchcore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Alex_Jobs on 27.04.2015.
 */
public class WatchConstants {
    public static final int UPDATE_MAX = 500;

    public static final Set<String> SET_PATH = new HashSet<String>() {{
        add(PATH_WITH_FEATURE_MATERIAL);
        add(PATH_WITH_FEATURE_PIZZA);

    }};
    //Material Settings params
    public static final String KEY_BACKGROUND_STYLE_ITALIC = "BACKGROUND_STYLE";
    public static final String KEY_HOURS_SHOW = "HOURS_SHOW";
    public static final String KEY_SHADOW_SHOW= "SHADOW_SHOW";
    public static final String KEY_DATE_SHOW = "DATE_SHOW";
    public static final String KEY_ROMAN_SHOW = "ROMAN_SHOW";
    public static final String KEY_NIGHT_MODE = "NIGHT_MODE";
    public static final String KEY_START_ABOUT = "START_ABOUT";
    public static final String PATH_WITH_FEATURE_MATERIAL = "/watch_face_config/Material";

    public static final List<String> MATERIAL_PROPERTIES_KEYS = new ArrayList<String>(){{
        add(KEY_BACKGROUND_STYLE_ITALIC);
        add(KEY_DATE_SHOW);
        add(KEY_HOURS_SHOW);
        add(KEY_SHADOW_SHOW);
        add(KEY_ROMAN_SHOW);
        add(KEY_NIGHT_MODE);
        add(KEY_START_ABOUT);
    }};

    public static final String PATH_WITH_FEATURE_PIZZA = "/watch_face_config/Pizza";

    public static final String START_ACTIVITY_PATH = "/start-activity";

}
