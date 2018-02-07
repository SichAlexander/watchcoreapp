package com.boost.watchcore.ui.faces.material;

import com.boost.watchcore.WatchConstants;
import com.google.android.gms.wearable.DataMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Alex_Jobs on 21.04.2015.
 */
public class MaterialWatchFaceUtils {

    private static HashMap<Integer, String> ROME_DIGITS = new HashMap<Integer, String>() {{
        put(1, "I");
        put(2, "II");
        put(3, "III");
        put(4, "IV");
        put(5, "V");
        put(6, "VI");
        put(7, "VII");
        put(8, "VIII");
        put(9, "IX");
    }};
    private static HashMap<Integer, String> ROME_DIGITS_TENTH = new HashMap<Integer, String>() {{
        put(10, "X");
        put(20, "XX");
        put(30, "XXX");
        put(40, "XL");
        put(50, "L");
        put(60, "LX");
    }};

    public static String getRomanDigit(int digit) {
        if (digit == 0)
            digit = 12;
        StringBuilder stringBuilder = new StringBuilder();
        List<Integer> listTenth = new ArrayList(ROME_DIGITS_TENTH.keySet());
        Collections.sort(listTenth);
        for (int i = ROME_DIGITS_TENTH.size(); i > 0; i--) {
            if (digit / listTenth.get(i - 1) != 0) {
                stringBuilder.append(ROME_DIGITS_TENTH.get(listTenth.get(i - 1)));
                digit -= listTenth.get(i - 1);
                break;
            }
        }
        List<Integer> list = new ArrayList(ROME_DIGITS.keySet());
        Collections.sort(list);
        for (int i = ROME_DIGITS.size(); i > 0; i--) {
            if (digit * 1.0f / list.get(i -1 ) == 1.0f) {
                stringBuilder.append(ROME_DIGITS.get(list.get(i -1)));
                break;
            }
        }
        return stringBuilder.toString();
    }

    public static void setDefaultValuesForMissingConfigKeys(DataMap config) {
        addIntKeyIfMissing(config, WatchConstants.KEY_BACKGROUND_STYLE_ITALIC,
                false);
        addIntKeyIfMissing(config, WatchConstants.KEY_HOURS_SHOW,
                false);
        addIntKeyIfMissing(config, WatchConstants.KEY_SHADOW_SHOW,
                false);
        addIntKeyIfMissing(config, WatchConstants.KEY_DATE_SHOW,
                false);
    }

    private static void addIntKeyIfMissing(DataMap config, String key, boolean show) {
        if (!config.containsKey(key)) {
            config.putBoolean(key, show);
        }
    }

}
